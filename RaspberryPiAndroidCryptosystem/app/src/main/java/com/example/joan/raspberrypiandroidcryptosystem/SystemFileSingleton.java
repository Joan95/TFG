package com.example.joan.raspberrypiandroidcryptosystem;

public class SystemFileSingleton {
    private static SystemFile currentSystemFile = null;

    public static SystemFile getCurrentSystemFile() {
        if (currentSystemFile == null) {
            currentSystemFile = new SystemFile();
        }

        return currentSystemFile;
    }
}
