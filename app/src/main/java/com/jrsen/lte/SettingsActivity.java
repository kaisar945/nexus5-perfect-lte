package com.jrsen.lte;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public final class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, DaemonService.class));
        finish();
    }

}
