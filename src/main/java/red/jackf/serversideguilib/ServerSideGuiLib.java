package red.jackf.serversideguilib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.jackf.serversideguilib.menus.test.TestMenu;

import static net.minecraft.commands.Commands.literal;

public class ServerSideGuiLib implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(literal("ssgltest").executes(ctx -> {
                new TestMenu(ctx.getSource().getPlayerOrException()).open();
                return 0;
            }))
        );
    }
}
