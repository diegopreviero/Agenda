package estudae.com.br;

/**
 * Created by cubas on 07/10/17.
 */

import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import estudae.com.br.bd.Contato;

public class ContatoUtil {

    public static String acessar(String endereco)
    {
        try {

            URL url = new URL(endereco);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Scanner scanner = new Scanner(is);
            String conteudo = scanner.useDelimiter("\\A").next();
            scanner.close();
            return conteudo;

        } catch (Exception e){

            Log.e("aula", e.getMessage());
            e.printStackTrace();

            return e.getMessage();
        }
    }

    public static List<Contato> contatoConverter(String conteudo) throws JSONException {

        List<Contato> contatos = new ArrayList<Contato>();

        JSONObject jsonObject = new JSONObject(conteudo);
        JSONArray listaContatos = jsonObject.getJSONArray("contatos");

        for (int i = 0; i < listaContatos.length(); i++) {
            JSONObject j = listaContatos.getJSONObject(i);
            Contato c = new Contato();
            c.nome = j.getString("nome");
            c.endereco = j.getString("endereco");
            c.telefone = j.getString("telefone");
            c.dataNascimento = j.getLong("dataNascimento");
            c.email = j.getString("email");
            c.site = j.getString("site");;
            contatos.add(c);
        }
        return contatos;
    }

}