package eu.stosdev.theresistance.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.screen.GameMainScreen;
import eu.stosdev.theresistance.utils.CanShowScreen;
import eu.stosdev.theresistance.utils.DpiConverter;
import eu.stosdev.theresistance.utils.ScreenConductor;
import flow.Flow;
import lombok.Getter;
import mortar.Blueprint;
import mortar.Mortar;

public class GameMainView extends RelativeLayout implements CanShowScreen<Blueprint> {
    @Inject GameMainScreen.Presenter presenter;
    @Inject DpiConverter c;
    @InjectView(R.id.container) FrameLayout container;
    @InjectView(R.id.card_container) RecyclerView mRecycleView;
    @InjectView(R.id.card_visibility_switcher) ImageButton switchButton;

    private ScreenConductor<Blueprint> gameMaestro;
    @Getter private CardController cardController;

    public GameMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
        gameMaestro = new ScreenConductor<>(getContext(), container);

        mRecycleView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecycleView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        MyAdapter mAdapter = new MyAdapter(getContext());
        mRecycleView.setAdapter(mAdapter);
        cardController = new CardController(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchButton.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
                }
            });
            switchButton.setClipToOutline(true);
        }

        if (!isInEditMode()) {
            presenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    @Override
    public void showScreen(Blueprint screen, Flow.Direction direction) {
        gameMaestro.showScreen(screen, direction);
    }

    public void setCardButtonEnable() {
        switchButton.animate().translationY(c.dp(232)).setDuration(500);
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private final List<PlotCard> plotCards = new ArrayList<>();
        private final Context context;
        private Optional<CardController.OnCardSelectedListener> callback = Optional.absent();

        public void setOnCardSelectedListener(@Nullable CardController.OnCardSelectedListener onCardSelectedListener) {
            callback = Optional.fromNullable(onCardSelectedListener);
        }

        public List<PlotCard> getItems() {
            return Collections.unmodifiableList(plotCards);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            @InjectView(R.id.card_view) CardView cardView;

            @InjectView(R.id.card_owner) TextView cardOwner;

            public ViewHolder(CardView v) {
                super(v);
                ButterKnife.inject(this, v);
            }

        }

        public MyAdapter(Context context) {
            this.context = context;
        }

        public void addCard(PlotCard plotCard) {
            plotCards.add(plotCard);
            notifyItemInserted(plotCards.size() - 1);
        }

        public void removeCard(PlotCard plotCard) {
            int i = plotCards.indexOf(plotCard);
            plotCards.remove(plotCard);
            notifyItemRemoved(i);
        }

        public void updateCard(PlotCard plotCard) {
            int i = plotCards.indexOf(plotCard);
            notifyItemChanged(i);
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_chart_layout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final PlotCard plotCard = plotCards.get(position);
            holder.cardOwner.setText(plotCard.getOwner());
            Picasso.with(context).load(plotCard.getDrawable()).into(new Target() {
                @Override public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            int rgb = palette.getDarkMutedSwatch().getRgb();
                            rgb = Color.argb(127, Color.red(rgb), Color.green(rgb), Color.blue(rgb));
                            holder.cardOwner.setBackgroundColor(rgb);
                            holder.cardOwner.setTextColor(palette.getDarkMutedSwatch().getBodyTextColor());
                        }
                    });
                }

                @Override public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            holder.cardView.setBackgroundResource(plotCard.getDrawable());
            holder.cardView.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    if (callback.isPresent()) {
                        callback.get().onCardSelected(plotCard);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return plotCards.size();
        }
    }

    private boolean visible;

    @OnClick(R.id.card_visibility_switcher)
    public void onCardVisibilitySwitched() {
        float a, b, d, e, startP, endP;
        int newContainerHeight;
        if (visible) {
            a = 0;
            b = c.dp(200);
            d = c.dp(32);
            e = c.dp(232);
            startP = 180;
            endP = 0;
            newContainerHeight = (int) (container.getHeight() + c.dp(200));
        } else {
            a = c.dp(200);
            b = 0;
            d = c.dp(232);
            e = c.dp(32);
            startP = 0;
            endP = 180;
            newContainerHeight = (int) (container.getHeight() - c.dp(200));
        }
        ValueAnimator vAnim = ValueAnimator.ofInt(container.getHeight(), newContainerHeight);
        vAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (LayoutParams) container.getLayoutParams();
                layoutParams.height = (int) animation.getAnimatedValue();
                container.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(mRecycleView, "translationY", a, b),
                ObjectAnimator.ofFloat(switchButton, "translationY", d, e),
                ObjectAnimator.ofFloat(switchButton, "rotation", startP, endP),
                vAnim);
        animatorSet.setDuration(500);
        animatorSet.start();
        visible = !visible;
    }
}

