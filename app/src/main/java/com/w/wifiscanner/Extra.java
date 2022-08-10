package com.w.wifiscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Environment;


import org.hdev.wifiwpspro.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell.SH;
import eu.chainfire.libsuperuser.Shell.SU;

public class Extra {

    public static String capabilitiesTypeResume(String capabilities) {
        if (capabilities.contains("WPA2")) {
            return "[WPA2]";
        }
        if (capabilities.contains("WPA")) {
            return "[WPA]";
        }
        if (capabilities.contains("WEP")) {
            return "[WEP]";
        }
        if (capabilities.contains("ESS")) {
            return "[OPEN]";
        }
        return capabilities;
    }

    private static int secondFragmentBSSID(String bssid) {
        String[] splitBSSID = bssid.split(":");
        return Integer.valueOf(splitBSSID[3] + splitBSSID[4] + splitBSSID[5], 16);
    }


    private static int ZhaoChunshengAlgorithm(String bssid) {
        int PIN = secondFragmentBSSID(bssid) % 10000000;
        return (PIN * 10) + wpsChecksum(PIN);
    }

    @SuppressLint("DefaultLocale")
    private static int ArcadyanAlgorithm(String bssid) {
        int mac = Integer.parseInt(bssid.replaceAll(":", "").substring(8, 12), 16);
        String serial0 = String.format("%05d", mac);
        int[] sn_int = new int[10];
        sn_int[6] = serial0.charAt(1) & 15;
        sn_int[7] = serial0.charAt(2) & 15;
        sn_int[8] = serial0.charAt(3) & 15;
        sn_int[9] = serial0.charAt(4) & 15;
        String mac_str = bssid.replaceAll(":", "");
        int[] mac_int = new int[mac_str.length()];
        for (byte i = (byte) 0; i < mac_int.length; i = (byte) (i + 1)) {
            mac_int[i] = Integer.parseInt(String.valueOf(mac_str.charAt(i)), 16) & 15;
        }
        int[] hpin = new int[7];
        int k1 = (((sn_int[6] + sn_int[7]) + mac_int[10]) + mac_int[11]) & 15;
        int k2 = (((sn_int[8] + sn_int[9]) + mac_int[8]) + mac_int[9]) & 15;
        hpin[0] = sn_int[9] ^ k1;
        hpin[1] = sn_int[8] ^ k1;
        hpin[2] = mac_int[9] ^ k2;
        hpin[3] = mac_int[10] ^ k2;
        hpin[4] = mac_int[10] ^ sn_int[9];
        hpin[5] = mac_int[11] ^ sn_int[8];
        hpin[6] = sn_int[7] ^ k1;
        int pin = Integer.parseInt(String.format("%1X%1X%1X%1X%1X%1X%1X", hpin[0], hpin[1], hpin[2], hpin[3], hpin[4], hpin[5], hpin[6]), 16) % 10000000;
        return (pin * 10) + wpsChecksum(pin);
    }

    private static int ArrisDG860AAlgorithm(String strMac) {
        int i;
        long[] fibnum = new long[6];
        long fibsum = 0;
        int counter = 0;
        String[] q = strMac.split(":");
        int[] arrayMacs = new int[q.length];
        int[] tmp = new int[q.length];
        for (int c = 0; c < q.length; c++) {
            arrayMacs[c] = Integer.valueOf(q[c], 16);
            tmp[c] = Integer.valueOf(q[c], 16);
        }
        for (i = 0; i < 6; i++) {
            if (tmp[i] > 30) {
                while (tmp[i] > 31) {
                    tmp[i] = tmp[i] - 16;
                    counter++;
                }
            }
            if (counter == 0) {
                if (tmp[i] < 3) {
                    tmp[i] = (((((tmp[0] + tmp[1]) + tmp[2]) + tmp[3]) + tmp[4]) + tmp[5]) - tmp[i];
                    if (tmp[i] > 255) {
                        tmp[i] = tmp[i] & 255;
                    }
                    tmp[i] = (tmp[i] % 28) + 3;
                }
                fibnum[i] = (long) FibGen(tmp[i]);
            } else {
                fibnum[i] = (long) (FibGen(tmp[i]) + FibGen(counter));
            }
            counter = 0;
        }
        for (i = 0; i < 6; i++) {
            fibsum += (fibnum[i] * ((long) FibGen(i + 16))) + ((long) arrayMacs[i]);
        }
        int int_fibsum = (int) (fibsum % 10000000);
        return (int_fibsum * 10) + wpsChecksum(int_fibsum);
    }

    private static int FibGen(int n) {
        if (n == 1 || n == 2 || n == 0) {
            return 1;
        }
        return FibGen(n - 1) + FibGen(n - 2);
    }

    private static int wpsChecksum(int pin) {
        int accum = 0;
        while (pin > 0) {
            accum = (((accum + (pin % 10) * 3)) + (((pin / 10)) % 10));
            pin = (pin / 100);
        }
        accum = (10 - accum % 10) % 10;
        return accum;
    }

