package com.example.joan.raspberrypiandroidcryptosystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionSignatures extends AppCompatActivity {
    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_signatures);

        Button buttonGenerate = findViewById(R.id.button_generate_signature);
        Button buttonCorrupt = findViewById(R.id.button_corrupt_signature);
        Button buttonCheck = findViewById(R.id.button_check_signature);

        buttonGenerate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("Signatures","Generate");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);

                Intent intent = new Intent(OptionSignatures.this, OptionSignaturesGenerate.class);
                startActivity(intent);
            }
        });

        buttonCorrupt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("Signatures","Corrupt");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);

                Intent intent = new Intent(OptionSignatures.this, OptionSignaturesCorrupt.class);
                startActivity(intent);
            }
        });

        buttonCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("Signatures","Check");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);

                Intent intent = new Intent(OptionSignatures.this, OptionSignaturesCheck.class);
                startActivity(intent);
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
