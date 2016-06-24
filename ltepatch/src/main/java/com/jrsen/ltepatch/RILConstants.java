package com.jrsen.ltepatch;

import com.reflect.core.Reflection;
import com.reflect.core.StaticField;

/**
 * Created by jrsen on 16-6-24.
 */
public final class RILConstants {

    public static StaticField<Integer> NETWORK_MODE_LTE_ONLY;

    static {
        Reflection.init("com.android.internal.telephony.RILConstants", RILConstants.class);
    }
}
