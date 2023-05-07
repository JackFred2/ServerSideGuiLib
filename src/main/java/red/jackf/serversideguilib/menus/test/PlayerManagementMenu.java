package red.jackf.serversideguilib.menus.test;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import red.jackf.serversideguilib.ServerSideGuiLib;
import red.jackf.serversideguilib.labels.Labels;
import red.jackf.serversideguilib.menus.Menu;
import red.jackf.serversideguilib.menus.MenuBuilder;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.utils.Sounds;

public class PlayerManagementMenu extends Menu {
    public PlayerManagementMenu(ServerPlayer player) {
        super(player);
    }

    @Override
    public void open() {
        var menu = MenuBuilder.make9x6(Component.literal("Player Manager"));

        menu.addButton(0, new Button(Labels.playerHead(player), input -> {
            if (input instanceof Input.LeftClick click && !click.shift()) {
                Sounds.success(player);
                ServerSideGuiLib.LOGGER.info("Player Clicked");
            }
        }));
        menu.addButton(-1, new Button(Labels.CLOSE, input -> {
            if (input instanceof Input.LeftClick click && !click.shift()) {
                Sounds.failure(player);
                player.closeContainer();
            }
        }));

        menu.open(player);
    }
}
