package estudae.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplaschActivity extends AppCompatActivity implements Runnable {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splasch);

        Handler h = new Handler();
        h.postDelayed(this, 6000);
    }

    public void run() {
        startActivity(new Intent(this, ListaContatosActivity.class));
        finish();
    }
}