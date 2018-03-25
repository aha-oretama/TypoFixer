package jp.aha.oretama.typoChecker;

import jp.aha.oretama.typoChecker.model.Event;
import jp.aha.oretama.typoChecker.model.Suggestion;
import jp.aha.oretama.typoChecker.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aha-oretama
 */
@RestController
@RequiredArgsConstructor
public class TypoFixController {

    private final SpellCheckerService service;
    private final GitHubTemplate template;
    private static final String EVENT_TYPE ="pull_request";
    private static final List<String> ACTIONS = Arrays.asList("opened", "edited", "reopened");

    @GetMapping("ping")
    public Map<String ,String> ping() {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Ping is OK");
        return response;
    }

    // TODO: this is for debug. Delete in production.
    @GetMapping("valid")
    public String getValid() throws IOException, GeneralSecurityException {
        return template.getInstallation();
    }

    @PostMapping(value = "typo-fixer")
    public Map<String, String> helloWorld(@RequestHeader(value = "X-GitHub-Event", required = false) String eventType, @RequestBody(required = false) Event event) throws IOException, GeneralSecurityException {
        HashMap<String, String> response = new HashMap<>();

        // Webhooks is sent from pull request.
        if(StringUtils.isEmpty(eventType) || !eventType.equals(EVENT_TYPE)) {
            response.put("message", "Event is not pull_request.");
            return response;
        }

        // Filter target events.
        if (!ACTIONS.contains(event.getAction())) {
            response.put("message", "This comment event is not a target.");
            return response;
        }

        Token token = template.getAuthToken(event);
        String rawDiff = template.getRawDiff(event, token);
        List<Suggestion> suggestions = service.getSuggestions(rawDiff);
        boolean isCreated =  template.postComment(event, suggestions, token);

        response.put("message", isCreated ? "Comment succeeded." : "Comment Failed.");
        return response;
    }

}
