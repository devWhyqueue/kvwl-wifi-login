package de.whyqueue.kvwlwifilogin.model;

import android.net.wifi.WifiManager;

import de.whyqueue.kvwlwifilogin.model.exception.WifiAuthenticationException;
import de.whyqueue.kvwlwifilogin.model.exception.WifiConnectionException;

public class WifiClient {

    private WifiConnection wifiConnection;
    private WifiAuthentication wifiAuthentication;

    public WifiClient(WifiManager wifiManager, Credentials credentials) {
        this.wifiConnection = new WifiConnection(wifiManager);
        this.wifiAuthentication = new WifiAuthentication(credentials);
    }

    public void connect() throws WifiConnectionException, WifiAuthenticationException {
        wifiConnection.connectToWifi();
        // TODO: WAIT FOR WIFI
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wifiAuthentication.login();
    }

    public void disconnect() throws WifiConnectionException, WifiAuthenticationException {
        wifiAuthentication.logout();
        wifiConnection.disconnectFromWifi();
    }
}
