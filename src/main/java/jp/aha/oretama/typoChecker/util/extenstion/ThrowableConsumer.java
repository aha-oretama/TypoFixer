package jp.aha.oretama.typoChecker.util.extenstion;

/**
 * @author aha-oretama
 */
public interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
}
