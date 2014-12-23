package in.konstant.BT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BTDevice {
    private static final String TAG = "BTDevice";
    private static final boolean DBG = true;

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

    private Handler stateHandler;
    private Handler dataHandler;

    private String address;
    private boolean connected;
    private boolean connecting;

    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothDevice bluetoothDevice;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    public BTDevice(final String address) {
        if (DBG) Log.d(TAG, "BTDevice(" + address + ")");

        this.address = address;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(this.address);

        connected = false;
        connecting = false;
    }

    public void setStateHandler(final Handler stateHandler) {
        this.stateHandler = stateHandler;
    }

    public void setDataHandler(final Handler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public void destroy() {
        if (DBG) Log.d(TAG, "destroy()");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connected = false;
        connecting = false;
    }

    // Interface -----------------------------------------------------------------------------------

    public void connect() {
        if (DBG) Log.d(TAG, "connect(" + address + ")");

        if (connecting) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
                connecting = false;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();

        if (stateHandler != null)
            stateHandler.sendEmptyMessage(BTStateEvent.CONNECTING);

        connecting = true;
    }

    public boolean send(byte[] data) {
        if (DBG) Log.d(TAG, "send()");
        ConnectedThread ct;

        synchronized (this) {
            if (!connected) return false;
            ct = connectedThread;
        }

        ct.write(data);

        return true;
    }

    public void disconnect() {
        if (DBG) Log.d(TAG, "disconnect()");

        if (connecting) {
            if (connectThread != null)
            {
                connectThread.cancel();
                connectThread = null;
                connecting = false;
            }
        }

        if (connectedThread != null) {
            connectedThread.close();
            connectedThread = null;
        }

        if (connected) {
            if (stateHandler != null)
                stateHandler.sendEmptyMessage(BTStateEvent.DISCONNECTED);
        }

        connected = false;
    }

    // Setter & Getter -----------------------------------------------------------------------------

    public BluetoothClass getBluetoothClass() {
        return bluetoothDevice.getBluetoothClass();
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    public UUID[] getUUIDs() {
        ParcelUuid[] parcels = bluetoothDevice.getUuids();

        ArrayList<UUID> list = new ArrayList<UUID>();

        for (ParcelUuid parcel : parcels) {
            list.add(parcel.getUuid());
        }

        return list.toArray(new UUID[list.size()]);
    }

    public boolean isConnected() {
        return connected;
    }

    // State Changers ------------------------------------------------------------------------------

    private void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (DBG) Log.d(TAG, "connected(" + socket + ", " + device + ")");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        connected = true;
        connecting = false;
    }

    private void connectionLost() {
        if (DBG) Log.d(TAG, "connectionLost()");
        if (stateHandler != null)
            stateHandler.sendEmptyMessage(BTStateEvent.CONNECTION_LOST);
        connected = false;
    }

    private void connectionFailed() {
        if (DBG) Log.d(TAG, "connectionFailed()");
        if (stateHandler != null)
            stateHandler.sendEmptyMessage(BTStateEvent.CONNECTION_FAILED);
        connected = false;
    }

    // Threads -------------------------------------------------------------------------------------

    private class ConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device) {
            if (DBG) Log.d(TAG, "ConnectThread()");
            bluetoothDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectThread() Socket create() failed", e);
            }

            bluetoothSocket = tmp;
        }

        public void run() {
            if (DBG) Log.d(TAG, "BEGIN ConnectThread");
            setName("ConnectThread");

            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
            } catch (IOException e) {

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    if (DBG) Log.d (TAG, "ConnectThread run() Socket close() failed", e1);
                }

                connectionFailed();

                return;
            }

            // Reset ConnectThread
            synchronized (BTDevice.this) {
                connectThread = null;
            }

            // Start ConnectedThread
            connected(bluetoothSocket, bluetoothDevice);

            if (DBG) Log.d(TAG, "END ConnectThread");
        }

        public void cancel() {
            if (DBG) Log.d(TAG, "ConnectThread cancel()");
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectThread cancel() Socket close() failed", e);
            }
        }
    }

// ##################################################################################################

    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        private boolean connected = false;

        public ConnectedThread(BluetoothSocket socket) {
            if (DBG) Log.d(TAG, "ConnectedThread()");

            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "Connected Thread() Socket getStream() failed", e);
            }

            connected = true;

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            if (DBG) Log.d(TAG, "BEGIN ConnectedThread");
            setName("ConnectedThread");

            if (stateHandler != null)
                stateHandler.sendEmptyMessage(BTStateEvent.CONNECTED);

            while (connected) {
                try {
                    if (!bluetoothSocket.isConnected()) {
                        if (DBG) Log.d(TAG, "ConnectedThread run() Socket disconnected");
                        connectionLost();
                        break;
                    }

                    int bytes = inputStream.available();

                    if (bytes > 0) {
                        byte[] buffer = new byte[bytes];
                        inputStream.read(buffer);

                        if (dataHandler != null) {
                            dataHandler.sendMessage(
                                    dataHandler.obtainMessage(BTDataEvent.DATA_RECEIVED, buffer)
                            );
                        }
                    }
                } catch (IOException e) {
                    if (DBG) Log.d(TAG, "ConnectedThread run() inStream read() failed", e);
                    if (connected) {
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
                outputStream.write(buffer);

                /*
                if (dataHandler != null) {
                    dataHandler.sendMessage(
                            dataHandler.obtainMessage(BTDataEvent.DATA_SENT, buffer)
                    );
                }
                */

            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectedThread write() outStream write() failed", e);
                connected = false;
                connectionLost();
            }
        }

        public void close() {
            if (DBG) Log.d(TAG, "ConnectedThread close()");

            connected = false;

            cancel();
        }

        public void cancel() {
            if (DBG) Log.d(TAG, "ConnectedThread cancel()");

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                if (DBG) Log.d(TAG, "ConnectedThread cancel() Socket close() failed", e);
            }
        }
    }

}
