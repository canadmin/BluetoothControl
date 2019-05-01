package com.example.canyard.bluetoothcontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.Image;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Kontrol extends AppCompatActivity {
    private static final int GIRIS_KODU = 1000;
    private String address=null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth=null;
    BluetoothSocket btSocket=null;
    TextView text;
    ImageView imageView;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket  mmServer;
    Button ileri,geri,sag,sol,dur,sesliKomut,deneme;
    private boolean isBtConnected=false;
    static final UUID myUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kontrol);

        Intent newint=getIntent();
        address=newint.getStringExtra(MainActivity.EXTRA_ADRESS);

        ileri=findViewById(R.id.ileri);
        geri=findViewById(R.id.geri);
        sag=findViewById(R.id.sag);
        sol=findViewById(R.id.sol);
        dur=findViewById(R.id.dur);
        sesliKomut=findViewById(R.id.sesliKomut);
        text=findViewById(R.id.textView);
       // ;

        imageView=findViewById(R.id.imageView);




        ileri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setRotation((float)90.0);

                if(btSocket!=null){
                    try{
                        btSocket.getOutputStream().write("f".getBytes());
                    }catch (IOException ex){


                    }
                }

            }
        });


        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setRotation((float)270.0);

                if(btSocket!=null){
                    try {
                        btSocket.getOutputStream().write("b".getBytes());
                    }catch (IOException ex){

                    }
                    }
            }
        });

        sag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setRotation((float)180.0);

                if(btSocket!=null){
                    try {
                        btSocket.getOutputStream().write("r".getBytes());
                    }catch (IOException ex){

                    }
                }
            }
        });

        sol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setRotation((float)0.0);

                if(btSocket!=null){
                    try {
                        btSocket.getOutputStream().write("l".getBytes());
                    }catch (IOException ex){

                    }
                }
            }
        });

        dur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSocket!=null){
                    try {
                        btSocket.getOutputStream().write("s".getBytes());
                    }catch (IOException ex){

                    }
                }
            }
        });

        sesliKomut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                konus();

            }
        });



        new BTbaglan().execute();
    }



    private void konus() {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Merhaba can seni dinliyorum");


        try{
            startActivityForResult(intent,GIRIS_KODU);
        }catch (Exception ex){

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GIRIS_KODU:{
                if (resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text.setText(result.get(0));


                    //    Toast.makeText(this,komut, Toast.LENGTH_SHORT).show();


                }
                break;
            }
        }
        String komut=text.getText().toString();

        if(komut.equals("İleri")){
            imageView.setRotation((float)90.0);

            if (btSocket != null) {
                try {
                    btSocket.getOutputStream().write("f".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(komut.equalsIgnoreCase("geri")){
            imageView.setRotation((float)270.0);

            if (btSocket != null) {
                try {
                    btSocket.getOutputStream().write("b".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(komut.equalsIgnoreCase("Sağ yap")){
            imageView.setRotation((float)180.0);

            if (btSocket != null) {
                try {
                    btSocket.getOutputStream().write("r".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(komut.equalsIgnoreCase("Sol yap")){
            imageView.setRotation((float)0.0);

            if (btSocket != null) {
                try {
                    btSocket.getOutputStream().write("l".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(komut.equalsIgnoreCase("dur")){
            if (btSocket != null) {
                try {
                    btSocket.getOutputStream().write("s".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();

            } catch (IOException e) {

            }

        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Disconnect();
    }


    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Kontrol.this, "Baglanıyor...", "Lütfen Bekleyin");
        }

        // https://gelecegiyazanlar.turkcell.com.tr/konu/android/egitim/android-301/asynctask
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();


                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                // msg("Baglantı Hatası, Lütfen Tekrar Deneyin");
                Toast.makeText(getApplicationContext(), "Bağlantı Hatası Tekrar Deneyin", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //   msg("Baglantı Basarılı");
                Toast.makeText(getApplicationContext(), "Bağlantı Başarılı", Toast.LENGTH_SHORT).show();

                isBtConnected = true;
            }
            progress.dismiss();
        }

    }
}
