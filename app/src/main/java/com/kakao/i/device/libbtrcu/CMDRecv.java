package com.kakao.i.device.libbtrcu;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class CMDRecv implements Runnable{
    final static String TAG = "BTRCU_JNI";

    public static final int CMD_RPORT = 55556;
    private DatagramSocket cmdSocket;
    private DatagramPacket cmdPacket;

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        int count = 0;
        int recvLength = 0;
        Log.d(TAG, "CMD START!!!.");
        try {
            cmdSocket = new DatagramSocket(CMD_RPORT);
        } catch (SocketException e1) {
            Log.d(TAG, "CMD SocketException.");
            e1.printStackTrace();
        }
        while(true) {
            cmdPacket = new DatagramPacket(buf, buf.length);
            try {
                cmdSocket.receive(cmdPacket);
            } catch (IOException e1) {
                Log.d(TAG, "CMD IOException.");
                e1.printStackTrace();
            }
            recvLength = cmdPacket.getLength();
            if(recvLength > 0)
                Log.d(TAG, "CMD recv: " + cmdPacket.toString());
        }
    }
}
