package eu.stosdev.theresistance.screen;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.event.InitializeGameEvent;
import eu.stosdev.theresistance.event.LocalEvent;
import eu.stosdev.theresistance.event.NewGoogleApiClientEvent;
import eu.stosdev.theresistance.event.StartNewTurnEvent;
import eu.stosdev.theresistance.model.card.KeepingCloseEyeCard;
import eu.stosdev.theresistance.model.card.NoConfidenceCard;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.card.RevealIdentityCard;
import eu.stosdev.theresistance.model.card.TakeResponsibilityCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.MissionVote;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.game.TeamVote;
import eu.stosdev.theresistance.model.game.Vote;
import eu.stosdev.theresistance.model.messages.AcknowledgeVoteMessage;
import eu.stosdev.theresistance.model.messages.AssignPlotCardMessage;
import eu.stosdev.theresistance.model.messages.DrawPlotCardMessage;
import eu.stosdev.theresistance.model.messages.InTheSpotlightMessage;
import eu.stosdev.theresistance.model.messages.InitialDataMessage;
import eu.stosdev.theresistance.model.messages.KeepingCloseEyeMessage;
import eu.stosdev.theresistance.model.messages.NoConfidenceMessage;
import eu.stosdev.theresistance.model.messages.OpenUpConfirmationMessage;
import eu.stosdev.theresistance.model.messages.RemoveCardMessage;
import eu.stosdev.theresistance.model.messages.RevealIdentityAcknowledgeMessage;
import eu.stosdev.theresistance.model.messages.RevealIdentityMessage;
import eu.stosdev.theresistance.model.messages.TakeResponsibilityMessage;
import eu.stosdev.theresistance.model.messages.TeamListMessage;
import eu.stosdev.theresistance.model.messages.TeamVoteApprovalUpdateMessage;
import eu.stosdev.theresistance.model.messages.TeamVoteMessage;
import eu.stosdev.theresistance.model.messages.VoteFinishedMessage;
import eu.stosdev.theresistance.model.messages.VoteResponseMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import eu.stosdev.theresistance.utils.FlowOwner;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.CardController;
import eu.stosdev.theresistance.view.GameMainView;
import eu.stosdev.theresistance.view.GameProgressView;
import flow.Flow;
import flow.Layout;
import flow.Parcer;
import mortar.Blueprint;
import mortar.MortarScope;

@Layout(R.layout.game_main)
public class GameMainScreen implements Blueprint {

    private final transient GoogleApiClient mGoogleApiClient;
    private final Room room;

