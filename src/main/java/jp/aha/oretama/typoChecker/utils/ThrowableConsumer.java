package jp.aha.oretama.typoChecker.utils;

/**
 * @author aha-oretama
 */
public interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
}
