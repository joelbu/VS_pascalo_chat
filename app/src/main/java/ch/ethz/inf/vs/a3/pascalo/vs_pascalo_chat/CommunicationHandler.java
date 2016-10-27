package ch.ethz.inf.vs.a3.pascalo.vs_pascalo_chat;


import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.PriorityQueue;

import ch.ethz.inf.vs.a3.message.MessageComparator;
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

    // This is the current instance, we only ever allow one to exist at a time
    private static CommunicationHandler mInstance;

    public static CommunicationHandler getInstance() {
        return mInstance;
    }

    // This is how we can get an instance externally, construct it with this function, then
    // get it with the one above
    public static void Initialise(InetAddress address, int port, String username, String uuid) {
        if (mInstance == null) {
            mInstance = new CommunicationHandler(address, port, username, uuid);
        }
    }

    // Cleanup, which also enables us to initialise a new instance (for example with a new username)
    public void destroy() {
        mSocket.close();
        mInstance = null;
    }

    // Constructor is private, we don't want to allow others (us in other classes) to construct
    // instances any other way than above
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
        reg_msg.set_header(mUsername, mUUID, "{}", MessageTypes.REGISTER);

        Log.d(TAG, "Sending register message:\n" + reg_msg.toString());

        // attempt sending asynchronously with 5 write attempts
        return trySendingAndRetryFiveTimes(reg_msg);
    }

    public boolean tryDeregisteringAndRetryFiveTimes() {
        boolean success = false;

        // build deregister message
        Message dereg_msg = new Message();
        dereg_msg.set_header(mUsername, mUUID, "{}", MessageTypes.DEREGISTER);

        Log.d(TAG, "Sending deregister message:\n" + dereg_msg.toString());

        // attempt sending asynchronously with 5 write attempts
        return trySendingAndRetryFiveTimes(dereg_msg);
    }

    private boolean trySendingAndRetryFiveTimes(Message message) {
        boolean received_ack = false;
        try {

            // Prepare data packet
            byte[] send_buf = message.toString().getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(send_buf, send_buf.length, mAddress, mPort);
            byte[] recv_buf = new byte[NetworkConsts.PAYLOAD_SIZE];
            DatagramPacket answer = new DatagramPacket(recv_buf, recv_buf.length, mAddress, mPort);

            // attempt sending the packet
            for(int i = 0; !received_ack && i <= 5; i++ ){
                mSocket.send(packet);
                try {
                    // Blocking call unless and until timeout exception occurs
                    mSocket.receive(answer);

                    // Converting the bytes to a string
                    String receivedMessageString = new String(answer.getData(), 0, answer.getLength(),"UTF-8");
                    Log.d(TAG, "Received message:\n" + receivedMessageString);

                    // Converting the string to a Message
                    Message receivedMessage = new Message(receivedMessageString);

                    // Checking the type for ack
                    received_ack = receivedMessage.header.type.equals(MessageTypes.ACK_MESSAGE);

                } catch (SocketTimeoutException e){

                    Log.d(TAG, "Receive timeout.");
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return received_ack;
    }


    public PriorityQueue<Message> tryRetrieveMessages() {

        boolean timeout = false;
        // Create priority queue for messages: with initial capacity 10
        PriorityQueue<Message> queue =
                new PriorityQueue<Message>(10,  new MessageComparator());

        // build retrive message
        Message ret_msg = new Message();
        ret_msg.set_header(mUsername, mUUID, "{}", MessageTypes.RETRIEVE_CHAT_LOG);

        Log.d(TAG, "Sending retrieveChatLog message:\n" + ret_msg.toString());



        try {

            // Prepare data packet
            byte[] send_buf = ret_msg.toString().getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(send_buf, send_buf.length, mAddress, mPort);
            byte[] recv_buf = new byte[NetworkConsts.PAYLOAD_SIZE];
            DatagramPacket answer = new DatagramPacket(recv_buf, recv_buf.length, mAddress, mPort);

            // attempt sending the packet
            mSocket.send(packet);


            while (!timeout) {
                try {
                    // Blocking call unless and until timeout exception occurs
                    mSocket.receive(answer);

                    // Converting the bytes to a string
                    String receivedMessageString = new String(answer.getData(), 0, answer.getLength(), "UTF-8");
                    Log.d(TAG, "Received message:\n" + receivedMessageString);

                    // Converting the string to a Message
                    Message receivedMessage = new Message(receivedMessageString);

                    //Insert messages to queue like this, sorting is automatic
                    queue.add(receivedMessage);


                } catch (SocketTimeoutException e) {
                    timeout = true;
                    Log.d(TAG, "Receive timeout.");
                    e.printStackTrace();
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }








        return null;
    }


}
