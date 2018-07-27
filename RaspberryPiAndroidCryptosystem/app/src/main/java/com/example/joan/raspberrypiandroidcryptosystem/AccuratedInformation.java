package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AccuratedInformation extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accurated_information);

        InfoFile infoFile = InfoFileSingleton.getInfoFile();

        Log.d("InfoFile", infoFile.toString());

        TextView usageCPUEncrypt = findViewById(R.id.valueCPUEncrypt);
        TextView usageMEMEncrypt = findViewById(R.id.valueMemoryEncrypt);
        TextView usageCPUDecrypt = findViewById(R.id.valueCPUDecrypt);
        TextView usageMEMDectypt = findViewById(R.id.valueMemoryDecrypt);
        TextView maxCPUEncrypt = findViewById(R.id.valueMaxCPUEncrypt);
        TextView maxMEMEncrypt = findViewById(R.id.valueMaxMEMEncrypt);
        TextView maxCPUDecrypt = findViewById(R.id.valueMaxCPUDecrypt);
        TextView maxMEMDecrypt = findViewById(R.id.valueMaxMEMDecrypt);
        TextView timeEncrypt = findViewById(R.id.valueTimeEncrypt);
        TextView timeDecrypt = findViewById(R.id.valueTimeDecrypt);

        usageCPUEncrypt.setText(String.valueOf(infoFile.getEncryptCPUUsage()) + "%");
        usageMEMEncrypt.setText(String.valueOf(infoFile.getEncryptMEMUsage()) + "%");
        usageCPUDecrypt.setText(String.valueOf(infoFile.getDecryptCPUUsage()) + "%");
        usageMEMDectypt.setText(String.valueOf(infoFile.getDecryptMEMUsage()) + "%");
        maxCPUEncrypt.setText(String.valueOf(infoFile.getEncryptMaxCPU()) + "%");
        maxMEMEncrypt.setText(String.valueOf(infoFile.getEncryptMaxMEM()) + "%");
        maxCPUDecrypt.setText(String.valueOf(infoFile.getDecryptMaxCPU()) + "%");
        maxMEMDecrypt.setText(String.valueOf(infoFile.getDecryptMaxMEM()) + "%");
        timeEncrypt.setText(String.valueOf(infoFile.getEncryptTime()));
        timeDecrypt.setText(String.valueOf(infoFile.getDecryptTime()));




    }
}
