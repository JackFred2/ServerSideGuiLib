package red.jackf.serversideguilib.menus.utils;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.menus.MenuBuilder;
import red.jackf.serversideguilib.menus.SucceedableMenu;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.CancellableCallback;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.utils.StackUtils;

/**
 * Opens an Anvil Menu in order for the player to input text.
 */
public class TextMenu extends SucceedableMenu<String> {
    private final Component title;
    private final String startText;

    public TextMenu(ServerPlayer player, Component title, String startText, CancellableCallback<String> callback) {
        super(player, callback);
        this.title = title;
        this.startText = startText;
    }

    @Override
    public void open() {
        var menu = new MenuBuilder(title, AnvilMenu::new, 3);

        menu.addButton(AnvilMenu.INPUT_SLOT, new Button(Label.builder().name(startText)
                .inputHint("Cancel", new Input.LeftClick(false))
                .item(Items.PAPER)
                .build(), input -> {
            if (input instanceof Input.LeftClick click && !click.shift()) {
                cancel();
            }
        }));

        // blank label because that gets overwritten but the listener does not
        menu.addButton(AnvilMenu.RESULT_SLOT, new Button(Label.builder().build(), input -> {
            if (input instanceof Input.LeftClick click && !click.shift()) {
                var stack = player.containerMenu.slots.get(AnvilMenu.RESULT_SLOT).getItem();
                if (stack != ItemStack.EMPTY) {
                    complete(stack.getHoverName().getString());
                }
            }
        }));

        menu.open(player);

        player.containerMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu containerToSend, int slotIndex, ItemStack stack) {
                if (slotIndex == AnvilMenu.RESULT_SLOT) {
                    var lore = StackUtils.getLore(stack);
                    if (lore.size() == 1 && lore.get(0).getString().startsWith("Accept")) return;
                    var displayTag = stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
                    var listTag = new ListTag();
                    listTag.add(StringTag.valueOf(Component.Serializer.toJson(
                                            Component.literal("Accept: ").withStyle(Label.HINT)
                                                .append(new Input.LeftClick(false).getHint()))));
                    displayTag.put(ItemStack.TAG_LORE, listTag);
                }
            }

            @Override
            public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {}
        });
    }
}
