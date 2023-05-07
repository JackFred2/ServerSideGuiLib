package red.jackf.serversideguilib.menus;

import net.minecraft.server.level.ServerPlayer;

/**
 * Represents a menu that runs a callback on exit. To do this, run {@link #complete()} when done i.e. by clicking a
 * {@link red.jackf.serversideguilib.labels.Labels#CANCEL} button. Commonly used to {@link Menu#open()} the previous
 * Menu.
 */
public abstract class ReturnableMenu extends Menu {
    private final Runnable callback;

    public ReturnableMenu(ServerPlayer player, Runnable callback) {
        super(player);
        this.callback = callback;
    }

    /**
     * Run the callback.
     */
    protected final void complete() {
        callback.run();
    }
}
