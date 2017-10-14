package estudae.com.br;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import estudae.com.br.bd.BancoDadosHelper;
import estudae.com.br.bd.Contato;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /** NOVO CODIGO **/
        BancoDadosHelper bd = new BancoDadosHelper(this);
        List<Contato> contatos = bd.listaContatos();
        bd.close();

        Localizador localizador = null;
        LatLng coordenada = null;
        for (Contato contato : contatos) {

            localizador = new Localizador(this);
            coordenada = localizador.geoCoordenada(contato.endereco);

            if(coordenada != null){

                MarkerOptions marcador = new MarkerOptions().position(coordenada)
                        .title(contato.nome).snippet(contato.endereco);

                mMap.addMarker(marcador);
            }
        }

        // LatLng sydney = new LatLng(-34, 151);

        //Localizador coderUtil = new Localizador(this);
        //LatLng local = coderUtil.geoCoordenada("Rua Irmã Arminda, 700 - Jardim Brasil, Bauru, São Paulo");
        //mMap.addMarker(new MarkerOptions().position(local).title("Usc, Bauru"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(local));

    }

}
