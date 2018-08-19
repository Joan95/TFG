package com.example.joan.raspberrypiandroidcryptosystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;


public class ShowDevices extends AppCompatActivity
{
    private AlertDialog.Builder alertDialog;
    private ArrayList<String> newDevicesList = new ArrayList<>();
    private ArrayList<List<String>> pairedDevicesList = new ArrayList<>();
    private ArrayAdapter newDevicesListAdapter;

    public static String BLUETOOTH_MAC_DEVICE = "MAC";

    /*Static calls to get all Singletons. */
    private ShowToasts myToasts = ShowToasts.getInstance();
    private BluetoothAdapter bluetoothAdapter = AdapterSingleton.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_devices);

        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonReScann = findViewById(R.id.buttonReScan);
        ListView showPairedDevicesList = findViewById(R.id.pairedDevicesList);
        ListView showNewDevicesList = findViewById(R.id.newDevicesList);

        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onDestroy();
            }
        });

        buttonReScann.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchBluetoothDevices();
            }
        });

        if (bluetoothAdapter == null)
        {
            bluetoothNotSupported();
        }

        if (!bluetoothAdapter.isEnabled())
        {
           bluetoothNotEnabled();
        }

        /*Bluetooth paired devices list.*/
        /*Only if our device admits Bluetooth and it's enabled. */
        for (BluetoothDevice device:bluetoothAdapter.getBondedDevices())
        {
            List<String> aux = new LinkedList<>();
            aux.add(device.getName());
            aux.add(device.getAddress());
            pairedDevicesList.add(aux);

        }

        /*Declaration of Adapters for both lists which will show the results. **/
        newDevicesListAdapter = new ArrayAdapter<>(ShowDevices.this, android.R.layout.activity_list_item, newDevicesList);
        showNewDevicesList.setAdapter(newDevicesListAdapter);

        ArrayAdapter pairedDevicesListAdapter = new ArrayAdapter<>(ShowDevices.this, android.R.layout.simple_list_item_1, pairedDevicesList);
        showPairedDevicesList.setAdapter(pairedDevicesListAdapter);

        /*Declaration of Listeners for both lists which will show the results. */
        showPairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                List<String> selectedInfo = (List<String>)(parent.getItemAtPosition(position));

                /*Only create bounding attempt if name equals 'raspberrypi'.*/
                if (selectedInfo.get(0).equals("raspberrypi"))
                {
                    myToasts.show(ShowDevices.this, selectedInfo.get(0));

                    Intent intent = new Intent(ShowDevices.this, BindingRaspberryPi.class);
                    intent.putExtra(BLUETOOTH_MAC_DEVICE,selectedInfo.get(1));
                    startActivity(intent);
                }
                else
                {
                    myToasts.show(ShowDevices.this,selectedInfo.get(0)+", only interactive with 'raspberrypi'");
                }
            }
        });



        /*TODO: Show all the new devices which are not paired with the mobile.*/
        /*Bluetooth Broadcast Detector**/
        /*Register broadcast receiver with the next serial of filters.*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        /*Register all filter changes on the receiver.*/
        registerReceiver(mobileReceiver, filter);
    }

    protected void bluetoothNotSupported()
    {
        alertDialog = new AlertDialog.Builder(ShowDevices.this);
        alertDialog.setTitle("Bluetooth Error");
        alertDialog.setMessage("Bluetooth is not supported for this Android version.");
        alertDialog.setCancelable(true);

        alertDialog.setPositiveButton(
                "Understood",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        stopAllActivities();
                    }
                }
        );

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    protected void bluetoothNotEnabled()
    {
        alertDialog = new AlertDialog.Builder(ShowDevices.this);
        alertDialog.setTitle("Bluetooth Error");
        alertDialog.setMessage("Bluetooth is not enabled, please enable it and try again.");
        alertDialog.setCancelable(true);

        alertDialog.setPositiveButton(
                "Switch it On & Restart",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        bluetoothAdapter.enable();
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        dialog.cancel();

                        /*Return to the first view, as well as give the opportunity to start again.*/
                        if (i != null)
                        {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        startActivity(i);
                    }
                }
        );

        alertDialog.setNegativeButton(
                "Keep it disabled",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                }
        );

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    /* For cause a cascade effect closing all activities. Including the mainActivity. */
    protected void stopAllActivities()
    {
        ActivityCompat.finishAffinity(this);
    }

    protected void onDestroy()
    {
        /*Unregister all broadcast listeners.*/
        unregisterReceiver(mobileReceiver);
        super.onDestroy();
    }

    /* BLUETOOTH STUFF */
    protected void searchBluetoothDevices()
    {
        /*Start discovery.*/
        bluetoothAdapter.startDiscovery();
        myToasts.show(ShowDevices.this,"Starting discovery...");
    }

    private final BroadcastReceiver mobileReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            /*This will be executed when discovery finds a device. */
            if (action.equals(BluetoothDevice.ACTION_FOUND))
            {
                myToasts.show(ShowDevices.this,"Detected!");
            }

            /*This will be executed when discovery had ended. */
            if (action.equals(ACTION_DISCOVERY_FINISHED))
            {
                myToasts.show(ShowDevices.this,"Discovery has ended");
                newDevicesListAdapter.notifyDataSetChanged();
            }
        }
    };
}
