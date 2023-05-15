package red.jackf.serversideguilib.utils;

import red.jackf.serversideguilib.buttons.Button;

import java.util.Map;

public interface SealedMenu {
    void ssgl_seal(Map<Integer, Button> inputs);

    boolean ssgl_isSealed();
}
