package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

        ConnectedThread.contextTAPTest = OptionTAPTest.this;
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
