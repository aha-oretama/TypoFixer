package jp.aha.oretama.typoFixer.util.extenstion;

/**
 * @author aha-oretama
 */
public interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
}
