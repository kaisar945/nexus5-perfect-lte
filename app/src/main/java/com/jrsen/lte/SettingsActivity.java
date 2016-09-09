package com.jrsen.lte;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public final class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            boolean isLTE = isMobile && activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
            if (isConnected && isMobile) {
                if (!isLTE) {
                    if (RootDetector.isRooted()) {
                        startService(new Intent(this, DaemonService.class));
                    } else {
                        DaemonService.tryManualSetPreferredNetwork(this);
                        Toast.makeText(this, "没有su权限,请手动切换到lte网络。", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "当前已经是lte网络，无需切换。", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "没有连接或者不是移动网络，无法切换。", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "没有连接网络!", Toast.LENGTH_LONG).show();
        }
        finish();
    }

}
