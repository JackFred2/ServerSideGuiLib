package red.jackf.serversideguilib.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.Nullable;

/**
 * Represents possible inputs from players, using cancelled inventory interactions. Use <code>instanceof</code> to check
 * which input is being used.
 * All of these can be triggered with or without an item present except the {@link Drop} inputs, which require an item
 * under the cursor.
 */
public sealed interface Input permits Input.DoubleLeftClick, Input.Drop, Input.Hotbar, Input.LeftClick, Input.MiddleClick, Input.RightClick {

    /**
     * Returns a hint of what key needs to be pressed to activate this specific handler.
     *
     * @return Text Component displaying the required handler.
     */
    Component getHint();

    /**
     * A left mouse click. Can't be rebound. Does not require an item. Can be off-screen.
     *
     * @param shift Whether either Shift button was pressed
     */
    record LeftClick(boolean shift) implements Input {
        @Override
        public Component getHint() {
            var text = Component.translatable("key.mouse.left");
            if (shift) text = Component.translatable("key.keyboard.left.shift")
                    .append(Component.literal(" + ").withStyle(BASE_STYLE))
                    .append(text);
            return Input.format(text);
        }
    }

    /**
     * A double left click. Can't be rebound. Does not require an item. Note that this is preceded by two {@link LeftClick} inputs.
     * Not sent if shift is held.
     */
    record DoubleLeftClick() implements Input {
        @Override
        public Component getHint() {
            return Component.literal("2 x ").withStyle(BASE_STYLE)
                    .append(Input.format(Component.translatable("key.mouse.left")));
        }
    }

    /**
     * A right mouse click. Can't be rebound. Does not require an item. Can be off-screen.
     *
     * @param shift Whether either Shift button was pressed
     */
    record RightClick(boolean shift) implements Input {
        @Override
        public Component getHint() {
            var text = Component.translatable("key.mouse.right");
            if (shift) text = Component.translatable("key.keyboard.left.shift")
                    .append(Component.literal(" + ").withStyle(BASE_STYLE))
                    .append(text);
            return Input.format(text);
        }
    }

    /**
     * A middle mouse click. Can't be rebound. Does not require an item. Can be off-screen. Requires the player to be
     * in creative mode.
     */
    record MiddleClick() implements Input {
        @Override
        public Component getHint() {
            return Input.format(Component.translatable("key.mouse.middle"));
        }
    }

    /**
     * A drop button press (by default bound to Q). Can be rebound. Requires an item.
     *
     * @param control Whether either Ctrl button was pressed
     */
    record Drop(boolean control) implements Input {
        @Override
        public Component getHint() {
            var text = Component.keybind("key.drop");
            if (control) text = Component.translatable("key.keyboard.left.control")
                    .append(Component.literal(" + ").withStyle(BASE_STYLE))
                    .append(text);
            return Input.format(text);
        }
    }

    /**
     * A hotbar button press (by default bound to 1 -> 9). Can be rebound. Does not require an item. If held down by the player,
     * will continuously trigger.
     *
     * @param index Hotbar slot pressed (0 indexed, so first slot = 0)
     */
    record Hotbar(int index) implements Input {
        public Hotbar {
            assert 0 <= index && index < 9;
        }

        @Override
        public Component getHint() {
            return Input.format(Component.keybind("key.hotbar." + (index + 1)));
        }
    }

    /**
     * Parses an Input from a given inventory interaction. Used in {@link red.jackf.serversideguilib.mixins.AbstractContainerMenuMixin}
     *
     * @return Parsed Input, or null if invalid
     */
    @Nullable
    static Input getInputFromRaw(int slotId, int button, ClickType clickType) {
        if (slotId == AbstractContainerMenu.SLOT_CLICKED_OUTSIDE) {
            if (clickType == ClickType.THROW) {
                if (button == 0) return new LeftClick(false);
                else if (button == 1) return new RightClick(false);
            } else if (clickType == ClickType.CLONE && button == 2) return new MiddleClick();
        } else switch (clickType) {
            case PICKUP -> {
                if (button == 0) return new LeftClick(false);
                else if (button == 1) return new RightClick(false);
            }
            case QUICK_MOVE -> {
                if (button == 0) return new LeftClick(true);
                else if (button == 1) return new RightClick(true);
            }
            case CLONE -> {
                if (button == 2) return new MiddleClick();
            }
            case THROW -> {
                if (button == 0) return new Drop(false);
                else if (button == 1) return new Drop(true);
            }
            case SWAP -> {
                if (0 <= button && button < 9) return new Hotbar(button);
            }
            case PICKUP_ALL -> {
                if (button == 0) return new DoubleLeftClick();
            }
        }
        return null;
    }

    /**
     * Component style for key hints
     */
    Style KEY_STYLE = Style.EMPTY.withColor(0x71CCE2).withItalic(false);

    /**
     * Component style for hint decorations
     */
    Style BASE_STYLE = Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false);

    private static MutableComponent format(MutableComponent text) {
        var base = Component.literal("[ ").withStyle(BASE_STYLE);
        return base.append(text.withStyle(KEY_STYLE)).append(" ]");
    }
}
