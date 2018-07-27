package com.example.joan.raspberrypiandroidcryptosystem;

public class InfoFileSingleton {
    private static InfoFile infoFile= null;

    public static InfoFile getInfoFile() {
        if (infoFile == null) {
            infoFile = new InfoFile();
        }
        return infoFile;
    }
}
