package de.whyqueue.kvwlwifilogin.model;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import de.whyqueue.kvwlwifilogin.model.exception.WifiAuthenticationException;
import de.whyqueue.kvwlwifilogin.model.exception.WifiConnectionException;

public class WifiClient {

    private WifiConnection wifiConnection;
    private WifiAuthentication wifiAuthentication;

    public WifiClient(WifiManager wifiManager, ConnectivityManager connectivityManager, Credentials credentials) {
        this.wifiConnection = new WifiConnection(wifiManager, connectivityManager);
        this.wifiAuthentication = new WifiAuthentication(credentials);
    }

    public void connect() throws WifiConnectionException, WifiAuthenticationException {
        wifiConnection.connectToWifi();
        wifiAuthentication.login();
    }

    public void disconnect() throws WifiConnectionException, WifiAuthenticationException {
        wifiAuthentication.logout();
        wifiConnection.disconnectFromWifi();
    }
}
