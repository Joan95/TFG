package com.example.joan.raspberrypiandroidcryptosystem;

import android.bluetooth.BluetoothSocket;

public class ConnectedThreadSingleton extends Thread {
    private static ConnectedThread currentThread = null;

    public static ConnectedThread getConnectedThread(BluetoothSocket socket) {
        if (currentThread == null) {
            currentThread = new ConnectedThread(socket);
        }
        return currentThread;
    }

    public static void closeStreams() {
        currentThread.closeStreams();
        currentThread = null;
    }
}
