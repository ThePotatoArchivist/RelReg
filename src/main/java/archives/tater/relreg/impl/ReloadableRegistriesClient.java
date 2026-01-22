package archives.tater.relreg.impl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import dev.xpple.clientarguments.arguments.CIdentifierArgument;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ReloadableRegistriesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            registerCommands();
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                dispatcher.register(literal("relreg_client")
                        .then(argument("registry", CIdentifierArgument.id()).executes(command -> ReloadableRegistriesImpl.executeListRegistry(
                                command.getSource().registryAccess(),
                                CIdentifierArgument.getId(command, "registry"),
                                command.getSource()::sendFeedback
                        )))
                );
        });
    }
}
