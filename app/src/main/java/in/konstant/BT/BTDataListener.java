package in.konstant.BT;

public interface BTDataListener {
    public void onDataReceived(byte[] data);
    public void onDataSent(byte[] data);
}
