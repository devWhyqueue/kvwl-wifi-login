package de.whyqueue.kvwlwifilogin.data;

import de.whyqueue.kvwlwifilogin.activity.exception.NoCredentialsInStoreException;
import de.whyqueue.kvwlwifilogin.model.Credentials;

public interface CredentialsDAO {

    Credentials loadCredentials() throws NoCredentialsInStoreException;
    void saveCredentials(Credentials credentials);
}
