package de.whyqueue.kvwlwifilogin.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import de.whyqueue.kvwlwifilogin.R;
import de.whyqueue.kvwlwifilogin.activity.exception.NoCredentialsInStoreException;
import de.whyqueue.kvwlwifilogin.activity.exception.ValidationException;
import de.whyqueue.kvwlwifilogin.activity.task.UserLoginTask;
import de.whyqueue.kvwlwifilogin.model.Credentials;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private CheckBox mSaveCredentialsView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = findViewById(R.id.usernameField);

        mPasswordView = findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mSaveCredentialsView = findViewById(R.id.saveCredentials);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.progressView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Credentials storedCredentials = loadCredentials();
            login(storedCredentials);
        } catch (NoCredentialsInStoreException e) {
            Log.w(LoginActivity.class.getName(), e.getMessage());
        }
    }

    private Credentials loadCredentials() throws NoCredentialsInStoreException {
        // TODO: Load credentials
        throw new NoCredentialsInStoreException("No credentials in store!");
    }

    private void attemptLogin() {
        try {
            Credentials credentials = getCredentials();
            if (saveCredentialsChecked()) {
                saveCredentials(credentials);
            }
            hideKeyboard();
            login(credentials);
        } catch (ValidationException e) {
            Log.w(LoginActivity.class.getName(), e.getMessage());
        }
    }

    private boolean saveCredentialsChecked() {
        return mSaveCredentialsView.isChecked();
    }

    private void saveCredentials(Credentials credentials) {
        // TODO: Save credentials
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Credentials getCredentials() throws ValidationException {
        resetErrors();
        validateInputs();

        return getInputs();
    }

    private void resetErrors() {
        mUsernameView.setError(null);
        mPasswordView.setError(null);
    }

    private void validateInputs() throws ValidationException {
        validateUsername();
        validatePassword();
    }

    private void validateUsername() throws ValidationException {
        String username = mUsernameView.getText().toString();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            String errorMsg = getString(R.string.error_field_required);
            setErrorAndFocus(mUsernameView, errorMsg);
            throw new ValidationException("Validation failed due to empty username field!");
        } else if (!isUsernameValid(username)) {
            String errorMsg = getString(R.string.error_invalid_username);
            setErrorAndFocus(mUsernameView, errorMsg);
            throw new ValidationException("Validation failed due to invalid username!");
        }
    }

    private boolean isUsernameValid(String ussername) {
        return ussername.contains("_");
    }

    private void validatePassword() throws ValidationException {
        String password = mPasswordView.getText().toString();

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            String errorMsg = getString(R.string.error_field_required);
            setErrorAndFocus(mPasswordView, errorMsg);
            throw new ValidationException("Validation failed due to empty password field!");
        } else if (!isPasswordValid(password)) {
            String errorMsg = getString(R.string.error_invalid_password);
            setErrorAndFocus(mPasswordView, errorMsg);
            throw new ValidationException("Validation failed due to invalid password!");
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void setErrorAndFocus(EditText mEditTextView, String error) {
        mEditTextView.setError(error);
        mEditTextView.requestFocus();
    }

    private Credentials getInputs() {
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        return new Credentials(username, password);
    }

    private void login(Credentials credentials) {
        if (mAuthTask != null) {
            return;
        }
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        TextView mUsernameTextView = findViewById(R.id.username);
        mUsernameTextView.setText(credentials.getUsername());

        showProgress(true);
        mAuthTask = new UserLoginTask(this, credentials);
        mAuthTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void setmAuthTask(UserLoginTask mAuthTask) {
        this.mAuthTask = mAuthTask;
    }
}