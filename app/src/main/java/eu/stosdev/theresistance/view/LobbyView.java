package eu.stosdev.theresistance.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.LobbyScreen;
import mortar.Mortar;

public class LobbyView extends RelativeLayout {

    @Inject LobbyScreen.Presenter presenter;

    @InjectView(R.id.lobby_tip) TextView tipText;
    @InjectView(R.id.lobby_label) TextView tipLabelText;

    @StringRes
    private static final int[] RESISTANCE_TIPS = {R.string.tip_r1, R.string.tip_r2, R.string.tip_r3, R.string.tip_r4};

    @StringRes
    private static final int[] SPY_TIPS = {R.string.tip_s1, R.string.tip_s2, R.string.tip_s3};

    public LobbyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            presenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void showSpyTip(int i) {
        tipLabelText.setTextColor(Color.parseColor("#EF5350"));
        tipLabelText.setText(R.string.spy_strategy_tip);
        tipText.setText(SPY_TIPS[i]);
    }

    public void showResistanceTip(int i) {
        tipLabelText.setTextColor(Color.parseColor("#1976D2"));
        tipLabelText.setText(R.string.resistance_strategy_tip);
        tipText.setText(RESISTANCE_TIPS[i]);
    }
}
