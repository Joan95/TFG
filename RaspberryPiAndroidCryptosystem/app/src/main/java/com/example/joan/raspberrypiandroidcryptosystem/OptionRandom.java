package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class OptionRandom extends AppCompatActivity {

    private ConnectedThread connectedThread = ConnectedThreadSingleton.getConnectedThread(null);
    private ShowToasts myToasts = ShowToasts.getInstance();

    public static Boolean operating = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_random);

        ConnectedThread.contextOptionRandom = OptionRandom.this;

        final EditText valueSizeRandom = findViewById(R.id.value_size_random_file);
        final EditText mbConverter = findViewById(R.id.mbConverter);
        final EditText valueNameRandom = findViewById(R.id.value_name_random_file);
        final Button buttonGenerateRandom = findViewById(R.id.buttonGenerateRandomFile);

        valueSizeRandom.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (valueSizeRandom.hasFocus()) {
                    if (valueSizeRandom.getText().toString().equals("")) {
                        mbConverter.setText(String.valueOf(0));
                    } else {
                        float aux = 0;
                        String auxS;

                        try {
                            aux = Float.parseFloat(valueSizeRandom.getText().toString());
                            auxS = String.valueOf(aux/1024);
                        } catch (NumberFormatException e) {
                            auxS = valueSizeRandom.getText().toString();
                        }

                        if ((auxS.length() > 6) && (aux > 999999)) {
                            auxS = "Number too long";
                        }

                        mbConverter.setText(auxS);
                    }
                }
            }
        });

        mbConverter.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (mbConverter.hasFocus()) {
                    if (mbConverter.getText().toString().equals("")) {
                        valueSizeRandom.setText(String.valueOf(0));
                    } else {
                        float aux = 0;
                        String auxS;

                        try {
                            aux = Float.parseFloat(mbConverter.getText().toString());
                            auxS = String.valueOf(aux*1024);
                        } catch (NumberFormatException e) {
                            auxS = mbConverter.getText().toString();
                        }

                        if ((auxS.length() > 6) && (aux > 999999)) {
                            auxS = "Number too long";
                        }

                        valueSizeRandom.setText(auxS);
                    }
                }
            }
        });

        buttonGenerateRandom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonMessage = new JSONObject();
                try {
                    String auxName;
                    jsonMessage.put("RANDOM","GENERATE");

                    if (valueSizeRandom.getText().toString().equals("")) {
                        valueSizeRandom.setText("0");
                    }

                    jsonMessage.put("size", Float.parseFloat(valueSizeRandom.getText().toString()));

                    if (valueNameRandom.getText().toString().equals("")) {
                        auxName = valueSizeRandom.getText().toString() + "kb.txt";
                    } else {
                        auxName = valueNameRandom.getText().toString() + ".txt";
                    }

                    jsonMessage.put("name", auxName);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                connectedThread.write(jsonMessage);
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (operating) {
            myToasts.show(OptionRandom.this, "Wait please, it is an operation underway.");
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
