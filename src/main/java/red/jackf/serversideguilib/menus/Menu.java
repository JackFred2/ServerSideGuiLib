package red.jackf.serversideguilib.menus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import red.jackf.serversideguilib.utils.Button;

/**
 * Class representing a menu to be opened by a player. Extend this class, then define your menu and open it for the player
 * in {@link #open()}.
 */
public abstract class Menu {
    protected final ServerPlayer player;

    public Menu(ServerPlayer player) {
        this.player = player;
    }
    /**
     * Called to open this menu. Create your {@link MenuBuilder} here, populate with {@link MenuBuilder#addButton(Integer, Button)},
     * then {@link net.minecraft.server.level.ServerPlayer#openMenu(MenuProvider)} the {@link MenuBuilder#provider()}. <br />
     * This may be called multiple times i.e. if you come back from a further menu.
     */
    public abstract void open();
}
