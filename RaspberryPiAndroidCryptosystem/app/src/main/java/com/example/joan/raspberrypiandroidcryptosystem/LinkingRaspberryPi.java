package com.example.joan.raspberrypiandroidcryptosystem;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



public class LinkingRaspberryPi extends AppCompatActivity
{
    /*Static calls to get all Singletons. */
    private ShowToasts myToasts = ShowToasts.getInstance();
    private BluetoothAdapter bluetoothAdapter = AdapterSingleton.getDefaultAdapter();

    /* Variables */
    private boolean connected = true;
    private boolean threadStopped = false;

    private BluetoothSocket socket = null;
    private boolean btConnected = false;
    private String raspberryMac = null;

    private ProgressDialog progressDialog = null;
    private String messageProgressDialog = null;
    private String messageProgressTitle = null;

    private Message message = new Message();

    private ConnectedThread connectedThread;
    private final int handlerState = 0;
    private Handler bluetoothHandler;

    private StringBuilder recDataString = new StringBuilder();

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linking_raspberry_pi);
        final Button sendButton = findViewById(R.id.sendButton);
        Button disconnectButton = findViewById(R.id.disconnectButton);
        Spinner spinnerMethod = findViewById(R.id.spinnerSelectMethod);
        Spinner spinnerTypeFile = findViewById(R.id.spinnerSelectTypeFile);
        final Spinner spinnerFile = findViewById(R.id.spinnerSelectFile);
        final TextView usageCPUEncrypt = findViewById(R.id.valueCPUEncrypt);
        final TextView usageCPUDecrypt = findViewById(R.id.valueCPUDecrypt);
        final TextView usageMemoryEncrypt = findViewById(R.id.valueMemoryEncrypt);
        final TextView usageMemoryDecrypt = findViewById(R.id.valueMemoryDecrypt);
        final EditText passwordValue = findViewById(R.id.passwordValue);
        final Switch switchHSM = findViewById(R.id.saltSwitch);

        Intent lastInt = getIntent();
        raspberryMac = lastInt.getStringExtra(ShowDevices.BLUETOOTH_MAC_DEVICE);

        myToasts.show(LinkingRaspberryPi.this, raspberryMac);

        /* Convertion to String[] to List<String>, getting resources from 'strings.xml'*/
        Resources res = getResources();
        final List<String> textList = Arrays.asList(res.getStringArray(R.array.text_array));
        final List<String> videoList = Arrays.asList(res.getStringArray(R.array.video_array));
        final List<String> audioList = Arrays.asList(res.getStringArray(R.array.audio_array));
        final List<String> binList = Arrays.asList(res.getStringArray(R.array.bin_array));


        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                /*Get values, check them and put all of them into the message sending it to the server. */
                if (message.setPassword(passwordValue.getText().toString())) {
                    myToasts.show(LinkingRaspberryPi.this, "Using a default password.");
                }
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("cipher", message.getCipher());
                    jsonMessage.put("typeFile", message.getTypeFile());
                    jsonMessage.put("nameFile", message.getNameFile());
                    jsonMessage.put("password", message.getPassword());
                    jsonMessage.put("hsm", message.getHSM());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                connectedThread.write(jsonMessage);
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onDestroy();
            }
        });

        spinnerMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    onNothingSelected(parent);
                } else {
                    message.setCipher(parent.getItemAtPosition(position).toString());
                    sendButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sendButton.setEnabled(false);
            }
        });

        spinnerTypeFile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> auxAdapter;

                String value = parent.getItemAtPosition(position).toString();
                message.setTypeFile(value);

                if (value.equals("Text")) {
                    auxAdapter = new ArrayAdapter<>(LinkingRaspberryPi.this, android.R.layout.simple_spinner_item, textList);
                    auxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFile.setAdapter(auxAdapter);
                }

                if (value.equals("Audio")) {
                    auxAdapter = new ArrayAdapter<>(LinkingRaspberryPi.this, android.R.layout.simple_spinner_item, audioList);
                    auxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFile.setAdapter(auxAdapter);
                }

                if (value.equals("Video")) {
                    auxAdapter = new ArrayAdapter<>(LinkingRaspberryPi.this, android.R.layout.simple_spinner_item, videoList);
                    auxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFile.setAdapter(auxAdapter);
                }

                if (value.equals("Bin")) {
                    auxAdapter = new ArrayAdapter<String>(LinkingRaspberryPi.this, android.R.layout.simple_spinner_item, binList);
                    auxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFile.setAdapter(auxAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sendButton.setEnabled(false);
            }
        });

        spinnerFile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                message.setNameFile(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sendButton.setEnabled(false);
            }
        });

        switchHSM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myToasts.show(LinkingRaspberryPi.this, "Hardware Security Module enabled");
                    message.setHSM(true);
                } else {
                    myToasts.show(LinkingRaspberryPi.this, "Hardware Security Module disabled");
                    message.setHSM(false);
                }
            }
        });

        bluetoothHandler = new Handler()
        {
            @SuppressLint("SetTextI18n")
            public void handleMessage(android.os.Message msg)
            {
                if (msg.what == handlerState)
                {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);

                    String[] msg_array = readMessage.split(";");

                    if (msg_array[0].equals("200"))
                    {
                        sendButton.setEnabled(false);
                        sendButton.setText("WAIT");
                        //myToasts.show(LinkingRaspberryPi.this, readMessage+" "+recDataString.length());

                        switch (msg_array[1]) {
                            case "0":
                                usageCPUEncrypt.setText("Encrypting...");
                                usageMemoryEncrypt.setText("Encrypting...");
                                break;

                            case "1":
                                usageCPUEncrypt.setText("DONE");
                                usageMemoryEncrypt.setText("DONE");
                                /*Maybe a counter here... 3 seconds*/

                                break;

                            case "2":
                                usageCPUDecrypt.setText("Decrypting...");
                                usageMemoryDecrypt.setText("Decrypting...");
                                break;
                            case "3":
                                if(!sendButton.isEnabled())
                                {
                                    sendButton.setEnabled(true);
                                    sendButton.setText("SEND");
                                }
                                usageCPUDecrypt.setText("DONE");
                                usageMemoryDecrypt.setText("DONE");
                                break;
                        }
                        deleteMessage();
                    }

                    if (msg_array[0].equals("400"))
                    {
                        switch (msg_array[1]) {
                            case "1":
                                myToasts.show(LinkingRaspberryPi.this, "Please choose a correct File.");
                                deleteMessage();
                                break;

                            case "2":
                                myToasts.show(LinkingRaspberryPi.this, "Please choose a correct Method.");
                                deleteMessage();
                                break;

                            case "3":
                                myToasts.show(LinkingRaspberryPi.this, "Please choose a correct Method and File.");
                                deleteMessage();
                                break;
                        }
                    }
                    if (msg_array[0].equals("404"))
                    {
                        //myToasts.show(LinkingRaspberryPi.this, "File " + message.getFile() + " couldn't be found. Please check whether it is in the server.");
                        if (!sendButton.isEnabled())
                        {
                            sendButton.setEnabled(true);
                            sendButton.setText("SEND");
                        }
                        usageCPUEncrypt.setText("File not found.");
                        usageMemoryEncrypt.setText("File not found.");
                        usageCPUDecrypt.setText("File not found.");
                        usageMemoryDecrypt.setText("File not found.");
                        deleteMessage();
                    }

                    if (msg_array[0].equals("417"))
                    {
                        //myToasts.show(LinkingRaspberryPi.this, "Fatal error has occurred inside encrypting file.");
                        if (!sendButton.isEnabled())
                        {
                            sendButton.setEnabled(true);
                            sendButton.setText("SEND");
                        }

                        if (usageCPUEncrypt.getText().equals("Encrypting..."))
                        {
                            usageCPUEncrypt.setText("Fatal error during encryption.");
                            usageMemoryEncrypt.setText("Fatal error during encryption.");
                        }
                        deleteMessage();
                    }
                }
            }
            void deleteMessage() {
                recDataString.delete(0, recDataString.length());
            }
        };

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
            progressDialog = ProgressDialog.show(LinkingRaspberryPi.this, messageProgressTitle, messageProgressDialog);
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
                myToasts.show(LinkingRaspberryPi.this, "Connection\nESTABLISHED!");

                /*Let's start the communication between devices.*/
                connectedThread = new ConnectedThread(socket);
                connectedThread.start();
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
    
    private class ConnectedThread extends Thread 
    {
        private final InputStream receivingFromRaspberry;
        private final OutputStream sendingToRaspberry;
        
        ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch(IOException ignored) {}

            receivingFromRaspberry = tmpIn;
            sendingToRaspberry = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            while(true && !threadStopped) {
                try
                {
                    bytes = receivingFromRaspberry.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    /*HANDLER*/
                    bluetoothHandler.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        void write(JSONObject message) {
            byte[] msgBuffer = message.toString().getBytes();

            try {
                sendingToRaspberry.write(msgBuffer);
                myToasts.show(LinkingRaspberryPi.this, "Send " + msgBuffer.length + " to Rasberry");
            } catch (IOException ignored) {

            }
        }

        void closeStreams()
        {
            try
            {
                sendingToRaspberry.close();
                receivingFromRaspberry.close();
            } catch (IOException ignore) {}
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        bluetoothHandler.removeCallbacksAndMessages(null);
        connected = false;
        threadStopped = true;

        if (connectedThread != null) {
            connectedThread.closeStreams();
        }

        if (socket != null) {
            try {
                socket.close();
            } catch(IOException ignored) {}
        }
    }
}
