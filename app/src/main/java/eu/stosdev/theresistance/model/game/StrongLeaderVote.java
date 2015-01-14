package eu.stosdev.theresistance.model.game;

import java.util.List;

public class StrongLeaderVote extends Vote {

    public StrongLeaderVote(List<String> voters, OnVoteCompleted callback) {
        super(voters, callback);
    }

    @Override protected void onVoteCompleted() {
        callback.get().onVoteCompleted(getVerdict());
    }

    @Override public boolean isCompleted() {
        return super.isCompleted() || votes.containsValue(false);
    }

    @Override protected boolean getVerdict(int approved, int against) {
        return against == 0;
    }
}
