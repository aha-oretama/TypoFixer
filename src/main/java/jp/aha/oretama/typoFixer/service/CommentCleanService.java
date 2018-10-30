package jp.aha.oretama.typoFixer.service;

import jp.aha.oretama.typoFixer.model.Comment;
import jp.aha.oretama.typoFixer.model.Suggestion;
import jp.aha.oretama.typoFixer.repository.GitHubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Service
@RequiredArgsConstructor
public class CommentCleanService {

    private final GitHubRepository repository;

    public static final String BOT_USER = "typofixer[bot]";

    public List<Suggestion> filterByPreviousComments(List<Suggestion> suggestions, String commentsUrl, String token) {
        List<Comment> comments = repository.getComments(commentsUrl, token);

        return suggestions.stream().filter(suggestion ->
                comments.stream().filter(comment -> comment.getUser().getLogin().equals(BOT_USER))
                        .noneMatch(comment -> suggestion.getPath().equals(comment.getPath())
                                && suggestion.getDiffLine().equals(comment.getPosition())
                                && suggestion.createMessage().equals(comment.getBody())))
                .collect(Collectors.toList());
    }
}
