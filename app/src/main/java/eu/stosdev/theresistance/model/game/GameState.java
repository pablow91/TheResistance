package eu.stosdev.theresistance.model.game;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.stosdev.theresistance.model.card.CardDeck;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.messages.InitialDataMessage;
import lombok.Getter;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkArgument;
import static eu.stosdev.theresistance.model.game.Player.Type;

public class GameState {

    private static final int[][] numberOfTypes = {{3, 2}, {4, 2}, {4, 3}, {5, 3}, {6, 3}, {6, 4}};

    @Getter private final List<Player> playerList = new ArrayList<>();
    @Getter @Setter private Player currentLeader;
    @Getter private final List<Turn> turnList = new ArrayList<>();
    @Getter private int currentTurnNum;
    private final Map<Player, Type> playerTypeMap = new HashMap<>();
    @Getter CardDeck cardDeck;
    @Getter private boolean expansionCard;

    public Map<Player, Type> getPlayerTypeMap() {
        return playerTypeMap;
    }

    @Getter private final Map<String, Type> secretPlayerTypeMap = new HashMap<>();
    private boolean initialized;

    public GameState() {
    }

    public void changeLeader() {
        int newLeader = (playerList.indexOf(currentLeader) + 1) % playerList.size();
        currentLeader = playerList.get(newLeader);
    }

    public void changeLeader(String key) {
        for (Player player : playerList) {
            if (player.getParticipantId().equals(key)) {
                currentLeader = player;
                return;
            }
        }
    }

    public void onTurnFinished() {
        if (!isGameCompleted()) {
            changeLeader();
            turnList.add(new Turn(this));
            currentTurnNum++;
        }
    }

    public Type getWinner() {
        int spy = 0, resistance = 0;
        for (Turn turn : turnList) {
            if (turn.getCurrentState() != Turn.State.FINISHED) {
                break;
            }
            if (turn.getWinner().equals(Type.SPY)) {
                spy++;
            } else {
                resistance++;
            }
        }
        if (spy >= 3 || getCurrentTurn().isSpyWin()) {
            return Type.SPY;
        }
        if (resistance >= 3) {
            return Type.RESISTANCE;
        }
        return null;
    }

    public boolean isGameCompleted() {
        return getWinner() != null || getCurrentTurn().isSpyWin();
    }

    public void generatePlayers(List<Participant> participantList, boolean usePlotCard) {
        List<Player> tmpPlayerList = new ArrayList<>();
        for (Participant participant : participantList) {
            tmpPlayerList.add(new Player(participant.getParticipantId(), participant.getDisplayName(), participant.getHiResImageUrl()));
        }
        List<Type> types = generateTypes(tmpPlayerList.size());
        Collections.shuffle(types);
        for (int i = 0; i < tmpPlayerList.size(); i++) {
            secretPlayerTypeMap.put(tmpPlayerList.get(i).getParticipantId(), types.get(i));
        }
        if (usePlotCard) {
            cardDeck = new CardDeck(participantList.size());
        }
    }

    public void initialize(InitialDataMessage.Event event, GoogleApiClient googleApiClient, Room room) {
        if (!initialized) {
            initialized = true;
            expansionCard = event.getUsePlotCards();
            for (String s : event.getPlayerList()) {
                Participant participant = room.getParticipant(s);
                playerList.add(new Player(s, participant.getDisplayName(), participant.getIconImageUrl()));
            }
            if (event.getSpies() != null) {
                for (String s : event.getSpies()) {
                    playerTypeMap.put(getPlayerByParticipant(s), Type.SPY);
                }
            }
            Player myPlayer = getMyPlayer(googleApiClient, room);
            playerTypeMap.put(myPlayer, event.getMyType());
            currentLeader = getPlayerByParticipant(event.getCurrentLeader());
            currentTurnNum = 1;
            turnList.add(new Turn(this));
        }
    }

    private List<Type> generateTypes(int players) {
        checkArgument(players >= 5 && players <= 10);
        int[] bla = numberOfTypes[players - 5];
        List<Type> array = new ArrayList<>(bla[0] + bla[1]);
        for (int i = 0; i < bla[0]; i++) {
            array.add(Type.RESISTANCE);
        }
        for (int i = 0; i < bla[1]; i++) {
            array.add(Type.SPY);
        }
        return array;
    }

    public Player getPlayerByParticipant(String participantId) {
        checkArgument(participantId != null, "participantId cannot be null");
        for (Player player : playerList) {
            if (player.getParticipantId().equals(participantId)) {
                return player;
            }
        }
        throw new NullPointerException("No such player");
    }

    public Player getMyPlayer(GoogleApiClient googleApiClient, Room room) {
        return getPlayerByParticipant(room.getParticipantId(Games.Players.getCurrentPlayerId(googleApiClient)));
    }

    public Turn getCurrentTurn() {
        return turnList.get(currentTurnNum - 1);
    }

    public List<String> getPlayerAsString() {
        List<String> list = new ArrayList<>();
        for (Player player : playerList) {
            list.add(player.getParticipantId());
        }
        return list;
    }

    public List<Player> getFilteredPlayerList(Predicate<Player> predicate) {
        List<Player> players = new ArrayList<>();
        for (Player player : playerList) {
            if (predicate.apply(player)) {
                players.add(player);
            }
        }
        return players;
    }

    public int getCardNumber(Class<? extends PlotCard> clazz) {
        int counter = 0;
        for (Player player : playerList) {
            for (PlotCard plotCard : player.getCardHand()) {
                if (clazz.isInstance(plotCard)) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public Turn getTurn(int currentTurnId) {
        return turnList.get(currentTurnId);
    }
}
