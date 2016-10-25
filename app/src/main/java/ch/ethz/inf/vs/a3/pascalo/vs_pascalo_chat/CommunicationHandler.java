package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;


import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

public class CommunicationHandler {
    private InetAddress mAddress;
    private int mPort;
    private String mUsername;
    private String mUUID;
    private DatagramSocket mSocket;

    private final String TAG = "CommunicationHandler";

    private static CommunicationHandler mInstance;

    public static CommunicationHandler getInstance() {
        return mInstance;
    }

    public static void Initialise(InetAddress address, int port, String username, String uuid) {
        if (mInstance == null) {
            mInstance = new CommunicationHandler(address, port, username, uuid);
        }
    }

    public void destroy() {
        mSocket.close();
        mInstance = null;
    }

    private CommunicationHandler(InetAddress address, int port, String username, String uuid) {
        mAddress = address;
        mPort = port;
        mUsername = username;
        mUUID = uuid;

        // create UDP Socket
        try {
            mSocket = new DatagramSocket(mPort);
            mSocket.setSoTimeout(NetworkConsts.SOCKET_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean tryRegisteringAndRetryFiveTimes() {

        // build register message
        Message reg_msg = new Message();
        reg_msg.set_header(mUsername, mUUID, "", MessageTypes.REGISTER);

        // attempt sending asynchronously with 5 write attempts
        Log.d(TAG, "Sending register message:\n" + reg_msg.toString());

        return trySendingAndRetryFiveTimes(reg_msg);
    }

    public boolean tryDeregisteringAndRetryFiveTimes() {
        boolean success = false;

        Message dereg_msg = new Message();
        dereg_msg.set_header(mUsername, mUUID, "", MessageTypes.DEREGISTER);

        Log.d(TAG, "Sending deregister message:\n" + dereg_msg.toString());

        return trySendingAndRetryFiveTimes(dereg_msg);
    }

    private boolean trySendingAndRetryFiveTimes(Message message) {
        boolean success = false;
        try {

            // Prepare data packet
            byte[] send_buf = message.toString().getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(send_buf, send_buf.length, mAddress, mPort);
            byte[] recv_buf = new byte[NetworkConsts.PAYLOAD_SIZE];
            DatagramPacket answer = new DatagramPacket(recv_buf, recv_buf.length, mAddress, mPort);

            // attempt sending the packet
            boolean wait_for_ack = true;
            for(int i = 0; wait_for_ack && i <= 5; i++ ){
                mSocket.send(packet);
                try {
                    mSocket.receive(answer);
                    Message ack = new Message( new String(answer.getData(), 0, answer.getLength(),"UTF-8"));
                    wait_for_ack = ack.header.type.equals(MessageTypes.ACK_MESSAGE);
                    success = true;
                } catch (SocketTimeoutException e){
                    Log.d(TAG, "Receive timeout.");
                    e.printStackTrace();
                    if (i == 5) success = false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }
}
