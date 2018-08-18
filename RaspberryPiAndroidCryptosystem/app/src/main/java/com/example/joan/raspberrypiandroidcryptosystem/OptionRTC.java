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

import java.util.Calendar;

public class OptionRTC extends AppCompatActivity {

    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    private long time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_rtc);

        ConnectedThread.contextRTC = OptionRTC.this;

        final EditText currentDeviceTime = findViewById(R.id.value_device_time);
        EditText hsmPreciseRTC = findViewById(R.id.value_hsm_precise_rtc);
        EditText hsmNotPreciseRTC = findViewById(R.id.value_hsm_not_precise_rtc);
        Button buttonRefreshRTC = findViewById(R.id.button_refresh_rtc);

        time = System.currentTimeMillis();

        Log.d("Current time device", String.valueOf(time));

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        currentDeviceTime.setText(calendar.getTime().toString());

        buttonRefreshRTC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    time = System.currentTimeMillis();
                    calendar.setTimeInMillis(time);
                    currentDeviceTime.setText(calendar.getTime().toString());

                    jsonMessage.put("RTC","Refresh");

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
