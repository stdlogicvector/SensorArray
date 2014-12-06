package in.konstant.sensors;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ASCII85 {
    public static String encodeFromInt(int value) {
        char[] encoded = new char[5];

        for (int i = 4; i >= 0; i--) {
            encoded[i] = (char) ((value % 85) + 33);
            value /= 85;
        }

        return new String(encoded);
    }

    public static String encodeFromFloat(float value) {
        ByteBuffer buf = ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putFloat(value);

        buf.rewind();

        return encodeFromInt(buf.getInt());
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
