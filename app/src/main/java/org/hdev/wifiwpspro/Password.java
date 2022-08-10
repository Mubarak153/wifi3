package org.hdev.wifiwpspro;



public class Password {
    private final String SSID;
    private final String outrun;

    public Password(String SSID, String outrun) {
        this.SSID = SSID;
        this.outrun = outrun;
    }

    public String getSSID() {
        return this.SSID;
    }

    public String getOutrun() {
        return this.outrun;
    }
}