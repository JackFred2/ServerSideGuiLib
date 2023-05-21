package red.jackf.serversideguilib.labels;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import red.jackf.serversideguilib.utils.Input;

/**
 * A collection of pre-defined labels to be used.
 */
public class Labels {
    private Labels() {
    }

    /**
     * Static cancel button for uniformity
     */
    public static final Label CANCEL = Label.builder()
            .item(Items.BARRIER)
            .name(Component.translatable("gui.cancel").withStyle(Label.NORMAL))
            .inputHint(new Input.LeftClick(false))
            .build();

    /**
     * Static close button for uniformity
     */
    public static final Label CLOSE = Label.builder()
            .item(Items.BARRIER)
            .name(Component.translatable("mco.selectServer.close").withStyle(Label.NORMAL))
            .inputHint(new Input.LeftClick(false))
            .build();

    /**
     * Divider used for separating parts of large GUIs
     */
    public static final Label DIVIDER = Label.item(Items.LIME_STAINED_GLASS_PANE, "");

    /**
     * Blank label, used for if input is still needed
     */
    public static final Label EMPTY = Label.builder().build();

    /**
     * Creates a player head from a given username
     *
     * @param name Username for the head to use
     */
    public static Label.LabelBuilder playerHead(String name) {
        var stack = new ItemStack(Items.PLAYER_HEAD);
        stack.getOrCreateTag().putString(PlayerHeadItem.TAG_SKULL_OWNER, name);
        return Label.builder().item(stack).name(name);
    }

    /**
     * Creates a head from a given player
     */
    public static Label.LabelBuilder playerHead(ServerPlayer player) {
        return playerHead(player.getGameProfile().getName());
    }
}
