package red.jackf.serversideguilib.utils;

import red.jackf.serversideguilib.labels.Label;

import java.util.function.Consumer;

/**
 * Represents a button on the GUI; this generally takes up a single slot.
 * @param label ItemStack shown in the GUI
 * @param handler Consumer that takes input.
 */
public record Button(Label label, Consumer<Input> handler) {
}
