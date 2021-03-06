package qna.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qna.CannotDeleteException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnswerTest {
    public static final Answer A1 = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2");

    @DisplayName("삭제 하고 delete 상태 변경")
    @Test
    public void deleted_success() throws Exception {
        //given
        Answer answer = A1.deleted(true, UserTest.JAVAJIGI);

        //then
        assertTrue(answer.isDeleted());
    }

    @DisplayName("삭제 가능한 유저인지 체크 하고 삭제")
    @Test
    public void delete_success_checkUser() throws Exception {
        A1.deleted(true, UserTest.JAVAJIGI);
    }

    @DisplayName("삭제 가능한 유저 아닐경우 체크")
    @Test
    public void delete_fail_checkUser() throws Exception {
        assertThatThrownBy(
                () -> A1.deleted(true, UserTest.SANJIGI)
        ).isInstanceOf(CannotDeleteException.class);
    }
}
