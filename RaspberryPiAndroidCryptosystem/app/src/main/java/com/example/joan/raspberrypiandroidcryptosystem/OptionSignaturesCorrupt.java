package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionSignaturesCorrupt extends AppCompatActivity {

    private SignatureSystems signatureSystems = SignatureSystemsSingleton.getCurrentSignatureSystems();
    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);

    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectedThread.contextOptionSignaturesCorrupt = OptionSignaturesCorrupt.this;

        setContentView(R.layout.option_signatures_corrupt);

        final Spinner spinnerSelectSignature = findViewById(R.id.spinner_select_signature);
        final TextInputLayout contentFileSignature = findViewById(R.id.value_signature_message);
        final TextInputLayout contentFileCorruptedSignature = findViewById(R.id.value_signature_message_corrupted);
        Button corruptButton = findViewById(R.id.button_corrupt_signature);
        Button refreshButton = findViewById(R.id.button_signature_refresh);

        contentFileCorruptedSignature.setEnabled(false);
        contentFileCorruptedSignature.setFocusable(false);
        contentFileSignature.setEnabled(false);
        contentFileSignature.setFocusable(false);

        spinnerSelectSignature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                title = parent.getItemAtPosition(position).toString();

                contentFileSignature.setFocusable(false);

                contentFileCorruptedSignature.setFocusable(true);
                contentFileCorruptedSignature.getEditText().setFocusable(true);

                String content = signatureSystems.getValueFromKey(title);
                contentFileSignature.getEditText().setText(content);
                contentFileCorruptedSignature.setEnabled(true);
                contentFileCorruptedSignature.getEditText().setEnabled(true);
                contentFileCorruptedSignature.getEditText().setText(content);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        corruptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Send refresh message to Server to upload the files. */
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("signatures","corrupt");
                    jsonMessage.put("title", title);
                    jsonMessage.put("newContent", contentFileCorruptedSignature.getEditText().getText());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);
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
