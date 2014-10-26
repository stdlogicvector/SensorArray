package in.konstant.sensors;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import in.konstant.R;

public class SensorArray extends BaseExpandableListAdapter {
    private static final String TAG = "SensorArray";
    private static final boolean DBG = true;

    private static final String PREFS_NAME = "SensorArray";
    private static final String DEVICE_ADDRESSES = "DeviceAddresses";

    private static SensorArray instance;

    static class DeviceViewHolder {
        public ImageView icSensorDeviceIcon;
        public TextView tvSensorDeviceName;
        public TextView tvSensorDeviceAddress;
        public TextView tvSensorDeviceSensors;
    }

    static class SensorViewHolder {
        public ImageView icSensorIcon;
        public TextView tvSensorName;
        public TextView tvSensorPart;
        public TextView tvSensorMeasurements;
    }

    private final Context context;

    private ArrayList<String> ids;
    private HashMap<String, SensorDevice> devices;

    private SensorArray(Context context) {
        devices = new HashMap<String, SensorDevice>();
        ids = new ArrayList<String>();

        // Add internal SensorDevice
        addDevice(new InternalSensorDevice(context));
        getDevice(0).initialize();

        this.context = context;
    }

    public static SensorArray getInstance(Context context) {
        if (instance == null) {
            instance = new SensorArray(context);
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
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.navdrawer_item_group, null);

            DeviceViewHolder deviceViewHolder = new DeviceViewHolder();
            deviceViewHolder.icSensorDeviceIcon = (ImageView) convertView.findViewById(R.id.icSensorDeviceIcon);
            deviceViewHolder.tvSensorDeviceName = (TextView) convertView.findViewById(R.id.tvSensorDeviceName);
            deviceViewHolder.tvSensorDeviceAddress = (TextView) convertView.findViewById(R.id.tvSensorDeviceAddress);
            deviceViewHolder.tvSensorDeviceSensors = (TextView) convertView.findViewById(R.id.tvSensorDeviceSensors);

            convertView.setTag(deviceViewHolder);
        }

        DeviceViewHolder holder = (DeviceViewHolder) convertView.getTag();
        SensorDevice item = getGroup(id);

        holder.tvSensorDeviceName.setText(item.getDeviceName());
        holder.tvSensorDeviceAddress.setText(item.getBluetoothAddress());

        if (item.isConnected()) {
            holder.icSensorDeviceIcon.setImageResource(R.drawable.ic_device_connected);
            holder.tvSensorDeviceSensors.setText("" + item.getNumberOfSensors());
        } else {
            holder.icSensorDeviceIcon.setImageResource(R.drawable.ic_device_disconnected);
            holder.tvSensorDeviceSensors.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int id,
                             final int childId,
                             boolean isLastChild,
                             View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.navdrawer_item_child, null);

            SensorViewHolder sensorViewHolder = new SensorViewHolder();
            sensorViewHolder.icSensorIcon = (ImageView) convertView.findViewById(R.id.icSensorIcon);
            sensorViewHolder.tvSensorName = (TextView) convertView.findViewById(R.id.tvSensorName);
            sensorViewHolder.tvSensorPart = (TextView) convertView.findViewById(R.id.tvSensorPart);
            sensorViewHolder.tvSensorMeasurements = (TextView) convertView.findViewById(R.id.tvSensorMeasurements);

            convertView.setTag(sensorViewHolder);
        }

        SensorViewHolder holder = (SensorViewHolder) convertView.getTag();
        Sensor item = getGroup(id).getSensor(childId);

        holder.tvSensorName.setText(item.getName());
        holder.tvSensorPart.setText(item.getPart());
        holder.tvSensorMeasurements.setText("" + item.getNumberOfMeasurements());

        int imageResource;

        switch (item.getType()) {
            default: imageResource = R.drawable.ic_generic; break;
            case ADC: imageResource = R.drawable.ic_adc; break;
            case AUDIO : imageResource = R.drawable.ic_audio; break;
            case COMPASS: imageResource = R.drawable.ic_compass; break;
            case COLOR: imageResource = R.drawable.ic_color; break;
            case DISTANCE: imageResource = R.drawable.ic_distance; break;
            case GAS: imageResource = R.drawable.ic_gas; break;
            case HUMIDITY: imageResource = R.drawable.ic_humidity; break;
            case LIGHT: imageResource = R.drawable.ic_light; break;
            case MAGNETIC: imageResource = R.drawable.ic_magnetic; break;
            case POLARISATION: imageResource = R.drawable.ic_polarisation; break;
            case PRESSURE: imageResource = R.drawable.ic_pressure; break;
            case RADIATION: imageResource = R.drawable.ic_radiation; break;
            case ROTATION: imageResource = R.drawable.ic_rotation; break;
            case SPATIAL: imageResource = R.drawable.ic_spatial; break;
            case TEMPERATURE: imageResource = R.drawable.ic_temperature; break;
            case THERMAL: imageResource = R.drawable.ic_infrared; break;
        }

        holder.icSensorIcon.setImageResource(imageResource);

        return convertView;
    }
}
