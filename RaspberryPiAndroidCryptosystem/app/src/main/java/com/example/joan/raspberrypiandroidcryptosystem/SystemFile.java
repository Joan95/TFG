package com.example.joan.raspberrypiandroidcryptosystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemFile {
    private Map<String, List<File>> systemFile = new HashMap<String, List<File>>();

    public SystemFile() {
    }

    public void putValues(String type, List<File> listFiles) {
        systemFile.put(type, listFiles);
    }

    public String[] getListKeys() {
        List<String> auxListKeys = new ArrayList<>();
        for (String key:systemFile.keySet())
            auxListKeys.add(key);

        return auxListKeys.toArray(new String[0]);
    }

    public String[] getListValues(String typeFile) {
        List<String> auxListValues = new ArrayList<>();
        for (File file:systemFile.get(typeFile))
            auxListValues.add(file.getName());

        return auxListValues.toArray(new String[0]);
    }

    @Override
    public String toString() {
        String aux = "";
        for (String key:systemFile.keySet()) {
            aux = aux + " key: " + key;
            for(File f:systemFile.get(key)) {
                aux = aux + " " + f.toString();
            }
        }
        return aux;
    }
}
