package com.kakao.i.device.libbtrcu;

public class NativeHIDRaw {

    static {
        System.loadLibrary("hidraw-lib");
    }

    private static class Singleton {
        private static final NativeHIDRaw instance = new NativeHIDRaw();
    }

    public static NativeHIDRaw getInstance() {
        return Singleton.instance;
    }

    private NativeHIDRaw() {
    }

    public native boolean isConnected();

    public native int recStart();

    public native int recStop();

    public native int restartD();

}
