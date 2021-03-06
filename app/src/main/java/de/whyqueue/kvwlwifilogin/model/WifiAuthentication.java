package de.whyqueue.kvwlwifilogin.model;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.whyqueue.kvwlwifilogin.model.exception.WifiAuthenticationException;

public class WifiAuthentication {
    private static final String LOGIN_URL = "http://wlangast.kvwl.de/login.html";
    private static final String LOGOUT_URL = "http://192.168.3.3/logout.html";
    private static final String FAILURE_RESPONSE_TITLE = "Web Authentication Failure";

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

    private void authenticationHandler(String urlStr, String postParameters) throws IOException, WifiAuthenticationException {
        client = openConnection(urlStr);
        configureRequest();
        sendRequest(postParameters);
        verifyAuthentication();
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

    }

    private void verifyAuthentication() throws IOException, WifiAuthenticationException {
        if(connectedToInternet()){
            return;
        }
        checkServerResponse();
    }

    private boolean connectedToInternet() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { Log.i(this.getClass().getName(), e.getMessage());}
        catch (InterruptedException e) { Log.i(this.getClass().getName(), e.getMessage()); }

        return false;
    }

    private void checkServerResponse() throws WifiAuthenticationException, IOException {
        InputStream is = new BufferedInputStream(client.getInputStream());
        Scanner sc = new Scanner(is).useDelimiter("\\A");
        String response = sc.hasNext() ? sc.next() : "";

        if(response.contains(FAILURE_RESPONSE_TITLE)){
            throw new WifiAuthenticationException("Wrong username or password!");
        }
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
