package com.example.canyard.bluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    BluetoothAdapter myBluetooth;
    private Button toggle_buton,pair_button;
    private Set<BluetoothDevice> pairedDevices;
    ListView pairedList;
    public static String EXTRA_ADRESS="device_address";
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myBluetooth=BluetoothAdapter.getDefaultAdapter();
        toggle_buton=findViewById(R.id.button_toggle);
        pair_button=findViewById(R.id.button_pair);
        pairedList=findViewById(R.id.device_list);


        toggle_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBluetooth();
            }
        });

        pair_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDevice();
            }
        });
    }

    private void listDevice() {
    pairedDevices=myBluetooth.getBondedDevices();

        ArrayList list=new ArrayList();
        if(pairedDevices.size()>0){
            for(BluetoothDevice bt:pairedDevices){
                list.add(bt.getName()+"\n"+bt.getAddress());
            }
        }else{
            Toast.makeText(getApplicationContext(), "Eşleşen cihaz bulunamadı", Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        pairedList.setAdapter(adapter);
        pairedList.setOnItemClickListener(selectDevice);
    }

    private void toggleBluetooth() {
        if(myBluetooth==null){
            Toast.makeText(getApplicationContext(), "Bluetooth Cihaz bulunamadi", Toast.LENGTH_SHORT).show();
        }if(!myBluetooth.isEnabled()){
            Intent enableBluetooth=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetooth);
        }
        if(myBluetooth.isEnabled()){
            myBluetooth .disable();
        }
    }

    public AdapterView.OnItemClickListener selectDevice=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info=((TextView) view).getText().toString();
            String address=info.substring(info.length()-17);

            Intent commintent=new Intent(MainActivity.this,Kontrol.class);
            commintent.putExtra(EXTRA_ADRESS,address);
            startActivity(commintent);
        }
    };
}
