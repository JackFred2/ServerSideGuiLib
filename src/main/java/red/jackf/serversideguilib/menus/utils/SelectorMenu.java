package red.jackf.serversideguilib.menus.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.menus.MenuBuilder;
import red.jackf.serversideguilib.menus.SucceedableMenu;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.CancellableCallback;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.utils.Sounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens a menu for a user to select an option. Resizes from 5x1, 9x1 up to 9x6, then paginated 9x6 if overflowing.
 * Can be cancelled. If paginated, also shows a search button filter by the label names; this currently uses translation
 * keys however.
 * @param <T> Type of option
 */
public class SelectorMenu<T> extends SucceedableMenu<T> {
    private static final int PAGINATION_THRESHOLD = 54;
    private static final int OPTIONS_PER_PAGE = 8 * 6;
    private final Component title;
    private final List<Pair<Label, T>> options;
    private final List<Pair<Label, T>> filteredOptions = new ArrayList<>();

    private int page = 1;
    private int maxPage;
    private final boolean paginated;
    private String filter = "";

    /**
     * Creates a selection menu from a map of label -> options.
     * @param player Player to create the menu for
     * @param title Title to display on the menu
     * @param options List of {@link Label} -> option pairs to be displayed in the selector.
     * @param callback CancellableCallback with success being run when an option is selected, or failure being run on cancellation
     */
    protected SelectorMenu(ServerPlayer player, Component title, List<Pair<Label, T>> options, CancellableCallback<T> callback) {
        super(player, callback);
        this.title = title;
        this.options = options;
        this.paginated = options.size() >= PAGINATION_THRESHOLD;
        if (paginated) filteredOptions.addAll(options);
        updateMaxPage();
    }

    private void updateMaxPage() {
        this.maxPage = paginated ? ((filteredOptions.size() - 1) / OPTIONS_PER_PAGE) + 1 : 1;
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

            var pageOptions = filteredOptions.subList((page - 1) * OPTIONS_PER_PAGE, Math.min(page * OPTIONS_PER_PAGE, filteredOptions.size()));
            for (int i = 0; i < pageOptions.size(); i++) {
                var option = pageOptions.get(i);
                var position = i + (i / 8);
                menu.addButton(position, Button.leftClick(option.getFirst(), () -> this.complete(option.getSecond())));
            }

            // previous page
            if (page > 1)
                menu.addButton(8, Button.leftClick(Label.item(Items.RED_CONCRETE, Component.translatable("spectatorMenu.previous_page").withStyle(Label.BLANK)), () -> {
                    this.page = Math.max(1, page - 1);
                    Sounds.interact(player, ((float) page / maxPage) + 1);
                    open();
                }));

            // page number display
            menu.addButton(17, Button.display(Label.item(Items.PAPER, Component.translatable("book.pageIndicator", page, maxPage).withStyle(Label.BLANK))));

            // next page
            if (page < maxPage)
                menu.addButton(26, Button.leftClick(Label.item(Items.LIME_CONCRETE, Component.translatable("spectatorMenu.next_page").withStyle(Label.BLANK)), () -> {
                    this.page = Math.min(maxPage, page + 1);
                    Sounds.interact(player, ((float) page / maxPage) + 1);
                    open();
                }));

            // search
            menu.addButton(35, new Button(Label.builder().item(Items.WRITABLE_BOOK)
                    .name("Current filter: " + this.filter)
                    .inputHint("Set Filter", new Input.LeftClick(false))
                    .inputHint("Clear Filter", new Input.RightClick(false))
                    .build(), input -> {
                if (input instanceof Input.LeftClick click && !click.shift()) {
                    Sounds.interact(player);
                    Menus.string(player, Component.literal("Set Filter"), this.filter, new CancellableCallback<>(s -> {
                            Sounds.success(player);
                            this.filter = s;
                            this.filteredOptions.clear();
                            this.options.stream()
                                    .filter(pair -> pair.getFirst().stack().getHoverName().getString().contains(s))
                                    .forEach(this.filteredOptions::add);
                            updateMaxPage();
                            this.page = Mth.clamp(this.page, 1, this.maxPage);
                            open();
                        }, () -> {
                            Sounds.failure(player);
                            open();
                        })
                    );
                } else if (input instanceof Input.RightClick click && !click.shift()) {
                    this.filter = "";
                    this.filteredOptions.clear();
                    this.filteredOptions.addAll(this.options);
                    Sounds.clear(player);
                    updateMaxPage();
                    open();
                }
            }));

            menu.addButton(-1, Button.cancel(this::cancel));

            menu.open(player);
        }
    }
}
