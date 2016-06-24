package com.jrsen.ltepatch;

import com.reflect.core.Reflection;
import com.reflect.core.StaticMethod;

/**
 * Created by jrsen on 16-6-24.
 */
public class SubscriptionManager {

    public static StaticMethod<Integer> getDefaultSubId;

    static {
        Reflection.init("android.telephony.SubscriptionManager", SubscriptionManager.class);
    }
}
