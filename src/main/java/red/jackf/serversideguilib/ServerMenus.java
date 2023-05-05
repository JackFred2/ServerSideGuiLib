package red.jackf.serversideguilib;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.jackf.serversideguilib.utils.Input;

import java.util.function.BiFunction;

public class ServerMenus {
    private ServerMenus() {}

    private static MenuProvider make(BiFunction<Integer, Inventory, AbstractContainerMenu> menuFunc, Component title) {
        return new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return title;
            }

            @Override
            public AbstractContainerMenu createMenu(int invIndex, Inventory inventory, Player player) {
                var menu = menuFunc.apply(invIndex, inventory);
                menu.slots.get(0).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.LeftClick(false).getHint()));
                menu.slots.get(1).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.LeftClick(true).getHint()));
                menu.slots.get(2).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.DoubleLeftClick().getHint()));
                menu.slots.get(3).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.MiddleClick().getHint()));
                menu.slots.get(4).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.RightClick(false).getHint()));
                menu.slots.get(5).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.RightClick(true).getHint()));
                menu.slots.get(6).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.Drop(false).getHint()));
                menu.slots.get(7).set(new ItemStack(Items.DIAMOND).setHoverName(new Input.Drop(true).getHint()));
                for (int i = 0; i < 9; i++) {
                    menu.slots.get(8 + i).set(new ItemStack(Items.EMERALD).setHoverName(new Input.Hotbar(i).getHint()));
                }
                ((SealedMenu) menu).seal();
                return menu;
            }

            @Override
            public boolean shouldCloseCurrentScreen() {
                return false;
            }
        };
    }

    public static MenuProvider make9x3(Component title) {
        return make(ChestMenu::threeRows, title);
    }
}
