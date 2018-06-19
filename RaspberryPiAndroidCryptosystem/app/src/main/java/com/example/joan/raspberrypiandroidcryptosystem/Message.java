package com.example.joan.raspberrypiandroidcryptosystem;

public class Message {
    private String cipher;
    private String typeFile;
    private String nameFile;
    private String password;
    private Boolean hsm = true;


    public Message(){
        this.cipher = null;
        this.typeFile = null;
        this.nameFile = null;
        this.password = null;
        this.hsm = true;
    }

    public Message(String cipher, String typeFile, String nameFile, String password, Boolean hsm) {
        this.cipher = cipher;
        this.typeFile = typeFile;
        this.nameFile = nameFile;
        this.password = password;
        this.hsm = hsm;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String method) {
        if (method.equals("Select Cipher"))
        {
            this.cipher = null;
        } else {
            this.cipher = method;
        }
    }

    public String getTypeFile() { return typeFile; }

    public void setTypeFile(String typeFile) {
        this.typeFile = typeFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getPassword() {
        return password;
    }

    public Boolean setPassword(String password) {
        if (password.equals("")) {
            this.password= "Def@ultP@ssw@rd&2018";
            return true;
        } else {
            this.password = password;
            return false;
        }
    }

    public Boolean getHSM() {
        return hsm;
    }

    public void setHSM(Boolean hsm) {
        this.hsm = hsm;
    }

    public String toString() {
        return getCipher() + ";" + getTypeFile() + ";" + getNameFile()+ ";" + getPassword()+ ";" + getHSM();
    }
}
