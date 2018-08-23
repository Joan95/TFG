package com.example.joan.raspberrypiandroidcryptosystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionTAPTest extends AppCompatActivity {

    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    public static Boolean operating = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_tap_test);

        ConnectedThread.contextOptionTAPTest = OptionTAPTest.this;

        final EditText valueTimeMS = findViewById(R.id.value_tap_test_time_ms);
        Button buttonTAPTest = findViewById(R.id.button_begin_tap_test);

        buttonTAPTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("TAPTest","TAPTestStart");

                    if (valueTimeMS.getText().toString().equals("")) {
                        int auxTimeMS = 0;
                        valueTimeMS.setText(String.valueOf(auxTimeMS));
                    }

                    jsonMessage.put("TAPTestSetTime", valueTimeMS.getText().toString());
                    connectedThread.write(jsonMessage);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (operating) {
            myToasts.show(OptionTAPTest.this, "Wait please, it is an operation underway.");
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
