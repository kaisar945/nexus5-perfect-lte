package com.jrsen.lte;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.jrsen.ltepatch.LteDaemon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isRooted()) {
            try {
                final String[] COMMANDS = {
                        "export CLASSPATH=" + getPackageCodePath(),
                        "app_process /system/bin " + LteDaemon.class.getName()
                };
                Runtime runtime = Runtime.getRuntime();
                Process su = runtime.exec("su");
                OutputStream os = su.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                for (String command : COMMANDS) {
                    bw.write(command + "\n");
                    bw.flush();
                }
                bw.close();
            } catch (IOException ignore) {
            }
            Toast.makeText(this, "自动切换到lte网络", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.RadioInfo");
            intent.setComponent(componentName);
            startActivity(intent);
            Toast.makeText(this, "手动切换到lte网络", Toast.LENGTH_LONG).show();
        }
        finish();
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
