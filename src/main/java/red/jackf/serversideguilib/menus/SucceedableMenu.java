package red.jackf.serversideguilib.menus;


import net.minecraft.server.level.ServerPlayer;
import red.jackf.serversideguilib.utils.CancellableCallback;

/**
 * Represents a menu that can optionally return a result. Commonly used when changing settings.
 */
public abstract class SucceedableMenu<T> extends Menu {
    private final CancellableCallback<T> callback;

    public SucceedableMenu(ServerPlayer player, CancellableCallback<T> callback) {
        super(player);
        this.callback = callback;
    }

    protected final void complete(T result) {
        callback.complete(result);
    }

    protected final void cancel() {
        callback.cancel();
    }
}
