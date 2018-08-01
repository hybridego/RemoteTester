package com.kakao.i.device.libbtrcu;

public class NativeHIDRaw {
    private static volatile NativeHIDRaw mNativeHIDRaw = null;

    static {
        System.loadLibrary("hidraw-lib");
    }

    public static NativeHIDRaw getInstance() {
        if (mNativeHIDRaw == null) {
            synchronized (NativeHIDRaw.class) {
                if (mNativeHIDRaw == null) {
                    mNativeHIDRaw = new NativeHIDRaw();
                }
            }
        }
        return mNativeHIDRaw;
    }

    public native boolean isConnected();
    public native int recStart();
    public native int recStop();

}
