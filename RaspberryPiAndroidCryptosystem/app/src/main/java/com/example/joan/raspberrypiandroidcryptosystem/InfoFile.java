package com.example.joan.raspberrypiandroidcryptosystem;

public class InfoFile {
    private static int number = 0;
    private String name = null;
    private float encryptCPUUsage = 0;
    private float encryptMEMUsage = 0;
    private float encryptMaxCPU = 0;
    private float encryptMaxMEM = 0;
    private int encryptTrustlyCPU = 0;
    private int encryptTrustlyMEM = 0;
    private String encryptTime = null;
    private float decryptCPUUsage = 0;
    private float decryptMEMUsage = 0;
    private float decryptMaxCPU = 0;
    private float decryptMaxMEM = 0;
    private int decryptTrustlyCPU = 0;
    private int decryptTrustlyMEM = 0;
    private String decryptTime = null;

    public InfoFile() {
        setName("Log "+(++number));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getEncryptCPUUsage() {
        return encryptCPUUsage;
    }

    public void setEncryptCPUUsage(float encryptCPUUsage) {
        this.encryptCPUUsage = encryptCPUUsage;
    }

    public float getEncryptMEMUsage() {
        return encryptMEMUsage;
    }

    public void setEncryptMEMUsage(float encryptMEMUsage) {
        this.encryptMEMUsage = encryptMEMUsage;
    }

    public float getEncryptMaxCPU() {
        return encryptMaxCPU;
    }

    public void setEncryptMaxCPU(float encryptMaxCPU) {
        this.encryptMaxCPU = encryptMaxCPU;
    }

    public float getEncryptMaxMEM() {
        return encryptMaxMEM;
    }

    public void setEncryptMaxMEM(float encryptMaxMEM) {
        this.encryptMaxMEM = encryptMaxMEM;
    }

    public String getEncryptTime() {
        return encryptTime;
    }

    public void setEncryptTime(String encryptTime) {
        this.encryptTime = encryptTime;
    }

    public float getDecryptCPUUsage() {
        return decryptCPUUsage;
    }

    public void setDecryptCPUUsage(float decryptCPUUsage) {
        this.decryptCPUUsage = decryptCPUUsage;
    }

    public float getDecryptMEMUsage() {
        return decryptMEMUsage;
    }

    public void setDecryptMEMUsage(float decryptMEMUsage) {
        this.decryptMEMUsage = decryptMEMUsage;
    }

    public float getDecryptMaxCPU() {
        return decryptMaxCPU;
    }

    public void setDecryptMaxCPU(float decryptMaxCPU) {
        this.decryptMaxCPU = decryptMaxCPU;
    }

    public float getDecryptMaxMEM() {
        return decryptMaxMEM;
    }

    public void setDecryptMaxMEM(float decryptMaxMEM) {
        this.decryptMaxMEM = decryptMaxMEM;
    }

    public String getDecryptTime() {
        return decryptTime;
    }

    public void setDecryptTime(String decryptTime) {
        this.decryptTime = decryptTime;
    }

    public static int getNumber() {
        return number;
    }

    public static void setNumber(int number) {
        InfoFile.number = number;
    }

    public int getEncryptTrustlyCPU() {
        return encryptTrustlyCPU;
    }

    public void setEncryptTrustlyCPU(int encryptTrustlyCPU) {
        this.encryptTrustlyCPU = encryptTrustlyCPU;
    }

    public int getEncryptTrustlyMEM() {
        return encryptTrustlyMEM;
    }

    public void setEncryptTrustlyMEM(int encryptTrustlyMEM) {
        this.encryptTrustlyMEM = encryptTrustlyMEM;
    }

    public int getDecryptTrustlyCPU() {
        return decryptTrustlyCPU;
    }

    public void setDecryptTrustlyCPU(int decryptTrustlyCPU) {
        this.decryptTrustlyCPU = decryptTrustlyCPU;
    }

    public int getDecryptTrustlyMEM() {
        return decryptTrustlyMEM;
    }

    public void setDecryptTrustlyMEM(int decryptTrustlyMEM) {
        this.decryptTrustlyMEM = decryptTrustlyMEM;
    }
}
