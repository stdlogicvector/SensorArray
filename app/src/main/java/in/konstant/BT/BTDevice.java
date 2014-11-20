package in.konstant.BT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BTDevice {
    private static final String TAG = "BTDevice";
    private static final boolean DBG = false;

    // Standard Serial Port UUID
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public static final class BTStateEvent {
        public static final int CONNECTION_FAILED = -3;
        public static final int CONNECTION_LOST = -2;
        public static final int DISCONNECTED = -1;
        public static final int CONNECTING = 0;
        public static final int CONNECTED = 1;
    }

    public static final class BTDataEvent {
        public static final int DATA_SENT = 1;
        public static final int DATA_RECEIVED = 2;
    }

    private Handler mStateHandler;
    private Handler mDataHandler;

    private String mAddress;
    private boolean mConnected;
    private boolean mConnecting;

    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothDevice mBluetoothDevice;

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public BTDevice(final String address) {
        if (DBG) Log.d(TAG, "BTDevice(" + address + ")");

        mAddress = address;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mAddress);

        mConnected = false;
        mConnecting = false;
    }

    public void setStateHandler(final Handler stateHandler) {
        this.mStateHandler = stateHandler;
    }

    public void setDataHandler(final Handler dataHandler) {
        this.mDataHandler = dataHandler;
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

        mConnected = false;
        mConnecting = false;
    }

    // Interface -----------------------------------------------------------------------------------

    public void connect() {
        if (DBG) Log.d(TAG, "connect(" + mAddress + ")");

        if (mConnecting) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
                mConnecting = false;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(mBluetoothDevice);
        mConnectThread.start();

        if (mStateHandler != null)
            mStateHandler.sendEmptyMessage(BTStateEvent.CONNECTING);

        mConnecting = true;
    }

    public void send(byte[] data) {
        if (DBG) Log.d(TAG, "send()");
        ConnectedThread ct;

        synchronized (this) {
            if (!mConnected) return;
            ct = mConnectedThread;
        }

        ct.write(data);
    }

    public void disconnect() {
        if (DBG) Log.d(TAG, "disconnect()");

        if (mConnecting) {
            if (mConnectThread != null)
            {
                mConnectThread.cancel();
                mConnectThread = null;
                mConnecting = false;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.close();
            mConnectedThread = null;
        }

        if (mConnected) {
            if (mStateHandler != null)
                mStateHandler.sendEmptyMessage(BTStateEvent.DISCONNECTED);
        }

        mConnected = false;
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
        return mConnected;
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

        mConnected = true;
        mConnecting = false;
    }

    private void connectionLost() {
        if (DBG) Log.d(TAG, "connectionLost()");
        if (mStateHandler != null)
            mStateHandler.sendEmptyMessage(BTStateEvent.CONNECTION_LOST);
        mConnected = false;
    }

    private void connectionFailed() {
        if (DBG) Log.d(TAG, "connectionFailed()");
        if (mStateHandler != null)
            mStateHandler.sendEmptyMessage(BTStateEvent.CONNECTION_FAILED);
        mConnected = false;
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

            if (mStateHandler != null)
                mStateHandler.sendEmptyMessage(BTStateEvent.CONNECTED);

            while (mmConnected) {
                try {
                    int bytes = mmInStream.available();

                    if (bytes > 0) {
                        byte[] buffer = new byte[bytes];
                        mmInStream.read(buffer);

                        if (mDataHandler != null) {
                            mDataHandler.sendMessage(
                                    mDataHandler.obtainMessage(BTDataEvent.DATA_RECEIVED, buffer)
                            );
                        }
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

                /*
                if (mDataHandler != null) {
                    mDataHandler.sendMessage(
                            mDataHandler.obtainMessage(BTDataEvent.DATA_SENT, buffer)
                    );
                }
                */

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
