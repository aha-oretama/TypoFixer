package jp.aha.oretama.typoChecker.utils.extenstions;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author aha-oretama
 */
public class FuntionalExtension {
    public static <T> Consumer<T> Try(ThrowableConsumer<T> onTry, BiConsumer<Exception, T> onCatch) {
        return x -> {
            try {
                onTry.accept(x);
            } catch (Exception t) {
                onCatch.accept(t, x);
            }
        };
    }
}
