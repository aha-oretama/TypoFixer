package jp.aha.oretama.typoChecker.utils.extenstions;

/**
 * @author aha-oretama
 */
public interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
}
