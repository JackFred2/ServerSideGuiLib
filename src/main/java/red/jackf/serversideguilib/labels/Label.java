package red.jackf.serversideguilib.labels;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a stack in a slot; this is just the visuals.
 */
public record Label(ItemStack stack) {
    public static final Style BLANK = Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false);

    public static LabelBuilder builder() {
        return new LabelBuilder();
    }

    public static Label item(ItemLike item, String name) {
        return builder()
                .item(item)
                .name(name)
                .build();
    }

    public static class LabelBuilder {
        private ItemStack stack = ItemStack.EMPTY;
        @Nullable
        private Component name = null;

        public LabelBuilder() {}

        public LabelBuilder item(ItemLike item) {
            this.stack = new ItemStack(item);
            return this;
        }

        public LabelBuilder item(ItemStack stack) {
            this.stack = stack.copy();
            return this;
        }

        public LabelBuilder name(Component name) {
            this.name = name;
            return this;
        }

        public LabelBuilder name(String name) {
            this.name = Component.literal(name).withStyle(BLANK);
            return this;
        }

        public Label build() {
            if (stack == ItemStack.EMPTY) return new Label(stack);
            if (name != null) stack.setHoverName(name);
            return new Label(stack);
        }
    }
}
