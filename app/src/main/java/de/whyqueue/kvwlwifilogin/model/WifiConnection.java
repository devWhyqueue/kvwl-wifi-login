package de.whyqueue.kvwlwifilogin.model;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;
import java.util.Optional;

import de.whyqueue.kvwlwifilogin.model.exception.WifiConnectionException;

public class WifiConnection {
    private static final String SSID = "gast";

    private WifiManager wifiManager;

    public WifiConnection(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    public void connectToWifi() throws WifiConnectionException {
        if (!wifiConfigurationExists()) {
            addWifiConfiguration();
        }

        enableWifi();
        connect();
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