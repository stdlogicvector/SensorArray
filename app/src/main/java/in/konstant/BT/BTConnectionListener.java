package in.konstant.BT;

public interface BTConnectionListener {
    public void onConnected();
    public void onConnecting();
    public void onDisconnected();
    public void onConnectionLost();
    public void onConnectionFailed();
}

