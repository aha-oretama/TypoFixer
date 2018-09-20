package jp.aha.oretama.typoChecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.aha.oretama.typoChecker.configuration.TestRestTemplateConfiguration;
import jp.aha.oretama.typoChecker.model.Event;
import jp.aha.oretama.typoChecker.model.Token;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author aha-oretama
 */
@RunWith(SpringRunner.class)
@RestClientTest({GitHubTemplate.class, TestRestTemplateConfiguration.class})
public class GitHubTemplateTest {

    @Autowired
    private GitHubTemplate template;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void getAuthToken() throws IOException, GeneralSecurityException {
        // Input
        Event event = new Event();
        Event.Installation installation = new Event.Installation();
        installation.setId("1234");
        event.setInstallation(installation);

        // Response
        Token token = new Token();
        token.setToken("1234567890");
        token.setExpiresAt("2020/10/10");
        String response = mapper.writeValueAsString(token);

        this.server
                .expect(requestTo("https://api.github.com/installations/1234/access_tokens"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", new StringContains("Bearer")))
                .andExpect(header("Accept", "application/vnd.github.machine-man-preview+json"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Token authToken = this.template.getAuthToken(event);
        assertEquals(token.getToken(), authToken.getToken());
        assertEquals(token.getExpiresAt(), authToken.getExpiresAt());
    }
}