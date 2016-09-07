package com.jrsen.lte;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.jrsen.ltepatch.LteDaemon;

import java.io.File;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * Created by jrsen on 16-9-7.
 */
public final class DaemonService extends Service {

    @Override
    public void onCreate() {
        if (isRooted()) {
            tryAutoSetPreferredNetwork();
        } else {
            tryManualSetPreferredNetwork();
            Toast.makeText(this, "没有su权限,请手动切换到lte网络.", Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void tryManualSetPreferredNetwork() {
        try {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.RadioInfo");
            intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ignore) {
        }
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
                return LteDaemon.ERROR_UNKNOW;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == 0) {
                    Toast.makeText(DaemonService.this, "已成功切换到lte网络！", Toast.LENGTH_LONG).show();
                } else if (result == LteDaemon.ERROR_UNKNOW) {
                    tryManualSetPreferredNetwork();
                    Toast.makeText(DaemonService.this, "自动切换失败，请手动切换到lte网络。", Toast.LENGTH_LONG).show();
                } else if (result == LteDaemon.ERROR_NO_LTE) {
                    tryManualSetPreferredNetwork();
                    Toast.makeText(DaemonService.this, "目前位置可能不支持lte网络，请尝试手动切换到lte网络。", Toast.LENGTH_LONG).show();
                }
                stopForeground(true);
                stopSelf();
            }
        }.execute();
    }

    public static boolean isRooted() {
        String path = System.getenv("PATH");//获取环境变量,eg:/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin:/vendor/bin
        if (path == null) {
            return false;
        }
        StringTokenizer stok = new StringTokenizer(path, ":");
        while (stok.hasMoreTokens()) {
            File su = new File(stok.nextToken(), "su");
            if (su.exists()) {
                return true;
            }
        }
        return false;
    }

}
