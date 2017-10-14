package estudae.com.br;

// https://stackoverflow.com/questions/35970142/broadcastreceiver-sms-received-not-working-on-new-devices

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;

import estudae.com.br.bd.BancoDadosHelper;
import estudae.com.br.bd.Contato;
public class ListaContatosActivity extends AppCompatActivity {
    private ListView listaContatos;
    private List<Contato> contatos;
    private Contato contatoSelecionado;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private int MY_PERMISSIONS_REQUEST_INTERNET = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listacontatos);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);



    }

    @Override
    protected void onResume() {
        super.onResume();

        carregaLista();

        listaContatos.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int posicao, long id) {
                Intent intent = new Intent(ListaContatosActivity.this, ContatoActivity.class);
                intent.putExtra("contato", contatos.get(posicao));
                startActivity(intent);
            }
        });

        listaContatos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter,
                                           View view, int posicao, long id) {
                contatoSelecionado = contatos.get(posicao);
                return false;
            }
        });

        registerForContextMenu(listaContatos);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.novo:
                //Toast.makeText(this, "Novo", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, ContatoActivity.class);
                startActivity(intent);
                return false;

            case R.id.sincronizar:
                Toast.makeText(this, "Enviar", Toast.LENGTH_LONG).show();
                return false;


            case R.id.receber:
                //new LendoServidor().start();
                new LendoServidorTask().execute();
                return false;

            case R.id.mapa:
                Intent mapa = new Intent(ListaContatosActivity.this, MapsActivity.class);
                startActivity(mapa);
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contato_contexto, menu);
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.excluir:

                new AlertDialog.Builder(ListaContatosActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deletar")
                        .setMessage("Deseja mesmo deletar ?")
                        .setPositiveButton("Quero",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        BancoDadosHelper dao = new BancoDadosHelper(ListaContatosActivity.this);
                                        dao.apagaContato(contatoSelecionado);
                                        dao.close();
                                        carregaLista();
                                    }
                                }).setNegativeButton("Nao", null).show();

                return false;


            case R.id.enviasms:
                Intent intentSms = new Intent(Intent.ACTION_VIEW);
                intentSms.setData(Uri.parse("sms:"+contatoSelecionado.telefone));
                intentSms.putExtra("sms_body", "Mensagem");
                item.setIntent(intentSms);
                return false;

            case R.id.enviaemail:
                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                intentEmail.setType("message/rfc822");
                intentEmail.putExtra(Intent.EXTRA_EMAIL,
                        new String[] { contatoSelecionado.email });
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Teste de email");
                intentEmail.putExtra(Intent.EXTRA_TEXT, "Corpo da mensagem");
                //item.setIntent(intentEmail);
                startActivity(Intent.createChooser(intentEmail, "Selecione a sua aplicação de Email"));

                return false;

            case R.id.ligar:
                Intent intentLigar = new Intent(Intent.ACTION_DIAL);
                intentLigar.setData(Uri.parse("tel:"+contatoSelecionado.telefone));
                item.setIntent(intentLigar);
                return false;

            case R.id.share:
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_SUBJECT, "Assunto que será compartilhado");
                intentShare.putExtra(Intent.EXTRA_TEXT, "Texto que será compartilhado");
                startActivity(Intent.createChooser(intentShare, "Escolha como compartilhar"));
                return false;

            case R.id.visualizarmapa:
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+contatoSelecionado.endereco);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return false;


            default:
                return super.onContextItemSelected(item);
        }
    }

    private void carregaLista() {
        BancoDadosHelper bd = new BancoDadosHelper(ListaContatosActivity.this);
        contatos = bd.listaContatos();
        bd.close();
        ArrayAdapter<Contato> adapter = new ArrayAdapter<Contato>(this, android.R.layout.simple_list_item_1, contatos);

        listaContatos = (ListView) findViewById(R.id.lista);

        listaContatos.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("aula", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }


    private class LendoServidor extends Thread{

        @Override
        public void run() {
            String urlContatos = Constants.URLSERVIDOR;
            String url = Uri.parse(urlContatos).toString();
            String textoContatos = ContatoUtil.acessar(url);

            Log.i("aula", textoContatos);
        }
    }


    private class LendoServidorTask extends AsyncTask<Object, Object, String> {

        private ProgressDialog progress;

        @Override
        protected String doInBackground(Object... params) {

            String urlContatos = Constants.URLSERVIDOR;
            String url = Uri.parse(urlContatos).toString();
            String conteudo = ContatoUtil.acessar(url);

            Log.i("aula", conteudo);

            try {
                List<Contato> listaContatosWeb = ContatoUtil.contatoConverter(conteudo);
                BancoDadosHelper bd = new BancoDadosHelper( ListaContatosActivity.this);
                for (Contato contato : listaContatosWeb) {
                    bd.insereContato(contato);
                }
                bd.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return conteudo;
        }

        @Override
        protected void onPreExecute() {
            // executa algo antes de iniciar a tarefa
            super.onPreExecute();
            progress =
                    ProgressDialog.show(ListaContatosActivity.this,"Aguarde ...","Recebendo Inf...");

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            carregaLista();
            progress.dismiss();
        }

    }

}
