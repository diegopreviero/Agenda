package estudae.com.br;

/**
 * Created by cubas on 30/09/17.
 */

import estudae.com.br.bd.BancoDadosHelper;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {


    public SMSReceiver(){

    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];

        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }

        BancoDadosHelper helper = new BancoDadosHelper(context);
        if (helper.isContato(smsMessage[0].getDisplayOriginatingAddress())) {


            MediaPlayer mp = MediaPlayer.create(context, R.raw.gol4);
            mp.start();
        }
        helper.close();
    }



}