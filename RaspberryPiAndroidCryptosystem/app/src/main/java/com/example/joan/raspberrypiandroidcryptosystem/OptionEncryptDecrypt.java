package com.example.joan.raspberrypiandroidcryptosystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class OptionEncryptDecrypt extends AppCompatActivity {

    private MessageEncDec messageEncDec = new MessageEncDec();

    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    private SystemFile systemFile = SystemFileSingleton.getCurrentSystemFile();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_encrypt_decrypt);

        ConnectedThread.contextOptionEncryptDecrypt = OptionEncryptDecrypt.this;

        connectedThread = ConnectedThreadSingleton.getConnectedThread(null);

        final Button sendButton = findViewById(R.id.sendButton);
        final Button refreshButton = findViewById(R.id.refreshButton);
        final Button detailsButton = findViewById(R.id.details);
        final Spinner spinnerMethod = findViewById(R.id.spinnerSelectMethod);
        final Spinner spinnerTypeFile = findViewById(R.id.spinnerSelectTypeFile);
        final Spinner spinnerFile = findViewById(R.id.spinnerSelectFile);
        final TextView usageCPUEncrypt = findViewById(R.id.valueCPUEncrypt);
        final TextView usageCPUDecrypt = findViewById(R.id.valueCPUDecrypt);
        final TextView usageMemoryEncrypt = findViewById(R.id.valueMemoryEncrypt);
        final TextView usageMemoryDecrypt = findViewById(R.id.valueMemoryDecrypt);
        final EditText passwordValue = findViewById(R.id.passwordValue);
        final Switch switchHSM = findViewById(R.id.saltSwitch);
        switchHSM.setChecked(false);
        messageEncDec.setHSM(false);

        detailsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionEncryptDecrypt.this, AccuratedInformation.class);
                startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Send refresh message to Server to upload the files. */
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("message","refresh");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (messageEncDec.setPassword(passwordValue.getText().toString())) {
                    usageCPUEncrypt.setText("New use");
                    usageCPUDecrypt.setText("New use");
                    usageMemoryEncrypt.setText("New use");
                    usageMemoryDecrypt.setText("New use");

                    myToasts.show(OptionEncryptDecrypt.this, "Using a default password.");
                }
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("cipher", messageEncDec.getCipher());
                    jsonMessage.put("typeFile", messageEncDec.getTypeFile());
                    jsonMessage.put("nameFile", messageEncDec.getNameFile());
                    jsonMessage.put("password", messageEncDec.getPassword());
                    jsonMessage.put("hsm", messageEncDec.getHSM());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                connectedThread.write(jsonMessage);
            }
        });

        spinnerMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    onNothingSelected(parent);
                } else {
                    messageEncDec.setCipher(parent.getItemAtPosition(position).toString());

                    ArrayAdapter<String> auxAdapter;

                    auxAdapter = new ArrayAdapter<>(OptionEncryptDecrypt.this, android.R.layout.simple_spinner_item, systemFile.getListKeys());
                    auxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTypeFile.setAdapter(auxAdapter);
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

                String value = parent.getItemAtPosition(position).toString();
                messageEncDec.setTypeFile(value);

                ArrayAdapter<String> auxAdapter;

                auxAdapter = new ArrayAdapter<>(OptionEncryptDecrypt.this, android.R.layout.simple_spinner_item, systemFile.getListValues(value));
                auxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFile.setAdapter(auxAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sendButton.setEnabled(false);
            }
        });

        spinnerFile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageEncDec.setNameFile(parent.getItemAtPosition(position).toString());
                sendButton.setEnabled(true);
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
                    myToasts.show(OptionEncryptDecrypt.this, "Hardware Security Module enabled");
                    messageEncDec.setHSM(true);
                } else {
                    myToasts.show(OptionEncryptDecrypt.this, "Hardware Security Module disabled");
                    messageEncDec.setHSM(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("message","endFunction");
            Log.d("message","endFunction");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        connectedThread.write(jsonMessage);
        super.onBackPressed();
        finish();
    }
}