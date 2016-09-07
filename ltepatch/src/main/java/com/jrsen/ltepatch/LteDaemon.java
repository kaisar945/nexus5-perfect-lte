package com.jrsen.ltepatch;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by jrsen on 16-6-24.
 */
public final class LteDaemon implements Runnable {

    private static final String LOG_TAG = "LTE";
    private static final long WAIT_LTE_READY_DELAY = 8 * 1000l;

    public static final int ERROR_UNKNOW = 1;
    public static final int ERROR_NO_LTE = 2;

    public static void main(String[] args) {
        new LteDaemon().run();
    }

    @Override
    public void run() {
        try {
            Log.i(LOG_TAG, "lte daemon running...");

            IBinder telService = ServiceManager.getService.invoke(Context.TELEPHONY_SERVICE);
            Object iTelephony = ITelephony_L.Stub.asInterface.invoke(telService);

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

                int networkType = ITelephony_L.getNetworkType.invoke(iTelephony);
                boolean isLte = networkType == TelephonyManager.NETWORK_TYPE_LTE;
                Log.i(LOG_TAG, "current dataNetworkType = " + networkType + " is lte = " + isLte);
                if (!isLte) {
                    System.exit(ERROR_NO_LTE);
                }
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Exception:" + Log.getStackTraceString(e));
            System.exit(ERROR_UNKNOW);
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
