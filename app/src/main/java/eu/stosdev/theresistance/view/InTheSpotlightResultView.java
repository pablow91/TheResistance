package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.InTheSpotlightResultScreen;
import mortar.Mortar;

public class InTheSpotlightResultView extends RelativeLayout {

    @Inject InTheSpotlightResultScreen.Presenter presenter;

    @InjectView(R.id.player_taker) TextView playerTaker;
    @InjectView(R.id.player_receiver) TextView playerReceiver;

    public InTheSpotlightResultView(Context context) {
        this(context, null);
    }

    public InTheSpotlightResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        if (!isInEditMode()) {
            presenter.takeView(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void setSpotlightedPlayer(String playerName) {
        if (playerName == null) {
            playerReceiver.setText(R.string.none);
        } else {
            playerReceiver.setText(playerName);
        }
    }

    public void setCardOwnerPlayer(String playerName) {
        playerTaker.setText(playerName);
    }
}
