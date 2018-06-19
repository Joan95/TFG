package com.example.joan.raspberrypiandroidcryptosystem;

import android.content.Context;
import android.widget.Toast;

public class ShowToasts {
    private static ShowToasts instance = null;
    private Toast currentToast;

    private ShowToasts() {};

    public static ShowToasts getInstance() {
        if (instance == null) {
            instance = new ShowToasts();
        }
        return(instance);
    }

    public void show(Context context, java.lang.String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
