package eu.stosdev.theresistance.view;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;

import eu.stosdev.theresistance.utils.DpiConverter;

public class FabController implements View.OnClickListener {

    private ImageButton fab;
    private final DpiConverter c;

    private Runnable listener;

    public FabController(DpiConverter converter) {
        this.c = converter;
    }

    public void show(@NonNull Runnable listener) {
        if (!isVisible()) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(fab, "translationY", c.dp(72), 0);
            anim.setDuration(500);
            anim.start();
        }
        this.listener = listener;
    }

    public void hide() {
        if (isVisible()) {
            listener = null;
            ObjectAnimator anim = ObjectAnimator.ofFloat(fab, "translationY", 0, c.dp(72));
            anim.setDuration(500);
            anim.start();
        }
    }

    private boolean isVisible() {
        return listener != null;
    }

    @Override public void onClick(View v) {
        if (listener != null) {
            Runnable curListener = listener;
            hide();
            curListener.run();
        }
    }

    public void setFab(ImageButton fab) {
        this.fab = fab;
        fab.setOnClickListener(this);
    }

}
