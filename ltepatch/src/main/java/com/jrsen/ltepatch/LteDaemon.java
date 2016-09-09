package com.jrsen.ltepatch;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by jrsen on 16-6-24.
 */
public final class LteDaemon implements Runnable {

    private static final String LOG_TAG = "LTE";
    private static final long WAIT_LTE_READY_DELAY = 7 * 1000l;
    private static final long WAIT_RESTORE_DELAY = 5 * 1000l;

    public static void main(String[] args) {
        new LteDaemon().run();
    }

    @Override
    public void run() {
        try {
            Log.i(LOG_TAG, "lte daemon running...");

            IBinder service = ServiceManager.getService.invoke(Context.TELEPHONY_SERVICE);
            Object iTelephony = ITelephony_L.Stub.asInterface.invoke(service);

            // save previous preferred network type
            int preferredNetworkType = getPreferredNetwork(iTelephony);
            Log.i(LOG_TAG, "previous network type = " + preferredNetworkType);

            // switch to lte network type
            boolean successful = setPreferredNetworkType(iTelephony, RILConstants.NETWORK_MODE_LTE_ONLY.get());
            Log.i(LOG_TAG, "changed = " + successful + " new network type = " + getPreferredNetwork(iTelephony));

            if (successful) {
                //wait to lte network prepare
                Thread.sleep(WAIT_LTE_READY_DELAY);

                //restore network type
                boolean result = setPreferredNetworkType(iTelephony, preferredNetworkType);
                Log.i(LOG_TAG, "restore success = " + result + " new network type = " + getPreferredNetwork(iTelephony));

                //wait to restore orig network type
                Thread.sleep(WAIT_RESTORE_DELAY);
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Exception:" + Log.getStackTraceString(e));
            System.exit(1);
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

}
