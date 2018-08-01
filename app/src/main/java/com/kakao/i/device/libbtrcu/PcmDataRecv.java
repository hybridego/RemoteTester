package com.kakao.i.device.libbtrcu;

import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class PcmDataRecv implements Runnable {
    final static String TAG = "BTRCU_JNI";

    public static final int DATA_RPORT = 55555;
    private DatagramSocket dataSocket;
    private DatagramPacket dataPacket;

    OutputStream pcmOutForTest = null;

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        int count = 0;
        int recvLength = 0;
        Log.d(TAG, "START!!!.");
        try {
            pcmOutForTest = new FileOutputStream("/sdcard/savedPCM");
            dataSocket = new DatagramSocket(DATA_RPORT);
        } catch (SocketException e1) {
            Log.d(TAG, "SocketException.");
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "FileNotFoundException.");
            e.printStackTrace();
        }

        while(true) {
            dataPacket = new DatagramPacket(buf, buf.length);
            try {
                dataSocket.receive(dataPacket);
            } catch (IOException e1) {
                Log.d(TAG, "IOException.");
                e1.printStackTrace();
            }
            recvLength = dataPacket.getLength();
            if (recvLength != 384)
                Log.d(TAG, "recv: " + dataPacket.getLength());
            else {
                if (count++ > 50) {
                    Log.d(TAG, "recv: " + dataPacket.getLength());
                    count = 0;
                }
            }
            try {
                pcmOutForTest.write(dataPacket.getData(), 0, dataPacket.getLength());
            } catch (IOException e) {
                Log.d(TAG, "IOException 2.");
                e.printStackTrace();
            }
        }
    }
}