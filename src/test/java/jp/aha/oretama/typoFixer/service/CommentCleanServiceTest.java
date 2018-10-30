package jp.aha.oretama.typoFixer.service;

import jp.aha.oretama.typoFixer.model.Comment;
import jp.aha.oretama.typoFixer.model.Suggestion;
import jp.aha.oretama.typoFixer.repository.GitHubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author aha-oretama
 */
public class CommentCleanServiceTest {

    private CommentCleanService cleanService;
    private GitHubRepository repository;

    private String url = "https://api.github.com/repos/Codertocat/Hello-World/pulls/1/comments";
    private String token = "1234567890";

    private Suggestion suggestion = new Suggestion("README", "README BODY", 1, 1, 0, 11, new ArrayList<>());

    @BeforeEach
    public void setUp() throws Exception {
        repository = mock(GitHubRepository.class);
        cleanService = new CommentCleanService(repository);
    }

    private Comment createComment(String path, Integer position, String body, String name) {
        Comment comment = new Comment();
        comment.setPath(path);
        comment.setPosition(position);
        comment.setBody(body);
        Comment.User user = new Comment.User();
        user.setLogin(name);
        comment.setUser(user);
        return comment;
    }

    // Case1: path, position, body, login_user is same pattern.
    @Test
    public void filterAllByPreviousComments() {
        // Arrange
        when(repository.getComments(url, token)).thenReturn(Collections.singletonList(createComment("README", 1, suggestion.createMessage(), CommentCleanService.BOT_USER)));

        // Act
        List<Suggestion> suggestions = cleanService.filterByPreviousComments(Collections.singletonList(suggestion), url, token);

        // Assert
        assertTrue(suggestions.isEmpty());
    }

    // Case2: path, position, body is same pattern. login_user is not same.
    // Case3: path, position, login_user is same pattern. body is not same.
    // Case4: path, body , login_user is same pattern. position is not same.
    // Case5: position, body , login_user is same pattern. path is not same.
    @ParameterizedTest
    @CsvSource({
            "README, 1, , AnotherBot",
            "README, 1, AnotherBody , ",
            "README, 2, ,",
            "This is README, 1, , "
    })
    public void notFilterByPreviousComments(String path, Integer position, String body, String login) {
        // Arrange
        String message = body == null ? suggestion.createMessage() : body;
        String loginName = login == null ? CommentCleanService.BOT_USER : login;
        when(repository.getComments(url, token)).thenReturn(Collections.singletonList(createComment(path, position, message, loginName)));

        // Act
        List<Suggestion> suggestions = cleanService.filterByPreviousComments(Collections.singletonList(suggestion), url, token);

        // Assert
        assertEquals(1, suggestions.size());
    }
}