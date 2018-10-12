package de.whyqueue.kvwlwifilogin.model;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import de.whyqueue.kvwlwifilogin.model.exception.WifiAuthenticationException;

public class WifiAuthentication {
    private static final String LOGIN_URL = "http://wlangast.kvwl.de/login.html";
    private static final String LOGOUT_URL = "http://192.168.3.3/logout.html";

    private Credentials credentials;

    private HttpURLConnection client;

    public WifiAuthentication(Credentials credentials) {
        this.credentials = credentials;
    }

    public void login() throws WifiAuthenticationException {
        try {
            String postData = buildLoginPostData();
            authenticationHandler(LOGIN_URL, postData);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getName(), e.getMessage());
            throw new WifiAuthenticationException(e.getMessage());
        }
    }

    public void logout() throws WifiAuthenticationException {
        try {
            String postData = buildLogoutPostData();
            authenticationHandler(LOGOUT_URL, postData);
        } catch (IOException e) {
            Log.e(this.getClass().getName(), e.getMessage());
            throw new WifiAuthenticationException(e.getMessage());
        }
    }

    private void authenticationHandler(String urlStr, String postParameters) throws IOException {
        client = openConnection(urlStr);
        configureRequest();
        sendRequest(postParameters);
        closeConnection();
    }

    private HttpURLConnection openConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        return (HttpURLConnection) url.openConnection();
    }

    private void configureRequest() throws ProtocolException {
        client.setRequestMethod("POST");
        client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        client.setDoOutput(true);
    }

    private void sendRequest(String postParameters) throws IOException {
        PrintWriter outputPost = new PrintWriter(client.getOutputStream());

        outputPost.write(postParameters);
        outputPost.flush();
        outputPost.close();

        // TODO: DEBUG
        InputStream is = new BufferedInputStream(client.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null)
            Log.i("RESPONSE", line);
    }

    private String buildLoginPostData() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("network_name", "Guest Network");
        parameters.put("username", credentials.getUsername());
        parameters.put("password", credentials.getPassword());
        parameters.put("buttonClicked", "4");

        return createQueryStringForParameters(parameters);
    }

    private String buildLogoutPostData() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("user_status", "1");

        return createQueryStringForParameters(parameters);
    }

    private String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append("&");
                }

                parametersAsQueryString.append(parameterName)
                        .append("=")
                        .append(URLEncoder.encode(
                                parameters.get(parameterName)));

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }

    private void closeConnection() {
        if (client != null) {
            client.disconnect();
        }
    }
}
