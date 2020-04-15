package bowling.domain.frame.state;

import bowling.domain.Pins;

public class FirstBowl implements State {

    private final Pins firstPins;

    public FirstBowl(final Pins firstPins) {
        this.firstPins = firstPins;
    }

    @Override
    public State bowl(int pinsCount) {
        Pins second = firstPins.bowl(pinsCount);
        if (second.isFinish()) {
            return new Spare(firstPins, second);
        }

        return new Miss(firstPins, second);
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}