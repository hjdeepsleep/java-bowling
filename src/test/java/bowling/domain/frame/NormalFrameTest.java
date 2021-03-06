package bowling.domain.frame;

import bowling.domain.frame.state.Miss;
import bowling.domain.frame.state.Ready;
import bowling.domain.frame.state.Spare;
import bowling.domain.frame.state.Strike;
import bowling.domain.score.Score;
import bowling.exception.BowlingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NormalFrameTest {

    @DisplayName("1~10번 프레임 까지만 등록 가능 하다")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void validate_success_frameNumber(int number) throws Exception {
        //then
        new NormalFrame(number);
    }

    @DisplayName("1~10번 외의 프레임 까지만 등록시 exception")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0, 11, 15, 100})
    public void validate_fail(int number) throws Exception {
        //then
        assertThatThrownBy(
                () -> new NormalFrame(number)
        ).isInstanceOf(BowlingException.class);
    }

    @DisplayName("10번째 생성된 프레임은 FinalFrame 이어야 한다")
    @Test
    public void createNext_success_finalFrame() throws Exception {
        //given
        Frame frame = new NormalFrame();

        //when
        for (int i = 0; i < 9; i++) {
            frame = frame.createNext();
        }

        //then
        assertTrue(frame instanceof FinalFrame);
    }

    @DisplayName("다음 프레임을 생성하면 현재 프래임의 멤버에 등록 한다")
    @Test
    public void createNext_success() throws Exception {
        //given
        Frame frame = new NormalFrame();

        //when
        Frame next = frame.createNext();

        //then
        assertTrue(frame.getNext().equals(next));
    }

    @DisplayName("ready 상태에서 10개를 치면 strike 상태를 반환")
    @Test
    public void bowl_success_strike() throws Exception {
        //given
        Frame frame = new NormalFrame();

        //when
        frame.bowl(10);

        //then
        assertTrue(frame.getState() instanceof Strike);
    }

    @DisplayName("ready 상태에서 5개 연속으로 치면 spare 상태를 반환")
    @Test
    public void bowl_success_spare() throws Exception {
        //given
        Frame frame = new NormalFrame();

        //when
        frame.bowl(5);
        frame.bowl(5);

        //then
        assertTrue(frame.getState() instanceof Spare);
    }

    @DisplayName("ready 상태에서 2회 투구 하여 10개 처리 못하면 miss 상태를 반환")
    @Test
    public void bowl_success_miss() throws Exception {
        //given
        Frame frame = new NormalFrame();

        //when
        frame.bowl(1);
        frame.bowl(1);

        //then
        assertTrue(frame.getState() instanceof Miss);
    }

    @DisplayName("남은 핀이 없으면 해당 프레임은 완료 상태 이다")
    @Test
    public void isFinish_success_strike() throws Exception {
        //given
        Frame frame = new NormalFrame();

        //when
        frame = frame.bowl(10);

        //then
        assertTrue(frame.isFinish());
    }

    @DisplayName("남은 핀이 있으면 해당 프레임은 완료 상태가 아니다")
    @Test
    public void isFinish_success_miss() throws Exception {
        //given
        Frame frame = new NormalFrame();

        //when
        frame = frame.bowl(5);

        //then
        assertFalse(frame.isFinish());
    }

    @DisplayName("연결된 마지막 NormalFrame를 가져온다")
    @Test
    public void findLast_success() throws Exception {
        //given
        NormalFrame next = new NormalFrame(2);
        NormalFrame frame = new NormalFrame(1, next, new Ready());

        //when
        Frame last = frame.findLast();

        //then
        assertTrue(next == last);
    }

    @DisplayName("연결된 마지막 FinalFrame를 가져온다")
    @Test
    public void findLast_success_finalFrame() throws Exception {
        //given
        FinalFrame finalFrame = new FinalFrame();
        NormalFrame next = new NormalFrame(2, finalFrame, new Ready());
        NormalFrame frame = new NormalFrame(1, next, new Ready());

        //when
        Frame last = frame.findLast();

        //then
        assertTrue(finalFrame == last);
    }

    @DisplayName("현제 프레임이 strike 상태이면 다음 2개 투구의 점수를 합산해 준다")
    @Test
    public void makeStrikeScore_success() throws Exception {
        //given
        Frame frame = new NormalFrame();
        Score compare = new Score(16);

        //when
        frame.bowl(10);
        Frame next = frame.createNext();
        next.bowl(1);
        next.bowl(5);

        //then
        Score score = frame.getCurrentScore();
        assertTrue(score.equals(compare));
    }

    @DisplayName("현제 프레임이 spare 상태이면 다음 1개 투구의 점수를 합산해 준다")
    @Test
    public void getScoreCalculate_success() throws Exception {
        //given
        Frame frame = new NormalFrame();
        Score compare = new Score(11);

        //when
        frame.bowl(5);
        frame.bowl(5);
        Frame next = frame.createNext();
        next.bowl(1);

        //then
        Score score = frame.getCurrentScore();
        assertTrue(score.equals(compare));
    }
}
