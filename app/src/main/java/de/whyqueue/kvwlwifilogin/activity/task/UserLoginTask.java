package de.whyqueue.kvwlwifilogin.activity.task;

import android.content.Intent;
import android.os.AsyncTask;

import de.whyqueue.kvwlwifilogin.activity.ErrorActivity;
import de.whyqueue.kvwlwifilogin.activity.LoginActivity;
import de.whyqueue.kvwlwifilogin.activity.SuccessActivity;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final LoginActivity loginActivity;

    private final String mEmail;
    private final String mPassword;

    public UserLoginTask(LoginActivity loginActivity, String email, String password) {
        this.loginActivity = loginActivity;

        mEmail = email;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against network.

        try {
            // Simulate network access.
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return false;
        }

        return true;
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

    private void startSuccessActivity(){
        Intent intent = new Intent(loginActivity, SuccessActivity.class);
        loginActivity.startActivity(intent);
    }

    private void startErrorActivity(){
        Intent intent = new Intent(loginActivity, ErrorActivity.class);
        loginActivity.startActivity(intent);
    }

    @Override
    protected void onCancelled() {
        loginActivity.setmAuthTask(null);
        loginActivity.showProgress(false);
    }
}