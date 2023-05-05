package red.jackf.serversideguilib.mixins;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.serversideguilib.SealedMenu;

/**
 * Adds handlers to 'button' clicking
 */
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements SealedMenu {
    @Unique
    private boolean sealed = false;

    // Supports:
    // left click (button 0, clicktype Pickup)
    // right click (button 1, clicktype Pickup)
    // shift left click (button 0, clicktype Quick Move)
    // shift right click (button 1, clicktype Quick Move)
    // middle click (button 2, clicktype Clone)
    // q (button 0, clicktype Throw) ONLY WITH ITEM, REBINDABLE
    // ctrl q (button 1, clicktype Throw) ONLY WITH ITEM, REBINDABLE
    // numbers 1->9 (button 0->8, clicktype swap), REBINDABLE
    // double left click (button 0, clicktype Pickup All) HAS 2 LEFT CLICKS BEFORE
    @Inject(method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true)
    private void serversideguilib_hooks(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        if (sealed) {
            System.out.printf("%d, %s%n", button, clickType);
            ci.cancel();
        }
    }

    public void seal() {
        this.sealed = true;
    }
}
