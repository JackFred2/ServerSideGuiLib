package red.jackf.serversideguilib.mixins;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.utils.SealedMenu;

import java.util.Map;

/**
 * Adds handlers to 'button' clicking
 */
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements SealedMenu {
    @Shadow
    @Final
    public NonNullList<Slot> slots;
    @Unique
    @Nullable
    private Map<Integer, Button> inputs = null;

    @Inject(method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true)
    private void serversideguilib_hooks(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        // is not a SSGL screen
        if (this.inputs == null) return;
        // a player inventory, or clicked outside if this screen isn't handling it
        if ((slotId == AbstractContainerMenu.SLOT_CLICKED_OUTSIDE && !this.inputs.containsKey(slotId)) || this.slots.get(slotId).container instanceof Inventory) return;
        ci.cancel(); // prevent taking items from menu
        var parsed = Input.getInputFromRaw(slotId, button, clickType);
        if (parsed == null) return;
        var input = this.inputs.get(slotId);
        if (input == null) return;
        input.handler().accept(parsed);
    }

    public void ssgl_seal(Map<Integer, Button> inputs) {
        this.inputs = inputs;
        inputs.forEach((slot, buttons) -> {
            if (slot != AbstractContainerMenu.SLOT_CLICKED_OUTSIDE) {
                this.slots.get(slot).set(buttons.label().stack());
            }
        });
    }

    @Override
    public boolean ssgl_isSealed() {
        return inputs != null;
    }
}
