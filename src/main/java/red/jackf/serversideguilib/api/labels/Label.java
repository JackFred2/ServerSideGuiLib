package red.jackf.serversideguilib.api.labels;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import red.jackf.serversideguilib.api.buttons.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the visuals of a button.
 */
public sealed interface Label {
    default ItemStack asStack() {
        return stacks().size() == 0 ? ItemStack.EMPTY : stacks().get(0);
    }

    List<ItemStack> stacks();

    @Nullable
    Component name();

    Style NORMAL = Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false);
    Style HINT = Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false);

    static LabelBuilder builder() {
        return new LabelBuilder();
    }

    /**
     * Creates a Label from a given item and name
     * @param item Item to be used for the Label
     * @param name Name to be used for the Label
     * @return Completed label
     */
    static Label item(ItemLike item, String name) {
        return builder()
                .item(item)
                .name(name)
                .build();
    }

    /**
     * Creates a Label from a given item and name
     * @param item Item to be used for the Label
     * @param name Name to be used for the Label
     * @return Completed label
     */
    static Label item(ItemLike item, Component name) {
        return builder()
                .item(item)
                .name(name)
                .build();
    }

    /**
     * Creates a LabelBuilder starting with a player head from a given username
     *
     * @param name Username for the head to use
     */
    static Label.LabelBuilder playerHead(String name) {
        var stack = new ItemStack(Items.PLAYER_HEAD);
        stack.getOrCreateTag().putString(PlayerHeadItem.TAG_SKULL_OWNER, name);
        return Label.builder().item(stack).name(name);
    }

    /**
     * Creates a LabelBuilder starting with a player head from a given ServerPlayer
     *
     * @param player Player whose head to use
     */
    static Label.LabelBuilder playerHead(ServerPlayer player) {
        return playerHead(player.getGameProfile().getName());
    }

    class LabelBuilder {
        private final List<ItemStack> stacks = new ArrayList<>();
        private int interval = 20;
        @Nullable
        private Component name = null;
        private final List<Component> hints = new ArrayList<>();

        private boolean keepLore = false;

        public LabelBuilder() {
        }

        public LabelBuilder item(ItemLike item) {
            this.stacks.add(new ItemStack(item));
            return this;
        }

        public LabelBuilder item(ItemStack stack) {
            this.stacks.add(stack.copy());
            return this;
        }

        public LabelBuilder interval(int interval) {
            this.interval = Math.max(interval, 1);
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
            return hint(Component.literal(action + ": ").withStyle(Label.HINT).append(input.getHint()));
        }

        public LabelBuilder hint(Component hint) {
            this.hints.add(hint);
            return this;
        }

        public LabelBuilder hint(String hint) {
            this.hints.add(Component.literal(hint).withStyle(HINT));
            return this;
        }

        public LabelBuilder keepLore() {
            this.keepLore = true;
            return this;
        }

        public Label build() {
            if (stacks.size() == 0) return new Label.Empty(name);
            for (var stack : stacks) {
                if (name != null) stack.setHoverName(name);
                if (hints.size() > 0) {
                    var displayTag = stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
                    if (!displayTag.contains(ItemStack.TAG_LORE, Tag.TAG_LIST))
                        displayTag.put(ItemStack.TAG_LORE, new ListTag());
                    var loreTag = displayTag.getList(ItemStack.TAG_LORE, Tag.TAG_STRING);
                    hints.forEach(component -> loreTag.add(StringTag.valueOf(Component.Serializer.toJson(component))));
                }
                if (!keepLore)
                    for (ItemStack.TooltipPart part : ItemStack.TooltipPart.values())
                        stack.hideTooltipPart(part);
            }
            if (stacks.size() == 1) return new Label.Static(name, stacks.get(0));
            else return new Label.Animated(name, stacks, interval);
        }
    }

    record Empty(@Nullable Component name) implements Label {
        @Override
        public List<ItemStack> stacks() {
            return List.of(ItemStack.EMPTY);
        }
    }

    record Static(@Nullable Component name, ItemStack stack) implements Label {
        public List<ItemStack> stacks() {
            return List.of(stack);
        }
    }

    record Animated(@Nullable Component name, List<ItemStack> stacks, int interval) implements Label {
    }
}
