package in.konstant.sensors;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import in.konstant.BT.BTDevice;

public class SensorCommandHandler
            extends Thread {

    private final static String TAG = "SensorCommandHandler";
    private final static boolean DBG = true;

    private static final char CMD_START_CHAR = '{';
    private static final char CMD_DELIMITER  = '|';
    private static final char CMD_END_CHAR   = '}';

    private static final class CMD {
        public static final char GET_NO_SENSORS         = 'a';
        public static final char GET_SENSOR_INFO        = 'b';
        public static final char GET_NO_MEAS            = 'c';
        public static final char GET_SENSOR_MEAS_INFO   = 'd';
        public static final char GET_SENSOR_MEAS        = 'e';
        public static final char SET_SENSOR_RANGE       = 'f';
        public static final char SET_SENSOR_OFF         = 'g';
        public static final char SET_SENSOR_ON          = 'h';
    }

    private Handler inHandler;

    private String command;
    private StringBuilder reply;
    private ArrayList<String> arguments;

    private Boolean commandToSend;
    private Boolean replyReceived;

    private final Object commandMonitor;
    private final Object replyMonitor;

    private BTDevice btDevice;

    public SensorCommandHandler(final BTDevice btDevice) {
        setPriority(Thread.NORM_PRIORITY);

        this.btDevice = btDevice;

        inHandler = new Handler(Looper.getMainLooper(), handleInMessage);
        btDevice.setDataHandler(inHandler);

        commandToSend = false;
        replyReceived = false;

        commandMonitor = new Object();
        replyMonitor = new Object();

        reply = new StringBuilder();
        arguments = new ArrayList<String>();
    }

    public String[] sendCommand(final String command) {
        if (DBG) Log.d(TAG, "sendCommand(" + command + ")");

        this.command = command;

        synchronized (commandMonitor) {
            commandToSend = true;
            commandMonitor.notify();
        }

        synchronized (replyMonitor) {
            try {
                while (!replyReceived)
                    replyMonitor.wait();
            } catch (InterruptedException e) {
                return null;
            }

            replyReceived = false;
        }

        return arguments.toArray(new String[arguments.size()]);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (commandMonitor) {
                try {
                    while (!commandToSend)
                        commandMonitor.wait();

                    btDevice.send(command.getBytes());

                    commandToSend = false;

                } catch (InterruptedException e) {

                }
            }
        }
    }

    private final Handler.Callback handleInMessage = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == BTDevice.BTDataEvent.DATA_RECEIVED) {

                byte[] data = (byte[]) msg.obj;

                // When new data has been received, pass it to the parser
                if (parse(data)) {
                    // When the Parser has found the Stop-Char
                    synchronized (replyMonitor) {
                        // Notify about completed reply reception
                        replyReceived = true;
                        replyMonitor.notify();
                    }
                }
            }
            return true;
        }
    };

    private boolean parse(byte[] data) {
        for (int b = 0; b < data.length && data[b] != 0; ++b) {
            switch (data[b]) {
                case CMD_START_CHAR:
                    arguments.clear();
                    reply.setLength(0);
                    break;

                case CMD_DELIMITER:
                    arguments.add(reply.toString());
                    reply.setLength(0);
                    break;

                default:
                    reply.append((char)data[b]);
                    break;

                case CMD_END_CHAR:
                    arguments.add(reply.toString());
                    return true;
            }
            data[b] = 0;
        }
        return false;
    }

//--------------------------------------------

    public int getNrOfSensors() {
        String cmd = buildCommand(CMD.GET_NO_SENSORS, '\0', '\0', '\0');
        String[] result = sendCommand(cmd);

        if (result[0].equals("" + CMD.GET_NO_SENSORS)) {
            return Integer.parseInt(result[1]);
        } else
            return 0;
    }

    public ExternalSensor getSensor(final int sensorId) {
        String cmd = buildCommand(CMD.GET_SENSOR_INFO, Character.forDigit(sensorId, 10), '\0', '\0');
        String[] result = sendCommand(cmd);

        if (result[0].equals("" + CMD.GET_SENSOR_INFO)) {
            return new ExternalSensor(
                        Integer.parseInt(result[1]),
                        result[2],
                        result[3]);
        } else
            return null;
    }

    public int getNrOfMeasurements(final int sensorId) {
        String cmd = buildCommand(CMD.GET_NO_MEAS, Character.forDigit(sensorId, 10), '\0', '\0');
        String[] result = sendCommand(cmd);

        if (result[0].equals("" + CMD.GET_NO_MEAS)) {
            return Integer.parseInt(result[1]);
        } else
            return 0;
    }

    public Measurement getMeasurement(final int sensorId, final int measurementId) {
        String cmd = buildCommand(CMD.GET_SENSOR_MEAS_INFO,
                                  Character.forDigit(sensorId, 10),
                                  Character.forDigit(measurementId, 10),
                                  '\0');

        String[] result = sendCommand(cmd);

        if (result[0].equals("" + CMD.GET_SENSOR_MEAS_INFO )) {
//            return new Measurement();
            //TODO: Create Measurement
            return null;
        } else
            return null;
    }


//--------------------------------------------

    private static String buildCommand(final char id, final char arg1, final char arg2, final char arg3) {
        StringBuilder cmd = new StringBuilder();

        cmd.append(CMD_START_CHAR);
        cmd.append(id);

        if (arg1 != 0) {
            cmd.append(CMD_DELIMITER);
            cmd.append(arg1);

            if (arg2 != 0) {
                cmd.append(CMD_DELIMITER);
                cmd.append(arg2);

                if (arg3 != 0) {
                    cmd.append(CMD_DELIMITER);
                    cmd.append(arg3);
                }
            }
        }

        cmd.append(CMD_END_CHAR);
        cmd.append(' ');

        return cmd.toString();
    }

}
