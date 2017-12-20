package com.bluetooth.rutkoski.unitybluetoothadapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//import com.unity3d.player.UnityPlayer;

/**
 * Created by rutkoski on 19/12/2017.
 */

public class ArduinoBluetoothAdapter {

    private static final String TAG = "HC-05";

    static class IncomingHandler extends Handler {
        private final WeakReference<ArduinoBluetoothAdapter> adapter;

        IncomingHandler(ArduinoBluetoothAdapter adapter) {
            this.adapter = new WeakReference<ArduinoBluetoothAdapter>(adapter);
        }

        @Override
        public void handleMessage(Message msg) {
            ArduinoBluetoothAdapter adapter = this.adapter.get();
            switch (msg.what) {
                case ArduinoBluetoothAdapter.RECEIVE_MESSAGE:                                                   // if receive massage
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                    adapter.sb.append(strIncom);                                                // append string
                    int endOfLineIndex = adapter.sb.indexOf("\r\n");                            // determine the end-of-line
                    if (endOfLineIndex > 0) {                                            // if end-of-line,
                        String sbprint = adapter.sb.substring(0, endOfLineIndex);               // extract string
                        adapter.sb.delete(0, adapter.sb.length());                                      // and clear

                        adapter.SendMessage("OnMessageReceived", sbprint);
                    }
                    break;
            }
        }
    }

    private IncomingHandler h;

    static final int RECEIVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private String address;

    public ArduinoBluetoothAdapter(String address) {
        this.address = address;

        h = new IncomingHandler(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
    }

    public void Connect() {
        //Log.d(TAG, "...onResume - try Connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            SendMessage("OnError", "Failed to create socket: " + e.getMessage());
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to Connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        //Log.d(TAG, "...Connecting...");

        try {
            btSocket.connect();

            //Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                SendMessage("OnError", "Unable to close socket during connection failure: " + e2.getMessage());
            }
        }

        // Create a data stream so we can talk to server.
        //Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        SendMessage("OnConnected");
    }

    public void Disconnect() {
        try {
            btSocket.close();
        } catch (IOException e2) {
            SendMessage("OnError", "Failed to close socket: " + e2.getMessage());
        }

        SendMessage("OnDisconnected");
    }

    public void Send(String message) {
        mConnectedThread.send(message);
    }

    /*private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not supported");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }*/

    /*private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        //finish();
    }*/

    private void SendMessage(String type) {
        SendMessage(type, "");
    }

    private void SendMessage(String type, String message) {
        //UnityPlayer.UnitySendMessage("ArduinoBluetoothAdapter", type, message);
        Log.d(TAG, type + ": " + message);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                SendMessage("OnError", "Could not create Insecure RFComm Connection: " + e.getMessage());
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, Arrays.copyOf(buffer, bytes)).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to Send data to the remote device */
        public void send(String message) {
           byte[] msgBuffer = message.getBytes();

            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                SendMessage("OnError", "Error sending data: " + e.getMessage());
            }
        }
    }
}