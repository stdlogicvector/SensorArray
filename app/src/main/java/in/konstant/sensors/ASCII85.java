package in.konstant.sensors;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ASCII85 {
    public static String encode(int data) {
        //TODO: ASCII85 encoding not yet implemented
        return null;
    }

    public static int decodeToInt(String encoded) {
        int value = 0;
        int m = 1;

        for (int i = 4; i >= 0; i--) {
            value += (encoded.charAt(i) - '!') * m;
            m *= 85;
        }

        return value;
    }

    public static float decodeToFloat(String encoded) {
        int value = decodeToInt(encoded);

        ByteBuffer buf = ByteBuffer.allocate(4)
                                   .order(ByteOrder.LITTLE_ENDIAN)
                                   .putInt(value);

        buf.rewind();

        return buf.getFloat();
    }
}
