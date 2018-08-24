package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionSignaturesCheck extends AppCompatActivity {

    private SignatureSystems signatureSystems = SignatureSystemsSingleton.getCurrentSignatureSystems();
    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectedThread.contextOptionSignaturesCheck = OptionSignaturesCheck.this;
        setContentView(R.layout.option_signatures_check);

        final Spinner spinnerChoosenSignature = findViewById(R.id.spinner_select_signature);
        final TextInputLayout contentFileSignature = findViewById(R.id.value_signature_message);
        final EditText statusSignature = findViewById(R.id.value_signature_status);
        Button refreshButton = findViewById(R.id.button_check_signature_refresh);
        Button checkButton = findViewById(R.id.button_check_signature);

        spinnerChoosenSignature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                title = parent.getItemAtPosition(position).toString();

                String content = signatureSystems.getValueFromKey(title);
                contentFileSignature.getEditText().setText(content);

                statusSignature.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Send refresh message to Server to upload the files. */
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("signatures","refresh");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);

                statusSignature.setText("");
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("signatures","check");
                    jsonMessage.put("title", title);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);
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
