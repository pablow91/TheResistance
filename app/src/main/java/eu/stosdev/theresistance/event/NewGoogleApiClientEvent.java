package eu.stosdev.theresistance.event;

import com.google.android.gms.common.api.GoogleApiClient;

public class NewGoogleApiClientEvent implements LocalEvent {
    private final GoogleApiClient mGoogleApiClient;

    public NewGoogleApiClientEvent(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
