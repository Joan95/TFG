package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionI2C extends AppCompatActivity {
    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_i2c);
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
