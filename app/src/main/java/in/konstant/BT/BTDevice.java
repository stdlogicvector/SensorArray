package in.konstant.BT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BTDevice {
    // Debug
    private static final String TAG = "BTDevice";
    private static final boolean DBG = false;

    // Standard Serial Port UUID
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public static final class MESSAGE {
        public static final int CONNECTED = 1;
        public static final int CONNECTING = 2;
        public static final int DISCONNECTED = 3;
        public static final int CONNECTION_LOST = 4;
        public static final int CONNECTION_FAILED = 5;
        public static final int DATA_SENT = 6;
        public static final int DATA_RECEIVED = 7;
    }

    public static final class STATE {
        public static final int DISCONNECTED = 0;
        public static final int CONNECTING = 1;
        public static final int CONNECTED = 2;
    }

    public static final String EXTRA_ADDRESS = "in.konstant.BT.device.extra.ADDRESS";
    public static final String EXTRA_DATA = "in.konstant.BT.device.extra.DATA";

    private String mAddress;
    private String mName;
    private String mManufacturer;
    private String mCompany;

    private final Context mContext;
    private Handler mHandler;

//    private final Object mBluetoothService;
//    private final Class mBluetoothServiceClass;
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothDevice mBluetoothDevice;

    private int mState;

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public BTDevice(Context context, String address) {
        if (DBG) Log.d(TAG, "BTDevice(" + address + ")");

        mContext = context;
        mHandler = null;
        mAddress = address;

//        mBluetoothService = mContext.getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothServiceClass = mBluetoothService.getClass();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mAddress);

        mState = STATE.DISCONNECTED;
    }

    public BTDevice(Context context, Handler handler, String address) {
        this(context, address);

        mHandler = handler;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }


    public void destroy() {
        if (DBG) Log.d(TAG, "destroy()");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mState = STATE.DISCONNECTED;
    }

    // Interface -----------------------------------------------------------------------------------

    public void connect() {
        if (DBG) Log.d(TAG, "connect(" + mAddress + ")");

        if (mState == STATE.CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(mBluetoothDevice);
        mConnectThread.start();

        sendMessage(MESSAGE.CONNECTING, null);

        setState(STATE.CONNECTING);
    }

    public void send(byte[] data) {
        if (DBG) Log.d(TAG, "send()");
        ConnectedThread ct;

        synchronized (this) {
            if (mState != STATE.CONNECTED) return;
            ct = mConnectedThread;
        }

        ct.write(data);
    }

    public void disconnect() {
        if (DBG) Log.d(TAG, "disconnect()");

        if (mState == STATE.CONNECTING) {
            if (mConnectThread != null)
            {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.close();
            mConnectedThread = null;
        }

        if (mState == STATE.CONNECTED) {
            sendMessage(MESSAGE.DISCONNECTED, null);
        }

        setState(STATE.DISCONNECTED);
    }

    // Setter & Getter -----------------------------------------------------------------------------

    public BluetoothClass getBluetoothClass() {
        return mBluetoothDevice.getBluetoothClass();
    }

    public String getAddress() {
        return mAddress;
    }

    public String getName() {
        return mBluetoothDevice.getName();
    }

    public UUID[] getUUIDs() {
        ParcelUuid[] parcels = mBluetoothDevice.getUuids();

        ArrayList<UUID> list = new ArrayList<UUID>();

        for (ParcelUuid parcel : parcels) {
            list.add(parcel.getUuid());
        }

        return list.toArray(new UUID[list.size()]);
    }

    public boolean isConnected() {
        if (mState == STATE.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    public int getState() {
        return mState;
    }

    // Helpers -------------------------------------------------------------------------------------

    private void setState(int state) {
        if (DBG) Log.d(TAG, "setState(" + state + ")");
        mState = state;
    }

    private void sendMessage(int event, byte[] data) {
        if (DBG) Log.d(TAG, "sendMessage(" + event + ")");

        if (mHandler != null) {
            android.os.Message msg = mHandler.obtainMessage(event);
            Bundle b = new Bundle();
            b.putString(EXTRA_ADDRESS, mAddress);

            if (data != null && data.length > 0) {
                b.putByteArray(EXTRA_DATA, data);
            }

            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }

    // State Changers ------------------------------------------------------------------------------

    private void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (DBG) Log.d(TAG, "connected(" + socket + ", " + device + ")");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE.CONNECTED);
    }

    private void connectionLost() {
        if (DBG) Log.d(TAG, "connectionLost()");
        sendMessage(MESSAGE.CONNECTION_LOST, null);
        setState(STATE.DISCONNECTED);
    }

    private void connectionFailed() {
        if (DBG) Log.d(TAG, "connectionFailed()");
        sendMessage(MESSAGE.CONNECTION_FAILED, null);
        setState(STATE.DISCONNECTED);
    }

    // Threads -------------------------------------------------------------------------------------

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            if (DBG) Log.d(TAG, "ConnectThread()");
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectThread() Socket create() failed", e);
            }

            mmSocket = tmp;
        }

        public void run() {
            if (DBG) Log.d(TAG, "BEGIN ConnectThread");
            setName("ConnectThread");

            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {

                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    if (DBG) Log.d (TAG, "ConnectThread run() Socket close() failed", e1);
                }

                connectionFailed();

                return;
            }

            // Reset ConnectThread
            synchronized (BTDevice.this) {
                mConnectThread = null;
            }

            // Start ConnectedThread
            connected(mmSocket, mmDevice);

            if (DBG) Log.d(TAG, "END ConnectThread");
        }

        public void cancel() {
            if (DBG) Log.d(TAG, "ConnectThread cancel()");
            try {
                mmSocket.close();
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectThread cancel() Socket close() failed", e);
            }
        }
    }

//##################################################################################################

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private boolean mmConnected = false;

        public ConnectedThread(BluetoothSocket socket) {
            if (DBG) Log.d(TAG, "ConnectedThread()");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "Connected Thread() Socket getStream() failed", e);
            }

            mmConnected = true;

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            if (DBG) Log.d(TAG, "BEGIN ConnectedThread");
            setName("ConnectedThread");

            sendMessage(MESSAGE.CONNECTED, null);

            while (mmConnected) {
                try {
                    int bytes = mmInStream.available();

                    if (bytes > 0) {
                        byte[] buffer = new byte[bytes];
                        mmInStream.read(buffer);
                        sendMessage(MESSAGE.DATA_RECEIVED, buffer);
                    }
                } catch (IOException e) {
                    if (DBG) Log.d(TAG, "ConnectedThread run() inStream read() failed", e);
                    if (mmConnected) {
                        // Only report connection loss if unintentional disconnect
                        connectionLost();
                    }
                    break;
                }
            }

            if (DBG) Log.d(TAG, "END ConnectedThread");
        }

        public void write(byte[] buffer) {
            if (DBG) Log.d(TAG, "ConnectedThread write()");
            try {
                mmOutStream.write(buffer);
                sendMessage(MESSAGE.DATA_SENT, buffer);
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectedThread write() outStream write() failed", e);
            }
        }

        public void close() {
            if (DBG) Log.d(TAG, "ConnectedThread close()");

            mmConnected = false;

            cancel();
        }

        public void cancel() {
            if (DBG) Log.d(TAG, "ConnectedThread cancel()");

            try {
                mmSocket.close();
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectedThread cancel() Socket close() failed", e);
            }
        }
    }

}
