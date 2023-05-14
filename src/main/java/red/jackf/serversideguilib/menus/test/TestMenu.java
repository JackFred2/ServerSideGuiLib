package red.jackf.serversideguilib.menus.test;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.ServerSideGuiLib;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.labels.Labels;
import red.jackf.serversideguilib.menus.Menu;
import red.jackf.serversideguilib.menus.MenuBuilder;
import red.jackf.serversideguilib.menus.utils.Menus;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.CancellableCallback;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.utils.Sounds;

import java.util.LinkedHashMap;

public class TestMenu extends Menu {
    private int lastUnboundedInt = 0;
    private int lastBoundedInt = 0;

    private double lastUnboundedDouble = 0.0;
    private double lastBoundedDouble = 0.0;
    private double lastUnboundedDoubleAllowNaN = 0.0;

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
            Menus.selector(player, Component.literal("Pagination Test"), options, CancellableCallback.of(result -> {
                ServerSideGuiLib.LOGGER.info("Returned %d".formatted(result));
                Sounds.success(player);
                open();
            }, () -> {
                ServerSideGuiLib.LOGGER.info("Cancelled");
                Sounds.failure(player);
                open();
            }));
        }));

        // Integer Tests
        menu.addButton(1, Button.leftClick(Label.builder().item(new ItemStack(Items.WRITABLE_BOOK, 1))
                .name("Integer input test")
                .hint("No bounds")
                .hint("Last: " + lastUnboundedInt)
                .inputHint("Open", new Input.LeftClick(false))
                .build(), () -> {
            Sounds.interact(player);
            Menus.integer(player, Component.literal("Unbounded Integer Test"), null, this.lastUnboundedInt, CancellableCallback.of(val -> {
                Sounds.success(player);
                this.lastUnboundedInt = val;
                open();
            }, () -> {
                Sounds.failure(player);
                open();
            }));
        }));
        menu.addButton(2, Button.leftClick(Label.builder().item(new ItemStack(Items.WRITABLE_BOOK, 2))
                .name("Integer input test")
                .hint("Bounded: [0, 30]")
                .hint("Last: " + lastBoundedInt)
                .inputHint("Open", new Input.LeftClick(false))
                .build(), () -> {
            Sounds.interact(player);
            Menus.boundedInteger(player,
                    Component.literal("Bounded Integer Test [0, 30]"),
                    null,
                    this.lastBoundedInt, 0, 30, CancellableCallback.of(val -> {
                        Sounds.success(player);
                        this.lastBoundedInt = val;
                        open();
                    }, () -> {
                        Sounds.failure(player);
                        open();
                    }));
        }));

        // Double Tests
        menu.addButton(4, Button.leftClick(Label.builder().item(new ItemStack(Items.FEATHER, 1))
                .name("Double input test")
                .hint("Unbounded")
                .hint("Last: " + lastUnboundedDouble)
                .inputHint("Open", new Input.LeftClick(false))
                .build(), () -> {
            Sounds.interact(player);
            Menus.ddouble(player,
                    Component.literal("Unbounded Double Test"),
                    null,
                    this.lastUnboundedDouble,
                    CancellableCallback.of(d -> {
                        Sounds.success(player);
                        this.lastUnboundedDouble = d;
                        open();
                    }, () -> {
                        Sounds.failure(player);
                        open();
                    }));
        }));
        menu.addButton(5, Button.leftClick(Label.builder().item(new ItemStack(Items.FEATHER, 2))
                .name("Double input test")
                .hint("Unbounded w/ NaN")
                .hint("Last: " + lastUnboundedDoubleAllowNaN)
                .inputHint("Open", new Input.LeftClick(false))
                .build(), () -> {
            Sounds.interact(player);
            Menus.doubleAllowingNaN(player,
                    Component.literal("Double w/ NaN"),
                    null,
                    this.lastUnboundedDoubleAllowNaN,
                    CancellableCallback.of(d -> {
                        Sounds.success(player);
                        this.lastUnboundedDoubleAllowNaN = d;
                        open();
                    }, () -> {
                        Sounds.failure(player);
                        open();
                    }));
        }));
        menu.addButton(6, Button.leftClick(Label.builder().item(new ItemStack(Items.FEATHER, 3))
                .name("Double input test")
                .hint("Bounded: [-90, 270]")
                .hint("Last: " + lastBoundedDouble)
                .inputHint("Open", new Input.LeftClick(false))
                .build(), () -> {
            Sounds.interact(player);
            Menus.boundedDouble(player,
                    Component.literal("Bounded Double Test"),
                    null,
                    this.lastBoundedDouble,
                    -90.0,
                    270.0,
                    CancellableCallback.of(d -> {
                        Sounds.success(player);
                        this.lastBoundedDouble = d;
                        open();
                    }, () -> {
                        Sounds.failure(player);
                        open();
                    }));
        }));

        menu.addButton(-1, Button.close(() -> {
            Sounds.failure(player);
            player.closeContainer();
        }));

        menu.open(player);
    }
}
