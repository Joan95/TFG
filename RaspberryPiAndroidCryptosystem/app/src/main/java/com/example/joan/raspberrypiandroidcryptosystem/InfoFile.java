package com.example.joan.raspberrypiandroidcryptosystem;

public class InfoFile {
    private static int number = 0;
    private String name = null;
    private float size = 0;
    private float encryptCPUUsage = 0;
    private float encryptMEMUsage = 0;
    private float encryptRAMUsage = 0;
    private float encryptMaxCPU = 0;
    private float encryptMaxMEM = 0;
    private float encryptMaxRAM = 0;
    private int encryptTrustlyCPU = 0;
    private int encryptTrustlyMEM = 0;
    private String encryptTime = null;
    private float decryptCPUUsage = 0;
    private float decryptMEMUsage = 0;
    private float decryptRAMUsage = 0;
    private float decryptMaxCPU = 0;
    private float decryptMaxMEM = 0;
    private float decryptMaxRAM = 0;
    private int decryptTrustlyCPU = 0;
    private int decryptTrustlyMEM = 0;
    private String decryptTime = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
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

    public float getEncryptRAMUsage() {
        return encryptRAMUsage;
    }

    public void setEncryptRAMUsage(float encryptRAMUsage) {
        this.encryptRAMUsage = encryptRAMUsage;
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

    public float getEncryptMaxRAM() {
        return encryptMaxRAM;
    }

    public void setEncryptMaxRAM(float encryptMaxRAM) {
        this.encryptMaxRAM = encryptMaxRAM;
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

    public float getDecryptRAMUsage() {
        return decryptRAMUsage;
    }

    public void setDecryptRAMUsage(float decryptRAMUsage) {
        this.decryptRAMUsage = decryptRAMUsage;
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

    public float getDecryptMaxRAM() {
        return decryptMaxRAM;
    }

    public void setDecryptMaxRAM(float decryptMaxRAM) {
        this.decryptMaxRAM = decryptMaxRAM;
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

    @Override
    public String toString() {
        return "InfoFile{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", encryptCPUUsage=" + encryptCPUUsage +
                ", encryptMEMUsage=" + encryptMEMUsage +
                ", encryptRAMUsage=" + encryptRAMUsage +
                ", encryptMaxCPU=" + encryptMaxCPU +
                ", encryptMaxMEM=" + encryptMaxMEM +
                ", encryptMaxRAM=" + encryptMaxRAM +
                ", encryptTrustlyCPU=" + encryptTrustlyCPU +
                ", encryptTrustlyMEM=" + encryptTrustlyMEM +
                ", encryptTime='" + encryptTime + '\'' +
                ", decryptCPUUsage=" + decryptCPUUsage +
                ", decryptMEMUsage=" + decryptMEMUsage +
                ", decryptRAMUsage=" + decryptRAMUsage +
                ", decryptMaxCPU=" + decryptMaxCPU +
                ", decryptMaxMEM=" + decryptMaxMEM +
                ", decryptMaxRAM=" + decryptMaxRAM +
                ", decryptTrustlyCPU=" + decryptTrustlyCPU +
                ", decryptTrustlyMEM=" + decryptTrustlyMEM +
                ", decryptTime='" + decryptTime + '\'' +
                '}';
    }
}
