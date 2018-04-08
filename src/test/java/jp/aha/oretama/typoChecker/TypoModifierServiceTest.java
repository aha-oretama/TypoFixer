package jp.aha.oretama.typoChecker;

import jp.aha.oretama.typoChecker.model.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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
    public void setUp() throws Exception {
        event = new Event();

        comment = new Event.Comment();
        event.setComment(comment);

        Event.Change changes = new Event.Change();
        body = new Event.Body();
        changes.setBody(body);
        event.setChanges(changes);
    }

    @Test
    public void subtract() {
        // before
        comment.setBody("Potential error at characters 19-26: Possible spelling mistake found\r\nSuggested correction(s): \r\n- [x] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");
        // after
        body.setFrom("Potential error at characters 19-26: Possible spelling mistake found\r\nSuggested correction(s): \r\n- [ ] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");

        Map<Boolean, String> modifications = service.getModifications(event);

        assertEquals(1, modifications.size());
        assertEquals("print",modifications.get(false));
    }

    @Test
    public void added() {
        // before
        comment.setBody("Potential error at characters 19-26: Possible spelling mistake found\r\nSuggested correction(s): \r\n- [ ] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");
        // after
        body.setFrom("Potential error at characters 19-26: Possible spelling mistake found\r\nSuggested correction(s): \r\n- [x] print\r\n- [ ] printed\r\n- [ ] printing\r\n- [ ] prints\r\n- [ ] printer");

        Map<Boolean, String> modifications = service.getModifications(event);

        assertEquals(1, modifications.size());
        assertEquals("print",modifications.get(true));
    }

}