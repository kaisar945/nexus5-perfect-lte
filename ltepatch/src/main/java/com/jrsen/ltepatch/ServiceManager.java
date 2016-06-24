package com.jrsen.ltepatch;

import android.os.IBinder;

import com.reflect.annotation.ClassParams;
import com.reflect.core.Reflection;
import com.reflect.core.StaticMethod;

/**
 * Created by jrsen on 16-6-24.
 */
public final class ServiceManager {

    public static Class clazz;

    @ClassParams({String.class})
    public static StaticMethod<IBinder> getService;

    static {
        clazz = Reflection.init("android.os.ServiceManager", ServiceManager.class);
    }
}
