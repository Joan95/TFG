package com.example.joan.raspberrypiandroidcryptosystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


public class AccuratedInformation extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accurated_information);

        InfoFile infoFile = InfoFileSingleton.getInfoFile();

        Log.d("InfoFile", infoFile.toString());

        TextView usageCPUEncrypt = findViewById(R.id.valueCPUEncrypt);
        TextView usageMEMEncrypt = findViewById(R.id.valueMemoryEncrypt);
        TextView usageRAMEncrypt = findViewById(R.id.valueMemoryEncryptRAM);
        TextView usageCPUDecrypt = findViewById(R.id.valueCPUDecrypt);
        TextView usageMEMDecrypt = findViewById(R.id.valueMemoryDecrypt);
        TextView usageRAMDecrypt = findViewById(R.id.valueMemoryDecryptRAM);
        TextView maxCPUEncrypt = findViewById(R.id.valueMaxCPUEncrypt);
        TextView maxMEMEncrypt = findViewById(R.id.valueMaxMEMEncrypt);
        TextView maxCPUDecrypt = findViewById(R.id.valueMaxCPUDecrypt);
        TextView maxMEMDecrypt = findViewById(R.id.valueMaxMEMDecrypt);
        TextView timeEncrypt = findViewById(R.id.valueTimeEncrypt);
        TextView timeDecrypt = findViewById(R.id.valueTimeDecrypt);
        TextView encryptOPCPU = findViewById(R.id.valueOPCPUEncrypt);
        TextView decryptOPCPU = findViewById(R.id.valueOPCPUDecrypt);
        TextView encryptOPMEM = findViewById(R.id.valueOPMEMEncrypt);
        TextView decryptOPMEM = findViewById(R.id.valueOPMEMDecrypt);

        usageCPUEncrypt.setText(String.valueOf(infoFile.getEncryptCPUUsage()) + "%");
        usageMEMEncrypt.setText("(" + String.valueOf(infoFile.getEncryptMEMUsage()) + "%)");
        usageRAMEncrypt.setText(String.valueOf(infoFile.getEncryptRAMUsage())+"KB");
        usageCPUDecrypt.setText(String.valueOf(infoFile.getDecryptCPUUsage()) + "%");
        usageMEMDecrypt.setText("(" + String.valueOf(infoFile.getDecryptMEMUsage()) + "%)");
        usageRAMDecrypt.setText(String.valueOf(infoFile.getDecryptRAMUsage())+"KB");
        maxCPUEncrypt.setText(String.valueOf(infoFile.getEncryptMaxCPU()) + "%");
        maxMEMEncrypt.setText(String.valueOf(infoFile.getEncryptMaxMEM()) + "%");
        maxCPUDecrypt.setText(String.valueOf(infoFile.getDecryptMaxCPU()) + "%");
        maxMEMDecrypt.setText(String.valueOf(infoFile.getDecryptMaxMEM()) + "%");
        timeEncrypt.setText(String.valueOf(infoFile.getEncryptTime()) + " sec");
        timeDecrypt.setText(String.valueOf(infoFile.getDecryptTime()) + " sec");
        encryptOPCPU.setText(String.valueOf(infoFile.getEncryptTrustlyCPU()));
        decryptOPCPU.setText(String.valueOf(infoFile.getDecryptTrustlyCPU()));
        encryptOPMEM.setText(String.valueOf(infoFile.getEncryptTrustlyMEM()));
        decryptOPMEM.setText(String.valueOf(infoFile.getDecryptTrustlyMEM()));




    }
}
