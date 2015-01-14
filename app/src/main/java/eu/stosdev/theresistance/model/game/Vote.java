package eu.stosdev.theresistance.model.game;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.stosdev.theresistance.model.messages.VoteFinishedMessage;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Vote {
    final List<String> voters = new ArrayList<>();
    @Getter private final boolean secret;
    final Map<String, Boolean> votes = new HashMap<>();
    final Optional<OnVoteCompleted> callback;

    Vote(List<String> voters, OnVoteCompleted callback) {
        this.voters.addAll(voters);
        this.secret = true;
        this.callback = Optional.fromNullable(callback);
    }

    public void vote(String voter, Boolean vote) {
        checkArgument(voters.contains(voter), "Provided player is not a voter");
        if (votes.get(voter) == null) {
            votes.put(voter, vote);
            onVoteCompleted();
        }
    }

    protected abstract void onVoteCompleted();

    public List<String> getVoters() {
        return Collections.unmodifiableList(voters);
    }

    public Map<String, Boolean> getAllVotes() {
        return Collections.unmodifiableMap(votes);
    }

    public boolean isCompleted() {
        return votes.size() == voters.size() && !votes.values().contains(null);
    }

    public boolean getVerdict() {
        int approved = 0, against = 0;
        for (Boolean aBoolean : votes.values()) {
            if (aBoolean != null) {
                if (aBoolean) {
                    approved++;
                } else {
                    against++;
                }
            }
        }
        return getVerdict(approved, against);
    }

    protected abstract boolean getVerdict(int approved, int against);

    public void parseFinishedMessage(VoteFinishedMessage.Event vfe) {
        if (vfe.isUsed()){
            return;
        }
        for (Map.Entry<String, Boolean> entry : vfe.getVotes().entrySet()) {
            if (isCompleted()) {
                break;
            }
            vote(entry.getKey(), entry.getValue());
        }
    }

    public interface OnVoteCompleted {
        void onVoteCompleted(boolean result);
    }

}
