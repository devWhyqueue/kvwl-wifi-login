package de.whyqueue.kvwlwifilogin.data;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.whyqueue.kvwlwifilogin.activity.exception.NoCredentialsInStoreException;
import de.whyqueue.kvwlwifilogin.model.Credentials;

public class CredentialsFileDAO implements CredentialsDAO {

    private static final String fileName = "credentials";

    private CredentialsDAOContext context;

    public CredentialsFileDAO(CredentialsDAOContext context) {
        this.context = context;
    }

    @Override
    public Credentials loadCredentials() throws NoCredentialsInStoreException {
        try (FileInputStream inputStream = context.openFileInput(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Credentials storedCredentials = (Credentials) objectInputStream.readObject();
            return storedCredentials;
        } catch (Exception e) {
            throw new NoCredentialsInStoreException("Error while retrieving credentials, message is: " + e.getMessage());
        }
    }

    @Override
    public void saveCredentials(Credentials credentials) {
        try (FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(credentials);
        } catch (IOException e) {
            Log.e(CredentialsFileDAO.class.getName(), "Error while saving credentials, message is: " + e.getMessage());
        }
    }
}
