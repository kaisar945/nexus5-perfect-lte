package com.jrsen.ltepatch;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by jrsen on 16-6-24.
 */
public final class LteDaemon implements Runnable {

    private static final long WAIT_LTE_READY_DELAY = 10000l;

    private final String[] args;

    public static void main(String[] args) {
        new LteDaemon(args).run();
    }

    public LteDaemon(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        log("LTE-DAEMON", "auto lte daemon start...");

        try {
            IBinder service = ServiceManager.getService.invoke(Context.TELEPHONY_SERVICE);
            Object iTelephony = ITelephony_L.Stub.asInterface.invoke(service);

            // save previous preferred network type
            int preferredNetworkType = getPreferredNetwork(iTelephony);
            log("LTE-DAEMON", "network type = " + preferredNetworkType);

            // switch to lte network type
            boolean successful = setPreferredNetworkType(iTelephony, RILConstants.NETWORK_MODE_LTE_ONLY.get());
            log("LTE-DAEMON", "changed = " + successful + " new network type = " + getPreferredNetwork(iTelephony));

            if (successful) {
                //wait to lte network prepare
                SystemClock.sleep(WAIT_LTE_READY_DELAY);

//                int dataNetworkType = getDataNetworkType(iTelephony);
//                System.out.println("current dataNetworkType = " + dataNetworkType);
//                boolean isLTE = dataNetworkType == TelephonyManager.NETWORK_TYPE_LTE;
//                if (isLTE) {

                //restore network type
                boolean result = setPreferredNetworkType(iTelephony, preferredNetworkType);
                log("LTE-DAEMON", "restore success = " + result + " new network type = " + getPreferredNetwork(iTelephony));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("LTE-DAEMON", "Exception:" + Log.getStackTraceString(e));
        }
    }

    private boolean setPreferredNetworkType(Object iTelephony, int networkType) {
        if (Build.VERSION.SDK_INT >= 23) {
            int subId = SubscriptionManager.getDefaultSubId.invoke();
            return ITelephony_M.setPreferredNetworkType.invoke(iTelephony, subId, networkType);
        } else {
            return ITelephony_L.setPreferredNetworkType.invoke(iTelephony, networkType);
        }
    }

    private int getPreferredNetwork(Object iTelephony) {
        if (Build.VERSION.SDK_INT >= 23) {
            int subId = SubscriptionManager.getDefaultSubId.invoke();
            return ITelephony_M.getPreferredNetworkType.invoke(iTelephony, subId);
        } else {
            return ITelephony_L.getPreferredNetworkType.invoke(iTelephony);
        }
    }

    private void log(String tag, String msg) {
        Log.i(tag, msg);
        System.out.println(msg);
    }
}
