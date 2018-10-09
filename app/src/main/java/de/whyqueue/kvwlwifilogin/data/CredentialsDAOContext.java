package de.whyqueue.kvwlwifilogin.data;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CredentialsDAOContext {

    private Context context;

    public CredentialsDAOContext(Context context) {
        this.context = context;
    }

    public FileInputStream openFileInput(String name)
            throws FileNotFoundException {
        return context.openFileInput(name);
    }

    public FileOutputStream openFileOutput(String name, int mode)
            throws FileNotFoundException {
        return context.openFileOutput(name, mode);
    }

}
