package com.jiek.jeventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JEventBus {
    private static final String TAG = "JEventBus";
    private volatile static JEventBus instance;

    private Map<Object, List<SubscriberMethod>> cachemap;
    private Handler mHandler;

    public JEventBus() {
        mHandler = new android.os.Handler(Looper.getMainLooper());
        cachemap = new HashMap<>();
    }

    public static JEventBus getDefault() {
        if (instance == null) {
            synchronized (JEventBus.class) {
                if (instance == null) {
                    instance = new JEventBus();
                }
            }
        }
        return instance;
    }

    public void register(Object obj) {
        Log.e(TAG, "register: " + obj.getClass().getName());
        List<SubscriberMethod> list = cachemap.get(obj);
        if (list == null) {
            list = getSubscribeMethod(obj);
        }
        if (list != null && list.size() > 0) {
            cachemap.put(obj, list);
        }
    }

    private List<SubscriberMethod> getSubscribeMethod(Object obj) {
        List<SubscriberMethod> list = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            String name = clazz.getName();
//            排除继承系统层的类判断
            if (
                    name.startsWith("java.")
                            || name.startsWith("javax.")
                            || name.startsWith("android.")
                            || name.startsWith("androidx.")) {
                break;
            }

//        clazz.getMethods();//public 方法
            Method[] methods = clazz.getDeclaredMethods();//所有申明方法
            for (Method method : methods) {
                Subscribe anno = method.getAnnotation(Subscribe.class);
                if (anno == null) {
                    continue;
                }
                Class<?>[] types = method.getParameterTypes();
                if (types.length != 1) {
                    throw new RuntimeException("订阅方法参数不正常");
                }
                ThreadMode threadMode = anno.threadMode();
                SubscriberMethod subscriberMethod = new SubscriberMethod(method, types[0], threadMode);

                list.add(subscriberMethod);
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    public void unregister(Object key) {
        cachemap.remove(key);
    }

    public void post(final Object data) {
        Set<Object> set = cachemap.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            List<SubscriberMethod> subscriberMethods = cachemap.get(obj);
            for (final SubscriberMethod subscriberMethod : subscriberMethods) {
                if (subscriberMethod.eventType.isAssignableFrom(data.getClass())) {
                    switch (subscriberMethod.threadMode) {
                        case MAIN:
                            if (Looper.myLooper() != Looper.getMainLooper()) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "run: " + subscriberMethod.methodString +
                                                "\t" + obj.getClass().getName() +
                                                "\t" + data);
                                        invoke(subscriberMethod, obj, data);
                                    }
                                });
                            } else {
                                invoke(subscriberMethod, obj, data);
                            }
                            break;
                        case ASYNC:
                            //使用线程池
                            invoke(subscriberMethod, obj, data);
                            break;
                    }
                }
            }
        }
    }

    private void invoke(SubscriberMethod subscriberMethod, Object obj, Object type) {
        Method method = subscriberMethod.method;
        method.setAccessible(true);
        try {
            method.invoke(obj, type);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
