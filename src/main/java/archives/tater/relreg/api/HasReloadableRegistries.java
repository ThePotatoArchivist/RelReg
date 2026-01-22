package archives.tater.relreg.api;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

/**
 * Extension to {@link Level}, {@link MinecraftServer} and {@link ClientPacketListener} allowing access to reloadable registries
 */
public interface HasReloadableRegistries {
    /**
     * @return A RegistryAccess containing all registries including synced reloadable ones
     */
    default RegistryAccess relreg_reloadableRegistries() {
        throw new AssertionError("Implemented by mixin");
    }
}
