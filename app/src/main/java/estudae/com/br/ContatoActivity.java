package estudae.com.br;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import estudae.com.br.bd.BancoDadosHelper;
import estudae.com.br.bd.Contato;


public class ContatoActivity extends AppCompatActivity {

    private int ano, mes, dia;
    private ImageView imgContato;

    private EditText txtNome;
    private EditText txtEndereco;
    private EditText txtTelefone;
    private EditText txtSite;
    private Button btnCadastro;
    private EditText txtEmail;
    private Contato contato;

    private static Button dataNascimento;
    private String localArquivoFoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contato);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar_child);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        dataNascimento = (Button) findViewById(R.id.txtDatanascimento);
        dataNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        imgContato = (ImageView) findViewById(R.id.imgContato);
        imgContato.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                dispatchTakePictureIntentSimple();
            }
        });



        txtNome = (EditText) findViewById(R.id.txtNome);
        txtEndereco = (EditText) findViewById(R.id.txtEndereco);
        txtTelefone = (EditText) findViewById(R.id.txtTelefone);
        txtSite = (EditText) findViewById(R.id.txtSite);
        txtEmail = (EditText) findViewById(R.id.txtEmail);


        Intent intent = getIntent();
        if(intent != null){
            contato = (Contato) intent.getSerializableExtra("contato");
            if(contato != null){
                this.txtNome.setText(contato.nome);
                this.txtEndereco.setText(contato.endereco);
                this.txtTelefone.setText(contato.telefone);
                this.txtSite.setText(contato.site);
                this.txtEmail.setText(contato.email);
                if(contato.dataNascimento != null){
                    this.dataNascimento.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(contato.dataNascimento)));
                }else{
                    this.dataNascimento.setText("data");
                }

                if(contato.foto != null){
                    readBitmapFile(contato.foto);
                }
            }else{
                contato = new Contato();
            }
        }


        btnCadastro = (Button) findViewById(R.id.btnCadastro);
        btnCadastro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // contato = new Contato();

                contato.nome = txtNome.getText().toString();
                contato.endereco = txtEndereco.getText().toString();
                contato.telefone = txtTelefone.getText().toString();
                contato.site = txtSite.getText().toString();
                contato.email = txtEmail.getText().toString();

                if(mCurrentPhotoPath != null){
                    contato.foto = mCurrentPhotoPath;
                }

                try {
                    contato.dataNascimento = new SimpleDateFormat("dd/MM/yyyy")
                            .parse(dataNascimento.getText().toString())
                            .getTime();
                } catch (ParseException e) {
                    contato.dataNascimento = null;
                }

                BancoDadosHelper bd = new BancoDadosHelper(ContatoActivity.this);
                if(contato.id == null){
                    bd.insereContato(contato);
                }else{
                    bd.atualizaContato(contato);
                }
                bd.close();

                finish();

            }
        });



    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return  dialog;

        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dataNascimento.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    // METODOS DE USO DA CAMERA

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgContato.setImageBitmap(imageBitmap);

            try {
                this.storeImage(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // METODOS PARA TRABALHAR COM A CAMERA - INICIO
    private void dispatchTakePictureIntentSimple() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void storeImage(Bitmap image) throws IOException {

        File pictureFile = createImageFile();
        if (pictureFile == null) {
            Log.d("ERRO", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERRO", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERRO", "Error accessing file: " + e.getMessage());
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void readBitmapFile(String path){

        Bitmap bitmap = null;
        File f = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        imgContato.setImageBitmap(bitmap);
    }

}