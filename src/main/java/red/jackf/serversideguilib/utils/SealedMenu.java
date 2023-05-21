package red.jackf.serversideguilib.utils;

import red.jackf.serversideguilib.buttons.Button;
import red.jackf.serversideguilib.menus.MenuBuilder;

import java.util.List;
import java.util.Map;

public interface SealedMenu {
    void ssgl_seal(Map<Integer, Button> inputs, List<MenuBuilder.MenuTicker> tickers);

    boolean ssgl_isSealed();
}
