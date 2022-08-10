package com.w.wifiscanner;


public class Networking {
    private final String BSSID;
    private final String ESSID;
    private final int signalisation;
    private final int lock;
    private final String signal;
    private final String information_wifi;

    public Networking(String BSSID, String ESSID, String information_wifi, String signal, int signalisation, int fermer) {
        this.BSSID = BSSID;
        this.ESSID = ESSID;
        this.information_wifi = information_wifi;
        this.signal = signal;
        this.signalisation = signalisation;
        this.lock = fermer;
    }

    public String getBSSID() {
        return this.BSSID;
    }

    public String getESSID() {
        return this.ESSID;
    }

    public String getINFO() {
        return this.information_wifi;
    }

    public String getSIGNAL() {
        return this.signal;
    }

    public int getLOCK() {
        return this.lock;
    }

    public int getWiFiSignalIMG() {
        return this.signalisation;
    }
}
