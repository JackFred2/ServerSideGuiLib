package red.jackf.serversideguilib.api.labels;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.api.buttons.Input;

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
     * Blank label, used for if input is needed on an empty slot
     */
    public static final Label EMPTY = Label.builder().build();
}
