package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionLed extends AppCompatActivity {

    /*Static calls to get all Singletons. */
    private ShowToasts myToasts = ShowToasts.getInstance();
    private ConnectedThread connectedThread = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_led);

        ConnectedThread.contextOptionLED = OptionLed.this;

        connectedThread = ConnectedThreadSingleton.getConnectedThread(null);

        Button buttonLedOn = findViewById(R.id.button_led_on);
        Button buttonLedOff = findViewById(R.id.button_led_off);
        Button buttonTrigger = findViewById(R.id.button_led_trigger);

        final EditText triggerMsOn = findViewById(R.id.value_ms_on);
        final EditText triggerMsOff = findViewById(R.id.value_ms_off);
        final EditText triggerNumFlashes = findViewById(R.id.value_num_flashes);

        buttonLedOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("LED","ON");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);
            }
        });

        buttonLedOff.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("LED","OFF");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);
            }
        });

        buttonTrigger.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("LED","TRIGGER");
                    jsonMessage.put("msOn", triggerMsOn.getText());
                    jsonMessage.put("msOff", triggerMsOff.getText());
                    jsonMessage.put("numFlashes", triggerNumFlashes.getText());
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
