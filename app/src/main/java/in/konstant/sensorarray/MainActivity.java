package in.konstant.sensorarray;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import in.konstant.BT.BTControl;
import in.konstant.BT.BTDeviceList;
import in.konstant.R;
import in.konstant.sensors.SensorArray;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                   SensorDeviceListDialog.SensorDeviceListDialogListener {

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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        SensorArray.getInstance(this).saveDevices();
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
                .replace(R.id.container, SensorFragment.newInstance(group, child))
                .commit();
    }

    public void onFragmentCreated(String title) {
        mTitle = title;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BTDeviceList.REQ_DEVICE_LIST && resultCode == Activity.RESULT_OK) {
            String address = data.getExtras().getString(BTDeviceList.EXTRA_DEVICE_ADDRESS);
            String name = data.getExtras().getString(BTDeviceList.EXTRA_DEVICE_NAME);

            SensorArray sensorArray = SensorArray.getInstance(this);

            if (address != null) {
                if (sensorArray.containsDevice(address)) {
                    Toast.makeText(this, R.string.toast_device_already_on_list, Toast.LENGTH_SHORT).show();
                } else {
                    sensorArray.addDevice(address, name);
                }
            }
        }
    }

    @Override
    public void onSensorDeviceListDialogConnect(int id, boolean connected) {
        SensorArray sensorArray = SensorArray.getInstance(this);

        if (connected) {
            sensorArray.getGroup(id).disconnect();
        } else {
            if (BTControl.enabled()) {
                sensorArray.getGroup(id).connect();
            }
        }
    }

    @Override
    public void onSensorDeviceListDialogSettings(int id) {

    }

    @Override
    public void onSensorDeviceListDialogDelete(int id) {
        final int deviceId = id; // For Access from inner Class
        final SensorArray sensorArray = SensorArray.getInstance(this);

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getResources().getString(
                        R.string.dialog_delete_message,
                        sensorArray.getGroup(id).getDeviceName()))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sensorArray.getGroup(deviceId).quit();
                        sensorArray.removeDevice(deviceId);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
