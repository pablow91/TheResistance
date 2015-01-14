package eu.stosdev.theresistance.model.game;

import java.util.List;

public class MissionVote extends Vote {

    private String publicResult;
    private boolean bla;

    public MissionVote(List<String> voters, OnVoteCompleted callback) {
        super(voters, callback);
    }

    @Override protected void onVoteCompleted() {
        if (isCompleted() && callback.isPresent()) {
            callback.get().onVoteCompleted(getVerdict());
        }
    }

    @Override protected boolean getVerdict(int approved, int against) {
        return against == 0;
    }

    public void setPublicResult(String playerId) {
        publicResult = playerId;
        bla = true;
    }

    public String getPublicResult() {
        return publicResult;
    }

    public boolean isBla() {
        return bla;
    }
}
