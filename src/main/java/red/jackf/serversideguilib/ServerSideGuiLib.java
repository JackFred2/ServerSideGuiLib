package red.jackf.serversideguilib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class ServerSideGuiLib implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("ssgltest").executes(ctx -> {
                ctx.getSource().getPlayerOrException().openMenu(ServerMenus.make9x3(Component.literal("Test Menu")));
                return 0;
            }));
        });
    }
}
