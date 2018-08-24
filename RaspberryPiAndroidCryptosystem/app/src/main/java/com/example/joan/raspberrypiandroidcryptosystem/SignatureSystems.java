package com.example.joan.raspberrypiandroidcryptosystem;

import java.util.LinkedList;
import java.util.List;

public class SignatureSystems {
    private List<SignatureFile> signatureFileList = new LinkedList<>();

    public void addFile(SignatureFile signatureFile) {
        signatureFileList.add(signatureFile);
    }


}
