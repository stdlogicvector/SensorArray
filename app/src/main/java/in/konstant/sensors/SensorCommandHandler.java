package in.konstant.sensors;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class SensorCommandHandler
            extends HandlerThread {

    private Handler inHandler, outHandler;

    public SensorCommandHandler() {
        super("SensorCommandHandler", HandlerThread.NORM_PRIORITY);
    }

    public void setReplyHandler(final Handler replyHandler) {
        outHandler = replyHandler;
    }

    public Handler getDataHandler() {
        return inHandler;
    }

    public synchronized void waitUntilReady() {
        // getLooper blocks until Looper is prepared
        inHandler = new Handler(getLooper(), handleInMessage);
    }

    private final Handler.Callback handleInMessage = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }
    };

    @Override
    public void run() {
        super.run();


    }
}
