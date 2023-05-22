package red.jackf.serversideguilib.api.buttons;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.api.labels.Label;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Creates a button that switches between a predefined set of states.
 */
public class SwitchButton {
    private static final Style ACTIVE = Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false);
    private static final Style INACTIVE = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);

    private SwitchButton() {
    }

    /**
     * Creates a boolean toggle button with a given name formatted with {@link Label#NORMAL}, and the default boolean
     * toggle labels.
     *
     * @param name         Title of the label to be shown
     * @param currentValue Current value of the boolean to show
     * @param onChange     Callback to be run on click; you should change the boolean's backing value here.
     * @return Button representing a boolean toggle
     */
    public static Button ofBoolean(String name, boolean currentValue, Consumer<Boolean> onChange) {
        return ofBoolean(Component.literal(name).withStyle(Label.NORMAL), currentValue, onChange);
    }

    /**
     * Creates a boolean toggle button with a given Component, and the default boolean toggle labels.
     *
     * @param name         Title of the label to be shown
     * @param currentValue Current value of the boolean to show
     * @param onChange     Callback to be run on click; you should change the boolean's backing value here.
     * @return Button representing a boolean toggle
     */
    public static Button ofBoolean(Component name, boolean currentValue, Consumer<Boolean> onChange) {
        return ofBoolean(defaultBooleanLabelFactory(name), currentValue, onChange);
    }

    /**
     * Creates a boolean toggle button with custom toggle labels
     *
     * @param labelFactory {@link BooleanLabelFactory} that supplies labels based on a given boolean. See {@link SwitchButton#defaultBooleanLabelFactory(Component)}
     *                     for a reference implementation
     * @param currentValue Current value of the boolean to show
     * @param onChange     Callback to be run on click; you should change the boolean's backing value here.
     * @return Button representing a boolean toggle
     */
    public static Button ofBoolean(BooleanLabelFactory labelFactory, boolean currentValue, Consumer<Boolean> onChange) {
        return Button.leftClick(labelFactory.get(currentValue), () -> onChange.accept(!currentValue));
    }

    /**
     * <p>Creates a button which switches between an enum's constants. Left/Right click to switch between options.</p>
     * <p>This version takes an enum implementing {@link Labelled}, which supplies the labels for each option. The {@link Label#name()}
     * of each label is used as the hint text for the option, while<code>optionName</code> is the stack name. </p>
     *
     * @param name   Title for the labels
     * @param clazz        Class object of the enum being switched through; must have at least 1 constant.
     * @param currentValue Current value of the enum to show
     * @param onChange     Callback to be run when the option is changed; you should change the enum's backing value here.
     * @param <E>          Enum which is being switched through
     * @return Button which switches between an enum's options
     */
    public static <E extends Enum<E> & Labelled> Button ofEnum(Component name, Class<E> clazz, E currentValue, Consumer<E> onChange) {
        var labels = new HashMap<E, Label>();
        for (E constant : clazz.getEnumConstants())
            labels.put(constant, constant.optionLabel());
        return ofEnum(name, clazz, currentValue, labels, onChange);
    }

    /**
     * Creates a button that switches between an enum's constants. This version lets you use an external Label -> E mapping,
     * useful for enums that you don't control. Left/Right click to switch between options.
     * @param name   Title for the button
     * @param clazz        Class object of the enum being switched through; must have at least 1 constant.
     * @param currentValue Current value of the enum to show
     * @param labels       Mapping from Enum values to Labels. This *must* contain a mapping for every constant in the enum,
     *                     or will throw an exception.
     * @param onChange     Callback to be run when the option is changed; you should change the enum's backing value here.
     * @param <E>          Enum which is being switched through
     * @return Button which switches between an enum's options
     */
    public static <E extends Enum<E>> Button ofEnum(Component name, Class<E> clazz, E currentValue, Map<E, Label> labels, Consumer<E> onChange) {
        if (clazz.getEnumConstants().length == 0)
            throw new IllegalArgumentException("Must have at least 1 Enum Constant, found 0 in " + clazz.getSimpleName());
        for (E constant : clazz.getEnumConstants())
            if (!labels.containsKey(constant))
                throw new IllegalArgumentException("Must have a Label for every mapping, but missing one for constant " + constant);

        // make label from existing
        var existingLabel = labels.get(currentValue);
        var builder = Label.builder().name(name);
        for (var stack : existingLabel.stacks())
            builder.item(stack);
        if (existingLabel instanceof Label.Animated animated) builder.interval(animated.interval());
        for (E option : clazz.getEnumConstants()) {
            var optionName = labels.get(option).name();
            if (optionName == null) optionName = Component.literal(option.name());
            builder.hint(optionName.copy().withStyle(option == currentValue ? ACTIVE : INACTIVE));
        }
        builder.inputHint("Next", new Input.LeftClick(false));
        builder.inputHint("Previous", new Input.RightClick(false));

        return new Button(builder.build(), input -> {
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

    /**
     * Example boolean label builder; uses lime and red concrete and True/False nomenclature in hints.
     *
     * @param optionName Title of the labels, such as a setting name
     * @return Factory that returns a label for each boolean state
     */
    public static BooleanLabelFactory defaultBooleanLabelFactory(Component optionName) {
        var trueLabel = Label.builder().item(Items.LIME_CONCRETE)
                .name(optionName)
                .hint("True")
                .inputHint("Toggle", new Input.LeftClick(false))
                .build();
        var falseLabel = Label.builder().item(Items.RED_CONCRETE)
                .name(optionName)
                .hint("False")
                .inputHint("Toggle", new Input.LeftClick(false))
                .build();
        return b -> b ? trueLabel : falseLabel;
    }

    public interface Labelled {
        /**
         * The label that this option is represented by. This label's name is overridden by the switch button's name, and
         * instead is shown as a hint on the label
         *
         * @return The label that represents this constant
         */
        Label optionLabel();
    }

    public interface BooleanLabelFactory {
        Label get(boolean currentValue);
    }
}
