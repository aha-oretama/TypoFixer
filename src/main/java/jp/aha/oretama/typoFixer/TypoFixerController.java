package jp.aha.oretama.typoFixer;

import jp.aha.oretama.typoFixer.model.*;
import jp.aha.oretama.typoFixer.repository.GitHubRepository;
import jp.aha.oretama.typoFixer.service.CommentCleanService;
import jp.aha.oretama.typoFixer.service.FilterService;
import jp.aha.oretama.typoFixer.service.TypoCheckerService;
import jp.aha.oretama.typoFixer.service.TypoModifierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * @author aha-oretama
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class TypoFixerController {

    private final TypoCheckerService checkerService;
    private final TypoModifierService modifierService;
    private final FilterService filterService;
    private final GitHubRepository repository;
    private final CommentCleanService cleanService;

    private static final String PULL_REQUEST_EVENT_TYPE = "pull_request";
    private static final String COMMENT_EVENT_TYPE = "pull_request_review_comment";
    private static final List<String> PULL_REQUEST_ACTIONS = Arrays.asList("opened", "edited", "reopened", "synchronize");
    private static final String COMMENT_ACTION = "edited";

    @GetMapping("ping")
    public Map<String, String> ping() {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Ping is OK");
        return response;
    }

    // TODO: this is for debug. Delete in production.
    @GetMapping("valid")
    public String getValid() throws IOException, GeneralSecurityException {
        return repository.getInstallation();
    }

    @PostMapping(value = "typo-fixer")
    public Map<String, String> typoFixer(@RequestHeader(value = "X-GitHub-Event", defaultValue = "") String eventType, @RequestBody(required = false) Event event) throws IOException, GeneralSecurityException {
        HashMap<String, String> response = new HashMap<>();

        Token token;
        String statusesUrl;
        switch (eventType) {
            case PULL_REQUEST_EVENT_TYPE:
                // Filter events except creating or updating a pull request.
                if (!PULL_REQUEST_ACTIONS.contains(event.getAction())) {
                    response.put("message", "This event action is not a target.");
                    break;
                }
                // To use GitHub's api, get token.
                token = repository.getAuthToken(event.getInstallation().getId());

                statusesUrl = event.getPullRequest().getStatusesUrl();
                // Update status to pending.
                repository.updateStatus(statusesUrl, Status.Pending, token.getToken());

                // Get added lines.
                String rawDiff = repository.getRawDiff(event.getPullRequest().getDiffUrl(), token.getToken());
                List<Diff> added = checkerService.getAdded(rawDiff);

                Event.Head head = event.getPullRequest().getHead();
                String contentsUrl = head.getRepo().getContentsUrl();
                String ref = head.getRef();

                // Get a configuration file
                Optional<String> configContent = repository.getRawContent(contentsUrl, "typo-fixer.json", ref, token.getToken());
                configContent.ifPresent(filterService::setConfig);
                filterService.filtering(added);

                // Get raw contents for each file.
                for (Diff diff : added) {
                    String content = repository.getRawContent(contentsUrl, diff.getPath(), ref, token.getToken())
                            .orElseThrow(() -> new RuntimeException("Getting contents fails. It may be because getting diffs is not correct."));
                    diff.setContent(content);
                }
                filterService.setParse();
                filterService.filtering(added);

                // Check typo.
                List<String> dictionary = checkerService.getRepositoryDictionary(event, token);
                checkerService.setDictionary(dictionary);
                List<Suggestion> suggestions = checkerService.getSuggestions(added);

                // Remove suggestions which previous comments exist.
                suggestions = cleanService.filterByPreviousComments(suggestions, event.getPullRequest().getReviewCommentsUrl(), token.getToken());

                // Create comments of pull request.
                boolean isCreated = repository.postComment(event, suggestions, token);

                if (isCreated) {
                    response.put("message", "Comment succeeded.");
                    // Update status to success.
                    repository.updateStatus(statusesUrl, Status.Success, token.getToken());
                } else {
                    response.put("message", "Comment failed.");
                    // Update status to failure.
                    repository.updateStatus(statusesUrl, Status.Failure, token.getToken());
                }
                break;
            case COMMENT_EVENT_TYPE:
                // Filter events except comments.
                if (!COMMENT_ACTION.equals(event.getAction())) {
                    response.put("message", "This event action is not a target.");
                    break;
                }
                Optional<Modification> modificationOpt = modifierService.getModification(event);
                if (modificationOpt.isEmpty()) {
                    response.put("message", "This event action is not a target.");
                    break;
                }
                Modification modification = modificationOpt.get();

                // To use GitHub's api, get token.
                token = repository.getAuthToken(event.getInstallation().getId());

                // Update status to pending.
                statusesUrl = event.getPullRequest().getStatusesUrl();
                repository.updateStatus(statusesUrl, Status.Pending, token.getToken());

                boolean isModified = repository.pushFromComment(event, modification, token);
                if (isModified) {
                    response.put("message", isModified ? "Pushing modification is succeeded." : "Pushing modification is failed.");
                }
                // Update status to success.
                repository.updateStatus(statusesUrl, Status.Success, token.getToken());
                break;
            default:
                response.put("message", "Event is not from GitHub or not target event.");
                break;
        }

        return response;
    }
}
