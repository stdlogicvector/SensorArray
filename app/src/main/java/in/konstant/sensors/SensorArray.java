package in.konstant.sensors;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class SensorArray extends Observable {

    private static SensorArray instance;

    private ArrayList<String> ids;
    private HashMap<String, SensorDevice> devices;

    private SensorArray() {
        devices = new HashMap<String, SensorDevice>();
        ids = new ArrayList<String>();
    }

    public static SensorArray getInstance() {
        if (instance == null) {
            instance = new SensorArray();
        }
        return instance;
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
        devices.remove(address);
        ids.remove(ids.indexOf(address));
        changed();
    }

    public void removeDevice(final int id) {
        devices.remove(ids.get(id));
        ids.remove(id);
        changed();
    }

    public void addDevice(final SensorDevice device) {
        devices.put(device.getBluetoothAddress(), device);
        ids.add(device.getBluetoothAddress());
        changed();
    }

    public void addDevice(final String address) {
        addDevice(new ExternalSensorDevice(address));
        changed();
    }

    public void addDevice(final String address, final String name) {
        SensorDevice newDevice = new ExternalSensorDevice(address);
        newDevice.setDeviceName(name);
        addDevice(newDevice);
        changed();
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
