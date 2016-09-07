package com.jrsen.ltepatch;

import android.os.IBinder;

import com.reflect.annotation.ClassParams;
import com.reflect.core.Method;
import com.reflect.core.Reflection;
import com.reflect.core.StaticMethod;

/**
 * Created by jrsen on 16-6-24.
 */
public final class ITelephony_L {

    public static Method<Integer> getPreferredNetworkType;
    @ClassParams({int.class})
    public static Method<Boolean> setPreferredNetworkType;

    public static Method<Integer> getNetworkType;

    static {
        Reflection.init("com.android.internal.telephony.ITelephony", ITelephony_L.class);
    }

    public static final class Stub {

        @ClassParams(IBinder.class)
        public static StaticMethod asInterface;

        static {
            Reflection.init("com.android.internal.telephony.ITelephony$Stub", Stub.class);
        }
    }
}
