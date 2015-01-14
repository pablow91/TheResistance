package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.StartScreen;
import mortar.Mortar;

public class StartView extends RelativeLayout {
    @Inject StartScreen.Presenter presenter;

    @InjectView(R.id.start_game_button) Button button;
    @InjectView(R.id.invitations_button) Button invitationButton;
    @InjectView(R.id.rules_button) Button rulesButton;

    public StartView(Context context, AttributeSet attrs) {
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

    @OnClick(R.id.start_game_button) void onGameStart() {
        presenter.onGameStart();
    }

    @OnClick(R.id.invitations_button) void onInvitationList() {
        presenter.onInvitationList();
    }

    @OnClick(R.id.rules_button) void onRules() {
        presenter.showRules();
    }

    public void blockElements() {
        button.setEnabled(false);
        invitationButton.setEnabled(false);
        rulesButton.setEnabled(false);
    }

    public void unblockElements() {
        button.setEnabled(true);
        invitationButton.setEnabled(true);
        rulesButton.setEnabled(true);
    }
}
