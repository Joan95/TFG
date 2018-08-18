package com.example.joan.raspberrypiandroidcryptosystem;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class BindingRaspberryPi  extends AppCompatActivity {

    /*Static calls to get all Singletons. */
    private ShowToasts myToasts = ShowToasts.getInstance();
    private ConnectedThread connectedThread = null;
    private BluetoothAdapter bluetoothAdapter = AdapterSingleton.getDefaultAdapter();

    private boolean connected = true;

    private BluetoothSocket socket = null;
    private boolean btConnected = false;
    private String raspberryMac = null;

    private ProgressDialog progressDialog = null;
    private String messageProgressDialog = null;
    private String messageProgressTitle = null;

    private Runnable changeProgressDialog = new Runnable()
    {
        @Override
        public void run()
        {
            progressDialog.setTitle(messageProgressTitle);
            progressDialog.setMessage(messageProgressDialog);
        }
    };


    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binding_raspberry_pi);

        ListView listFunctionOption = findViewById(R.id.listFunction);
        Button sendButton = findViewById(R.id.buttonBluetooth);

        Intent lastInt = getIntent();
        raspberryMac = lastInt.getStringExtra(ShowDevices.BLUETOOTH_MAC_DEVICE);

        myToasts.show(BindingRaspberryPi.this, raspberryMac);

        String[] functions = new String[]
                {
                        "Get RTC",
                        "LED Control",
                        "Encrypt/Decrypt Files",
                        "Generate RANDOM",
                        "Signatures",
                        "ECDSA",
                        "I2C Options",
                        "TAP"
                };

        ArrayAdapter optionFunction = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, functions);
        listFunctionOption.setAdapter(optionFunction);

        listFunctionOption.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String selectOptionFunction = (String) parent.getItemAtPosition(position);

                JSONObject jsonMessage = new JSONObject();

                Intent intent;

                switch (selectOptionFunction){
                    case "Get RTC":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function","RTC");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionRTC.class);
                        startActivity(intent);

                        break;
                    case "LED Control":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function","LED");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionLed.class);
                        startActivity(intent);
                        break;
                    case "Encrypt/Decrypt Files":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function","encrypt_decrypt");

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionEncryptDecrypt.class);
                        startActivity(intent);
                        break;
                    case "Generate RANDOM":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function", "random");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionRandom.class);
                        startActivity(intent);
                        break;
                    case "Signatures":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function", "signatures");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionSignatures.class);
                        startActivity(intent);
                        break;
                    case "ECDSA":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function", "ecdsa");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionECDSA.class);
                        startActivity(intent);
                        break;
                    case "I2C Options":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function", "i2c");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionI2C.class);
                        startActivity(intent);
                        break;
                    case "TAP":
                        myToasts.show(BindingRaspberryPi.this, "Option: "+position);
                        try {
                            jsonMessage.put("Function",selectOptionFunction);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        connectedThread.write(jsonMessage);

                        intent = new Intent(BindingRaspberryPi.this, OptionTAP.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("message","I'm ALIVE!");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);
            }
        });

        new StablishConnection().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class StablishConnection extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            messageProgressDialog = "Establishing connection between devices...";
            messageProgressTitle = "Connection status";
            progressDialog = ProgressDialog.show(BindingRaspberryPi.this, messageProgressTitle, messageProgressDialog);
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            /* If there is not an established connection yet or
            if there is any device connected, the connection with MAC device
            is established. */

            if (socket == null || !btConnected)
            {
                BluetoothDevice raspberryDevice = bluetoothAdapter.getRemoteDevice(raspberryMac);
                UUID SERIAL_UUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
                //UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                socket = null;

                try {
                    socket = raspberryDevice.createRfcommSocketToServiceRecord(SERIAL_UUID);
                } catch (Exception e) {
                    messageProgressDialog = e.toString();
                    runOnUiThread(changeProgressDialog);
                }

                messageProgressDialog = "Socket created successfully";
                runOnUiThread(changeProgressDialog);

                try {
                    socket.connect();
                    connected = true;
                } catch (IOException e) {
                    messageProgressDialog = e.toString();
                    runOnUiThread(changeProgressDialog);

                    try {
                        socket = (BluetoothSocket) raspberryDevice.getClass().getMethod("createRfcommSocket", UUID.class).invoke(raspberryDevice,1);
                        socket.connect();
                    } catch (Exception e2) {
                        connected = false;
                        messageProgressDialog = e.toString();
                        messageProgressTitle = "ERROR";
                        runOnUiThread(changeProgressDialog);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (connected) {
                btConnected = true;
                messageProgressTitle = "Connection ESTABLISHED";
                messageProgressDialog = "Starting data communication between " + raspberryMac;
                runOnUiThread(changeProgressDialog);
                progressDialog.dismiss();
                myToasts.show(BindingRaspberryPi.this, "Connection\nESTABLISHED!");

                /*Let's start the communication between devices.*/
                if (connectedThread == null) {
                    connectedThread = ConnectedThreadSingleton.getConnectedThread(socket);
                    connectedThread.start();
                } else {
                    myToasts.show(BindingRaspberryPi.this, connectedThread.toString());
                }
            }
            else
            {
                messageProgressTitle = "Connection status FAILED";
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(changeProgressDialog);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        ConnectedThreadSingleton.closeStreams();
        super.onBackPressed();
    }
}
