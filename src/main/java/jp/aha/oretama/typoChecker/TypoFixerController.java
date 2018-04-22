package jp.aha.oretama.typoChecker;

import jp.aha.oretama.typoChecker.model.Event;
import jp.aha.oretama.typoChecker.model.Modification;
import jp.aha.oretama.typoChecker.model.Suggestion;
import jp.aha.oretama.typoChecker.model.Token;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

/**
 * @author aha-oretama
 */
@RestController
@RequiredArgsConstructor
public class TypoFixerController {

    private final TypoCheckerService checkerService;
    private final TypoModifierService modifierService;
    private final GitHubTemplate template;

    private static final String PULL_REQUEST_EVENT_TYPE = "pull_request";
    private static final String COMMENT_EVENT_TYPE = "pull_request_review_comment";
    private static final List<String> PULL_REQUEST_ACTIONS = Arrays.asList("opened", "edited", "reopened", "synchronize");
    private static final String COMMENT_ACTION = "edited";

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
    public Map<String, String> typoFixer(@RequestHeader(value = "X-GitHub-Event", defaultValue = "") String eventType, @RequestBody(required = false) Event event) throws IOException, GeneralSecurityException {
        HashMap<String, String> response = new HashMap<>();

        Token token;
        switch (eventType) {
            case PULL_REQUEST_EVENT_TYPE:
                // Filter target events.
                if (!PULL_REQUEST_ACTIONS.contains(event.getAction())) {
                    response.put("message", "This event action is not a target.");
                    break;
                }

                token = template.getAuthToken(event);
                String rawDiff = template.getRawDiff(event, token);
                List<String> dictionary = template.getProjectDictionary(event, token);
                List<Suggestion> suggestions = checkerService.getSuggestions(rawDiff,dictionary);
                boolean isCreated =  template.postComment(event, suggestions, token);

                response.put("message", isCreated ? "Comment succeeded." : "Comment failed.");
                break;
            case COMMENT_EVENT_TYPE:
                if (!COMMENT_ACTION.equals(event.getAction())) {
                    response.put("message", "This event action is not a target.");
                    break;
                }

                token = template.getAuthToken(event);
                Optional<Modification> modification = modifierService.getModification(event);
                if(modification.isPresent()) {
                    boolean isModified = template.pushFromComment(event, modification.get(), token);
                    response.put("message", isModified ? "Pushing modification is succeeded." : "Pushing modification is failed.");
                }else {
                    response.put("message", "Not target format.");
                }
                break;
            default:
                response.put("message", "Event is not from GitHub or not target event.");
                break;
        }

        return response;
    }

}
