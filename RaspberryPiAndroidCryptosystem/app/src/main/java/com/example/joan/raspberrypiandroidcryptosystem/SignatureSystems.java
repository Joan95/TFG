package com.example.joan.raspberrypiandroidcryptosystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignatureSystems {
    private int numOfSignatures;
    private Map<String, String> signatureFileMap = new HashMap<>();

    public void putValues(String title, String content) {
        signatureFileMap.put(title, content);
    }

    public String[] getListKeys() {
        List<String> auxListKeys = new ArrayList<>();
        for (String key:signatureFileMap.keySet()) {
            auxListKeys.add(key);
        }

        return auxListKeys.toArray(new String[0]);
    }

    public String getValueFromKey(String title) {
        return signatureFileMap.get(title);
    }

    public int getNumOfSignatures() {
        return numOfSignatures;
    }

    public void setNumOfSignatures(int numOfSignatures) {
        this.numOfSignatures = numOfSignatures;
    }

    @Override
    public String toString() {
        String aux = "";
        for (String key:signatureFileMap.keySet()) {
            aux = aux + " key: " + key + ", content: " + signatureFileMap.get(key);
        }
        return aux;
    }
}
