package in.konstant.sensors;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SensorArray extends BaseExpandableListAdapter {
    private static final String TAG = "SensorArray";
    private static final boolean DBG = true;

    private static final String PREFS_NAME = "SensorArray";
    private static final String DEVICE_ADDRESSES = "DeviceAddresses";

    private static SensorArray instance;

    private final Activity activity;

    private ArrayList<String> ids;
    private HashMap<String, SensorDevice> devices;

    private SensorArray(Activity activity) {
        devices = new HashMap<String, SensorDevice>();
        ids = new ArrayList<String>();

        this.activity = activity;
    }

    private static SensorArray getInstance(Activity activity) {
        if (instance == null) {
            instance = new SensorArray(activity);
        }
        return instance;
    }

    @Override
    public SensorDevice getGroup(int groupId) {
        return getDevice(groupId);
    }

    public SensorDevice getDevice(int id) {
        return getDevice(ids.get(id));
    }

    public SensorDevice getDevice(String address) {
        return devices.get(address);
    }

    public Sensor getChild(int groupId, int childId) {
        return getGroup(groupId).getSensor(childId);
    }

    @Override
    public long getGroupId(int groupId) {
        return groupId;
    }

    @Override
    public long getChildId(int groupId, int childId) {
        return childId;
    }

    @Override
    public int getGroupCount() {
        return ids.size();
    }

    @Override
    public int getChildrenCount(int groupId) {
        return getGroup(groupId).getNumberOfSensors();
    }

    @Override
    public boolean isChildSelectable(int groupId, int childId) {
        return true;    //TODO: Only if connected?
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public Set<String> getKeySet() {
        return devices.keySet();
    }

    public void clear() {
        devices.clear();
        ids.clear();
        notifyDataSetInvalidated();
    }

    public void addDevice(SensorDevice device) {
        devices.put(device.getBluetoothAddress(), device);
        ids.add(device.getBluetoothAddress());
        notifyDataSetChanged();
    }

    public void removeDevice(String address) {
        devices.remove(address);
        ids.remove(ids.indexOf(address));
        notifyDataSetChanged();
    }

    public void removeDevice(int id) {
        devices.remove(ids.get(id));
        ids.remove(id);
        notifyDataSetChanged();
    }

    public boolean containsDevice(String address) {
        return devices.containsKey(address);
    }

    @Override
    public View getGroupView(int id,
                             boolean isExpanded,
                             View convertView,
                             ViewGroup parent) {

        return convertView;
    }

    @Override
    public View getChildView(int id,
                             final int childId,
                             boolean isLastChild,
                             View convertView,
                             ViewGroup parent) {

        return convertView;
    }
}
