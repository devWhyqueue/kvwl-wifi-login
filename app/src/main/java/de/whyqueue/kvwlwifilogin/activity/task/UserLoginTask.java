package de.whyqueue.kvwlwifilogin.activity.task;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import de.whyqueue.kvwlwifilogin.activity.ErrorActivity;
import de.whyqueue.kvwlwifilogin.activity.LoginActivity;
import de.whyqueue.kvwlwifilogin.activity.SuccessActivity;
import de.whyqueue.kvwlwifilogin.model.Credentials;
import de.whyqueue.kvwlwifilogin.model.WifiClient;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final LoginActivity loginActivity;
    private final Credentials credentials;

    public UserLoginTask(LoginActivity loginActivity, Credentials credentials) {
        this.loginActivity = loginActivity;
        this.credentials = credentials;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        WifiManager wifiManager = getWifiManager();
        ConnectivityManager connectivityManager = getConnectivityManager();
        WifiClient wifiClient = new WifiClient(wifiManager, connectivityManager, credentials);

        try {
            wifiClient.connect();
            return true;
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            return false;
        }
    }

    private WifiManager getWifiManager() {
        Context context = loginActivity.getApplicationContext();
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    private ConnectivityManager getConnectivityManager(){
        Context context = loginActivity.getApplicationContext();
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        loginActivity.setmAuthTask(null);
        loginActivity.showProgress(false);

        if (success) {
            loginActivity.finish();
            startSuccessActivity();
        } else {
            startErrorActivity();
        }
    }

    private void startSuccessActivity() {
        Intent intent = new Intent(loginActivity, SuccessActivity.class);
        loginActivity.startActivity(intent);
    }

    private void startErrorActivity() {
        Intent intent = new Intent(loginActivity, ErrorActivity.class);
        loginActivity.startActivity(intent);
    }

    @Override
    protected void onCancelled() {
        loginActivity.setmAuthTask(null);
        loginActivity.showProgress(false);
    }
}