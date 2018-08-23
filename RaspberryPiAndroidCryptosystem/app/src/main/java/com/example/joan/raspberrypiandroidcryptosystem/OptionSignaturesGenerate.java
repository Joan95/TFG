package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionSignaturesGenerate extends AppCompatActivity {

    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    public static Boolean operating = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_signatures_generate);

        ConnectedThread.contextOptionSignaturesGenerate = OptionSignaturesGenerate.this;

        final EditText messageTitle = findViewById(R.id.value_signature_set_title);
        final TextInputLayout valueMessage = findViewById(R.id.value_signature_message);
        Button buttonSignMessage = findViewById(R.id.button_sign_message);

        buttonSignMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String aux;
                try {
                    aux = valueMessage.getEditText().getText().toString();
                } catch (NullPointerException e) {
                    aux = "";
                }

                if (messageTitle.getText().toString().equals("") || aux.equals("")) {
                    myToasts.show(OptionSignaturesGenerate.this, "Sorry. There are empty fields.");
                } else {
                    JSONObject jsonMessage = new JSONObject();
                    try {
                        jsonMessage.put("SignaturesGenerate", "Generate");
                        jsonMessage.put("Title",messageTitle.getText().toString());
                        jsonMessage.put("Message", valueMessage.getEditText().getText().toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    connectedThread.write(jsonMessage);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (operating) {
            myToasts.show(OptionSignaturesGenerate.this, "Wait please, there is an operation underway.");
        } else {
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

}
