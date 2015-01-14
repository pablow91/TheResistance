package eu.stosdev.theresistance.utils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.Room;

import org.apache.commons.lang3.SerializationUtils;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;

public class MessageSender {

    private final String serverId;
    private final TypedBus<AbsEvent> bus;
    private final Room room;
    private GoogleApiClient mGoogleApiClient;

    public MessageSender(String serverId, GoogleApiClient mGoogleApiClient, TypedBus<AbsEvent> bus, Room room) {
        this.serverId = serverId;
        this.mGoogleApiClient = mGoogleApiClient;
        this.bus = bus;
        this.room = room;
    }

    public void sendMessage(AbsMessage<?> message) {
        if (serverId.equals(room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)))) {
            bus.post(message.getEvent(serverId));
        } else {
            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, SerializationUtils.serialize(message), room.getRoomId(), serverId);
        }
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.mGoogleApiClient = googleApiClient;
    }

}
