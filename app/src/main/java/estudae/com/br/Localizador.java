package estudae.com.br;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cubas on 07/10/17.
 */

public class Localizador {

    private Geocoder geo;

    public Localizador(Context ctx) {
        this.geo = new Geocoder(ctx, Locale.getDefault());
    }

    public LatLng geoCoordenada(String endereco) {
        try {

            List<Address> listaEnderecos = geo.getFromLocationName(endereco, 5);
            if (!listaEnderecos.isEmpty()) {
                Address address = listaEnderecos.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

    }
}
