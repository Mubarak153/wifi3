package com.w.wifiscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.hdev.wifiwpspro.R;

import java.util.ArrayList;



public class NetAdapter extends ArrayAdapter {
    Activity context;
    ArrayList<Networking> networkingList;

    public NetAdapter(Activity context, ArrayList<Networking> networkingList) {
        super(context, R.layout.list, networkingList);
        this.context = context;
        this.networkingList = networkingList;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = context.getLayoutInflater().inflate(R.layout.network_info, null);
        ImageView wifiSignal = item.findViewById(R.id.iconSignal);
        TextView bssid = item.findViewById(R.id.BSSID_ID);
        TextView lblESSID = item.findViewById(R.id.ESSID_ID);
        TextView wpsEnabled = item.findViewById(R.id.wps_activated);
        TextView secured = item.findViewById(R.id.isOK);
        ((TextView) item.findViewById(R.id.INFO)).setText(context.getString(R.string.encryption_type)+" "+ Extra.capabilitiesTypeResume((networkingList.get(position)).getINFO()));
        if ((networkingList.get(position)).getINFO().contains("WPS")) {
//            lblESSID.setTextColor(ContextCompat.getColor(context, R.color.color_green_new));
            wpsEnabled.setText(context.getString(R.string.wps_enabled)+" Yes");
            item.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colour_green));
            wifiSignal.setColorFilter(ContextCompat.getColor(context, R.color.colour_green_new), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
//            lblESSID.setTextColor(ContextCompat.getColor(context, R.color.color_red_new));
            wpsEnabled.setText(context.getString(R.string.wps_enabled)+" No");
            item.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colour_red));
            wifiSignal.setColorFilter(ContextCompat.getColor(context, R.color.colour_red_new), android.graphics.PorterDuff.Mode.SRC_IN);

        }
        if(Extra.capabilitiesTypeResume((networkingList.get(position)).getINFO()).contains("WPA") ||
                Extra.capabilitiesTypeResume((networkingList.get(position)).getINFO()).contains("WPA2") ||
                Extra.capabilitiesTypeResume((networkingList.get(position)).getINFO()).contains("WEP")){
            secured.setText("Secured");
        }
        String essid = (networkingList.get(position)).getESSID();
        if (essid == null || essid.trim().isEmpty()) {
            lblESSID.setText(R.string.noSSID);
        } else {
            lblESSID.setText(context.getString(R.string.ssid_name)+" "+(networkingList.get(position)).getESSID());
        }
        bssid.setText(context.getString(R.string.mac_name)+" "+(networkingList.get(position)).getBSSID());
        ((TextView) item.findViewById(R.id.signal)).setText((networkingList.get(position)).getSIGNAL()+" "+context.getString(R.string.dbm));
        ((ImageView) item.findViewById(R.id.lock_icon)).setImageResource((networkingList.get(position)).getLOCK());
        wifiSignal.setImageResource((networkingList.get(position)).getWiFiSignalIMG());
        return item;
    }
}