package com.example.joan.raspberrypiandroidcryptosystem;

public class SignatureSystemsSingleton {
    private static SignatureSystems currentSignatureSystems = null;

    public static SignatureSystems getCurrentSignatureSystems() {
        if (currentSignatureSystems == null) {
            currentSignatureSystems = new SignatureSystems();
        }

        return currentSignatureSystems;
    }
}
