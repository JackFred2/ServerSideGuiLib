package red.jackf.serversideguilib.internal.utils;

import red.jackf.serversideguilib.api.buttons.Button;
import red.jackf.serversideguilib.api.menus.MenuBuilder;

import java.util.List;
import java.util.Map;

/**
 * Used to add functionality to a server-side menu
 */
public interface SealedMenu {
    void ssgl_seal(Map<Integer, Button> inputs, List<MenuBuilder.MenuTicker> tickers);

    boolean ssgl_isSealed();
}
