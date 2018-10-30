package jp.aha.oretama.typoFixer.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.aha.oretama.typoFixer.configuration.TestRestTemplateConfiguration;
import jp.aha.oretama.typoFixer.model.Comment;
import jp.aha.oretama.typoFixer.model.Status;
import jp.aha.oretama.typoFixer.model.Token;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * @author aha-oretama
 */
@RestClientTest({GitHubRepository.class, TestRestTemplateConfiguration.class})
public class GitHubRepositoryTest {

    @Autowired
    private GitHubRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    private EncryptionRepository encryptionRepository;

    @Test
    public void getAuthToken() throws IOException, GeneralSecurityException {
        // Input
        String installationId = "1234";

        // Mock
        doReturn("jwt1234").when(encryptionRepository).getJwt();

        // Response
        Token token = new Token();
        token.setToken("1234567890");
        token.setExpiresAt("2020/10/10");
        String response = mapper.writeValueAsString(token);

        this.server
                .expect(requestTo("https://api.github.com/installations/1234/access_tokens"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer jwt1234"))
                .andExpect(header("Accept", "application/vnd.github.machine-man-preview+json"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Token authToken = this.repository.getAuthToken(installationId);
        assertEquals(token.getToken(), authToken.getToken());
        assertEquals(token.getExpiresAt(), authToken.getExpiresAt());
    }

    @Test
    public void getRawContentWhenFileExists() {
        // Input
        String contentsUrl = "http://api.github.com/repos/octocat/Hello-World/contents/{+path}";
        String ref = "new-topic";
        String token = "1234567890";
        // Define a path
        String path = "src/test/java/jp/aha/oretama/typoFixer/GitHubTemplateTest.java";

        // Response
        String content = "This is content";

        this.server
                .expect(requestTo("http://api.github.com/repos/octocat/Hello-World/contents/src/test/java/jp/aha/oretama/typoFixer/GitHubTemplateTest.java?ref=new-topic"))
                .andExpect(header("Authorization", "token 1234567890"))
                .andExpect(header("Accept", "application/vnd.github.VERSION.raw"))
                .andRespond(withSuccess(content, MediaType.APPLICATION_JSON))
        ;

        Optional<String> result = repository.getRawContent(contentsUrl, path, ref, token);
        assertEquals(content, result.get());
    }

    @Test
    public void getRawContentWhenFileNotExists() {
        // Input
        String contentsUrl = "http://api.github.com/repos/octocat/Hello-World/contents/{+path}";
        String ref = "new-topic";
        String token = "1234567890";
        // Define a path
        String path = "src/test/java/jp/aha/oretama/typoFixer/GitHubTemplateTest.java";

        this.server
                .expect(requestTo("http://api.github.com/repos/octocat/Hello-World/contents/src/test/java/jp/aha/oretama/typoFixer/GitHubTemplateTest.java?ref=new-topic"))
                .andExpect(header("Authorization", "token 1234567890"))
                .andExpect(header("Accept", "application/vnd.github.VERSION.raw"))
                .andRespond(withNoContent());

        Optional<String> result = repository.getRawContent(contentsUrl, path, ref, token);
        assertFalse(result.isPresent());
    }

    @Test
    public void getRawDiff() {
        // Input
        String diffUrl = "https://github.com/octocat/Hello-World/pull/1347.diff";
        String token = "1234";

        // Output
        String content = "This is raw diff.";

        this.server
                .expect(requestTo("https://github.com/octocat/Hello-World/pull/1347.diff"))
                .andExpect(header("Authorization", "token 1234"))
                .andExpect(header("Accept", "application/vnd.github.machine-man-preview+json"))
                .andRespond(withSuccess(content, MediaType.APPLICATION_JSON));

        String rawDiff = repository.getRawDiff(diffUrl, token);
        assertEquals(content, rawDiff);
    }

    @Test
    public void updateStatus() {
        String token = "1234567890";
        String url = "https://api.github.com/repos/octocat/Hello-World/statuses/6dcb09b5b57875f334f61aebed695e2e4193db5e";

        this.server
                .expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"state\": \"pending\" ,\"target_url\": \"https://github.com/apps/typofixer\",\"description\":\"TypoFixer points out your typos instead of reviewers.\",\"context\":\"TypoFixer\" }"))
                .andExpect(header("Authorization", "token 1234567890"))
                .andExpect(header("Accept", "application/vnd.github.machine-man-preview+json"))
                .andRespond(withCreatedEntity(URI.create(url)));

        boolean result = this.repository.updateStatus(url, Status.Pending, token);
        assertTrue(result);
    }

    @Test
    public void getComments() throws JsonProcessingException {
        // Input
        String token = "1234567890";
        String url = "https://api.github.com/repos/Codertocat/Hello-World/pulls/1/comments";

        // Output
        Comment comment = new Comment();
        comment.setPath("README.md");
        comment.setPosition(3);
        comment.setBody("This is test");
        List<Comment> comments = Collections.singletonList(comment);
        String response = mapper.writeValueAsString(comments);

        // Mock
        this.server
                .expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "token 1234567890"))
                .andExpect(header("Accept", "application/vnd.github.v3.raw+json"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        List<Comment> result = this.repository.getComments(url, token);

        assertEquals(1, result.size());
        Comment resultComment = result.get(0);
        assertEquals("README.md", resultComment.getPath());
        assertEquals(3, resultComment.getPosition().intValue());
        assertEquals("This is test", resultComment.getBody());
    }
}