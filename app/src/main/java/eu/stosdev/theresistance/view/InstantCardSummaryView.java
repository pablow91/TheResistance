package eu.stosdev.theresistance.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.screen.InstantCardSummaryScreen;
import mortar.Mortar;

public class InstantCardSummaryView extends RelativeLayout {

    @Inject InstantCardSummaryScreen.Presenter presenter;
    @InjectView(R.id.player_taker) TextView playerTaker;
    @InjectView(R.id.player_receiver) TextView playerReceiver;
    @InjectView(R.id.action) TextView action;
    private Class<? extends PlotCard> takenCard;

    public InstantCardSummaryView(Context context) {
        this(context, null);
    }

    public InstantCardSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            presenter.takeView(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void setTaker(String playerName, @Nullable Player.Type playerType, @StringRes int action) {
        setTakerIdentity(playerName, playerType);
        if (takenCard != null) {
            try {
                Resources res = getResources();
                PlotCard plotCard = takenCard.newInstance();
                String text = String.format(res.getString(action), getContext().getText(plotCard.getName()));
                this.action.setText(text);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            this.action.setText(action);
        }
    }

    public void setTakerIdentity(String playerName, @Nullable Player.Type playerType) {
        playerTaker.setText(playerName + " - " + (playerType == null ? "Unknown" : playerType.toString()));
    }

    public void setReceiver(String playerName, @Nullable Player.Type playerType) {
        playerReceiver.setText(playerName + " - " + (playerType == null ? "Unknown" : playerType.toString()));
    }

    public void setTakenCard(Class<? extends PlotCard> takenCard) {
        this.takenCard = takenCard;
    }
}
