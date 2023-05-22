package red.jackf.serversideguilib.internal.test;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.internal.ServerSideGuiLib;
import red.jackf.serversideguilib.api.buttons.Button;
import red.jackf.serversideguilib.api.buttons.SwitchButton;
import red.jackf.serversideguilib.api.labels.Label;
import red.jackf.serversideguilib.api.labels.Labels;
import red.jackf.serversideguilib.api.menus.Menu;
import red.jackf.serversideguilib.api.menus.MenuBuilder;
import red.jackf.serversideguilib.api.menus.ReturnableMenu;
import red.jackf.serversideguilib.api.menus.input.Menus;
import red.jackf.serversideguilib.api.menus.CancellableCallback;
import red.jackf.serversideguilib.api.buttons.Input;
import red.jackf.serversideguilib.api.utils.Sounds;

import java.util.LinkedHashMap;

public class TestMenu extends Menu {
    private int lastUnboundedInt = 0;
    private int lastBoundedInt = 0;

    private double lastUnboundedDouble = 0.0;
    private double lastBoundedDouble = 0.0;
    private double lastUnboundedDoubleAllowNaN = 0.0;
    private boolean switchBooleanTest = false;
    private SwitchTest switchEnumTest1 = SwitchTest.ALPHA;
    private SwitchTest switchEnumTest2 = SwitchTest.ALPHA;
    private SwitchTest switchEnumTest3 = SwitchTest.ALPHA;

    public TestMenu(ServerPlayer player) {
        super(player);
    }

