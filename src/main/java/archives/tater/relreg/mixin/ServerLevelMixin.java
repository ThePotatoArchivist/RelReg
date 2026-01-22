package archives.tater.relreg.mixin;

import archives.tater.relreg.api.HasReloadableRegistries;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements HasReloadableRegistries {
    @Shadow
    @Final
    private MinecraftServer server;

    @Override
    public RegistryAccess relreg_reloadableRegistries() {
        return server.relreg_reloadableRegistries();
    }
}
