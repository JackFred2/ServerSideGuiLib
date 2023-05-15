package red.jackf.serversideguilib.buttons;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.utils.Input;

import java.util.function.Consumer;

/**
 * Creates a button that switches between a predefined set of states.
 */
public class SwitchButton {
    private static final Style ACTIVE = Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false);
    private static final Style INACTIVE = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);

    private SwitchButton() {
    }

    public static Button ofBoolean(String name, boolean currentValue, Consumer<Boolean> onChange) {
        return ofBoolean(Component.literal(name).withStyle(Label.NORMAL), currentValue, onChange);
    }

    public static Button ofBoolean(Component name, boolean currentValue, Consumer<Boolean> onChange) {
        return ofBoolean(defaultBooleanLabelFactory(name), currentValue, onChange);
    }

    public static Button ofBoolean(LabelFactory<Boolean> labelFactory, boolean currentValue, Consumer<Boolean> onChange) {
        return Button.leftClick(labelFactory.get(currentValue), () -> onChange.accept(!currentValue));
    }

    public static <E extends Enum<E> & Labelled> Button ofEnum(Component optionName, Class<E> clazz, E currentValue, Consumer<E> onChange) {
        return new Button(enumLabelBuilder(optionName, clazz, currentValue), input -> {
            var ord = currentValue.ordinal();
            int newOrd = ord;
            if (input instanceof Input.LeftClick click && !click.shift()) {
                newOrd = ord + 1;
                if (newOrd >= clazz.getEnumConstants().length) newOrd = 0;
            } else if (input instanceof Input.RightClick click && !click.shift()) {
                newOrd = ord - 1;
                if (newOrd < 0) newOrd = clazz.getEnumConstants().length - 1;
            }
            onChange.accept(clazz.getEnumConstants()[newOrd]);
        });
    }

    public interface Labelled {
        Label optionLabel();
    }

    public interface LabelFactory<T> {
        Label get(T currentValue);
    }

    private static <E extends Enum<E> & Labelled> Label enumLabelBuilder(Component name, Class<E> clazz, E e) {
        var current = e.optionLabel().stack().copy();
        var builder = Label.builder()
                .item(current)
                .name(name);
        for (E option : clazz.getEnumConstants()) {
            var optionName = option.optionLabel().name();
            if (optionName == null) optionName = Component.literal(option.name());
            builder.hint(optionName.copy().withStyle(option == e ? ACTIVE : INACTIVE));
        }
        builder.inputHint("Next", new Input.LeftClick(false));
        builder.inputHint("Previous", new Input.RightClick(false));
        return builder.build();
    }

    public static LabelFactory<Boolean> defaultBooleanLabelFactory(Component optionName) {
        return b -> Label.builder().item(b ? Items.LIME_CONCRETE : Items.RED_CONCRETE)
                .name(optionName)
                .hint(b ? "True" : "False")
                .inputHint("Toggle", new Input.LeftClick(false))
                .build();
    }
}
