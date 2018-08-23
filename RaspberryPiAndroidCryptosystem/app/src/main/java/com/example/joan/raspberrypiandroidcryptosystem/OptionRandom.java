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
        final EditText byteConverter = findViewById(R.id.bytesConverter);
        final EditText mbConverter = findViewById(R.id.mbConverter);
        final EditText valueNameRandom = findViewById(R.id.value_name_random_file);
        final Button buttonGenerateRandom = findViewById(R.id.buttonGenerateRandomFile);

        byteConverter.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (byteConverter.hasFocus()) {
                    if (byteConverter.getText().toString().equals("")) {
                        valueSizeRandom.setText(String.valueOf(0));
                        mbConverter.setText(String.valueOf(0));
                    } else {
                        float aux;
                        String auxKB;
                        String auxMB;

                        try {
                            aux = Float.parseFloat(byteConverter.getText().toString());
                            auxKB = String.valueOf(aux/1024);
                            auxMB = String.valueOf(aux/(1024*1024));
                            valueSizeRandom.setText(auxKB);
                            mbConverter.setText(auxMB);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            myToasts.show(OptionRandom.this, "Number not valid.");
                        }
                    }
                }
            }
        });

        valueSizeRandom.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (valueSizeRandom.hasFocus()) {
                    if (valueSizeRandom.getText().toString().equals("")) {
                        byteConverter.setText(String.valueOf(0));
                        mbConverter.setText(String.valueOf(0));
                    } else {
                        float aux;
                        String auxB;
                        String auxMB;

                        try {
                            aux = Float.parseFloat(valueSizeRandom.getText().toString());
                            auxB = String.valueOf(aux*1024);
                            auxMB = String.valueOf(aux/1024);

                            byteConverter.setText(auxB);
                            mbConverter.setText(auxMB);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            myToasts.show(OptionRandom.this, "Number not valid.");
                        }
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
                        float aux;
                        String auxB;
                        String auxKB;

                        try {
                            aux = Float.parseFloat(mbConverter.getText().toString());
                            auxB = String.valueOf(aux*1024*1024);
                            auxKB = String.valueOf(aux*1024);

                            byteConverter.setText(auxB);
                            valueSizeRandom.setText(auxKB);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            myToasts.show(OptionRandom.this, "Number not valid.");
                        }
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

                    try {
                        jsonMessage.put("size", Float.parseFloat(valueSizeRandom.getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (valueNameRandom.getText().toString().equals("")) {
                        auxName = valueSizeRandom.getText().toString() + "kb.txt";
                    } else {
                        auxName = valueNameRandom.getText().toString() + ".txt";
                    }

                    jsonMessage.put("name", auxName);


                    connectedThread.write(jsonMessage);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (operating) {
            myToasts.show(OptionRandom.this, "Wait please, there is an operation underway.");
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