    public GameMainScreen(@NonNull GoogleApiClient mGoogleApiClient, @NonNull Room room) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.room = room;
    }

    @Override
    public String getMortarScopeName() {
        return room.getRoomId();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = {GameMainView.class, GameProgressView.class, StartTurnDispatcher.class}, addsTo = Main.Module.class, library = true)
    public class Module {
        @Provides @Singleton @Named("game") Flow provideFlow(Presenter presenter) {
            return presenter.getFlow();
        }

        @Provides @Singleton GoogleApiClient provideGoogleApiClient() {
            return mGoogleApiClient;
        }

        @Provides @Singleton Room provideRoom() {
            return room;
        }

        @Provides @Singleton GameState provideGameState(Presenter presenter) {
            return presenter.gameState;
        }

        @Provides @Singleton @Named("serverId") String provideServerId() {
            for (Participant par : room.getParticipants()) {
                if (par.getParticipantId().equals(room.getCreatorId())) {
                    return par.getParticipantId();
                }
            }
            return null;
        }

        @Provides @Singleton @Named("myParticipantId") String myParticipantId() {
            return room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
        }

        @Provides @Singleton boolean isServer() {
            return myParticipantId().equals(provideServerId());
        }

        @Provides @Singleton MessageSender provideMessageSender(TypedBus<AbsEvent> bus) {
            return new MessageSender(provideServerId(), mGoogleApiClient, bus, room);
        }

        @Provides @Singleton CardController provideCardController(Presenter presenter) {
            return presenter.getCardController();
        }

    }

    @Singleton
    public static class Presenter extends FlowOwner<Blueprint, GameMainView> {

        private final Room room;
        private GoogleApiClient mGoogleApiClient;
        private final TypedBus<AbsEvent> bus;
        private final String serverId;
        private final String myParticipantId;
        private final GameState gameState;
        private final TypedBus<LocalEvent> eventBus;
        private final MessageSender messageSender;

        @Inject
        public Presenter(Parcer<Object> flowParcer, Room room, GoogleApiClient mGoogleApiClient, TypedBus<LocalEvent> eventBus, TypedBus<AbsEvent> bus, @Named("serverId") String serverId, @Named("myParticipantId") String myParticipantId, MessageSender messageSender) {
            super(flowParcer);
            this.room = room;
            this.mGoogleApiClient = mGoogleApiClient;
            this.bus = bus;
            this.serverId = serverId;
            this.myParticipantId = myParticipantId;
            this.eventBus = eventBus;
            this.messageSender = messageSender;
            this.gameState = new GameState();
        }

        public CardController getCardController() {
            return getView().getCardController();
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
            eventBus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
            eventBus.unregister(this);
        }

        @Override protected Blueprint getFirstScreen() {
            if (myParticipantId.equals(serverId)) {
                return new ParticipantListScreen(room.getParticipants());
            } else {
                return new LobbyScreen();
            }
        }

        @Subscribe public void subscribeNewGoogleApiClientEvent(NewGoogleApiClientEvent ngace) {
            this.mGoogleApiClient = ngace.getGoogleApiClient();
            messageSender.setGoogleApiClient(mGoogleApiClient);
        }

        @Subscribe public void subscribeInitializeGameEvent(InitializeGameEvent ige) {
            List<String> participants = ige.getParticipants();
            List<String> spies = new ArrayList<>();
            Map<String, Player.Type> typeMap = gameState.getSecretPlayerTypeMap();
            for (Map.Entry<String, Player.Type> entry : typeMap.entrySet()) {
                if (entry.getValue().equals(Player.Type.SPY)) {
                    spies.add(entry.getKey());
                }
            }
            for (String participant : typeMap.keySet()) {
                String currentLeader = participants.get(0);
                Player.Type myType = typeMap.get(participant);
                InitialDataMessage message = new InitialDataMessage(participants, currentLeader, myType, myType.equals(Player.Type.SPY) ? spies : null, ige.getUsePlotCards());
                if (participant.equals(myParticipantId)) {
                    gameState.initialize(message.getEvent(myParticipantId), mGoogleApiClient, room);
                    if (gameState.isExpansionCard()) {
                        getView().setCardButtonEnable();
                    }

                } else {
                    Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, SerializationUtils.serialize(message), room.getRoomId(), participant);
                }
            }
            eventBus.post(new StartNewTurnEvent());
        }

        @Subscribe public void subscribeInitialDataEvent(InitialDataMessage.Event event) {
            gameState.initialize(event, mGoogleApiClient, room);
            if (gameState.isExpansionCard()) {
                getView().setCardButtonEnable();
            }
            eventBus.post(new StartNewTurnEvent());
        }

        @Subscribe public void subscribeTeamListEvent(TeamListMessage.Event teamListEvent) {
            if (teamListEvent.getParticipantId().equals(gameState.getCurrentLeader().getParticipantId())) {
                ArrayList<String> teamMembers = teamListEvent.getTeamMembers();
                broadcastMessage(new TeamVoteMessage(teamMembers));
            }
        }

        @Subscribe public void subscribeTeamVoteEvent(TeamVoteMessage.Event teamVoteEvent) {
            if (teamVoteEvent.getParticipantId().equals(serverId)) {
                gameState.getCurrentTurn().pickTeam(teamVoteEvent.getTeamMembers());
            }
        }

        @Subscribe
        public void subscribeVoteResponseMessage(VoteResponseMessage.Event voteResponseMessage) {
            Vote currentVote = gameState.getCurrentTurn().getCurrentVote();
            currentVote.vote(voteResponseMessage.getParticipantId(), voteResponseMessage.getResponse());
            if (currentVote.isCompleted()) {
                broadcastMessage(new VoteFinishedMessage(currentVote.getAllVotes()));
            } else {
                broadcastMessage(new AcknowledgeVoteMessage(voteResponseMessage.getParticipantId()));
            }
        }

        @Subscribe public void subscribeVoteFinishedEvent(VoteFinishedMessage.Event vfe) {
            if (!serverId.equals(myParticipantId)) {
                Vote currentVote = gameState.getCurrentTurn().getCurrentVote();
                currentVote.parseFinishedMessage(vfe);
            }
        }

        @Subscribe public void subscribeAcknowledgeVoteEvent(AcknowledgeVoteMessage.Event ave) {
            if (gameState.getCurrentTurn().isVoteInProgress() && !gameState.getCurrentTurn().getCurrentVote().getAllVotes().containsKey(ave.getPlayerId())) {
                gameState.getCurrentTurn().getCurrentVote().vote(ave.getPlayerId(), null);
            }
        }

        @Subscribe public void subscribeDrawPloCardEvent(DrawPlotCardMessage.Event dpce) {
            gameState.getCurrentTurn().getPickedCards().addAll(dpce.getPlotCard());
        }

        @Subscribe public void subscribeStartNewTurn(StartNewTurnEvent snt) {
            if (gameState.isExpansionCard() && gameState.getCurrentTurn().getPickedCards().isEmpty() && gameState.getCardDeck() != null) {
                int size = gameState.getPlayerList().size();
                int cardNum = size < 7 ? 1 : size < 9 ? 2 : 3;
                List<PlotCard> plotCards = new ArrayList<>();
                for (int i = 0; i < cardNum; i++) {
                    plotCards.add(gameState.getCardDeck().getNextCard());
                }
                broadcastMessage(new DrawPlotCardMessage(plotCards));
            }
        }

        @Subscribe public void subscribeAssignPlotCardMessage(AssignPlotCardMessage.Event apce) {
            if (gameState.isExpansionCard()) {
                if (gameState.getCurrentTurn().assignPlayerToCard(apce.getPlotCard(), apce.getTakerId())) {
                    if (serverId.equals(myParticipantId)) {
                        broadcastMessage(new AssignPlotCardMessage(apce.getPlotCard(), apce.getTakerId()));
                    }
                    PlotCard plotCard = gameState.getCurrentTurn().getPickedCards().get(apce.getPlotCard());
                    if (plotCard.getType() != PlotCard.Type.INSTANT) {
                        getCardController().addCard(plotCard);
                    }
                }
            }
        }

        @Subscribe
        public void subscribeOpenUpConfirmationEvent(OpenUpConfirmationMessage.Event ouce) {
            if (serverId.equals(myParticipantId)) {
                sendMessage(new RevealIdentityMessage(ouce.getFrom(), gameState.getSecretPlayerTypeMap().get(ouce.getFrom())), ouce.getTo());
                broadcastMessage(new RevealIdentityAcknowledgeMessage(ouce.getCardOwner(), ouce.getCardPosition(), ouce.getFrom(), ouce.getTo()));
            }
        }

        @Subscribe
        public void subscribeRevealIdentityAcknowledgeMessage(RevealIdentityAcknowledgeMessage.Event ria) {
            RevealIdentityCard card = (RevealIdentityCard) gameState.getPlayerByParticipant(ria.getCardOwner()).getCardHand().get(ria.getCardPosition());
            card.setFrom(ria.getFrom());
            card.setTo(ria.getTo());
        }

        @Subscribe public void subscribeRevealIdentityEvent(RevealIdentityMessage.Event rim) {
            gameState.getPlayerTypeMap().put(gameState.getPlayerByParticipant(rim.getPlayer()), rim.getIdentity());
        }

        @Subscribe public void subscribeKeepingCloseEyeEvent(KeepingCloseEyeMessage.Event kce) {
            List<PlotCard> cardHand = gameState.getPlayerByParticipant(kce.getParticipantId()).getCardHand();
            for (int i = 0; i < cardHand.size(); i++) {
                PlotCard plotCard = cardHand.get(i);
                if (plotCard instanceof KeepingCloseEyeCard) {
                    broadcastMessage(new RemoveCardMessage(kce.getParticipantId(), i));
                    break;
                }
            }
        }

        @Subscribe public void subscribeNoConfidenceEvent(NoConfidenceMessage.Event nce) {
            TeamVote teamVote = gameState.getCurrentTurn().getCurrentTeamVote();
            if (!teamVote.isApproved() && !teamVote.isNullified()) {
                if (!nce.isApproved()) {
                    Player player = gameState.getPlayerByParticipant(nce.getParticipantId());
                    int position = -1;
                    List<PlotCard> cardHand = player.getCardHand();
                    for (int i = 0; i < cardHand.size(); i++) {
                        PlotCard plotCard = cardHand.get(i);
                        if (plotCard instanceof NoConfidenceCard) {
                            position = i;
                        }
                    }
                    broadcastMessage(new RemoveCardMessage(nce.getParticipantId(), position));
                }
                broadcastMessage(new TeamVoteApprovalUpdateMessage(nce.isApproved(), nce.getParticipantId()));
            }
        }

        @Subscribe public void subscribeRemoveCardEvent(RemoveCardMessage.Event rcm) {
            List<PlotCard> cardHand = gameState.getPlayerByParticipant(rcm.getParticipantId()).getCardHand();
            getCardController().removeCard(cardHand.get(rcm.getCardNumber()));
        }

        @Subscribe
        public void subscribeTeamVoteApprovalUpdateEvent(TeamVoteApprovalUpdateMessage.Event mvaue) {
            TeamVote teamVote = gameState.getCurrentTurn().getCurrentTeamVote();
            if (mvaue.isApproved()) {
                teamVote.approveVerdict();
            } else {
                teamVote.nullifyVerdict(mvaue.getPlayerId());
            }
        }

        @Subscribe
        public void subscribeTakeResponsibilityEvent(TakeResponsibilityMessage.Event trme) {
            if (trme.isRedistributable()) {
                broadcastMessage(new TakeResponsibilityMessage(trme.getFrom(), trme.getTo(), trme.getCardId(), trme.getTakeCardId(), false));
            } else {
                TakeResponsibilityCard takeResponsibilityCard = (TakeResponsibilityCard) gameState.getPlayerByParticipant(trme.getTo()).getCardHand().get(trme.getTakeCardId());
                if (trme.getCardId() == -1) {
                    takeResponsibilityCard.setUnused(true);
                } else {
                    PlotCard plotCard = gameState.getPlayerByParticipant(trme.getFrom()).getCardHand().remove(trme.getCardId());
                    plotCard.setOwner(trme.getTo());
                    takeResponsibilityCard.setFrom(trme.getFrom());
                    takeResponsibilityCard.setTo(trme.getTo());
                    takeResponsibilityCard.setPlotCardClass(plotCard.getClass());
                    gameState.getPlayerByParticipant(trme.getTo()).getCardHand().add(plotCard);
                    getCardController().updateCard(plotCard);
                }
            }
        }

        @Subscribe
        public void subscribeInTheSpotlightEvent(InTheSpotlightMessage.Event itse) {
            if (itse.isRedistributable()) {
                broadcastMessage(new InTheSpotlightMessage(itse.getPlayerId(), false));
            } else {
                MissionVote missionVote = gameState.getCurrentTurn().getMissionVote();
                missionVote.setPublicResult(itse.getPlayerId());
            }
        }

        private void sendMessage(AbsMessage<?> message, String playerId) {
            if (playerId.equals(myParticipantId)) {
                bus.post(message.getEvent(serverId));
            } else {
                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, SerializationUtils.serialize(message), room.getRoomId(), playerId);
            }
        }

        private void broadcastMessage(AbsMessage<?> message) {
            for (Player player : gameState.getPlayerList()) {
                sendMessage(message, player.getParticipantId());
            }
        }
    }
}