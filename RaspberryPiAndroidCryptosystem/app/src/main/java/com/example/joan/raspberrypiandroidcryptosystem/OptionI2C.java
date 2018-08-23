package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionI2C extends AppCompatActivity {
    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_i2c);

        ConnectedThread.contextOptionI2C = OptionI2C.this;

        ListView list30 = findViewById(R.id.list_i2c_0x30);
        ListView list60 = findViewById(R.id.list_i2c_0x60);
        final EditText newI2CValue = findViewById(R.id.value_selected_i2c_address);
        final Button buttonSetNewI2C = findViewById(R.id.button_set_new_i2c);

        list30.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectOptionFunction = (String) parent.getItemAtPosition(position);

                newI2CValue.setText(selectOptionFunction);

                if (!buttonSetNewI2C.isEnabled()) {
                   buttonSetNewI2C.setEnabled(true);
                }
            }
        });

        list60.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectOptionFunction = (String) parent.getItemAtPosition(position);

                newI2CValue.setText(selectOptionFunction);

                if (!buttonSetNewI2C.isEnabled()) {
                    buttonSetNewI2C.setEnabled(true);
                }
            }
        });

        buttonSetNewI2C.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("I2CNewAddress",newI2CValue.getText().toString());
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
