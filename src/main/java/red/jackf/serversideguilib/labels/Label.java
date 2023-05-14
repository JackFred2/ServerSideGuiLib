package red.jackf.serversideguilib.labels;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import red.jackf.serversideguilib.utils.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stack in a slot; this is just the visuals.
 */
public record Label(ItemStack stack) {
    public static final Style NORMAL = Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false);
    public static final Style HINT = Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false);

    public static LabelBuilder builder() {
        return new LabelBuilder();
    }

    public static Label item(ItemLike item, String name) {
        return builder()
                .item(item)
                .name(name)
                .build();
    }

    public static Label item(ItemLike item, Component name) {
        return builder()
                .item(item)
                .name(name)
                .build();
    }

    public static class LabelBuilder {
        private ItemStack stack = ItemStack.EMPTY;
        @Nullable
        private Component name = null;
        private final List<Component> hints = new ArrayList<>();

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
            this.name = Component.literal(name).withStyle(NORMAL);
            return this;
        }

        public LabelBuilder inputHint(Input input) {
            this.hints.add(input.getHint());
            return this;
        }

        public LabelBuilder inputHint(String action, Input input) {
            this.hints.add(Component.literal(action + ": ").withStyle(Label.HINT).append(input.getHint()));
            return this;
        }

        public LabelBuilder hint(Component hint) {
            this.hints.add(hint);
            return this;
        }

        public LabelBuilder hint(String hint) {
            this.hints.add(Component.literal(hint).withStyle(HINT));
            return this;
        }

        public Label build() {
            if (stack == ItemStack.EMPTY) return new Label(stack);
            if (name != null) stack.setHoverName(name);
            if (hints.size() > 0) {
                var displayTag = stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
                if (!displayTag.contains(ItemStack.TAG_LORE, Tag.TAG_LIST)) displayTag.put(ItemStack.TAG_LORE, new ListTag());
                var loreTag = displayTag.getList(ItemStack.TAG_LORE, Tag.TAG_STRING);
                hints.forEach(component -> loreTag.add(StringTag.valueOf(Component.Serializer.toJson(component))));
            }
            return new Label(stack);
        }
    }
}
