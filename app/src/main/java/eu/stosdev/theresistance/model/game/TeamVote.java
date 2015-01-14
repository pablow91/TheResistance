package eu.stosdev.theresistance.model.game;

import java.util.Collections;
import java.util.List;

import eu.stosdev.theresistance.model.messages.VoteFinishedMessage;
import lombok.Getter;

public class TeamVote extends Vote {
    private final List<String> teamMembers;
    private final List<String> opinionMakers;
    private final List<String> restVoters;
    @Getter private final int approvalNeeded;
    @Getter private int approvalGathered;
    private boolean nullified;
    private String player;

    public TeamVote(List<String> restVoters, List<String> opinionMakers, List<String> teamMembers, int approvalNeeded, OnVoteCompleted onVoteCompleted) {
        super(opinionMakers.isEmpty() ? restVoters : opinionMakers, onVoteCompleted);
        this.teamMembers = teamMembers;
        this.approvalNeeded = approvalNeeded;
        this.opinionMakers = opinionMakers;
        this.restVoters = restVoters;
    }

    public List<String> getTeamMembers() {
        return Collections.unmodifiableList(teamMembers);
    }

    @Override public void parseFinishedMessage(VoteFinishedMessage.Event vfe) {
        if (vfe.isUsed()) {
            return;
        }
        super.parseFinishedMessage(vfe);
        if (voters.size() < opinionMakers.size() + restVoters.size()) {
            voters.addAll(restVoters);
        }
    }

    @Override protected void onVoteCompleted() {
        if (isCompleted() && isApproved() && callback.isPresent() && (voters.size() == opinionMakers.size() + restVoters.size())) {
            callback.get().onVoteCompleted(getVerdict());
        }
    }

    @Override protected boolean getVerdict(int approved, int against) {
        return !nullified && approved > against;
    }

    public boolean isApproved() {
        return (approvalGathered == approvalNeeded) || (isCompleted() && !getVerdict());
    }

    public void approveVerdict() {
        approvalGathered++;
        if (isApproved()) {
            callback.get().onVoteCompleted(getVerdict());
        }
    }

    public void nullifyVerdict(String player) {
        nullified = true;
        this.player = player;
        if (callback.isPresent()) {
            callback.get().onVoteCompleted(getVerdict());
        }
    }

    public boolean isNullified() {
        return nullified;
    }

    public String getPlayer() {
        return player;
    }

}
