package red.jackf.serversideguilib.menus.test;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import red.jackf.serversideguilib.ServerSideGuiLib;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.labels.Labels;
import red.jackf.serversideguilib.menus.Menu;
import red.jackf.serversideguilib.menus.MenuBuilder;
import red.jackf.serversideguilib.menus.utils.SelectorMenu;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.CancellableCallback;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.utils.Sounds;

import java.util.LinkedHashMap;

public class TestMenu extends Menu {
    public TestMenu(ServerPlayer player) {
        super(player);
    }

    @Override
    public void open() {
        var menu = MenuBuilder.make9x6(Component.literal("SSGL Test"));

        menu.addButton(0, Button.leftClick(Labels.playerHead(player).name("Pagination Test")
                .inputHint("Open", new Input.LeftClick(false))
                .build(), () -> {
            Sounds.interact(player);
            var options = new LinkedHashMap<Label, Integer>();
            for (int i = 0; i < 500; i++) {
                options.put(Label.item(BuiltInRegistries.ITEM.byId(i + 1), String.valueOf(i)), i);
            }
            new SelectorMenu<>(player, Component.literal("Pagination Test"), options, CancellableCallback.of(result -> {
                ServerSideGuiLib.LOGGER.info("Returned %d".formatted(result));
                Sounds.success(player);
                open();
            }, () -> {
                ServerSideGuiLib.LOGGER.info("Cancelled");
                Sounds.failure(player);
                open();
            })).open();
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
