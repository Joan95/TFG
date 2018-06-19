package com.example.joan.raspberrypiandroidcryptosystem;


import android.bluetooth.BluetoothAdapter;

public class AdapterSingleton {
    private static BluetoothAdapter currentAdapter = null;

    public static BluetoothAdapter getDefaultAdapter() {
        if (currentAdapter == null) {
            currentAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return currentAdapter;
    }
}
