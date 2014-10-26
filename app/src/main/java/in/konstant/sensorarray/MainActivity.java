package in.konstant.sensorarray;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import in.konstant.BT.BTControl;
import in.konstant.R;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    MenuItem miBluetooth;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;    // Last screen title. For use in {@link #restoreActionBar()}.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (!BTControl.available()) {
            Toast.makeText(this, R.string.toast_no_bt, Toast.LENGTH_LONG).show();
        } else {
            BTControl.registerStateChangeReceiver(this, BTStateChangeReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        BTControl.unregisterStateChangeReceiver(this, BTStateChangeReceiver);
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int group, int child) {
        // update the main content by replacing fragments

        //TODO: Fragment Classes for Device, Sensor, etc. Views
        //TODO: Child = -1 -> Device View

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(group, child))
                .commit();
    }

    public void onSectionAttached(int device, int sensor) {
        mTitle = "Device " + device + " Sensor " + sensor;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);

            miBluetooth = menu.findItem(R.id.action_bluetooth);
            miBluetooth.setEnabled(BTControl.available());
            setBTIcon(BTControl.enabled());

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_bluetooth:
                if (BTControl.enabled()) {
                    BTControl.disable();
                } else {
                    BTControl.enable(this);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final BroadcastReceiver BTStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        setBTIcon(true);
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        setBTIcon(false);
                        break;
                }
            }
        }
    };

    private void setBTIcon(boolean state) {
        if (state) {
            miBluetooth.setIcon(R.drawable.ai_bluetooth_connected);
        } else {
            miBluetooth.setIcon(R.drawable.ai_bluetooth);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        // The fragment argument representing the section number for this fragment.
        private static final String ARG_DEVICE_NUMBER = "device_number";
        private static final String ARG_SENSOR_NUMBER = "sensor_number";

        // Returns a new instance of this fragment for the given section number.
        public static PlaceholderFragment newInstance(int deviceNumber, int sensorNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_DEVICE_NUMBER, deviceNumber);
            args.putInt(ARG_SENSOR_NUMBER, sensorNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_DEVICE_NUMBER),
                    getArguments().getInt(ARG_SENSOR_NUMBER));
        }
    }

}
