package red.jackf.serversideguilib.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/**
 * Represents possible inputs from players, using cancelled inventory interactions.
 */
public sealed interface Input permits Input.DoubleLeftClick, Input.Drop, Input.Hotbar, Input.LeftClick, Input.MiddleClick, Input.RightClick {

    /**
     * Returns a hint of what key needs to be pressed to activate this specific input.
     * @return Text Component displaying the required input.
     */
    MutableComponent getHint();

    /**
     * A left mouse click (can not be rebound)
     * @param shift Whether either Shift button was pressed
     */
    record LeftClick(boolean shift) implements Input {
        @Override
        public MutableComponent getHint() {
            var text = Component.translatable("key.mouse.left");
            if (shift) text = Component.translatable("key.keyboard.left.shift").append(" + ").append(text);
            return Input.format(text);
        }
    }

    /**
     * A double left click (can not be rebound). Note that this is preceded by two {@link LeftClick} inputs.
     * If Shift is held, then this is instead sent as a third {@link LeftClick} with shift = true.
     */
    record DoubleLeftClick() implements Input {
        @Override
        public MutableComponent getHint() {
            return Component.literal("2x").withStyle(KEY_STYLE).append(Input.format(Component.translatable("key.mouse.left")));
        }
    }

    /**
     * A right mouse click (can not be rebound)
     * @param shift Whether either Shift button was pressed
     */
    record RightClick(boolean shift) implements Input {
        @Override
        public MutableComponent getHint() {
            var text = Component.translatable("key.mouse.right");
            if (shift) text = Component.translatable("key.keyboard.left.shift").append(" + ").append(text);
            return Input.format(text);
        }
    }

    /**
     * A middle mouse button press (can not be rebound)
     */
    record MiddleClick() implements Input {
        @Override
        public MutableComponent getHint() {
            return Input.format(Component.translatable("key.mouse.middle"));
        }
    }

    /**
     * A drop button press (by default bound to Q)
     * @param control Whether either Ctrl button was pressed
     */
    record Drop(boolean control) implements Input {
        @Override
        public MutableComponent getHint() {
            var text = Component.keybind("key.drop");
            if (control) text = Component.translatable("key.keyboard.left.control").append(" + ").append(text);
            return Input.format(text);
        }
    }

    /**
     * A hotbar button press (by default bound to 1 thru 9)
     * @param index Hotbar slot pressed (0 indexed, so first slot = 0)
     */
    record Hotbar(int index) implements Input {
        public Hotbar {
            assert 0 <= index && index < 9;
        }

        @Override
        public MutableComponent getHint() {
            return Input.format(Component.keybind("key.hotbar." + (index + 1)));
        }
    }

    /**
     * Component style for key hints
     */
    Style KEY_STYLE = Style.EMPTY.withColor(ChatFormatting.AQUA);

    private static MutableComponent format(MutableComponent text) {
        return Component.literal("[").append(text).append("]").withStyle(KEY_STYLE);
    }
}
