package bowling.domain.frame.state;

import bowling.domain.Pins;
import bowling.exception.BowlingException;

public class Spare implements State {

    private final Pins firstPins;
    private final Pins secondPins;

    public Spare(Pins firstPins, Pins secondPins) {
        validatePinsCount(firstPins);
        this.firstPins = firstPins;
        this.secondPins = secondPins;
    }

    private void validatePinsCount(Pins firstPins) {
        if (firstPins.isFinish()) {
            throw new BowlingException();
        }
    }

    @Override
    public State bowl(int pinsCount) {
        throw new BowlingException();
    }

    @Override
    public boolean isFinish() {
        return true;
    }
}