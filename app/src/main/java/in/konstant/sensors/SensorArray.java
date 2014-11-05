package in.konstant.sensors;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class SensorArray
        extends Observable
        implements SensorDeviceEventListener {

    private final static String TAG = "SensorArray";
    private final static boolean DBG = true;

    private static SensorArray instance;

    private ArrayList<SensorArrayEventListener> mStateListeners;

    private ArrayList<String> ids;
    private HashMap<String, SensorDevice> devices;

    private SensorArray() {
        devices = new HashMap<String, SensorDevice>();
        ids = new ArrayList<String>();
        mStateListeners = new ArrayList<SensorArrayEventListener>();
    }

    public static SensorArray getInstance() {
        if (instance == null) {
            instance = new SensorArray();
        }
        return instance;
    }

    public boolean registerStateListener(SensorArrayEventListener listener) {
        if (DBG) Log.d(TAG, "registerStateListener");
        return mStateListeners.add(listener);
    }

    public boolean unregisterStateListener(SensorArrayEventListener listener) {
        if (DBG) Log.d(TAG, "unregisterStateListener");
        return mStateListeners.remove(listener);
    }

    private void notifySensorArrayEvent(final SensorDevice device, final int event) {
        for (SensorArrayEventListener listener : mStateListeners) {
            listener.onSensorArrayEvent(device, event);
        }
    }

    public void onSensorDeviceEvent(final SensorDevice device, final int event) {
        // Connection State of a SensorDevice has changed
        notifySensorArrayEvent(device, event);

        // Notify Observers to initiate redrawing of lists
        changed();
    }

    private void changed() {
        setChanged();
        notifyObservers();
    }

    public int count() {
        return ids.size();
    }

    public void clear() {
        // TODO: Delete Internal Sensors, too?
        devices.clear();
        ids.clear();
    }

    public void removeDevice(final String address) {
        SensorDevice device = devices.get(address);

        device.unregisterStateListener(this);
        notifySensorArrayEvent(device, SensorEvent.REMOVED);

        devices.remove(address);
        ids.remove(ids.indexOf(address));

        changed();
    }

    public void removeDevice(final int id) {
        SensorDevice device = devices.get(ids.get(id));

        device.unregisterStateListener(this);
        notifySensorArrayEvent(device, SensorEvent.REMOVED);

        devices.remove(ids.get(id));
        ids.remove(id);

        changed();
    }

    public void addDevice(final SensorDevice device) {
        devices.put(device.getBluetoothAddress(), device);
        ids.add(device.getBluetoothAddress());

        device.registerStateListener(this);
        notifySensorArrayEvent(device, SensorEvent.ADDED);

        changed();
    }

    public void addDevice(final String address) {
        addDevice(new ExternalSensorDevice(address));
    }

    public void addDevice(final String address, final String name) {
        SensorDevice newDevice = new ExternalSensorDevice(address);
        newDevice.setDeviceName(name);
        addDevice(newDevice);
    }

    public boolean containsDevice(final String address) {
        return devices.containsKey(address);
    }

    public SensorDevice getDevice(final int id) {
        return getDevice(ids.get(id));
    }

    public SensorDevice getDevice(final String address) {
        return devices.get(address);
    }

    public void load(final Context applicationContext) {
        // Internal Device
        addDevice(new InternalSensorDevice(applicationContext));
        getDevice(0).initialize();

        changed();
    }

    public void save() {

    }

}
