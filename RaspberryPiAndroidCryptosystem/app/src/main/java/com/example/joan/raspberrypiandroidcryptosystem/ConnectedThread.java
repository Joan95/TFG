package com.example.joan.raspberrypiandroidcryptosystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConnectedThread extends Thread{

    private final int handlerState = 0;

    private ShowToasts myToasts = ShowToasts.getInstance();

    private StringBuilder recDataString = new StringBuilder();

    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionRTC;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionLED;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionEncryptDecrypt;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionRandom;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionSignaturesGenerate;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionSignaturesCorrupt;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionSignaturesCheck;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionI2C;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionTAP;
    @SuppressLint("StaticFieldLeak")
    public static Context contextOptionTAPTest;

    private SystemFile systemFile = SystemFileSingleton.getCurrentSystemFile();
    private SignatureSystems signatureSystems = SignatureSystemsSingleton.getCurrentSignatureSystems();
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

                    if (jsonMessage.has("RTC")) {
                        JSONObject rtcContent = jsonMessage.getJSONObject("RTC");

                        Log.d("RTC operation", jsonMessage.toString());

                        Button buttonRTCRefreshRTC = ((Activity)contextOptionRTC).findViewById(R.id.button_refresh_rtc);
                        if (rtcContent.getString("RTCOperation").equals("started")) {
                            buttonRTCRefreshRTC.setEnabled(false);
                        }

                        if (rtcContent.getString("RTCOperation").equals("ended")) {
                            buttonRTCRefreshRTC.setEnabled(true);
                        }

                        if (rtcContent.getString("RTCOperation").equals("results")) {
                            long rtcNotPrecise = rtcContent.getLong("RTCNotPreciseTime") * 1000;
                            long rtcPrecise = rtcContent.getLong("RTCPreciseTime") * 1000;

                            EditText rtcNotPreciseHSMValue = ((Activity)contextOptionRTC).findViewById(R.id.value_hsm_not_precise_rtc);
                            EditText rtcPreciseHSMValue = ((Activity)contextOptionRTC).findViewById(R.id.value_hsm_precise_rtc);

                            Calendar calendar = Calendar.getInstance();

                            calendar.setTimeInMillis(rtcNotPrecise);
                            rtcNotPreciseHSMValue.setText(calendar.getTime().toString());

                            calendar.setTimeInMillis(rtcPrecise);
                            rtcPreciseHSMValue.setText(calendar.getTime().toString());
                        }
                    }

                    if (jsonMessage.has("LED")) {
                        Log.d("LED operation", jsonMessage.toString());
                    }


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
                            Spinner spinnerSelectMethod = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectMethod);
                            Spinner spinnerSelectTypeFile = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectTypeFile);
                            Spinner spinnerSelectFile = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectFile);

                            spinnerSelectMethod.setSelection(0);
                            spinnerSelectTypeFile.setAdapter(null);
                            spinnerSelectFile.setAdapter(null);

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    if (jsonMessage.has("encrypt_decrypt")) {
                        JSONObject encryptDecryptContent = jsonMessage.getJSONObject("encrypt_decrypt");

                        Log.d("encrypt_decrypt", encryptDecryptContent.toString());

                        TextView usageCPUEncrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueCPUEncrypt);
                        TextView usageCPUDecrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueCPUDecrypt);
                        TextView usageMemoryEncrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueMemoryEncrypt);
                        TextView usageMemoryDecrypt = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.valueMemoryDecrypt);

                        Switch hsmSwitch = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.saltSwitch);
                        EditText password = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.passwordValue);

                        Button sendButton = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.sendButton);
                        Button refreshButton = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.refreshButton);
                        Button detailsButton = ((Activity)contextOptionEncryptDecrypt).findViewById(R.id.details);

                        if (encryptDecryptContent.has("refreshOperation")) {
                            String operationStatus = encryptDecryptContent.getString("refreshOperation");

                            if (operationStatus.equals("started")) {
                                OptionEncryptDecrypt.operating = true;
                                password.setEnabled(false);
                                refreshButton.setEnabled(false);
                                sendButton.setEnabled(false);
                                hsmSwitch.setEnabled(false);

                                try {
                                    Spinner spinnerSelectMethod = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectMethod);
                                    Spinner spinnerSelectTypeFile = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectTypeFile);
                                    Spinner spinnerSelectFile = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectFile);

                                    spinnerSelectMethod.setEnabled(false);
                                    spinnerSelectTypeFile.setEnabled(false);
                                    spinnerSelectFile.setEnabled(false);

                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (operationStatus.equals("ended")) {
                                OptionEncryptDecrypt.operating = false;
                                password.setEnabled(true);
                                refreshButton.setEnabled(true);
                                sendButton.setEnabled(true);
                                hsmSwitch.setEnabled(true);

                                try {
                                    Spinner spinnerSelectMethod = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectMethod);
                                    Spinner spinnerSelectTypeFile = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectTypeFile);
                                    Spinner spinnerSelectFile = ((Activity) contextOptionEncryptDecrypt).findViewById(R.id.spinnerSelectFile);

                                    spinnerSelectMethod.setEnabled(true);
                                    spinnerSelectTypeFile.setEnabled(true);
                                    spinnerSelectFile.setEnabled(true);

                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (encryptDecryptContent.has("operationEncryption")) {
                            String actionJson = encryptDecryptContent.getString("operationEncryption");

                            if (actionJson.equals("started")) {
                                OptionEncryptDecrypt.operating = true;
                                usageCPUEncrypt.setText("Start encryption");
                                usageMemoryEncrypt.setText("Start encryption");

                                password.setEnabled(false);
                                refreshButton.setEnabled(false);
                                sendButton.setEnabled(false);
                                hsmSwitch.setEnabled(false);
                                detailsButton.setEnabled(false);
                            }

                            if (actionJson.equals("ended")) {
                                usageCPUEncrypt.setText("Encryption has finished");
                                usageMemoryEncrypt.setText("Waiting for results");
                            }
                        }

                        if (encryptDecryptContent.has("operationDecryption")) {
                            String actionJson = encryptDecryptContent.getString("operationDecryption");

                            if (actionJson.equals("started")) {
                                usageCPUDecrypt.setText("Start decryption");
                                usageMemoryDecrypt.setText("Start decryption");
                            }

                            if (actionJson.equals("ended")) {
                                OptionEncryptDecrypt.operating = false;
                                usageCPUDecrypt.setText("Decryption has finished");
                                usageMemoryDecrypt.setText("Waiting for results");
                                password.setEnabled(true);
                                refreshButton.setEnabled(true);
                                sendButton.setEnabled(true);
                                hsmSwitch.setEnabled(true);
                                detailsButton.setEnabled(true);
                            }
                        }


                        /*if (encryptDecryptContent.has("error")) {
                            String errorJson = encryptDecryptContent.getString("error");
                            String bodyJson = encryptDecryptContent.getString("body");

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

                        */
                        if (encryptDecryptContent.has("encryptionResults")) {
                            String typeResult = encryptDecryptContent.getString("encryptionResults");
                            JSONObject results = encryptDecryptContent.getJSONObject("values");

                            Log.d("RESULTS", results.toString());

                            if (typeResult.equals("header")) {
                                infoFile.setName(results.getString("name"));
                                infoFile.setSize(Float.parseFloat(results.getString("size")));
                            }

                            if (typeResult.equals("CPU")) {
                                try {
                                    infoFile.setEncryptCPUUsage(Float.parseFloat(results.getString("CPUUsage")));
                                    infoFile.setEncryptMaxCPU(Float.parseFloat(results.getString("maxCPU")));
                                    infoFile.setEncryptTrustlyCPU(results.getInt("cntCPU"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                usageCPUEncrypt.setText(results.getString("CPUUsage")+'%');
                            }

                            if (typeResult.equals("MEM")) {
                                try {
                                    infoFile.setEncryptMEMUsage(Float.parseFloat(results.getString("MEMUsage")));
                                    infoFile.setEncryptMaxMEM(Float.parseFloat(results.getString("maxMEM")));
                                    infoFile.setEncryptTrustlyMEM(results.getInt("cntMEM"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                usageMemoryEncrypt.setText(results.getString("MEMUsage")+'%');
                            }

                            if (typeResult.equals("RAM")) {
                                try {
                                    infoFile.setEncryptRAMUsage(Float.parseFloat(results.getString("RAMUsage")));
                                    infoFile.setEncryptMaxRAM(Float.parseFloat(results.getString("maxRAM")));
                                    infoFile.setEncryptTime(results.getString("timeUsed"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.d("InfoFileEncrypt", infoFile.toString());
                        }

                        if (encryptDecryptContent.has("decryptionResults")) {
                            String typeResult = encryptDecryptContent.getString("decryptionResults");
                            JSONObject results = encryptDecryptContent.getJSONObject("values");

                            Log.d("RESULTS", results.toString());

                            if (typeResult.equals("CPU")) {
                                try {
                                    infoFile.setDecryptCPUUsage(Float.parseFloat(results.getString("CPUUsage")));
                                    infoFile.setDecryptMaxCPU(Float.parseFloat(results.getString("maxCPU")));
                                    infoFile.setDecryptTrustlyCPU(results.getInt("cntCPU"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                usageCPUDecrypt.setText(results.getString("CPUUsage")+'%');
                            }

                            if (typeResult.equals("MEM")) {
                                try {
                                    infoFile.setDecryptMEMUsage(Float.parseFloat(results.getString("MEMUsage")));
                                    infoFile.setDecryptMaxMEM(Float.parseFloat(results.getString("maxMEM")));
                                    infoFile.setDecryptTrustlyMEM(results.getInt("cntMEM"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                usageMemoryDecrypt.setText(results.getString("MEMUsage")+'%');
                            }

                            if (typeResult.equals("RAM")) {
                                try {
                                    infoFile.setDecryptRAMUsage(Float.parseFloat(results.getString("RAMUsage")));
                                    infoFile.setDecryptMaxRAM(Float.parseFloat(results.getString("maxRAM")));
                                    infoFile.setDecryptTime(results.getString("timeUsed"));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.d("InfoFileDecrypt", infoFile.toString());

                            detailsButton.setEnabled(true);
                        }
                    }

                    if (jsonMessage.has("RANDOM")) {
                        JSONObject randomContent = jsonMessage.getJSONObject("RANDOM");

                        Log.d("RANDOM message", randomContent.toString());

                        String randomOperation = randomContent.getString("RANDOMOperation");

                        EditText bytes = ((Activity)contextOptionRandom).findViewById(R.id.bytesConverter);
                        EditText kiloBytes = ((Activity)contextOptionRandom).findViewById(R.id.value_size_random_file);
                        EditText megaBytes = ((Activity)contextOptionRandom).findViewById(R.id.mbConverter);
                        EditText nameFile = ((Activity)contextOptionRandom).findViewById(R.id.value_name_random_file);
                        Button generateRandom = ((Activity)contextOptionRandom).findViewById(R.id.buttonGenerateRandomFile);

                        if (randomOperation.equals("started")) {
                            OptionRandom.operating = true;
                            bytes.setEnabled(false);
                            kiloBytes.setEnabled(false);
                            megaBytes.setEnabled(false);
                            nameFile.setEnabled(false);
                            myToasts.show(contextOptionRandom,"Please wait.");
                            generateRandom.setEnabled(false);
                        }

                        if (randomOperation.equals("ended")) {
                            OptionRandom.operating = false;
                            bytes.setEnabled(true);
                            kiloBytes.setText("");
                            kiloBytes.setEnabled(true);
                            megaBytes.setEnabled(true);
                            nameFile.setText("");
                            nameFile.setEnabled(true);
                            myToasts.show(contextOptionRandom,"All done!");
                            generateRandom.setEnabled(true);
                        }

                    }

                    if (jsonMessage.has("signatures")) {
                        JSONObject signaturesContent = jsonMessage.getJSONObject("signatures");

                        Log.d("Signatures message", signaturesContent.toString());

                        if (signaturesContent.has("operationSignaturesGenerateStatus")) {
                            String statusOperation = signaturesContent.getString("operationSignaturesGenerateStatus");

                            Button buttonSignMessage = ((Activity)contextOptionSignaturesGenerate).findViewById(R.id.button_sign_message);
                            EditText messageTitle = ((Activity)contextOptionSignaturesGenerate).findViewById(R.id.value_signature_set_title);
                            TextInputLayout messageContent = ((Activity)contextOptionSignaturesGenerate).findViewById(R.id.value_signature_message);

                            if (statusOperation.equals("started")) {
                                buttonSignMessage .setEnabled(false);
                                messageTitle.setEnabled(false);
                                messageContent.setEnabled(false);
                            }

                            if (statusOperation.equals("ended")) {
                                myToasts.show(contextOptionSignaturesGenerate, "Operation has ended");
                                buttonSignMessage .setEnabled(true);
                                messageTitle.setText("");
                                messageTitle.setEnabled(true);
                                messageContent.getEditText().setText("");
                                messageContent.setEnabled(true);
                            }
                        }

                        if (signaturesContent.has("refreshOperationCheck")) {
                            String operationStatus = signaturesContent.getString("refreshOperationCheck");

                            if (operationStatus.equals("started")) {
                                Spinner spinnerSelectSignature = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.spinner_select_signature);
                                Button checkButton = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.button_check_signature);
                                Button refreshButton = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.button_check_signature_refresh);
                                spinnerSelectSignature.setEnabled(false);
                                checkButton.setEnabled(false);
                                refreshButton.setEnabled(false);
                            }

                            if (operationStatus.equals("ended")) {
                                Spinner spinnerSelectSignature = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.spinner_select_signature);
                                Button checkButton = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.button_check_signature);
                                Button refreshButton = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.button_check_signature_refresh);
                                spinnerSelectSignature.setEnabled(true);
                                checkButton.setEnabled(true);
                                refreshButton.setEnabled(true);

                                ArrayAdapter<String> auxAdapter;

                                auxAdapter = new ArrayAdapter<>(contextOptionSignaturesCheck, android.R.layout.simple_spinner_item, signatureSystems.getListKeys());
                                auxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerSelectSignature.setAdapter(auxAdapter);
                            }
                        }

                        if (signaturesContent.has("statusSignature")) {
                            String statusSignature = signaturesContent.getString("statusSignature");

                            EditText valueStatusSignature = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.value_signature_status);
                            valueStatusSignature.setText(statusSignature);
                        }
                    }

                    if (jsonMessage.has("Signatures Files")) {
                        JSONObject signaturesFilesContent = jsonMessage.getJSONObject("Signatures Files");

                        Log.d("SignaturesFiles message", signaturesFilesContent.toString());

                        if (signaturesFilesContent.has("NumberOfFiles")) {
                            int numberOfFiles = signaturesFilesContent.getInt("NumberOfFiles");
                            if (numberOfFiles == 0) {
                                try {
                                    EditText numberOfSignatures = ((Activity) contextOptionSignaturesCheck).findViewById(R.id.value_number_of_singatures);
                                    Spinner spinnerSelectSignature = ((Activity) contextOptionSignaturesCheck).findViewById(R.id.spinner_select_signature);

                                    numberOfSignatures.setText(String.valueOf(0));
                                    spinnerSelectSignature.setEnabled(false);

                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                myToasts.show(contextOptionSignaturesCheck, "Sorry, 0 signatures, should create one first");
                            } else {
                                try {
                                    EditText numberOfSignatures = ((Activity) contextOptionSignaturesCheck).findViewById(R.id.value_number_of_singatures);
                                    Spinner spinnerSelectSignature = ((Activity)contextOptionSignaturesCheck).findViewById(R.id.spinner_select_signature);

                                    numberOfSignatures.setText(String.valueOf(numberOfFiles));
                                    spinnerSelectSignature.setEnabled(true);

                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (signaturesFilesContent.has("OperationStatus")) {
                            if (signaturesFilesContent.getString("OperationStatus").equals("Sending resources")) {
                                String titleSignatureFile = signaturesFilesContent.getString("title");
                                String contentSignatureFile = signaturesFilesContent.getString("content");

                                signatureSystems.putValues(titleSignatureFile, contentSignatureFile);
                            }
                        }
                    }

                    if (jsonMessage.has("I2C")) {
                        JSONObject i2cContent = jsonMessage.getJSONObject("I2C");

                        Log.d("I2C message", i2cContent.toString());
                        String i2cOperation = i2cContent.getString("I2CCurrentAddress");
                        try {
                            EditText currentI2CAddress = ((Activity)contextOptionI2C).findViewById(R.id.value_current_i2c_address);
                            currentI2CAddress.setText(i2cOperation);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    if (jsonMessage.has("TAP")) {
                        JSONObject tapContent = jsonMessage.getJSONObject("TAP");

                        Log.d("TAP message", tapContent.toString());

                        if (tapContent.has("TAPCurrentGlobalSensibility")) {
                            String currentTAPSensibility = tapContent.getString("TAPCurrentGlobalSensibility");
                            try {
                                EditText valueCurrentTAPSensibility = ((Activity)contextOptionTAP).findViewById(R.id.value_current_tap_sensibility);
                                valueCurrentTAPSensibility.setText(currentTAPSensibility);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        if (tapContent.has("TAPCurrentAxisXSensibility")) {
                            String currentAxisX = tapContent.getString("TAPCurrentAxisXSensibility");
                            String currentAxisY = tapContent.getString("TAPCurrentAxisYSensibility");
                            String currentAxisZ = tapContent.getString("TAPCurrentAxisZSensibility");
                            try {
                                EditText valueAxisX = ((Activity)contextOptionTAP).findViewById(R.id.value_current_axis_x);
                                EditText valueAxisY = ((Activity)contextOptionTAP).findViewById(R.id.value_current_axis_y);
                                EditText valueAxisZ = ((Activity)contextOptionTAP).findViewById(R.id.value_current_axis_z);
                                valueAxisX.setText(currentAxisX);
                                valueAxisY.setText(currentAxisY);
                                valueAxisZ.setText(currentAxisZ);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (jsonMessage.has("TAPTest")) {
                        JSONObject tapTestContent = jsonMessage.getJSONObject("TAPTest");

                        Log.d("TAPTest message", tapTestContent.toString());

                        if (tapTestContent.has("TAPTestStatus")) {
                            String operationTAPTest = tapTestContent.getString("TAPTestStatus");

                            if (operationTAPTest.equals("started")) {
                                Button buttonStartTAPTest = ((Activity)contextOptionTAPTest).findViewById(R.id.button_begin_tap_test);
                                buttonStartTAPTest.setEnabled(false);
                            }

                            if (operationTAPTest.equals("ended")) {
                                Button buttonStartTAPTest = ((Activity)contextOptionTAPTest).findViewById(R.id.button_begin_tap_test);
                                buttonStartTAPTest.setEnabled(true);
                            }
                        }

                        if (tapTestContent.has("TAPTestValues")) {
                            String axisTAPValue = tapTestContent.getString("TAPTestValues");
                            String gForceTAPTest = tapTestContent.getString("TAPTestGForce");
                            String tapDirectionTAPTest = tapTestContent.getString("TAPTestTAPDirection");
                            String tapDirectionTAPMeaning = null;

                            if (Integer.parseInt(tapDirectionTAPTest) == -1) {
                                tapDirectionTAPMeaning = "Axis Down";
                            }

                            if (Integer.parseInt(tapDirectionTAPTest) == 0) {
                                tapDirectionTAPMeaning = "No movement";
                            }

                            if (Integer.parseInt(tapDirectionTAPTest) == 1) {
                                tapDirectionTAPMeaning = "Axis UP";
                            }

                            if (axisTAPValue.equals("AxisX")) {
                                EditText gForceAxisXValue = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_x_g_force);
                                EditText tapDirectionValue = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_x_tap_direction);
                                EditText tapDirectionValueMeaning = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_x_tap_direction_meaning);

                                try {
                                    gForceAxisXValue.setText(gForceTAPTest);
                                    tapDirectionValue.setText(tapDirectionTAPTest);
                                    tapDirectionValueMeaning.setText(tapDirectionTAPMeaning);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (axisTAPValue.equals("AxisY")) {
                                EditText gForceAxisYValue = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_y_g_force);
                                EditText tapDirectionValue = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_y_tap_direction);
                                EditText tapDirectionValueMeaning = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_y_tap_direction_meaning);

                                try {
                                    gForceAxisYValue.setText(gForceTAPTest);
                                    tapDirectionValue.setText(tapDirectionTAPTest);
                                    tapDirectionValueMeaning.setText(tapDirectionTAPMeaning);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (axisTAPValue.equals("AxisZ")) {
                                EditText gForceAxisZValue = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_z_g_force);
                                EditText tapDirectionValue = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_z_tap_direction);
                                EditText tapDirectionValueMeaning = ((Activity)contextOptionTAPTest).findViewById(R.id.value_tap_test_axis_z_tap_direction_meaning);

                                try {
                                    gForceAxisZValue.setText(gForceTAPTest);
                                    tapDirectionValue.setText(tapDirectionTAPTest);
                                    tapDirectionValueMeaning.setText(tapDirectionTAPMeaning);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
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
            Log.d("Message Operation", "Deleted");
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
