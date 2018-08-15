package com.example.joan.raspberrypiandroidcryptosystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ConnectedThread extends Thread{

    private final int handlerState = 0;

    private StringBuilder recDataString = new StringBuilder();

    public static Context contextOptionLED;
    public static Context contextOptionEncryptDecrypt;

    private SystemFile systemFile = SystemFileSingleton.getCurrentSystemFile();
    private InfoFile infoFile = InfoFileSingleton.getInfoFile();

    private final InputStream receivingFromRaspberry;
    private final OutputStream sendingToRaspberry;

    @SuppressLint("HandlerLeak")
    private Handler bluetoothHandler = new Handler()
    {
        @SuppressLint("SetTextI18n")
        public void handleMessage(android.os.Message msg)
        {
            if (msg.what == handlerState)
            {
                String readMessage = (String) msg.obj;
                recDataString.append(readMessage);

                try {
                    JSONObject jsonMessage = new JSONObject(readMessage);

                    Log.d("JSON", jsonMessage.toString());

                    if (jsonMessage.has("System Files")) {

                        JSONObject content = jsonMessage.getJSONObject("System Files");
                        Log.d("Content", content.toString());

                        if (content.has("type")) {
                            String typeFile = content.getString("type");
                            Log.d("type", typeFile);

                            if (content.has("files")) {
                                List<File> files = new ArrayList<>();

                                JSONArray jsonFiles = content.getJSONArray("files");
                                for (int i = 0; i < jsonFiles.length(); i++) {
                                    JSONObject jsonFile = jsonFiles.getJSONObject(i);
                                    File file = new File(jsonFile.getString("name"),jsonFile.getInt("size"));
                                    files.add(file);
                                    Log.d("file", file.toString());
                                }

                                systemFile.putValues(typeFile, new ArrayList<>(files));

                                Log.d("jsonFiles", jsonFiles.toString());
                                Log.d("systemFile", systemFile.toString());
                            }
                        }

                        try {
                            Spinner spinnerMethod = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectMethod);
                            Spinner spinnerTypeFile = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectTypeFile);
                            Spinner spinnerFile = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectFile);

                            spinnerMethod.setSelection(0);
                            spinnerTypeFile.setAdapter(null);
                            spinnerFile.setAdapter(null);

                        } catch (NullPointerException e) {
                            Log.d("ERROR", e.toString());
                        }


                    } else {

                        if (jsonMessage.has("message")) {
                            String messageJson = jsonMessage.getString("message");

                            TextView usageCPUEncrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueCPUEncrypt);
                            TextView usageCPUDecrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueCPUDecrypt);
                            TextView usageMemoryEncrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueMemoryEncrypt);
                            TextView usageMemoryDecrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueMemoryDecrypt);

                            Button sendButton = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.sendButton);
                            Button detailsButton = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.details);

                            if (jsonMessage.has("action")) {
                                String actionJson = jsonMessage.getString("action");

                                if (messageJson.equals("encryption")) {
                                    if (actionJson.equals("start")){
                                        usageCPUEncrypt.setText("Start encryption");
                                        usageMemoryEncrypt.setText("Start encryption");
                                        sendButton.setEnabled(false);
                                    }

                                    if (actionJson.equals("end")) {
                                        usageCPUEncrypt.setText("Encryption has finished");
                                        usageMemoryEncrypt.setText("Waiting for results");
                                    }
                                }

                                if (messageJson.equals("decryption")) {
                                    if (actionJson.equals("start")) {
                                        usageCPUDecrypt.setText("Start decryption");
                                        usageMemoryDecrypt.setText("Start decryption");
                                    }

                                    if (actionJson.equals("end")) {
                                        usageCPUDecrypt.setText("Decryption has finished");
                                        usageMemoryDecrypt.setText("Waiting for results");
                                        sendButton.setEnabled(true);
                                    }
                                }
                            }

                            if (jsonMessage.has("error")) {
                                String errorJson = jsonMessage.getString("error");
                                String bodyJson = jsonMessage.getString("body");

                                if (messageJson.equals("encryption")) {
                                    if (errorJson.equals("error")){
                                        usageCPUEncrypt.setText(bodyJson);
                                        usageMemoryEncrypt.setText(bodyJson);
                                        sendButton.setEnabled(true);
                                    }
                                }

                                if (messageJson.equals("decryption")) {
                                    if (errorJson.equals("error")) {
                                        usageCPUDecrypt.setText(bodyJson);
                                        usageMemoryDecrypt.setText(bodyJson);
                                        sendButton.setEnabled(true);
                                    }
                                }
                            }

                            if (jsonMessage.has("result")) {
                                if (messageJson.equals("encryption")) {
                                    JSONObject jsonObjectE = jsonMessage.getJSONObject("result");

                                    infoFile.setName(jsonObjectE.getString("name"));
                                    infoFile.setSize(Float.parseFloat(jsonObjectE.getString("size")));

                                    infoFile.setEncryptCPUUsage(Float.parseFloat(jsonObjectE.getString("CPUUsage")));
                                    infoFile.setEncryptMEMUsage(Float.parseFloat(jsonObjectE.getString("MEMUsage")));
                                    infoFile.setEncryptRAMUsage(Float.parseFloat(jsonObjectE.getString("RAMUsage")));
                                    infoFile.setEncryptMaxCPU(Float.parseFloat(jsonObjectE.getString("maxCPU")));
                                    infoFile.setEncryptMaxMEM(Float.parseFloat(jsonObjectE.getString("maxMEM")));
                                    infoFile.setEncryptMaxRAM(Float.parseFloat(jsonObjectE.getString("maxRAM")));
                                    infoFile.setEncryptTime(jsonObjectE.getString("timeUsed"));
                                    infoFile.setEncryptTrustlyCPU(jsonObjectE.getInt("cntCPU"));
                                    infoFile.setEncryptTrustlyMEM(jsonObjectE.getInt("cntMEM"));

                                    Log.d("InfoFileEncrypt", infoFile.toString());

                                    usageCPUEncrypt.setText(jsonObjectE.getString("CPUUsage")+'%');
                                    usageMemoryEncrypt.setText(jsonObjectE.getString("MEMUsage")+'%');
                                }

                                if (messageJson.equals("decryption")) {
                                    JSONObject jsonObjectD = jsonMessage.getJSONObject("result");

                                    infoFile.setDecryptCPUUsage(Float.parseFloat(jsonObjectD.getString("CPUUsage")));
                                    infoFile.setDecryptMEMUsage(Float.parseFloat(jsonObjectD.getString("MEMUsage")));
                                    infoFile.setDecryptRAMUsage(Float.parseFloat(jsonObjectD.getString("RAMUsage")));
                                    infoFile.setDecryptMaxCPU(Float.parseFloat(jsonObjectD.getString("maxCPU")));
                                    infoFile.setDecryptMaxMEM(Float.parseFloat(jsonObjectD.getString("maxMEM")));
                                    infoFile.setDecryptMaxRAM(Float.parseFloat(jsonObjectD.getString("maxRAM")));
                                    infoFile.setDecryptTime(jsonObjectD.getString("timeUsed"));
                                    infoFile.setDecryptTrustlyCPU(jsonObjectD.getInt("cntCPU"));
                                    infoFile.setDecryptTrustlyMEM(jsonObjectD.getInt("cntMEM"));

                                    Log.d("InfoFileDecrypt", infoFile.toString());

                                    usageCPUDecrypt.setText(jsonObjectD.getString("CPUUsage")+'%');
                                    usageMemoryDecrypt.setText(jsonObjectD.getString("MEMUsage")+'%');
                                }

                                detailsButton.setEnabled(true);
                            }
                        }
                    }

                    deleteMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        void deleteMessage() {
        recDataString.delete(0, recDataString.length());
    }
    };

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

        boolean threadStopped = false;
        while(true && !threadStopped) {
            try
            {
                bytes = receivingFromRaspberry.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                /*HANDLER*/
                Log.d("Message", "Message received");
                Log.d("Message", readMessage);
                bluetoothHandler.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();

            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(JSONObject message) {
        byte[] msgBuffer = message.toString().getBytes();

        try {
            sendingToRaspberry.write(msgBuffer);
        } catch (IOException ignored) {

        }
    }

    public void closeStreams()
    {
        try
        {
            bluetoothHandler.removeCallbacksAndMessages(null);
            bluetoothHandler = null;
            sendingToRaspberry.close();
            receivingFromRaspberry.close();
        } catch (IOException ignore) {}
    }
}
