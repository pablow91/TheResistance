package eu.stosdev.theresistance.view;

import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.utils.DpiConverter;

public class ToolbarController {

    private final static String SAVE_ACTIVE = "toolbar-active";
    private final static String SAVE_FRACTION = "toolbar-fraction";
    private final static int MAX_HEIGHT = 160;

    private Toolbar toolbar;
    private final DpiConverter c;

    @InjectView(R.id.fraction_type_view) FractionTypeView fractionImage;
    @InjectView(R.id.game_progress_view) GameProgressView gameProgressView;
    @InjectView(R.id.turn_progress_view) TurnProgressView turnProgressView;
    @InjectView(R.id.info_container) RelativeLayout infoContainer;

    private Boolean previousState;
    private Player.Type fraction;

    public ToolbarController(DpiConverter c) {
        this.c = c;
    }

    public void setTitle(@StringRes int stringRes) {
        toolbar.setTitle(stringRes);
    }

    public void setFraction(Player.Type fraction) {
        infoContainer.setVisibility(View.VISIBLE);
        this.fraction = fraction;
        if (fraction == null) {
            if (fractionImage != null) {
                toolbar.removeView(fractionImage);
                fractionImage = null;
            }
        } else {
            animateToolbar(toolbar.getHeight(), (int) c.dp(MAX_HEIGHT));
            fractionImage.setFraction(fraction);
        }
    }

    public void setCurrentLeader(@NonNull Player player) {
        String leader = toolbar.getContext().getString(R.string.leader);
        toolbar.setSubtitle(leader + ": " + player.getNick());
    }

    private int colorByState(boolean state) {
        return state ? Color.parseColor("#43A047") : Color.parseColor("#EF5350");//Green than red
    }

    public void setState(Boolean state) {
        if (previousState == null) {
            previousState = state;
            TypedValue tv = new TypedValue();
            toolbar.getContext().getTheme().resolveAttribute(R.attr.colorPrimary, tv, true);
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(tv.data), new ColorDrawable(colorByState(state))});
            toolbar.setBackground(transitionDrawable);
            transitionDrawable.startTransition(1000);
        }
        if (state != previousState) {
            TransitionDrawable transition;
            if (state == null) {
                TypedValue typedValue = new TypedValue();
                toolbar.getContext().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, typedValue, true);
                int data = typedValue.data;
                transition = new TransitionDrawable(new Drawable[]{new ColorDrawable(colorByState(previousState)), new ColorDrawable(data)});
            } else {
                transition = new TransitionDrawable(new Drawable[]{new ColorDrawable(colorByState(previousState)), new ColorDrawable(colorByState(state))});
            }
            toolbar.setBackground(transition);
            transition.startTransition(1000);
            previousState = state;
        }
    }

    public void resetState() {
        setState(null);
        setFraction(null);
        toolbar.setTitle("");
        infoContainer.setVisibility(View.GONE);
        hide();
    }

    private void animateToolbar(int from, int to) {
        ValueAnimator vAnim = ValueAnimator.ofInt(from, to);
        vAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
                layoutParams.height = (int) animation.getAnimatedValue();
                toolbar.setLayoutParams(layoutParams);
            }
        });
        vAnim.setInterpolator(new AccelerateInterpolator());
        vAnim.setDuration(500);
        vAnim.start();
    }

    private int getDefaultActionBarHeight() {
        final TypedArray styledAttributes = toolbar.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return mActionBarSize;
    }

    public void hide() {
        animateToolbar(toolbar.getHeight(), 0);
    }

    public void show() {
        animateToolbar(0, (int) (getDefaultActionBarHeight() + c.dp(25)));
    }

    public void loadState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_ACTIVE)) {
            previousState = null;
            fraction = null;
            fractionImage = null;
            boolean state = savedInstanceState.getBoolean(SAVE_ACTIVE);
            setState(state);
            String fraction = savedInstanceState.getString(SAVE_FRACTION, null);
            if (fraction != null) {
                setFraction(Player.Type.valueOf(fraction));
            }
        }
    }

    public void saveState(Bundle outState) {
        if (previousState != null) {
            outState.putBoolean(SAVE_ACTIVE, previousState);
            outState.putString(SAVE_FRACTION, fraction.toString());
        }
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        ButterKnife.inject(this, toolbar);
    }

    public void setTeamVoteResult(int vote, boolean result) {
        turnProgressView.setResult(vote, result);
    }

    public void setMissionVoteResult(int vote, @NonNull Player.Type result) {
        gameProgressView.setTurn(vote, result);
        turnProgressView.resetResults();
    }
}
