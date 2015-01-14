package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.screen.WinnerScreen;
import mortar.Mortar;

public class WinnerView extends RelativeLayout {

    @Inject WinnerScreen.Presenter presenter;
    @Inject GameState gameState;

    @InjectView(R.id.winner) TextView winner;

    public WinnerView(Context context) {
        this(context, null);
    }

    public WinnerView(Context context, AttributeSet attrs) {
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

    public void setWinner(Player.Type type) {
        winner.setText(type.toString());
    }
}