    @Override
    public void open() {
        var builder = MenuBuilder.make9x6(Component.literal("SSGL Test"));

        builder.addButton(0, Button.leftClick(Label.playerHead(player).name("Pagination Test")
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
        builder.addButton(1, Button.leftClick(Label.builder().item(new ItemStack(Items.WRITABLE_BOOK, 1))
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
        builder.addButton(2, Button.leftClick(Label.builder().item(new ItemStack(Items.WRITABLE_BOOK, 2))
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
        builder.addButton(4, Button.leftClick(Label.builder().item(new ItemStack(Items.FEATHER, 1))
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
        builder.addButton(5, Button.leftClick(Label.builder().item(new ItemStack(Items.FEATHER, 2))
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
        builder.addButton(6, Button.leftClick(Label.builder().item(new ItemStack(Items.FEATHER, 3))
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

        builder.addButton(8, SwitchButton.ofBoolean("Boolean test", this.switchBooleanTest, b -> {
            Sounds.interact(player);
            this.switchBooleanTest = b;
            open();
        }));

        builder.addButton(9, SwitchButton.ofEnum(Component.literal("Enum Test")
                .withStyle(Label.NORMAL), SwitchTest.class, this.switchEnumTest1, e -> {
            Sounds.interact(player);
            this.switchEnumTest1 = e;
            open();
        }));

        if (switchEnumTest1 == SwitchTest.DELTA)
            builder.addButton(10, SwitchButton.ofEnum(Component.literal("Enum Test 2")
                    .withStyle(Label.NORMAL), SwitchTest.class, this.switchEnumTest2, e -> {
                Sounds.interact(player);
                this.switchEnumTest2 = e;
                open();
            }));

        if (switchEnumTest2 == SwitchTest.GAMMA)
            builder.addButton(11, SwitchButton.ofEnum(Component.literal("Enum Test 3")
                    .withStyle(Label.NORMAL), SwitchTest.class, this.switchEnumTest3, e -> {
                Sounds.interact(player);
                this.switchEnumTest3 = e;
                open();
            }));

        builder.addButton(13, Button.display(Label.builder().item(Items.RED_CONCRETE).item(Items.LIME_CONCRETE)
                .item(Items.BLUE_CONCRETE).name("Animated Test 1").hint("Interval: 20").build()));
        builder.addButton(14, Button.display(Label.builder().item(Items.RED_CONCRETE).item(Items.LIME_CONCRETE)
                .item(Items.BLUE_CONCRETE).name("Animated Test 2").hint("Interval: 30").interval(30).build()));
        builder.addButton(15, Button.display(Label.builder().item(Items.RED_CONCRETE).item(Items.LIME_CONCRETE)
                .item(Items.BLUE_CONCRETE).name("Animated Test 3").hint("Interval: 40").interval(40).build()));
        builder.addButton(16, new Button(Label.builder().item(Items.WHITE_CONCRETE).item(Items.LIGHT_GRAY_CONCRETE)
                .name("Animated Test 4").hint("Interval: 2").interval(2)
                .inputHint("Play Sound", new Input.Drop(false)).build(), input -> {
            if (input instanceof Input.Drop drop && !drop.control()) {
                Sounds.play(player, SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 1f);
            }
        }));

        builder.addButton(17, Button.display(Label.builder().item(Items.DARK_OAK_SIGN).name("Empty Slot Button Test")
                .hint("Press a hotbar button on the slot below.").build()));
        builder.addButton(26, new Button(Labels.EMPTY, input -> {
            if (input instanceof Input.Hotbar hotbar) {
                Sounds.interact(player, 1f + hotbar.index() / 8f);
            }
        }));

        builder.addButton(18, new Button(Label.builder().item(Items.SCULK_SENSOR).name("Offside Test")
                .inputHint(new Input.DoubleLeftClick()).build(), input -> {
            if (input instanceof Input.DoubleLeftClick) {
                Sounds.interact(player);
                new OffsideTestMenu(player, () -> {
                    Sounds.failure(player);
                    open();
                }).open();
            }
        }));
        builder.addButton(19, Button.display(Label.builder().item(Items.DIAMOND_HELMET).name("Keep Lore Test")
                .keepLore().hint("only here to remove \"unused method\" warnings").hint("in my IDE").build()));
        builder.addButton(20, Button.display(Label.item(Items.DIAMOND_HELMET, "Default Lore Behavior")));

        builder.addButton(-1, Button.close(() -> {
            Sounds.failure(player);
            player.closeContainer();
        }));

        var oddHighlight = Label.item(Items.YELLOW_CONCRETE, "Ticker Test").asStack();

        builder.addTicker(((menu, ticksOpen) -> {
            if (ticksOpen % 10 == 0) {
                for (int i = 45; i < 53; i++) {
                    menu.slots.get(i).set(((ticksOpen / 10) + i) % 4 == 0 ? oddHighlight : ItemStack.EMPTY);
                }
            }
        }));

        builder.open(player);
    }

    private static class OffsideTestMenu extends ReturnableMenu {
        private int lastSelected = -1;

        public OffsideTestMenu(ServerPlayer player, Runnable callback) {
            super(player, callback);
        }

        @Override
        public void open() {
            var builder = MenuBuilder.make3x3(Component.literal("Click a dust or off-screen"));
            builder.addButton(4, Button.close(this::complete));
            for (int i = 0; i < 9; i++) {
                if (i != 4) {
                    int finalI = i;
                    builder.addButton(i, Button.leftClick(Label.item(lastSelected == i ? Items.GLOWSTONE_DUST : Items.GUNPOWDER, ""), () -> {
                        Sounds.interact(player);
                        lastSelected = finalI;
                        open();
                    }));
                }
            }
            builder.addButton(MenuBuilder.SLOT_CLICKED_OUTSIDE, Button.leftClick(Labels.EMPTY, () -> {
                Sounds.clear(player);
                lastSelected = -1;
                open();
            }));

            player.openMenu(builder.provider());
        }
    }

    private enum SwitchTest implements SwitchButton.Labelled {
        ALPHA(Label.item(Items.IRON_INGOT, "Alpha")),
        BETA(Label.item(Items.GOLD_INGOT, "Beta")),
        DELTA(Label.builder().item(Items.DIAMOND).name("Delta").hint("With persistent hints").build()),
        GAMMA(Label.builder().item(Items.NETHERITE_AXE).name("Gamma")
                .hint("Takes the below option names from label titles").hint("and replaces the original name").build()),
        EPSILON(Label.item(Items.PAPER, "Epsilon")),
        RHO(Label.builder().item(Items.STRUCTURE_VOID).hint("Nameless Label Test").build()),
        PSI(Label.item(Items.SPAWNER, "Psi"));

        private final Label optionLabel;

        SwitchTest(Label optionLabel) {
            this.optionLabel = optionLabel;
        }

        @Override
        public Label optionLabel() {
            return this.optionLabel;
        }
    }
}
