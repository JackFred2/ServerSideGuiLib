package red.jackf.serversideguilib.utils;

import java.util.function.Consumer;

/**
 * Takes two callbacks; one of which can return a value and is ran on a successful operation, and one that is ran on a failure
 *
 * @param onSuccess Callback to run on success
 * @param onFailure Callback to run on failure
 * @param <T>       Type of value to return on onSuccess
 */
public record CancellableCallback<T>(Consumer<T> onSuccess, Runnable onFailure) {
    public void complete(T value) {
        onSuccess.accept(value);
    }

    public void cancel() {
        onFailure.run();
    }

    public static <T> CancellableCallback<T> of(Consumer<T> success, Runnable failure) {
        return new CancellableCallback<>(success, failure);
    }
}
