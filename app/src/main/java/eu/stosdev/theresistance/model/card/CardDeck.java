package eu.stosdev.theresistance.model.card;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CardDeck {

    private final Stack<PlotCard> plotCards = new Stack<>();

    private static final Map<Class<? extends PlotCard>, Integer> atFivePlayers = ImmutableMap.of(
            NoConfidenceCard.class, 1,
            StrongLeaderCard.class, 2,
            KeepingCloseEyeCard.class, 2,
            TakeResponsibilityCard.class, 1,
            OpinionMakerCard.class, 1
    );

    private static final Map<Class<? extends PlotCard>, Integer> atSevenPlayers = ImmutableMap.<Class<? extends PlotCard>, Integer>builder()
            .put(NoConfidenceCard.class, 2)
            .put(OverheardConversationCard.class, 2)
            .put(InTheSpotlightCard.class, 1)
            .put(EstablishConfidenceCard.class, 1)
            .put(OpenUpCard.class, 1)
            .put(OpinionMakerCard.class, 1)
            .build();

    public CardDeck(int playerNum) {
        addAll(atFivePlayers.entrySet());
        if (playerNum >= 7) {
            addAll(atSevenPlayers.entrySet());
        }
        Collections.shuffle(plotCards);
    }

    private void addAll(Set<Map.Entry<Class<? extends PlotCard>, Integer>> entrySet) {
        try {
            for (Map.Entry<Class<? extends PlotCard>, Integer> entry : entrySet) {
                for (Integer i = 0; i < entry.getValue(); i++) {
                    plotCards.add(entry.getKey().newInstance());
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException();
        }
    }

    public PlotCard getNextCard() {
        return plotCards.pop();
    }
}
