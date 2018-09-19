package jp.aha.oretama.typoChecker;

import jp.aha.oretama.typoChecker.configuration.RestTemplateConfiguration;
import jp.aha.oretama.typoChecker.model.Event;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

/**
 * @author aha-oretama
 */
@RunWith(SpringRunner.class)
@RestClientTest({GitHubTemplate.class, RestTemplateConfiguration.class})
public class GitHubTemplateTest {

    @Autowired
    private GitHubTemplate template;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void getAuthToken() throws IOException, GeneralSecurityException {

        Event event = new Event();
        Event.Installation installation = new Event.Installation();
        installation.setId("1234");
        event.setInstallation(installation);

        this.server
                .expect(requestTo("https://api.github.com/installations/1234/access_tokens"));
//                .andExpect(method(HttpMethod.POST))
//                .andExpect(header("Authorization", new StringContains("Bearer")))
//                .andExpect(header("Accept", "application/vnd.github.machine-man-preview+json"));

        this.template.getAuthToken(event);
    }
}