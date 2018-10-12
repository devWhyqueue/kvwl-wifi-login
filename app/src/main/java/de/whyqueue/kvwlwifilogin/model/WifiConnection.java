package de.whyqueue.kvwlwifilogin.model;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;
import java.util.Optional;

import de.whyqueue.kvwlwifilogin.model.exception.WifiConnectionException;

public class WifiConnection {
    private static final String SSID = "gast";

    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;

    public WifiConnection(WifiManager wifiManager, ConnectivityManager connectivityManager) {
        this.wifiManager = wifiManager;
        this.connectivityManager = connectivityManager;
    }

    public void connectToWifi() throws WifiConnectionException {
        if (!wifiConfigurationExists()) {
            addWifiConfiguration();
        }

        enableWifi();
        connect();
        waitForConnection();
    }

    private void waitForConnection() throws WifiConnectionException {
        int c = 0;
        while(!isConnected()){
            if(c++ > 10){
                throw new WifiConnectionException("Connection to network " + SSID + " failed!");
            }
            wait(1);
        }
    }

    private void wait(int seconds){
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Log.w(this.getClass().getName(), "Interrupted in waitForConnection()!");
        }
    }

    private boolean isConnected(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    public void disconnectFromWifi() throws WifiConnectionException {
        boolean disconnectSuccessful = wifiManager.disconnect();
        if(!disconnectSuccessful){
            throw new WifiConnectionException("Could not disconnect from network!");
        }
    }

    private boolean wifiConfigurationExists() {
        Optional<WifiConfiguration> wifiConfiguration = getWifiConfiguration();
        return wifiConfiguration.isPresent();
    }

    private void addWifiConfiguration() {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", SSID);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        wifiManager.addNetwork(wifiConfig);
    }

    private Optional<WifiConfiguration> getWifiConfiguration() {
        List<WifiConfiguration> wifiList = wifiManager.getConfiguredNetworks();
        return wifiList.stream().filter(wifi -> wifi.SSID.equals("\"" + SSID + "\"")).findFirst();
    }

    private void enableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    private void connect() throws WifiConnectionException {
        Optional<WifiConfiguration> wifiConfig = getWifiConfiguration();

        if (!wifiConfig.isPresent()) {
            throw new WifiConnectionException("WifiConfiguration for network " + SSID + " does not exist!");
        }

        boolean connectionSucceeded;
        connectionSucceeded = wifiManager.disconnect();
        connectionSucceeded &= wifiManager.enableNetwork(wifiConfig.get().networkId, true);
        connectionSucceeded &= wifiManager.reconnect();

        if (!connectionSucceeded) {
            throw new WifiConnectionException("Connection to network " + SSID + " failed!");
        }
    }
}