package red.jackf.serversideguilib.labels;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AnimatedLabelTicker {
    public static final AnimatedLabelTicker INSTANCE = new AnimatedLabelTicker();

    private final List<Tracked> tracking = new ArrayList<>();

    private long lastTick = 0;

    public AnimatedLabelTicker() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            lastTick++;

            for (Tracked tracked : tracking) {
                var ticksSinceTracked = lastTick - tracked.startTick;
                var interval = ticksSinceTracked % ((long) tracked.label.interval() * tracked.label.stacks().size());
                if (interval % tracked.label.interval() == 0) {
                    var stack = tracked.label.stacks().get((int) (interval / tracked.label.interval()));
                    tracked.slot.set(stack != null ? stack : ItemStack.EMPTY);
                }
            }
        });
    }

    private record Tracked(AbstractContainerMenu menu, Slot slot, Label.Animated label, long startTick) {}

    public void add(AbstractContainerMenu menu, Slot slot, Label.Animated animated) {
        if (animated.stacks().size() > 0)
            tracking.add(new Tracked(menu, slot, animated, lastTick));
    }

    public void removed(AbstractContainerMenu menu) {
        tracking.removeIf(tracked -> tracked.menu == menu);
    }
}
