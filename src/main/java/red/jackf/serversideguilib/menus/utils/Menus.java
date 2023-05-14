package red.jackf.serversideguilib.menus.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.utils.CancellableCallback;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Collection of menus that cover common use cases and utilities.
 */
public class Menus {
    private Menus() {}

    /**
     * Opens a menu that allows the player to select from a list of options. Uses the smallest menu possible, with pages if
     * the number of options exceeds 54. Allows filtering if paginated.
     * @param player Player that this menu opens for
     * @param title Title to show at the top of the menu
     * @param options List of pairs from {@link Label}s to options. The selected option from this list is passed to {@link CancellableCallback#complete(T)}
     * @param callback Callback that handles completion/cancellation
     * @param <T> Type of option that will be returned
     */
    public static <T> void selector(ServerPlayer player, Component title, List<Pair<Label, T>> options, CancellableCallback<T> callback) {
        new SelectorMenu<>(player, title, options, callback).open();
    }

    /**
     * Convenience overload for {@link Menus#selector(ServerPlayer, Component, List, CancellableCallback)}. For consistent ordering,
     * use a linked map such as {@link java.util.LinkedHashMap}.
     */
    public static <T> void selector(ServerPlayer player, Component title, Map<Label, T> options, CancellableCallback<T> callback) {
        selector(player, title, options.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).collect(Collectors.toList()), callback);
    }

    /**
     * Opens a text input field, using the Anvil menu. Click the output slot to accept the new value, or the input slot to
     * cancel.
     * @param player Player that this menu opens for
     * @param title Title to show at the top of the menu
     * @param initialText Initial text to be shown. Usually, the previously set value for e.g. a config
     * @param callback Callback that handles completion/cancellation
     */
    public static void string(ServerPlayer player, Component title, String initialText, CancellableCallback<String> callback) {
        new TextMenu(player, title, initialText, callback).open();
    }

    /**
     * Opens an integer input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel. This version allows for integer bounds on the result.
     * @param player Player that this menu opens for
     * @param title Title to show at the top of the menu
     * @param initialValue Initial value to be shown. Usually the previous value. Use {@code null} to not display a value.
     * @param lowerBound Lower bound that the result is allowed to be.
     * @param upperBound Upper bound that the result is allowed to be.
     * @param clamp If this should clamp the value if outside the given range. False means that {@link CancellableCallback#cancel()} is
     *              called instead if outside the bounds.
     * @param callback Callback that handles completion/cancellation
     */
    public static void integer(ServerPlayer player, Component title, @Nullable Integer initialValue, Integer lowerBound, Integer upperBound, boolean clamp, CancellableCallback<Integer> callback) {
        string(player, title, initialValue != null ? String.valueOf(initialValue) : "", CancellableCallback.of(s -> {
            try {
                var parsed = Integer.parseInt(s);
                var clamped = Mth.clamp(parsed, lowerBound, upperBound);
                if (clamp || parsed == clamped) callback.complete(clamped);
                else callback.cancel();
            } catch (NumberFormatException ex) {
                callback.cancel();
            }
        }, callback::cancel));
    }

    /**
     * Opens an integer input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel. This version allows for integer bounds on the result. This overload for {@link Menus#integer(ServerPlayer, Component, Integer, Integer, Integer, boolean, CancellableCallback)} defaults to clamping instead of
     * cancellation.
     * @param player Player that this menu opens for
     * @param title Title to show at the top of the menu
     * @param initialValue Initial value to be shown. Usually the previous value. Use {@code null} to not display a value.
     * @param lowerBound Lower bound that the result is allowed to be.
     * @param upperBound Upper bound that the result is allowed to be.
     * @param callback Callback that handles completion/cancellation
     */
    public static void integer(ServerPlayer player, Component title, Integer initialValue, @Nullable Integer lowerBound, Integer upperBound, CancellableCallback<Integer> callback) {
        integer(player, title, initialValue, lowerBound, upperBound, true, callback);
    }

    /**
     * Opens an integer input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel. This overload for {@link Menus#integer(ServerPlayer, Component, Integer, Integer, Integer, boolean, CancellableCallback)}
     * does not do any bounds checking.
     * @param player Player that this menu opens for
     * @param title Title to show at the top of the menu
     * @param initialValue Initial value to be shown. Usually the previous value. Use {@code null} to not display a value.
     * @param callback Callback that handles completion/cancellation
     */
    public static void integer(ServerPlayer player, Component title, @Nullable Integer initialValue, CancellableCallback<Integer> callback) {
        integer(player, title, initialValue, Integer.MIN_VALUE, Integer.MAX_VALUE, true, callback);
    }
}
