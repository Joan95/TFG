package com.example.joan.raspberrypiandroidcryptosystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class AccuratedInformation extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accurated_information);

        Intent lastIntent = getIntent();



        TextView usageCPUEncrypt = findViewById(R.id.valueCPUEncrypt);
        TextView usageMEMEncrypt = findViewById(R.id.valueMemoryEncrypt);
        TextView usageCPUDecrypt = findViewById(R.id.valueCPUDecrypt);
        TextView usageMEMDectypt = findViewById(R.id.valueMemoryDecrypt);
        TextView maxCPUEncrypt;

    }
}
