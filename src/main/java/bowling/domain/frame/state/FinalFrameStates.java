package bowling.domain.frame.state;

import bowling.domain.pin.Pins;
import bowling.domain.score.Score;
import bowling.exception.BowlingException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FinalFrameStates implements State {

    private static final int FINAL_STATE_SIZE = 2;
    private static final String FINAL_STATE_SIZE_ERR_MESSAGE = "마지막 프레임은 최대 3번 투구 가능";

    private final LinkedList<State> states;

    public FinalFrameStates(final LinkedList<State> states) {
        validateStateSize(states);
        this.states = new LinkedList<>(states);
    }

    public static FinalFrameStates of() {
        LinkedList<State> states = new LinkedList<>();
        states.add(new Ready());
        return new FinalFrameStates(states);
    }

    private void validateStateSize(final List<State> states) {
        if (states.size() > FINAL_STATE_SIZE) {
            throw new BowlingException(FINAL_STATE_SIZE_ERR_MESSAGE);
        }
    }

    public FinalFrameStates addState(final State state) {
        LinkedList<State> merge = new LinkedList<>(states);
        merge.add(state);
        return new FinalFrameStates(merge);
    }

    private boolean isThrowAbleBonus() {
        if (isNormalTurnStrikeOrSpare() && !isHaveBonus()) {
            return true;
        }
        return false;
    }

    private boolean isNormalTurnStrikeOrSpare() {
        if (states.getFirst() instanceof Strike
                || states.getFirst() instanceof Spare) {
            return true;
        }

        return false;
    }

    private boolean isNormalTurnFirstBowl() {
        if (isHaveBonus()) {
            return false;
        }

        if (states.getFirst() instanceof FirstBowl) {
            return true;
        }

        return false;
    }

    private boolean isNormalTurnMissOrGutter() {
        if (states.getLast() instanceof Miss || states.getLast() instanceof Gutter) {
            return true;
        }

        return false;
    }

    public boolean isHaveBonus() {
        if (states.size() == FINAL_STATE_SIZE && !(states.getLast() instanceof Ready)) {
            return true;
        }
        return false;
    }

    public Pins getFirstPins() {
        return states.getFirst().getPins();
    }

    public Pins getLastPins() {
        if (isHaveBonus()) {
            return states.getLast().getPins();
        }
        return null;
    }

    @Override
    public FinalFrameStates bowl(final int pinCount) {
        if (isFinish()) {
            throw new BowlingException(State.CANT_THROW_BALL);
        }

        if (isNormalTurnStrikeOrSpare()) {
            State third = new Ready();
            third = third.bowl(pinCount);
            return addState(third);
        }

        State bowl = states.getFirst().bowl(pinCount);
        LinkedList<State> states = new LinkedList<>(Arrays.asList(bowl));
        return new FinalFrameStates(states);
    }

    @Override
    public boolean isFinish() {
        if (states.getFirst() instanceof Ready) {
            return false;
        }

        if (isHaveBonus()) {
            return true;
        }

        if (isThrowAbleBonus()) {
            return false;
        }

        if (isNormalTurnFirstBowl()) {
            return false;
        }

        if (isNormalTurnMissOrGutter()) {
            return true;
        }

        if (states.size() == FINAL_STATE_SIZE) {
            return false;
        }

        return true;
    }

    @Override
    public Score getCurrentScore() {
        Score firstStateScore = states.getFirst().getCurrentScore();

        if (isHaveBonus()) {
            Score secondStateScore = states.getLast().getCurrentScore();
            return new Score(firstStateScore.getScore() + secondStateScore.getScore(), 0);
        }

        return new Score(firstStateScore.getScore(), 0);
    }

    @Override
    public Score getCalculateScore(Score before) {
        before = states.getFirst().getCalculateScore(before);

        if (before.canAddNextScore()) {
            return states.getLast().getCalculateScore(before);
        }

        return before;
    }

    @Override
    public Pins getPins() {
        throw new BowlingException();
    }
}
