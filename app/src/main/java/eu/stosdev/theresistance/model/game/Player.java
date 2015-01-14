package eu.stosdev.theresistance.model.game;

import android.support.annotation.DrawableRes;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.PlotCard;
import lombok.Getter;

public class Player {

    public enum Type {
        SPY(R.drawable.ic_launcher), RESISTANCE(R.drawable.ic_launcher);

        @Getter private final @DrawableRes int image;

        Type(@DrawableRes int image) {
            this.image = image;
        }
    }

    @Getter private final String participantId;
    @Getter private final String nick;
    @Getter private final Optional<String> photoUrl;
    @Getter private final List<PlotCard> cardHand = new ArrayList<>();

    public Player(String participantId, String nick, String photoUrl) {
        this.participantId = participantId;
        this.nick = nick;
        this.photoUrl = Optional.fromNullable(photoUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return !(participantId != null ? !participantId.equals(player.participantId) : player.participantId != null);
    }

    @Override
    public int hashCode() {
        return participantId != null ? participantId.hashCode() : 0;
    }

}
