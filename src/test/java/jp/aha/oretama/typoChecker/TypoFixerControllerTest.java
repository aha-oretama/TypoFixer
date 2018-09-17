package jp.aha.oretama.typoChecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.aha.oretama.typoChecker.model.Diff;
import jp.aha.oretama.typoChecker.model.Event;
import jp.aha.oretama.typoChecker.model.Suggestion;
import jp.aha.oretama.typoChecker.model.Token;
import jp.aha.oretama.typoChecker.parser.NoopParser;
import jp.aha.oretama.typoChecker.parser.Parser;
import jp.aha.oretama.typoChecker.parser.ParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author aha-oretama
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TypoFixerController.class)
@Slf4j
public class TypoFixerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TypoCheckerService checkerService;
    @MockBean
    private TypoModifierService modifierService;
    @MockBean
    private ParserFactory factory;
    @MockBean
    private GitHubTemplate template;

    private Token token = new Token();
    private Event event = new Event();
    private String rawDiff = "This is raw diff.";
    private String path = "src/main/java/test.java";
    private List<Diff> added = Collections.singletonList(new Diff(path, new HashMap<Integer, String>() {
        {
            put(99, "this is sentence");
        }
    }));
    private String content = "Content.\nThis is raw diff.";
    private List<Suggestion> suggestions = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        token = new Token();
        suggestions.add(new Suggestion(path, "this is sentence",99, null));
        Parser parser = new NoopParser(content);
        List<String> dictionary = new ArrayList<>();

        doReturn(token).when(template).getAuthToken(event);
        doReturn(rawDiff).when(template).getRawDiff(event, token);
        doReturn(added).when(checkerService).getAdded(rawDiff);
        doReturn(content).when(template).getRawContent(event, path, token);
        doReturn(parser).when(factory).create(path, content);
        doReturn(suggestions).when(checkerService).getSuggestions(added);
        doReturn(true).when(template).postComment(event, suggestions, token);
    }

    @Test
    public void pingReturnOk() throws Exception {
        Map<String, String> expected = new HashMap<>();
        expected.put("message", "Ping is OK");

        this.mvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));
    }

    @Test
    public void typoCheckerNotAcceptNoHeader() throws Exception {
        Map<String, String> expected = new HashMap<>();
        expected.put("message", "Event is not from GitHub or not target event.");

        this.mvc.perform(post("/typo-fixer"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));
    }

    @Test
    public void typoCheckerNotAcceptExceptPullRequest() throws Exception {
        Map<String, String> expected = new HashMap<>();
        expected.put("message", "Event is not from GitHub or not target event.");

        this.mvc.perform(post("/typo-fixer").header("X-GitHub-Event","issue_comment"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));
    }

    @Test
    public void typoCheckerNotWorkExceptTargetAction() throws Exception {
        // Arrange
        event.setAction("assigned");
        String body = mapper.writeValueAsString(event);
        log.info(body);

        Map<String, String> expected = new HashMap<>();
        expected.put("message", "This event action is not a target.");

        // Act
        this.mvc.perform(post("/typo-fixer").header("X-GitHub-Event","pull_request").content(body).contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));
    }

    @Test
    public void typoCheckerAcceptPullRequestOpened() throws Exception {
        // Arrange
        event.setAction("opened");
        String body = mapper.writeValueAsString(event);

        Map<String, String> expected = new HashMap<>();
        expected.put("message", "Comment succeeded.");

        // Act
        this.mvc.perform(post("/typo-fixer").header("X-GitHub-Event","pull_request").content(body).contentType(MediaType.APPLICATION_JSON))
        // Assert
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expected)));
    }
}