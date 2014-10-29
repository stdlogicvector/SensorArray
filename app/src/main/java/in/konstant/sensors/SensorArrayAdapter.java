package in.konstant.sensors;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import in.konstant.R;

public class SensorArrayAdapter
        extends BaseExpandableListAdapter
        implements Observer {

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
    private final SensorArray sensorArray;

    private int selectedGroupId;
    private int selectedChildId;

    public SensorArrayAdapter(final Context context) {
        this.context = context;

        sensorArray = SensorArray.getInstance();
        sensorArray.addObserver(this);
    }

    public void selectGroup(final int groupId) {
        selectedGroupId = groupId;
        selectedChildId = -1;

        notifyDataSetChanged();
    }

    public void selectChild(final int groupId, final int childId) {
        selectedGroupId = groupId;
        selectedChildId = childId;

        notifyDataSetChanged();
    }

    @Override
    public SensorDevice getGroup(final int groupId) {
        return sensorArray.getDevice(groupId);
    }

    @Override
    public Sensor getChild(final int groupId, final int childId) {
        return getGroup(groupId).getSensor(childId);
    }

    @Override
    public long getGroupId(final int groupId) {
        return groupId;
        //return ((long) groupId << 32) + 0;
    }

    @Override
    public long getChildId(final int groupId, final int childId) {
        return childId;
        //return ((long) groupId << 32) + childId;
    }

    @Override
    public int getGroupCount() {
        return sensorArray.count();
    }

    @Override
    public int getChildrenCount(final int groupId) {
        return getGroup(groupId).getNumberOfSensors();
    }

    @Override
    public boolean isChildSelectable(final int groupId, final int childId) {
        return getGroup(groupId).isConnected(); // Children only selectable if Connected
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void update(Observable observable, Object data) {
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(final int id,
                             final boolean isExpanded,
                             View convertView,
                             final ViewGroup parent) {
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

        if (selectedChildId == -1 && selectedGroupId == id)
            convertView.setBackgroundResource(R.drawable.nav_list_item_selected);
        else
            convertView.setBackgroundResource(R.drawable.nav_list_item_normal);

        return convertView;
    }

    @Override
    public View getChildView(final int id,
                             final int childId,
                             final boolean isLastChild,
                             View convertView,
                             final ViewGroup parent) {

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
        Sensor item = getChild(id, childId);

        holder.tvSensorName.setText(item.getName());
        holder.tvSensorPart.setText(item.getPart());
        holder.tvSensorMeasurements.setText("" + item.getNumberOfMeasurements());
        holder.icSensorIcon.setImageResource(item.getType().icon());

        if (selectedChildId == childId && selectedGroupId == id)
            convertView.setBackgroundResource(R.drawable.nav_list_item_selected);
        else
            convertView.setBackgroundResource(R.drawable.nav_list_item_normal);

        return convertView;
    }
}
