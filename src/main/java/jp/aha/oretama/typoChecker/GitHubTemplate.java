package jp.aha.oretama.typoChecker;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jp.aha.oretama.typoChecker.model.Event;
import jp.aha.oretama.typoChecker.model.Modification;
import jp.aha.oretama.typoChecker.model.Suggestion;
import jp.aha.oretama.typoChecker.model.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class GitHubTemplate {

    @Value("${application.pem-file}")
    private String PEM_FILE;
    @Value("${application.app-id}")
    private String appId;
    private final RestTemplate restTemplate;
    private final ResourceLoader resourceLoader;

    private static final long EXPIRATION_TIME = 30 * 1000; // 30 second.
    private static final long TIME_DELTA = 5 * 1000; // 5 second.

    public Token getAuthToken(Event event) throws IOException, GeneralSecurityException {
        String jwt = getJwt();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer " + jwt);
        httpHeaders.add("Accept", "application/vnd.github.machine-man-preview+json");

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Token> response = restTemplate.exchange(String.format("https://api.github.com/installations/%s/access_tokens", event.getInstallation().getId()), HttpMethod.POST, requestEntity, Token.class);
        return response.getBody();
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
            if(responseEntity.getStatusCode() ==  HttpStatus.CREATED) {
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
            Map<String, String> map = getShaAndContent(token, contentUrl, ref);
            content = map.get("content");
            if(modification.isAdded()) {
                if (!content.endsWith("\n")) {
                    content += "\n";
                }
                content += modification.getTypo();
            }else {
                List<String> lines = IOUtils.readLines(new StringReader(content));
                lines.removeIf(s -> s.equals(modification.getTypo()));
                content = lines.stream().collect(Collectors.joining("\n"));
            }
            sha = Optional.of(map.get("sha"));
        }catch (final HttpClientErrorException e) {
            if(e.getStatusCode() != HttpStatus.NOT_FOUND) {
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

        Map<String, String> map = getShaAndContent(token, contentUrl, ref);

        List<String> lines = IOUtils.readLines(new StringReader(map.get("content")));
        String older = lines.get(modification.getLine() - 1);
        // That modification is not added means to revert the word from correct to typo.
        String newer = modification.isAdded() ?
                older.replaceAll(modification.getTypo(), modification.getCorrect()) :
                older.replaceAll(modification.getCorrect(), modification.getTypo());

        // There are no replacement
        if(older.equals(newer)) {
            return false;
        }

        lines.set(modification.getLine() - 1, newer);
        String newContent = lines.stream().collect(Collectors.joining("\n"));
        String newEncoded = Base64.getEncoder().encodeToString(newContent.getBytes(StandardCharsets.UTF_8));
        String message = modification.isAdded() ?
                String.format("TypoFixer has fixed typo from \"%s\" to \"%s\" at %d line.", modification.getTypo(), modification.getCorrect(), modification.getLine()) :
                String.format("TypoFixer has reverted typo from \"%s\" to \"%s\" at %d line.", modification.getCorrect(), modification.getTypo(), modification.getLine());
        return pushContent(token, contentUrl, newEncoded, message, Optional.of(map.get("sha")), ref);
    }

    @NotNull
    private Map<String,String> getShaAndContent(Token token, String contentUrl, String ref) {
        RequestEntity requestEntity = RequestEntity
                .get(URI.create(contentUrl + "?ref=" + ref))
                .header("Authorization", "token " + token.getToken())
                .header("Accept","application/vnd.github.VERSION.raw").build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("sha", responseEntity.getHeaders().getETag().replace("\"","")); // Etag starts and ends with ". Remove ".
        map.put("content", responseEntity.getBody());
        return map;
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


    public String getRawDiff(Event event, Token token) {
        RequestEntity requestEntity = RequestEntity
                .get(URI.create(event.getPullRequest().getDiffUrl()))
                .header("Authorization", "token " + token.getToken())
                .header("Accept", "application/vnd.github.machine-man-preview+json")
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return responseEntity.getBody();
    }

    public String getInstallation() throws IOException, GeneralSecurityException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer " + getJwt());
        httpHeaders.add("Accept", "application/vnd.github.machine-man-preview+json");

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> exchange = restTemplate.exchange("https://api.github.com/app/installations", HttpMethod.GET, entity, String.class);
        return exchange.getBody();
    }

    public List<String> getProjectDictionary(Event event, Token token) throws IOException {
        Event.Head head = event.getPullRequest().getHead();
        String contentsUrl = head.getRepo().getContentsUrl();
        contentsUrl = contentsUrl.replace("{+path}", "typofixer.dic");
        String ref = head.getRef();

        List<String> dict = new ArrayList<>();
        try {
            Map<String, String> map = getShaAndContent(token, contentsUrl, ref);
            String content = map.get("content");
            dict.addAll(IOUtils.readLines(new StringReader(content)));
        }catch (HttpClientErrorException e) {
            log.debug("There is no typofixer.dic.");
        }

        return dict;
    }

    private PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
        String pemStr = System.getenv().getOrDefault("PEM","");
        if (StringUtils.isEmpty(pemStr)) {
            Resource resource = resourceLoader.getResource("classpath:" + PEM_FILE);
            return EncryptionUtil.getPrivateKey(resource.getInputStream());
        }
        return EncryptionUtil.getPrivateKey(pemStr);
    }

    private String getJwt() throws IOException, GeneralSecurityException {
        String jwt = Jwts.builder().setIssuer(appId)
                .setIssuedAt(new Date(System.currentTimeMillis() - TIME_DELTA))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .compact();
        log.info(jwt);
        return jwt;
    }

}