    private static int pin28bit(String bssid) {
        int pin = (int) ((completeBSSID(bssid) & 268435455) % 10000000);
        return (pin * 10) + wpsChecksum(pin);
    }

    private static int pin32bit(String bssid) {
        int pin = (int) ((completeBSSID(bssid) % Long.parseLong("100000000", 16)) % 10000000);
        return (pin * 10) + wpsChecksum(pin);
    }

    public static long completeBSSID(String bssid) {
        String[] splitBSSID = bssid.split(":");
        return Long.parseLong(splitBSSID[0] + splitBSSID[1] + splitBSSID[2] + splitBSSID[3] + splitBSSID[4] + splitBSSID[5], 16);
    }


    public static String[] calculePIN(Networking networking) {
        String[] ret = new String[5];

        ret[0] = String.valueOf(ZhaoChunshengAlgorithm(networking.getBSSID()));
        ret[1] = String.valueOf(ArcadyanAlgorithm(networking.getBSSID()));
        ret[2] = String.valueOf(ArrisDG860AAlgorithm(networking.getBSSID()));
        ret[3] = String.valueOf(pin28bit(networking.getBSSID()));
        ret[4] = String.valueOf(pin32bit(networking.getBSSID()));

        return ret;
    }

    public static String[] calculePINNew(String net) {
        String[] ret = new String[5];

        ret[0] = String.valueOf(ZhaoChunshengAlgorithm(net));
        ret[1] = String.valueOf(ArcadyanAlgorithm(net));
        ret[2] = String.valueOf(ArrisDG860AAlgorithm(net));
        ret[3] = String.valueOf(pin28bit(net));
        ret[4] = String.valueOf(pin32bit(net));

        return ret;
    }

    protected static String loadLib(Context context) {
        String name = "wpa_cli";
        String code ;
        if (!SH.run("wpa_cli -v").isEmpty()) {
            return name;
        }
        int id_wpaCli;
        switch (VERSION.SDK_INT) {
            case 14:
                code = "_14";
                id_wpaCli = R.raw.wpa_code_pins_1;
                break;
            case 15:
                code = "_14";
                id_wpaCli = R.raw.wpa_code_pins_1;
                break;
            case 16:
                code = "_16";
                id_wpaCli = R.raw.wpa_code_pins_2;
                break;
            case 17:
                code = "_16";
                id_wpaCli = R.raw.wpa_code_pins_2;
                break;
            case 18:
                code = "_16";
                id_wpaCli = R.raw.wpa_code_pins_2;
                break;
            case 19:
                code = "_19";
                id_wpaCli = R.raw.code_pins;
                break;
            case 21:
                code = "_19";
                id_wpaCli = R.raw.code_pins;
                break;
            case 22:
                code = "_23";
                id_wpaCli = R.raw.wpa_code_pins_3;
                break;
            case 23:
                code = "_23";
                id_wpaCli = R.raw.wpa_code_pins_3;
                break;
            default:
                code = "_";
                id_wpaCli = R.raw.wpa_code_pins;
                break;
        }
        name = name + code;
        if (!context.getFileStreamPath(name).exists()) {
            InputStream ins = context.getResources().openRawResource(id_wpaCli);
            try {
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = context.openFileOutput(name, 0);
                fos.write(buffer);
                fos.close();
                context.getFileStreamPath(name).setExecutable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return context.getFileStreamPath(name).getAbsolutePath();
    }

    protected static boolean doBackup(String fileName, ArrayList<Password> pwdList, String SSID, String PASS) {
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (file == null) {
            return false;
        }
        PrintWriter out = new PrintWriter(file);
        Iterator it = pwdList.iterator();
        while (it.hasNext()) {
            Password pwd = (Password) it.next();
            out.println(SSID + " " + pwd.getSSID());
            out.println(PASS + " " + pwd.getOutrun());
            out.println("---------------------------------------");
        }
        out.close();
        return true;
    }

    protected static List<String> parseSupplicant() {
        List<String> output = new LinkedList();
        if (!SU.available()) {
            return output;
        }
        List<String> result = SU.run("cat /data/misc/wifi/wpa_supplicant.conf");
        List<String> networks = new ArrayList();
        for (String line : result) {
            String tmp = line.replace("\t", "");
            if (tmp.length() >= 6 && !tmp.substring(0, 6).equalsIgnoreCase("bssid=")) {
                if (tmp.substring(0, 5).equalsIgnoreCase("ssid=")) {
                    networks.add("s" + tmp.substring(6).replace("\"", ""));
                } else if (tmp.substring(0, 4).equalsIgnoreCase("psk=")) {
                    networks.add("p" + tmp.substring(5).replace("\"", ""));
                }
            }
        }
        return networks;
    }
}
