package red.jackf.serversideguilib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.jackf.serversideguilib.menus.test.PlayerManagementMenu;
import red.jackf.serversideguilib.utils.Button;
import red.jackf.serversideguilib.utils.Input;
import red.jackf.serversideguilib.labels.Label;
import red.jackf.serversideguilib.menus.MenuBuilder;

import static net.minecraft.commands.Commands.literal;

public class ServerSideGuiLib implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("ssgltest").executes(ctx -> {
                new PlayerManagementMenu(ctx.getSource().getPlayerOrException()).open();
                return 0;
            }));
        });
    }
}
