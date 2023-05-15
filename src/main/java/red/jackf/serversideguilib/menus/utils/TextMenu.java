package red.jackf.serversideguilib.menus.utils;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import red.jackf.serversideguilib.buttons.Button;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.labels.Labels;
import red.jackf.serversideguilib.menus.MenuBuilder;
import red.jackf.serversideguilib.menus.SucceedableMenu;
import red.jackf.serversideguilib.utils.*;

import java.util.function.Predicate;

/**
 * Opens an Anvil Menu in order for the player to input text.
 */
public class TextMenu extends SucceedableMenu<String> {
    public static final Item RESULT_ITEM = Items.EMERALD;
    private final Component title;
    private final String startText;
    @Nullable
    private final Label additional;
    @Nullable
    private final Predicate<String> predicate;

    protected TextMenu(ServerPlayer player, Component title, @Nullable Label additional, String startText,@Nullable Predicate<String> predicate, CancellableCallback<String> callback) {
        super(player, callback);
        this.title = title;
        this.additional = additional;
        this.startText = startText;
        this.predicate = predicate;
    }

    @Override
    public void open() {
        var menu = new MenuBuilder(title, AnvilMenu::new, 3);

        menu.addButton(AnvilMenu.INPUT_SLOT, new Button(Label.builder()
                .item(Items.BARRIER)
                .name(startText)
                .inputHint("Cancel", new Input.LeftClick(false))
                .inputHint("Reset", new Input.RightClick(false))
                .build(), input -> {
            if (input instanceof Input.LeftClick leftClick && !leftClick.shift()) {
                this.cancel();
            } else if (input instanceof Input.RightClick rightClick && !rightClick.shift() && player.containerMenu instanceof AnvilMenu anvilMenu) {
                Sounds.clear(player);
                anvilMenu.setItemName(startText);
            }
        }));

        // blank label because that gets overwritten but the listener does not
        menu.addButton(AnvilMenu.RESULT_SLOT, new Button(Labels.EMPTY, input -> {
            if (input instanceof Input.LeftClick click && !click.shift()) {
                var stack = player.containerMenu.slots.get(AnvilMenu.RESULT_SLOT).getItem();
                if (stack.is(RESULT_ITEM)) {
                    complete(stack.getHoverName().getString());
                }
            }
        }));

        if (additional != null) menu.addButton(AnvilMenu.ADDITIONAL_SLOT, Button.display(additional));
        // prevents overwriting output with input on client end
        else menu.addButton(AnvilMenu.ADDITIONAL_SLOT, Button.display(Labels.DIVIDER));

        menu.open(player);

        if (predicate != null) ((SSGLAnvilMenu) player.containerMenu).ssgl_setTextPredicate(predicate);

        player.containerMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu containerToSend, int slotIndex, ItemStack stack) {
                if (slotIndex == AnvilMenu.RESULT_SLOT && stack.is(RESULT_ITEM)) {
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
            public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
            }
        });
    }
}
