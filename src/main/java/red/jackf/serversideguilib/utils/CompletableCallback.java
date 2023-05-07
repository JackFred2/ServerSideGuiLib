package red.jackf.serversideguilib.utils;

import java.util.function.Consumer;

/**
 * Takes two callbacks; one of which can return a value and is ran on a successful operation, and one that is ran on a failure
 * @param success Callback to run on success
 * @param failure Callback to run on failure
 * @param <T> Type of value to return on success
 */
public record CompletableCallback<T>(Consumer<T> success, Runnable failure) {
    public void complete(T value) {
        success.accept(value);
    }

    public void cancel() {
        failure.run();
    }

    public static <T> CompletableCallback<T> of(Consumer<T> success, Runnable failure) {
        return new CompletableCallback<>(success, failure);
    }
}
