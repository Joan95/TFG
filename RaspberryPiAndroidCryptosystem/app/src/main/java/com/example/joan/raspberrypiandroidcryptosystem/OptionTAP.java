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

public class OptionTAP extends AppCompatActivity {

    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_tap);

        ConnectedThread.contextTAP = OptionTAP.this;

        final EditText globalTapSensibilityValue = findViewById(R.id.value_selected_tap_sensibility);
        final EditText valueAxisX = findViewById(R.id.value_axis_x);
        final EditText valueAxisY = findViewById(R.id.value_axis_y);
        final EditText valueAxisZ = findViewById(R.id.value_axis_z);
        Button buttonGlobalTapSensibility = findViewById(R.id.button_set_new_tap_sensibility);
        Button buttonAxisTapSensibility = findViewById(R.id.button_set_new_axis_tap_sensibility);
        final Button buttonStartTAPTest = findViewById(R.id.button_start_tap_test);

        buttonGlobalTapSensibility.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    int sensibility;
                    jsonMessage.put("TAP","TAPGlobalSensibility");

                    if (globalTapSensibilityValue.getText().toString().equals("")) {
                        myToasts.show(OptionTAP.this, "Please enter a value");
                    } else {
                        sensibility = Integer.parseInt(globalTapSensibilityValue.getText().toString());
                        if ((sensibility < 0) || (sensibility > 100)) {
                            myToasts.show(OptionTAP.this, "Value not valid!");
                            globalTapSensibilityValue.setText("");
                        } else {
                            valueAxisX.setText(String.valueOf(sensibility));
                            valueAxisY.setText(String.valueOf(sensibility));
                            valueAxisZ.setText(String.valueOf(sensibility));

                            jsonMessage.put("Sensibility", Integer.parseInt(globalTapSensibilityValue.getText().toString()));

                            buttonStartTAPTest.setEnabled(true);

                            connectedThread.write(jsonMessage);
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });

        buttonAxisTapSensibility.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    int sensibility;
                    jsonMessage.put("TAP","TAPAxisSensibility");
                    sensibility = 50;

                    if (valueAxisX.getText().toString().equals("")) {
                        valueAxisX.setText(String.valueOf(sensibility));
                        myToasts.show(OptionTAP.this, "Setting AXIS X DEFAULT value");
                    } else {
                        if (Integer.parseInt(valueAxisX.getText().toString()) > 100) {
                            valueAxisX.setText(String.valueOf(sensibility));
                            myToasts.show(OptionTAP.this, "Incorrect value of AXIS X. Changed");
                        }
                    }

                    if (valueAxisY.getText().toString().equals("")) {
                        valueAxisY.setText(String.valueOf(sensibility));
                        myToasts.show(OptionTAP.this, "Setting AXIS Y DEFAULT value");
                    } else {
                        if (Integer.parseInt(valueAxisY.getText().toString()) > 100) {
                            valueAxisY.setText(String.valueOf(sensibility));
                            myToasts.show(OptionTAP.this, "Incorrect value of AXIS Y. Changed");
                        }
                    }

                    if (valueAxisZ.getText().toString().equals("")) {
                        valueAxisZ.setText(String.valueOf(sensibility));
                        myToasts.show(OptionTAP.this, "Setting AXIS Z DEFAULT value");
                    } else {
                        if (Integer.parseInt(valueAxisZ.getText().toString()) > 100) {
                            valueAxisZ.setText(String.valueOf(sensibility));
                            myToasts.show(OptionTAP.this, "Incorrect value of AXIS Z. Changed");
                        }
                    }

                    jsonMessage.put("AxisX", Integer.parseInt(valueAxisX.getText().toString()));
                    jsonMessage.put("AxisY", Integer.parseInt(valueAxisY.getText().toString()));
                    jsonMessage.put("AxisZ", Integer.parseInt(valueAxisZ.getText().toString()));

                    buttonStartTAPTest.setEnabled(true);

                    connectedThread.write(jsonMessage);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
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
