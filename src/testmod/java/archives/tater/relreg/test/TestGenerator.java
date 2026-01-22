package archives.tater.relreg.test;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class TestGenerator extends FabricCodecDataProvider<String> {
    private static String getDirectory(ResourceKey<? extends Registry<?>> registry) {
        return Objects.equals(registry.identifier().getNamespace(), Identifier.DEFAULT_NAMESPACE)
                ? registry.identifier().getPath()
                : registry.identifier().getNamespace() + "/" + registry.identifier().getPath();
    }

    protected TestGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.DATA_PACK, getDirectory(ReloadableRegistriesTest.TEST), ReloadableRegistriesTest.TEST_CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, String> provider, HolderLookup.Provider lookup) {
        provider.accept(Identifier.fromNamespaceAndPath("relregtest", "b"), "BBB");
    }

    @Override
    public String getName() {
        return "";
    }
}
