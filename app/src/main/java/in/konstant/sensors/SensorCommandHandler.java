package in.konstant.sensors;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import in.konstant.BT.BTDevice;

public class SensorCommandHandler
            extends HandlerThread {

    private final static String TAG = "SensorCommandHandler";
    private final static boolean DBG = true;

    private static final char CMD_START_CHAR = '{';
    private static final char CMD_DELIMITER  = '|';
    private static final char CMD_END_CHAR   = '}';

    public static final class CMD {
        public static final char GET_NO_SENSORS = 'a';
        public static final char GET_SENSOR_INFO = 'b';
        public static final char GET_SENSOR_MEAS_INFO = 'c';
        public static final char GET_SENSOR_MEAS = 'd';

        public static final char SET_SENSOR_RANGE = 'e';
        public static final char SET_SENSOR_OFF = 'f';
        public static final char SET_SENSOR_ON = 'g';
    }

    private Handler inHandler;

    private String command;
    private StringBuilder reply;
    private Boolean commandToSend;
    private Boolean replyReceived;

    private final Object commandMonitor;
    private final Object replyMonitor;

    private BTDevice btDevice;

    public SensorCommandHandler(final BTDevice btDevice) {
        super("SensorCommandHandler", HandlerThread.NORM_PRIORITY);

        this.btDevice = btDevice;

        commandToSend = false;
        replyReceived = false;

        commandMonitor = new Object();
        replyMonitor = new Object();

        reply = new StringBuilder();
    }

    public synchronized void waitUntilReady() {
        // getLooper blocks until Looper is prepared
        inHandler = new Handler(getLooper(), handleInMessage);

        btDevice.setDataHandler(inHandler);
    }

    public String sendCommand(final String command) {
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

            return reply.toString();
        }
    }

    @Override
    public void run() {
        super.run(); //TODO: Blocks :(
        if (DBG) Log.d(TAG, "running...");

        while (!isInterrupted()) {
            synchronized (commandMonitor) {
                try {
                    while (!commandToSend)
                        commandMonitor.wait();

                    btDevice.send(command.getBytes());

                } catch (InterruptedException e) {

                }
            }
        }
    }

    private final Handler.Callback handleInMessage = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == BTDevice.BTDataEvent.DATA_RECEIVED) {
                if (DBG) Log.d(TAG, "received = " + (byte[]) msg.obj);
                // When new data has been received, pass it to the parser
                if (parse((byte[])msg.obj)) {
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
                    reply.setLength(0);
                    break;

                default:
                    reply.append(data[b]);
                    break;

                case CMD_END_CHAR:
                    return true;
            }
            data[b] = 0;
        }
        return false;
    }

    public static String buildCommand(final char id) {
        return buildCommand(id, '\0', '\0', '\0');
    }

    public static String buildCommand(final char id, final char arg1) {
        return buildCommand(id, arg1, '\0', '\0');
    }

    public static String buildCommand(final char id, final char arg1, final char arg2) {
        return buildCommand(id, arg1, arg2, '\0');
    }

    public static String buildCommand(final char id, final char arg1, final char arg2, final char arg3) {
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
        cmd.append(10);

        return cmd.toString();
    }

}
