package eu.stosdev.theresistance.model.card;

import lombok.Getter;
import lombok.Setter;

public abstract class RevealIdentityCard extends InstantPlotCard {
    @Getter @Setter private String from;
    @Getter @Setter private String to;
}
