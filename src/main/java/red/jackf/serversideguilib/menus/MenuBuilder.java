package red.jackf.serversideguilib.menus;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.NotNull;
import red.jackf.serversideguilib.ServerSideGuiLib;
import red.jackf.serversideguilib.buttons.Button;
import red.jackf.serversideguilib.utils.SealedMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBuilder {
    public static final int SLOT_CLICKED_OUTSIDE = AbstractContainerMenu.SLOT_CLICKED_OUTSIDE;
    private final Component title;
    private final MenuType.MenuSupplier<?> menuConstructor;
    private final Map<Integer, Button> inputs = new HashMap<>();
    private final int maxSlots;
    private final List<MenuTicker> tickers = new ArrayList<>();

    public MenuBuilder(Component title, MenuType.MenuSupplier<?> menuConstructor, int maxSlots) {
        this.title = title;
        this.menuConstructor = menuConstructor;
        this.maxSlots = maxSlots;
    }

    /**
     * Creates a 5x1 menu using the Hopper's screen.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 5x1
     */
    public static MenuBuilder make5x1(Component title) {
        return new MenuBuilder(title, HopperMenu::new, 5);
    }

    /**
     * Creates a 3x3 menu using the Dispenser's screen.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 3x3
     */
    public static MenuBuilder make3x3(Component title) {
        return new MenuBuilder(title, DispenserMenu::new, 9);
    }

    /**
     * Creates a 9x1 menu.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 9x1
     */
    public static MenuBuilder make9x1(Component title) {
        return new MenuBuilder(title, ChestMenu::oneRow, 9);
    }

    /**
     * Creates a 9x2 menu.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 9x2
     */
    public static MenuBuilder make9x2(Component title) {
        return new MenuBuilder(title, ChestMenu::twoRows, 18);
    }

    /**
     * Creates a 9x3 menu.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 9x3
     */
    public static MenuBuilder make9x3(Component title) {
        return new MenuBuilder(title, ChestMenu::threeRows, 27);
    }

    /**
     * Creates a 9x4 menu.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 9x4
     */
    public static MenuBuilder make9x4(Component title) {
        return new MenuBuilder(title, ChestMenu::fourRows, 36);
    }

    /**
     * Creates a 9x5 menu.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 9x5
     */
    public static MenuBuilder make9x5(Component title) {
        return new MenuBuilder(title, ChestMenu::fiveRows, 45);
    }

    /**
     * Creates a 9x6 menu.
     *
     * @param title Title shown on the menu
     * @return MenuBuilder instance for the 9x6
     */
    public static MenuBuilder make9x6(Component title) {
        return new MenuBuilder(title, ChestMenu::sixRows, 54);
    }

    /**
     * Adds a button to this menu in the designated slot
     *
     * @param slot   Slot to add the button to. Can be negative, starting backwards from the end of the menu.
     *               Can be {@link AbstractContainerMenu#SLOT_CLICKED_OUTSIDE} = -999 to run when clicked outside the menu.
     * @param button Button to place in the slot
     */
    public void addButton(Integer slot, Button button) {
        if (slot != SLOT_CLICKED_OUTSIDE && (slot >= maxSlots || slot < -maxSlots)) {
            ServerSideGuiLib.LOGGER.warn("Tried to add button outside of given slots: %d".formatted(slot));
            return;
        }
        if (slot < 0 && slot != SLOT_CLICKED_OUTSIDE) slot = maxSlots + slot;
        if (this.inputs.containsKey(slot))
            ServerSideGuiLib.LOGGER.warn("Overwriting button at slot %d".formatted(slot));
        this.inputs.put(slot, button);
    }

    public void addTicker(MenuTicker ticker) {
        this.tickers.add(ticker);
    }

    public interface MenuTicker {
        void tick(AbstractContainerMenu menu, long ticksOpen);
    }

    /**
     * Creates a {@link MenuProvider} to open this, generally used in {@link Player#openMenu(MenuProvider)}.
     */
    public MenuProvider provider() {
        return new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return title;
            }

            @Override
            public AbstractContainerMenu createMenu(int invIndex, Inventory inventory, Player player) {
                var menu = menuConstructor.create(invIndex, inventory);
                ((SealedMenu) menu).ssgl_seal(inputs, tickers);
                return menu;
            }

            @Override
            public boolean shouldCloseCurrentScreen() {
                return false;
            }
        };
    }

    /**
     * Shorthand to open this for a player
     *
     * @param player Player to open this menu for.
     */
    public void open(ServerPlayer player) {
        player.openMenu(provider());
    }
}
