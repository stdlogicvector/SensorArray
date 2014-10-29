package in.konstant.BT;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.konstant.R;

public class BTDeviceListAdapter extends BaseAdapter {

    private class BTDeviceListEntry {
        public final String name;
        public final String type;

        public BTDeviceListEntry(final String name, final String type) {
            this.name = name;
            this.type = type;
        }
    }

    private Context context;

    private ArrayList<String> ids;
    private HashMap<String, BTDeviceListEntry> values;

    public BTDeviceListAdapter(final Context context) {
        super();
        this.context = context;

        ids = new ArrayList<String>();
        values = new HashMap<String, BTDeviceListEntry>();
    }

    public void add(final String name, final String type, final String address) {
        if (!values.containsKey(address)) {
            values.put(address, new BTDeviceListEntry(name, type));
            ids.add(address);
        }
    }

    public void remove(final String address) {
        if (values.containsKey(address)) {
            values.remove(address);
            ids.remove(ids.indexOf(address));
        }
    }

    public void clear() {
        values.clear();
        ids.clear();
    }

    public int getCount() {
        return ids.size();
    }

    public BTDeviceListEntry getItem(final int id) {
        if (values.containsKey(ids.get(id)))
            return values.get(ids.get(id));
        else
            return null;
    }

    public long getItemId(final int id) {
        return id;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.arrayadapter_btdevicelist, null);
        }

        ((TextView) convertView.findViewById(R.id.tvBTDeviceName)).
                setText(values.get(ids.get(position)).name);

        ((TextView) convertView.findViewById(R.id.tvBTDeviceType)).
                setText(values.get(ids.get(position)).type);

        ((TextView) convertView.findViewById(R.id.tvBTDeviceAddress)).
                setText(ids.get(position));

        return convertView;
    }
}
