package red.jackf.serversideguilib.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.menus.MenuBuilder;

import java.util.ArrayList;
import java.util.List;

public class Ticker {
    public static final Ticker INSTANCE = new Ticker();

    private final List<AnimatedLabelEntry> animatedLabels = new ArrayList<>();

    private final List<MenuTickerEntry> menuTickers = new ArrayList<>();

    private long lastTick = 0;

    public Ticker() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            // ticked menus
            for (MenuTickerEntry menuTicker : menuTickers)
                menuTicker.ticker.tick(menuTicker.menu, lastTick - menuTicker.startTick);

            // animated labels
            for (AnimatedLabelEntry animatedLabel : animatedLabels) {
                var ticksSinceTracked = lastTick - animatedLabel.startTick;
                var interval = ticksSinceTracked % ((long) animatedLabel.label.interval() * animatedLabel.label.stacks()
                        .size());
                if (interval % animatedLabel.label.interval() == 0) {
                    var stack = animatedLabel.label.stacks().get((int) (interval / animatedLabel.label.interval()));
                    animatedLabel.slot.set(stack != null ? stack : ItemStack.EMPTY);
                }
            }

            lastTick++;
        });
    }

    private record MenuTickerEntry(AbstractContainerMenu menu, MenuBuilder.MenuTicker ticker, long startTick) {
    }

    private record AnimatedLabelEntry(AbstractContainerMenu menu, Slot slot, Label.Animated label, long startTick) {
    }

    public void addMenuTicker(AbstractContainerMenu menu, MenuBuilder.MenuTicker ticker) {
        menuTickers.add(new MenuTickerEntry(menu, ticker, lastTick));
    }

    public void addAnimated(AbstractContainerMenu menu, Slot slot, Label.Animated animated) {
        if (animated.stacks().size() > 0)
            animatedLabels.add(new AnimatedLabelEntry(menu, slot, animated, lastTick));
    }

    public void removed(AbstractContainerMenu menu) {
        animatedLabels.removeIf(animatedLabel -> animatedLabel.menu == menu);
        menuTickers.removeIf(menuTicker -> menuTicker.menu == menu);
    }
}
