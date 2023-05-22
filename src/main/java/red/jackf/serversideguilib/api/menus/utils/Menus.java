package red.jackf.serversideguilib.api.menus.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import red.jackf.serversideguilib.api.labels.Label;
import red.jackf.serversideguilib.api.menus.CancellableCallback;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Collection of menus that cover common use cases and utilities.
 */
public class Menus {
    private Menus() {
    }

    /**
     * Opens a menu that allows the player to select from a list of options. Uses the smallest menu possible, with pages if
     * the number of options exceeds 54. Allows filtering if paginated.
     *
     * @param player   Player that this menu opens for
     * @param title    Title to show at the top of the menu
     * @param options  List of pairs from {@link Label}s to options. The selected option from this list is passed to {@link CancellableCallback#complete(T)}
     * @param callback Callback that handles completion/cancellation
     * @param <T>      Type of option that will be returned
     */
    public static <T> void selector(ServerPlayer player, Component title, List<Pair<Label, T>> options, CancellableCallback<T> callback) {
        new SelectorMenu<>(player, title, options, callback).open();
    }

    /**
     * Convenience overload for {@link Menus#selector(ServerPlayer, Component, List, CancellableCallback)}. For consistent ordering,
     * use a linked map such as {@link java.util.LinkedHashMap}.
     */
    public static <T> void selector(ServerPlayer player, Component title, Map<Label, T> options, CancellableCallback<T> callback) {
        selector(player, title, options.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList()), callback);
    }

    /**
     * Opens a text input field, using the Anvil menu. Click the output slot to accept the new value, or the input slot to
     * cancel. This method allows for a predicate to be applied to the text input.
     *
     * @param player      Player that this menu opens for
     * @param title       Title to show at the top of the menu
     * @param additional  {@link Label} that gets put in the additional slot; use this with {@link red.jackf.serversideguilib.api.labels.Label.LabelBuilder#hint(String)} to add
     *                    context via hints to the GUI i.e. bounds
     * @param initialText Initial text to be shown. Usually, the previously set value for e.g. a config. Note: the output
     *                    field will not be updated when the text field is blank (MC-124327)
     * @param predicate   Predicate to test if the anvil should show a result
     * @param callback    Callback that handles completion/cancellation
     */
    public static void string(ServerPlayer player, Component title, @Nullable Label additional, String initialText, Predicate<String> predicate, CancellableCallback<String> callback) {
        new TextMenu(player, title, additional, initialText, predicate, callback).open();
    }

    /**
     * Opens a text input field, using the Anvil menu. Click the output slot to accept the new value, or the input slot to
     * cancel.
     *
     * @param player      Player that this menu opens for
     * @param title       Title to show at the top of the menu
     * @param additional  {@link Label} that gets put in the additional slot; use this with {@link red.jackf.serversideguilib.api.labels.Label.LabelBuilder#hint(String)} to add
     *                    context via hints to the GUI i.e. bounds
     * @param initialText Initial text to be shown. Usually, the previously set value for e.g. a config. Note: the output
     *                    field will not be updated when the text field is blank
     * @param callback    Callback that handles completion/cancellation
     */
    public static void string(ServerPlayer player, Component title, @Nullable Label additional, String initialText, CancellableCallback<String> callback) {
        string(player, title, additional, initialText, null, callback);
    }

    /**
     * Opens a bounded integer input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel.
     *
     * @param player       Player that this menu opens for
     * @param title        Title to show at the top of the menu
     * @param additional   {@link Label} that gets put in the additional slot; use this with {@link red.jackf.serversideguilib.api.labels.Label.LabelBuilder#hint(String)}
     *                     to add context via hints to the GUI i.e. bounds
     * @param initialValue Initial value to be shown. Usually the previous value. Defaults to 0.
     * @param lowerBound   Lower bound that the result is allowed to be.
     * @param upperBound   Upper bound that the result is allowed to be.
     * @param callback     Callback that handles completion/cancellation
     */
    public static void boundedInteger(ServerPlayer player, Component title, @Nullable Label additional, @Nullable Integer initialValue, Integer lowerBound, Integer upperBound, CancellableCallback<Integer> callback) {
        var initial = String.valueOf(initialValue != null ? initialValue : Mth.clamp(0, lowerBound, upperBound));
        if (upperBound < lowerBound)
            throw new IllegalArgumentException("Upper bound must be greater than or equal than lower bound");
        if (additional == null && (lowerBound != Integer.MIN_VALUE || upperBound != Integer.MAX_VALUE)) {
            var label = Label.builder().name("Additional Info").item(Items.PAPER);
            var str = "x";
            if (lowerBound != Integer.MIN_VALUE) str = lowerBound + " ≤ " + str;
            if (upperBound != Integer.MAX_VALUE) str = str + " ≤ " + upperBound;
            label.hint(str);
            additional = label.build();
        }
        string(player, title, additional, initial, s -> {
            try {
                var i = Integer.parseInt(s);
                return lowerBound <= i && i <= upperBound;
            } catch (NumberFormatException ex) {
                return false;
            }
        }, CancellableCallback.of(s -> {
            try {
                var parsed = Integer.parseInt(s);
                if (lowerBound <= parsed && parsed <= upperBound) callback.complete(parsed);
                else callback.cancel();
            } catch (NumberFormatException ex) {
                callback.cancel();
            }
        }, callback::cancel));
    }

    /**
     * Opens an unbounded integer input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel.
     *
     * @param player       Player that this menu opens for
     * @param title        Title to show at the top of the menu
     * @param additional   {@link Label} that gets put in the additional slot; use this with {@link red.jackf.serversideguilib.api.labels.Label.LabelBuilder#hint(String)}
     *                     to add context via hints to the GUI i.e. bounds
     * @param initialValue Initial value to be shown. Usually the previous value. Defaults to 0.
     * @param callback     Callback that handles completion/cancellation
     */
    public static void integer(ServerPlayer player, Component title, @Nullable Label additional, @Nullable Integer initialValue, CancellableCallback<Integer> callback) {
        var initial = String.valueOf(initialValue != null ? initialValue : 0);
        string(player, title, additional, initial, s -> {
            try {
                var ignored = Integer.parseInt(s);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }, CancellableCallback.of(s -> {
            try {
                callback.complete(Integer.parseInt(s));
            } catch (NumberFormatException ex) {
                callback.cancel();
            }
        }, callback::cancel));
    }

    /**
     * Opens a bounded double input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel. This version does not allow NaN values.
     *
     * @param player       Player that this menu opens for
     * @param title        Title to show at the top of the menu
     * @param additional   {@link Label} that gets put in the additional slot; use this with {@link red.jackf.serversideguilib.api.labels.Label.LabelBuilder#hint(String)}
     *                     to add context via hints to the GUI i.e. bounds
     * @param initialValue Initial value to be shown. Usually the previous value. Defaults to 0. Will throw an error if NaN.
     * @param lowerBound   Lower bound that the result is allowed to be. Will throw an error if NaN.
     * @param upperBound   Upper bound that the result is allowed to be. Will throw an error if NaN.
     * @param callback     Callback that handles completion/cancellation
     */
    public static void boundedDouble(ServerPlayer player, Component title, @Nullable Label additional, @Nullable Double initialValue, Double lowerBound, Double upperBound, CancellableCallback<Double> callback) {
        if (initialValue != null && initialValue.isNaN())
            throw new IllegalArgumentException("Initial value must not be NaN");
        var initial = String.valueOf(initialValue != null ? initialValue : Mth.clamp(0, lowerBound, upperBound));
        if (lowerBound.isNaN()) throw new IllegalArgumentException("Lower bound must not be NaN");
        if (upperBound.isNaN()) throw new IllegalArgumentException("Upper bound must not be NaN");
        if (upperBound < lowerBound)
            throw new IllegalArgumentException("Upper bound must be greater than or equal than lower bound");
        if (additional == null && (lowerBound != Double.NEGATIVE_INFINITY || upperBound != Double.POSITIVE_INFINITY)) {
            var label = Label.builder().name("Additional Info").item(Items.PAPER);
            var str = "x";
            if (lowerBound != Double.NEGATIVE_INFINITY) str = lowerBound + " ≤ " + str;
            if (upperBound != Double.POSITIVE_INFINITY) str = str + " ≤ " + upperBound;
            label.hint(str);
            additional = label.build();
        }
        string(player, title, additional, initial, s -> {
            try {
                var d = Double.parseDouble(s);
                if (Double.isNaN(d)) return false;
                return lowerBound <= d && d <= upperBound;
            } catch (NumberFormatException ex) {
                return false;
            }
        }, CancellableCallback.of(s -> {
            try {
                double parsed = Double.parseDouble(s);
                if (Double.isNaN(parsed)) {
                    callback.cancel();
                    return;
                }
                if (lowerBound <= parsed && parsed <= upperBound) callback.complete(parsed);
                else callback.cancel();
            } catch (NumberFormatException ex) {
                callback.cancel();
            }
        }, callback::cancel));
    }

    /**
     * Opens an unbounded double input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel. This version does not allow NaN values.
     *
     * @param player       Player that this menu opens for
     * @param title        Title to show at the top of the menu
     * @param additional   {@link Label} that gets put in the additional slot; use this with {@link red.jackf.serversideguilib.api.labels.Label.LabelBuilder#hint(String)}
     *                     to add context via hints to the GUI i.e. bounds
     * @param initialValue Initial value to be shown. Usually the previous value. Defaults to 0. Will throw an error if NaN.
     * @param callback     Callback that handles completion/cancellation
     */
    public static void ddouble(ServerPlayer player, Component title, @Nullable Label additional, @Nullable Double initialValue, CancellableCallback<Double> callback) {
        if (initialValue != null && initialValue.isNaN())
            throw new IllegalArgumentException("Initial value must not be NaN");
        var initial = String.valueOf(initialValue != null ? initialValue : 0);
        string(player, title, additional, initial, s -> {
            try {
                var d = Double.parseDouble(s);
                return !Double.isNaN(d);
            } catch (NumberFormatException ex) {
                return false;
            }
        }, CancellableCallback.of(s -> {
            try {
                double parsed = Double.parseDouble(s);
                if (Double.isNaN(parsed)) {
                    callback.cancel();
                    return;
                }
                callback.complete(parsed);
            } catch (NumberFormatException ex) {
                callback.cancel();
            }
        }, callback::cancel));
    }

    /**
     * Opens an unbounded double input field, using the Anvil menu. Click the output slot to accept the new value, or the input
     * slot to cancel. This version allows NaN values.
     *
     * @param player       Player that this menu opens for
     * @param title        Title to show at the top of the menu
     * @param additional   {@link Label} that gets put in the additional slot; use this with {@link red.jackf.serversideguilib.api.labels.Label.LabelBuilder#hint(String)}
     *                     to add context via hints to the GUI i.e. bounds
     * @param initialValue Initial value to be shown. Usually the previous value. Defaults to 0.
     * @param callback     Callback that handles completion/cancellation
     */
    public static void doubleAllowingNaN(ServerPlayer player, Component title, @Nullable Label additional, @Nullable Double initialValue, CancellableCallback<Double> callback) {
        var initial = String.valueOf(initialValue != null ? initialValue : 0);
        string(player, title, additional, initial, s -> {
            try {
                var ignored = Double.parseDouble(s);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }, CancellableCallback.of(s -> {
            try {
                callback.complete(Double.parseDouble(s));
            } catch (NumberFormatException ex) {
                callback.cancel();
            }
        }, callback::cancel));
    }
}
