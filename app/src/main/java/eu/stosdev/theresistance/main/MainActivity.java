package eu.stosdev.theresistance.main;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.event.ActivateStartButtonEvent;
import eu.stosdev.theresistance.event.EndGameEvent;
import eu.stosdev.theresistance.event.InvitationListEvent;
import eu.stosdev.theresistance.event.LocalEvent;
import eu.stosdev.theresistance.event.NewGoogleApiClientEvent;
import eu.stosdev.theresistance.event.StartGameEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import eu.stosdev.theresistance.screen.GameMainScreen;
import eu.stosdev.theresistance.screen.StartScreen;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.MainView;
import flow.Flow;
import mortar.Mortar;

public class MainActivity extends MortarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnInvitationReceivedListener, RoomUpdateListener, RealTimeMessageReceivedListener {

    private static final String TAG = "MainActivity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int RC_SELECT_PLAYERS = 666;
    private static final int RC_INVITATION_INBOX = 667;
    private static final int RC_WAITING_ROOM = 668;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIsInResolution;

    @InjectView(R.id.main_view) MainView mainView;

    private Room mRoom;
    private Flow mainFlow;

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this).setMessageReceivedListener(this);
    }

    @Inject TypedBus<LocalEvent> localBus;
    @Inject TypedBus<AbsEvent> gameEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mortar.inject(this, this);
        localBus.register(this);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(mainView.getToolbar());
        mainFlow = mainView.getFlow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Games.API)
                    .addScope(Games.SCOPE_GAMES)
                            // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBus.unregister(this);
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        switch (request) {
            case RC_WAITING_ROOM:
                if (response != RESULT_OK) {
                    Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoom.getRoomId());
                }
                break;
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
            case RC_SELECT_PLAYERS:
                if (response != RESULT_OK) {
                    mRoom = null;
                    localBus.post(new ActivateStartButtonEvent());
                    return;
                }

                // Get the invitee list.
                final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
                roomConfigBuilder.addPlayersToInvite(invitees);
                RoomConfig roomConfig = roomConfigBuilder.build();

                // Create and start the match.
                Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
                break;
            case RC_INVITATION_INBOX:
                if (response != RESULT_OK) {
                    return;
                }
                // get the selected invitation
                Bundle extras = data.getExtras();
                Invitation invitation = extras.getParcelable(Multiplayer.EXTRA_INVITATION);
                Toast.makeText(this, "Accepted invitation", Toast.LENGTH_SHORT).show();

                roomConfig = makeBasicRoomConfigBuilder()
                        .setInvitationIdToAccept(invitation.getInvitationId())
                        .build();
                // accept it!
                Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfig);

                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            default:
                super.onActivityResult(request, response, data);
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        localBus.post(new NewGoogleApiClientEvent(mGoogleApiClient));
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);
        if (connectionHint != null) {
            Invitation inv = connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null) {
                // accept invitation
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
                roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId());
                Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());

                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        if (mRoom != null) {
            Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
            startActivityForResult(intent, RC_INVITATION_INBOX);
        } else {
            Games.RealTimeMultiplayer.declineInvitation(mGoogleApiClient, invitation.getInvitationId());
        }
    }

    @Override
    public void onInvitationRemoved(String s) {
    }

    @Override
    public void onRoomCreated(int i, Room room) {
        mRoom = room;
        switch (i) {
            case GamesStatusCodes.STATUS_OK:
                Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, mRoom, 5);
                startActivityForResult(intent, RC_WAITING_ROOM);
                break;
            default:
                Toast.makeText(this, "Error during room creation " + i, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onJoinedRoom(int i, Room room) {
        mRoom = room;
        switch (i) {
            case GamesStatusCodes.STATUS_OK:
                Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, mRoom, 5);
                startActivityForResult(intent, RC_WAITING_ROOM);
                break;
            default:
                Toast.makeText(this, "Error during room creation " + i, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLeftRoom(int i, String s) {
        localBus.post(new ActivateStartButtonEvent());
        mRoom = null;
    }

    @Override
    public void onRoomConnected(int i, Room room) {
        if (i == GamesStatusCodes.STATUS_OK) {
            mainFlow.goTo(new GameMainScreen(mGoogleApiClient, room));
        }
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        AbsMessage<?> message = SerializationUtils.deserialize(realTimeMessage.getMessageData());
        gameEventBus.post(message.getEvent(realTimeMessage.getSenderParticipantId()));
    }

    @Subscribe
    public void startGameEvent(StartGameEvent event) {
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 4, 9, false);
        startActivityForResult(intent, MainActivity.RC_SELECT_PLAYERS);
    }

    @Subscribe
    public void invitationListEvent(InvitationListEvent event) {
        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, MainActivity.RC_INVITATION_INBOX);
    }

    @Subscribe
    public void subscribeEndGameEvent(EndGameEvent event) {
        mainFlow.resetTo(new StartScreen());
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoom.getRoomId());
    }

    @Override
    public void onBackPressed() {
    }
}
