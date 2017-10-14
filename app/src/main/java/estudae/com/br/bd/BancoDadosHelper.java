package estudae.com.br.bd;

/**
 * Created by cubas on 23/09/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BancoDadosHelper extends SQLiteOpenHelper {

    public static final String BANCO = "AGENDA";
    public static final int VERSAO = 2;

    private String scriptSQLCreate[] = {
            "CREATE TABLE agenda (id INTEGER PRIMARY KEY, email TEXT,  nome TEXT, endereco TEXT, telefone NUMERIC, datanascimento NUMERIC, site TEXT, foto TEXT);",
            "INSERT INTO agenda VALUES(1,'fernando.collor@gmail.com','Fernando Collor','R. Assungui, 27, Cursino, São Paulo, 04131-000, Brasil',800200300,NULL,'www.google.com.br',NULL);",
            "INSERT INTO agenda VALUES(2,'dilma@gmail.com','Dilma','R. José Cocciuffo, 90 - Cursino, São Paulo, 04121-120, Brasil',800235468,NULL,'www.uol.com.br',NULL);",
            "INSERT INTO agenda VALUES(3,'lula@gmail.com','Lula','R. José Cocciuffo, 56 - Cursino, São Paulo, 04121-120, Brasil',80023587,NULL,'www.google.com',NULL);",
            "INSERT INTO agenda VALUES(4,'maluf@gmail.com','Maluf','R. Camilo José, 48 - Cursino, São Paulo, 04125-140, Brasil',800025774,NULL,'www.uol.com.br',NULL);" };

    private String scriptSQLDelete = "drop table agenda";



    public BancoDadosHelper(Context context) {
        super(context, BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("aula", "Criando banco com sql");

        int qtdeScripts = scriptSQLCreate.length;

        // Executa cada sql passado como parâmetro
        for (int i = 0; i < qtdeScripts; i++) {
            String sql = scriptSQLCreate[i];
            Log.i("aula", sql);
            // Cria o banco de dados executando o script de criação
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("aula", "Atualizando da versão " + oldVersion + " para " + newVersion + ". Todos os registros serão deletados.");
        Log.i("aula", scriptSQLDelete);
        // Deleta as tabelas...
        db.execSQL(scriptSQLDelete);
        // Cria novamente...
        onCreate(db);
    }

    public List<Contato> listaContatos() {
        List<Contato> contatos = new ArrayList<Contato>();

        Cursor c = getWritableDatabase().query("agenda",
                new String[] { "id", "nome", "endereco", "email", "telefone","foto","datanascimento" },
                null, null, null, null, null);

        // se encontrou
        if (c.moveToFirst()) {
            do {
                Contato contato = new Contato();
                contato.id = c.getLong(0);
                contato.nome = c.getString(1);
                contato.endereco = c.getString(2);
                contato.email = c.getString(3);
                contato.telefone = c.getString(4);
                contato.foto = c.getString(5);
                contato.dataNascimento = c.getLong(6);
                contatos.add(contato);
            } while (c.moveToNext());
        }
        c.close();

        return contatos;
    }

    public void insereContato(Contato contato) {
        ContentValues valores = new ContentValues();
        valores.put("nome", contato.nome);
        valores.put("endereco", contato.endereco);
        valores.put("email", contato.email);
        valores.put("telefone", contato.telefone);
        valores.put("datanascimento", contato.dataNascimento);
        valores.put("foto", contato.foto);
        getWritableDatabase().insert("agenda", null, valores);
    }

    public void atualizaContato(Contato contato) {
        ContentValues valores = new ContentValues();
        valores.put("nome", contato.nome);
        valores.put("endereco", contato.endereco);
        valores.put("email", contato.email);
        valores.put("telefone", contato.telefone);
        valores.put("datanascimento", contato.dataNascimento);
        valores.put("foto", contato.foto);
        getWritableDatabase().update("agenda", valores, "id=" + contato.id, null);
    }

    public void apagaContato(Contato contato) {
        getWritableDatabase().delete("agenda", "id=" + contato.id, null);
    }

    public List<Contato> listaContatos(String filtro) {
        List<Contato> contatos = new ArrayList<Contato>();

        Cursor c = getWritableDatabase().query("agenda",
                new String[] { "id", "nome", "endereco", "email", "telefone","foto","datanascimento" },
                "nome like ?", new String[] { filtro }, null, null, null);

        // se encontrou
        if (c.moveToFirst()) {
            do {
                Contato contato = new Contato();
                contato.id = c.getLong(0);
                contato.nome = c.getString(1);
                contato.endereco = c.getString(2);
                contato.email = c.getString(3);
                contato.telefone = c.getString(4);
                contato.foto = c.getString(5);
                contato.dataNascimento = c.getLong(6);
                contatos.add(contato);
            } while (c.moveToNext());
        }

        c.close();
        return contatos;
    }

    public boolean isContato(String telefone) {
        Cursor rawQuery = getReadableDatabase().rawQuery(
                "SELECT telefone from agenda WHERE telefone = ? ",
                new String[] { telefone });
        int total = rawQuery.getCount();
        rawQuery.close();
        return total > 0;
    }
}
