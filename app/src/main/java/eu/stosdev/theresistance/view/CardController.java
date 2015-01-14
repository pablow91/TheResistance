package eu.stosdev.theresistance.view;

import java.util.List;

import eu.stosdev.theresistance.model.card.PlotCard;

public class CardController {
    private final GameMainView.MyAdapter adapter;

    public CardController(GameMainView.MyAdapter adapter) {
        this.adapter = adapter;
    }

    public void addCard(PlotCard plotCard) {
        adapter.addCard(plotCard);
    }

    public void removeCard(PlotCard plotCard) {
        adapter.removeCard(plotCard);
    }

    public void setOnCardSelectedListener(OnCardSelectedListener onCardSelectedListener) {
        adapter.setOnCardSelectedListener(onCardSelectedListener);
    }

    public List<PlotCard> getAllCards() {
        return adapter.getItems();
    }

    public void updateCard(PlotCard plotCard) {
        adapter.updateCard(plotCard);
    }

    public interface OnCardSelectedListener {
        void onCardSelected(PlotCard plotCard);
    }
}
