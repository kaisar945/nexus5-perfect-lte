package com.jrsen.lte;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.jrsen.ltepatch.LteDaemon;

import java.io.OutputStream;

/**
 * Created by jrsen on 16-9-7.
 */
public final class DaemonService extends Service {

    @Override
    public void onCreate() {
        tryAutoSetPreferredNetwork();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void tryAutoSetPreferredNetwork() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                startForeground(1, new Notification());
                Toast.makeText(DaemonService.this, "正在检测是否支持lte网络...", Toast.LENGTH_LONG).show();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    final String[] COMMANDS = {
                            "export CLASSPATH=" + getPackageCodePath(),
                            "app_process /system/bin " + LteDaemon.class.getName()
                    };
                    Runtime runtime = Runtime.getRuntime();
                    Process su = runtime.exec("su");
                    OutputStream os = su.getOutputStream();
                    for (String command : COMMANDS) {
                        os.write((command + "\n").getBytes());
                    }
                    os.flush();
                    os.close();
                    return su.waitFor();
                } catch (Exception ignore) {
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == 0) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) {
                        boolean isConnected = activeNetwork.isConnectedOrConnecting();
                        boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                        boolean isLTE = isMobile && activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
                        if (isConnected && isMobile && isLTE) {
                            Toast.makeText(DaemonService.this, "已成功切换到lte网络！", Toast.LENGTH_LONG).show();
                        } else {
                            tryManualSetPreferredNetwork(DaemonService.this);
                            Toast.makeText(DaemonService.this, "目前位置可能不支持lte网络，请尝试手动切换到lte网络。", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        tryManualSetPreferredNetwork(DaemonService.this);
                        Toast.makeText(DaemonService.this, "自动切换失败，请手动切换到lte网络。", Toast.LENGTH_LONG).show();
                    }
                } else {
                    tryManualSetPreferredNetwork(DaemonService.this);
                    Toast.makeText(DaemonService.this, "自动切换失败，请手动切换到lte网络。", Toast.LENGTH_LONG).show();
                }
                stopForeground(true);
                stopSelf();
            }
        }.execute();
    }

    public static void tryManualSetPreferredNetwork(Context context) {
        try {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.RadioInfo");
            intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignore) {
        }
    }

}
