package jp.aha.oretama.typoChecker.repository;

import jp.aha.oretama.typoChecker.model.Event;
import jp.aha.oretama.typoChecker.model.Modification;
import jp.aha.oretama.typoChecker.model.Suggestion;
import jp.aha.oretama.typoChecker.model.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class GitHubRepository {

    private final RestTemplate restTemplate;
    private final EncryptionRepository encryptionRepository;

    public Token getAuthToken(String installationId) throws IOException, GeneralSecurityException {
        String jwt = encryptionRepository.getJwt();

        RequestEntity requestEntity = RequestEntity
                .post(URI.create(String.format("https://api.github.com/installations/%s/access_tokens", installationId)))
                .header("Authorization", "Bearer " + jwt)
                .header("Accept", "application/vnd.github.machine-man-preview+json")
                .build();

        ResponseEntity<Token> response = restTemplate.exchange(requestEntity, Token.class);
        return response.getBody();
    }

    public String getRawDiff(String diffUrl, String token) {
        RequestEntity requestEntity = RequestEntity
                .get(URI.create(diffUrl))
                .header("Authorization", "token " + token)
                .header("Accept", "application/vnd.github.machine-man-preview+json")
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return responseEntity.getBody();
    }

    public Optional<String> getRawContent(String contentsUrl, String path, String ref, String token) {
        String url = contentsUrl.replace("{+path}", path);
        return getContent(token, url, ref);
    }

    public boolean postComment(Event event, List<Suggestion> suggestions, Token token) {

        String contentsUrl = event.getPullRequest().getReviewCommentsUrl();
        boolean isAllCreated = true;

        for (Suggestion suggestion : suggestions) {
            Map<String, Object> body = new HashMap<>();
            body.put("body", suggestion.createMessage());
            body.put("commit_id", event.getPullRequest().getHead().getSha());
            body.put("path", suggestion.getPath());
            body.put("position", suggestion.getLine());

            RequestEntity requestEntity = RequestEntity
                    .post(URI.create(contentsUrl))
                    .header("Authorization", "token " + token.getToken())
                    .header("Accept", "application/vnd.github.machine-man-preview+json")
                    .body(body);

            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                isAllCreated = false;
                log.warn(String.format("The request is failed, path:%s, position:%d.", suggestion.getPath(), suggestion.getLine()));
            }
        }

        return isAllCreated;
    }

    public boolean pushFromComment(Event event, Modification modification, Token token) throws IOException {
        if (modification.getCorrect().equals(Suggestion.REGISTER_DICTIONARY)) {
            return pushDictionary(event, modification, token);
        }

        return pushReplacement(event, modification, token);
    }

    private boolean pushDictionary(Event event, Modification modification, Token token) throws IOException {
        Event.Head head = event.getPullRequest().getHead();
        String contentUrl = head.getRepo().getContentsUrl().replace("{+path}", "typofixer.dic");
        String ref = head.getRef();

        String content;
        Optional<String> sha = Optional.empty();
        try {
            Map<String, String> map = getShaAndContent(token.getToken(), contentUrl, ref);
            content = map.get("content");
            if (modification.isAdded()) {
                if (!content.endsWith("\n")) {
                    content += "\n";
                }
                content += modification.getTypo();
            } else {
                List<String> lines = IOUtils.readLines(new StringReader(content));
                lines.removeIf(s -> s.equals(modification.getTypo()));
                content = lines.stream().collect(Collectors.joining("\n"));
            }
            sha = Optional.of(map.get("sha"));
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw e;
            }
            content = modification.getTypo();
        }
        String encoded = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        String message = modification.isAdded() ?
                String.format("TypoFixer has registered word \"%s\" in \"typofixer.dic\" dictionary.", modification.getTypo()) :
                String.format("TypoFixer has removed word \"%s\" in \"typofixer.dic\" dictionary.", modification.getTypo());

        return pushContent(token, contentUrl, encoded, message, sha, ref);
    }

    private boolean pushReplacement(Event event, Modification modification, Token token) throws IOException {
        Event.Head head = event.getPullRequest().getHead();
        String contentUrl = head.getRepo().getContentsUrl();
        contentUrl = contentUrl.replace("{+path}", event.getComment().getPath());
        String ref = head.getRef();

        Map<String, String> map = getShaAndContent(token.getToken(), contentUrl, ref);

        List<String> lines = IOUtils.readLines(new StringReader(map.get("content")));
        String older = lines.get(modification.getLine() - 1);
        // That modification is not added means to revert the word from correct to typo.
        String newer = modification.isAdded() ?
                older.replaceAll(modification.getTypo(), modification.getCorrect()) :
                older.replaceAll(modification.getCorrect(), modification.getTypo());

        // There are no replacement
        if (older.equals(newer)) {
            return false;
        }

        lines.set(modification.getLine() - 1, newer);
        String newContent = String.join("\n", lines);
        String newEncoded = Base64.getEncoder().encodeToString(newContent.getBytes(StandardCharsets.UTF_8));
        String message = modification.isAdded() ?
                String.format("TypoFixer has fixed typo from \"%s\" to \"%s\" at %d line.", modification.getTypo(), modification.getCorrect(), modification.getLine()) :
                String.format("TypoFixer has reverted typo from \"%s\" to \"%s\" at %d line.", modification.getCorrect(), modification.getTypo(), modification.getLine());
        return pushContent(token, contentUrl, newEncoded, message, Optional.of(map.get("sha")), ref);
    }

    @NotNull
    private Map<String, String> getShaAndContent(String token, String contentUrl, String ref) {
        RequestEntity requestEntity = RequestEntity
                .get(URI.create(contentUrl + "?ref=" + ref))
                .header("Authorization", "token " + token)
                .header("Accept", "application/vnd.github.VERSION.raw").build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("sha", responseEntity.getHeaders().getETag().replace("\"", "")); // Etag starts and ends with ". Remove ".
        map.put("content", responseEntity.getBody());
        return map;
    }

    /**
     * Get a content in a repository.
     *
     * @return content's string. null if file does not exist.
     * @see <a href="https://developer.github.com/v3/repos/contents/#get-contents">GitHub API</a>
     */
    @NotNull
    private Optional<String> getContent(String token, String contentUrl, String ref) {
        String content;

        RequestEntity requestEntity = RequestEntity
                .get(URI.create(contentUrl + "?ref=" + ref))
                .header("Authorization", "token " + token)
                .header("Accept", "application/vnd.github.VERSION.raw").build();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            content = responseEntity.getBody();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw e;
            }
            content = null;
        }
        return Optional.ofNullable(content);
    }

    private boolean pushContent(Token token, String contentsUrl, String encoded, String message, Optional<String> sha, String ref) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("content", encoded);
        sha.ifPresent(s -> body.put("sha", s));
        body.put("branch", ref);

        RequestEntity requestEntity = RequestEntity
                .put(URI.create(contentsUrl))
                .header("Authorization", "token " + token.getToken())
                .header("Accept", "application/vnd.github.machine-man-preview+json")
                .body(body);

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return responseEntity.getStatusCode() == HttpStatus.OK || responseEntity.getStatusCode() == HttpStatus.CREATED;
    }

    public String getInstallation() throws IOException, GeneralSecurityException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + encryptionRepository.getJwt());
        httpHeaders.add("Accept", "application/vnd.github.machine-man-preview+json");

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> exchange = restTemplate.exchange("https://api.github.com/app/installations", HttpMethod.GET, entity, String.class);
        return exchange.getBody();
    }

}
