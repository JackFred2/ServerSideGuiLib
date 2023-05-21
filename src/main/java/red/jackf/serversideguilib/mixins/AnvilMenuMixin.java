package red.jackf.serversideguilib.mixins;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.menus.utils.TextMenu;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.utils.SSGLAnvilMenu;
import red.jackf.serversideguilib.utils.SealedMenu;

import java.util.function.Predicate;

/**
 * Used for extended features for the text input
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends AbstractContainerMenu implements SSGLAnvilMenu {
    /**
     * This looks dodgy but has a reason: if we don't manually overwrite the output slot every text field update,
     * the client will do it on its own, which results in an empty slot. We can't send the same stack to a slot again, so
     * we alternate each time to keep the invalid label there.
     */
    private static final ItemStack INVALID1 = Label.item(Items.BARRIER, "Invalid Input").stacks().get(0);
    private static final ItemStack INVALID2 = Label.item(Items.BARRIER, "Invalid Input").stacks().get(0);
    static {
        INVALID2.getOrCreateTag().putBoolean("ssgl_itemToggleHack", true);
    }
    @Unique
    private boolean invalidToggleHack;
    @Nullable
    @Unique
    private Predicate<String> predicate;

    protected AnvilMenuMixin(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
        throw new AssertionError();
    }

    @Override
    public void ssgl_setTextPredicate(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    @Inject(method = "setItemName",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/AnvilMenu;itemName:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER), cancellable = true)
    private void ssgl_updateItemName(String newName, CallbackInfo ci) {
        if (((SealedMenu) this).ssgl_isSealed()) {
            ci.cancel();
            //noinspection AssignmentUsedAsCondition
            this.slots.get(AnvilMenu.RESULT_SLOT).set((predicate == null || predicate.test(newName)) ?
                    Label.builder().item(TextMenu.RESULT_ITEM).name(newName).inputHint(new Input.LeftClick(false)).build().stacks().get(0) :
                    (invalidToggleHack = !invalidToggleHack) ? INVALID1 : INVALID2);
        }
    }
}
