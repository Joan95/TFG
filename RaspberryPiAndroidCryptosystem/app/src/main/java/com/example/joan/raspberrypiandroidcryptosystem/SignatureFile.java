package com.example.joan.raspberrypiandroidcryptosystem;

public class SignatureFile {
    private String title = null;
    private String content = null;

    public SignatureFile(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

