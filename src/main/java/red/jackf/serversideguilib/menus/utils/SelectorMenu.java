package red.jackf.serversideguilib.menus.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.menus.MenuBuilder;
import red.jackf.serversideguilib.menus.SucceedableMenu;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.CancellableCallback;
import red.jackf.serversideguilib.utils.Sounds;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Opens a menu for a user to select an option. Resizes from 5x1, 9x1 up to 9x6, then paginated 9x6 if overflowing.
 * Can be cancelled.
 * @param <T> Type of option
 */
public class SelectorMenu<T> extends SucceedableMenu<T> {
    private static final int PAGINATION_THRESHOLD = 54;
    private static final int OPTIONS_PER_PAGE = 8 * 6;
    private final Component title;
    private final List<Pair<Label, T>> options;

    private int page = 1;
    private final int maxPage;
    private final boolean paginated;

    /**
     * Creates a selection menu from a map of label -> options.
     * @param player Player to create the menu for
     * @param title Title to display on the menu
     * @param options List of {@link Label} -> option pairs to be displayed in the selector.
     * @param callback CancellableCallback with success being run when an option is selected, or failure being run on cancellation
     */
    public SelectorMenu(ServerPlayer player, Component title, List<Pair<Label, T>> options, CancellableCallback<T> callback) {
        super(player, callback);
        this.title = title;
        this.options = options;
        this.paginated = options.size() >= PAGINATION_THRESHOLD;
        this.maxPage = paginated ? ((options.size() - 1) / OPTIONS_PER_PAGE) + 1 : 1;
        assert maxPage != 1;
    }

    /**
     * Creates a selection menu from a map of label -> options. You can use a linked map such as
     * {@link java.util.LinkedHashMap}  in order to have consistent ordering in the option menu.
     * @param player Player to create the menu for
     * @param title Title to display on the menu
     * @param options Map of options to be displayed in the selector. Keys should be labels displayed to the player, while
     *                values should be what is returned.
     * @param callback CancellableCallback with success being run when an option is selected, or failure being run on cancellation
     */
    public SelectorMenu(ServerPlayer player, Component title, Map<Label, T> options, CancellableCallback<T> callback) {
        this(player, title, options.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).collect(Collectors.toList()), callback);

    }

    /**
     * Returns a MenuBuilder that fits the specified number of elements, assuming size <= 54.
     */
    private static MenuBuilder smallestMenuThatFits(Component title, int size) {
        if (size <= 5) return MenuBuilder.make5x1(title);
        if (size <= 9) return MenuBuilder.make9x1(title);
        if (size <= 18) return MenuBuilder.make9x2(title);
        if (size <= 27) return MenuBuilder.make9x3(title);
        if (size <= 36) return MenuBuilder.make9x4(title);
        if (size <= 45) return MenuBuilder.make9x5(title);
        return MenuBuilder.make9x6(title);
    }

    @Override
    public void open() {
        if (!paginated) {
            var menu = smallestMenuThatFits(title, options.size() + 1);

            for (int i = 0; i < options.size(); i++) {
                var option = options.get(i);
                menu.addButton(i, Button.leftClick(option.getFirst(), () -> this.complete(option.getSecond())));
            }

            menu.addButton(-1, Button.cancel(this::cancel));
            menu.open(player);
        } else { // 9x6 with 48 per page and right hand for page controls
            var menu = MenuBuilder.make9x6(title);

            var pageOptions = options.subList((page - 1) * OPTIONS_PER_PAGE, Math.min(page * OPTIONS_PER_PAGE, options.size()));
            for (int i = 0; i < pageOptions.size(); i++) {
                var option = pageOptions.get(i);
                var position = i + (i / 8);
                menu.addButton(position, Button.leftClick(option.getFirst(), () -> this.complete(option.getSecond())));
            }

            if (page > 1)
                menu.addButton(8, Button.leftClick(Label.item(Items.RED_CONCRETE, Component.translatable("spectatorMenu.previous_page").withStyle(Label.BLANK)), () -> {
                    this.page = Math.max(1, page - 1);
                    Sounds.interact(player, ((float) page / maxPage) + 1);
                    open();
                }));

            menu.addButton(17, Button.display(Label.item(Items.PAPER, Component.translatable("book.pageIndicator", page, maxPage).withStyle(Label.BLANK))));

            if (page < maxPage)
                menu.addButton(26, Button.leftClick(Label.item(Items.LIME_CONCRETE, Component.translatable("spectatorMenu.next_page").withStyle(Label.BLANK)), () -> {
                    this.page = Math.min(maxPage, page + 1);
                    Sounds.interact(player, ((float) page / maxPage) + 1);
                    open();
                }));


            menu.addButton(-1, Button.cancel(this::cancel));

            menu.open(player);
        }
    }
}
