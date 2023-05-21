package red.jackf.serversideguilib.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StackUtils {
    private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);

    public static List<Component> getLore(ItemStack stack) {
        var display = stack.getTagElement(ItemStack.TAG_DISPLAY);
        if (display == null || display.getTagType(ItemStack.TAG_LORE) != Tag.TAG_LIST) return new ArrayList<>();
        var lore = display.getList(ItemStack.TAG_LORE, Tag.TAG_STRING);
        var list = new ArrayList<Component>();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.getString(i);

            try {
                MutableComponent component = Component.Serializer.fromJson(line);
                if (component != null)
                    list.add(ComponentUtils.mergeStyles(component, LORE_STYLE));
            } catch (Exception var19) {
                return new ArrayList<>();
            }
        }
        return list;
    }
}
