package com.jrsen.lte;

import java.io.File;
import java.util.StringTokenizer;

/**
 * Created by jrsen on 16-9-9.
 */
public final class RootDetector {

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
