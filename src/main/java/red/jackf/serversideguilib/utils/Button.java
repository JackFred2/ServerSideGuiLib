package red.jackf.serversideguilib.utils;

import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.labels.Labels;

import java.util.function.Consumer;

/**
 * Represents a button on the GUI; this generally takes up a single slot.
 * @param label ItemStack shown in the GUI
 * @param handler Consumer that takes input.
 */
public record Button(Label label, Consumer<Input> handler) {
    public static final Consumer<Input> DO_NOTHING = i -> {};

    /**
     * Shorthand to create a button that runs on left click
     * @param label Label to display on the button
     * @param onClick Runnable to run on click
     * @return Created button
     */
    public static Button leftClick(Label label, Runnable onClick) {
        return new Button(label, input -> {
            if (input instanceof Input.LeftClick click && !click.shift()) {
                onClick.run();
            }
        });
    }

    /**
     * Creates a 'button' that does not take input.
     * @param label Label to display on the button
     * @return Created display button
     */
    public static Button display(Label label) {
        return new Button(label, DO_NOTHING);
    }

    /**
     * Create a button displaying a cancel label.
     * @param onCancel Ran when the button is left-clicked.
     * @return Created cancel button
     */
    public static Button cancel(Runnable onCancel) {
        return leftClick(Labels.CANCEL, onCancel);
    }

    /**
     * Create a button displaying a close label.
     * @param onClose Ran when the button is left-clicked.
     * @return Created close button
     */
    public static Button close(Runnable onClose) {
        return leftClick(Labels.CLOSE, onClose);
    }
}
