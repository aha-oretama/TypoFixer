package jp.aha.oretama.typoFixer.service;

import jp.aha.oretama.typoFixer.model.Event;
import jp.aha.oretama.typoFixer.model.Modification;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author aha-oretama
 */
public class TypoModifierServiceTest {

    Event event;
    Event.Comment comment;
    Event.Body body;

    TypoModifierService service = new TypoModifierService();

    @Before
    public void setUp() {
        event = new Event();

        comment = new Event.Comment();
        event.setComment(comment);

        Event.Change changes = new Event.Change();
        body = new Event.Body();
        changes.setBody(body);
        event.setChanges(changes);
    }

    @Test
    public void added() {
        // before
        comment.setBody("Typo? \"println\" at 14 line.\r\n- [x] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");
        // after
        body.setFrom("Typo? \"println\" at 14 line.\r\n- [ ] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");

        Optional<Modification> optModification = service.getModification(event);

        assertTrue(optModification.isPresent());
        Modification modification = optModification.get();
        assertTrue(modification.isAdded());
        assertEquals(14, modification.getLine());
        assertEquals("println", modification.getTypo());
        assertEquals("print", modification.getCorrect());
    }

    @Test
    public void subtracted() {
        // before
        comment.setBody("Typo? \"println\" at 14 line.\r\n- [ ] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");
        // after
        body.setFrom("Typo? \"println\" at 14 line.\r\n- [x] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");

        Optional<Modification> optModification = service.getModification(event);

        assertTrue(optModification.isPresent());
        Modification modification = optModification.get();
        assertFalse(modification.isAdded());
        assertEquals(14, modification.getLine());
        assertEquals("println", modification.getTypo());
        assertEquals("print", modification.getCorrect());
    }

    @Test
    public void otherComment() {
        // before
        comment.setBody("This is test before comment is changed.\nSecond line.");
        // after
        body.setFrom("This is test after comment is changed.\nSecond Line.\nAdd Third line.");

        Optional<Modification> optModification = service.getModification(event);

        assertTrue(!optModification.isPresent());
    }

}