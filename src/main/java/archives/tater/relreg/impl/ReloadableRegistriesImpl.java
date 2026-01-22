package archives.tater.relreg.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootDataType;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNullElse;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@ApiStatus.Internal
public class ReloadableRegistriesImpl implements ModInitializer {
	public static final String MOD_ID = "relreg";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final List<ResourceKey<? extends Registry<?>>> RELOADABLE_REGISTRIES =
			Lists.newArrayList(LootDataType.PREDICATE.registryKey(), LootDataType.MODIFIER.registryKey(), LootDataType.TABLE.registryKey());

	public static final List<ResourceKey<? extends Registry<?>>> CUSTOM_RELOADABLE_REGISTRIES = new ArrayList<>();

	public static final List<LootDataType<?>> CUSTOM_LOOT_DATA_TYPES = new ArrayList<>();

	private static final LootDataType.Validator<?> EMPTY_VALIDATOR = (validationContext, resourceKey, object) -> {};

	@SuppressWarnings("unchecked")
    private static <T> LootDataType.Validator<T> emptyValidator() {
		return (LootDataType.Validator<T>) EMPTY_VALIDATOR;
	}

    public static <T> void register(ResourceKey<Registry<T>> key, Codec<T> codec, LootDataType.@Nullable Validator<T> validator) {
		RELOADABLE_REGISTRIES.add(key);
		CUSTOM_RELOADABLE_REGISTRIES.add(key);
		CUSTOM_LOOT_DATA_TYPES.add(new LootDataType<>(key, codec, requireNonNullElse(validator, emptyValidator())));
	}

    public static @Unmodifiable List<ResourceKey<? extends Registry<?>>> getReloadableRegistries() {
		return RELOADABLE_REGISTRIES;
    }

    public static @Unmodifiable List<ResourceKey<? extends Registry<?>>> getCustomReloadableRegistries() {
        return CUSTOM_RELOADABLE_REGISTRIES;
    }

	public static Stream<LootDataType<?>> streamLootDataTypes() {
		return CUSTOM_LOOT_DATA_TYPES.stream();
	}

	public static int executeListRegistry(HolderLookup.Provider registryAccess, Identifier registryId, Consumer<Component> responder) {
		var entries = registryAccess.lookupOrThrow(ResourceKey.createRegistryKey(registryId)).listElements().toList();
		responder.accept(Component.literal("Registry " + registryId + " has " + entries.size() + " entries:"));
		for (var entry : entries) {
			responder.accept(Component.literal(entry.key().identifier().toString()).withStyle(ChatFormatting.YELLOW)
					.append(Component.literal(": ").withStyle(ChatFormatting.WHITE))
					.append(Component.literal(entry.value().toString()).withStyle(ChatFormatting.GRAY)));
		}
		return entries.size();
	}

	private static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("relreg_server")
					.then(argument("registry", IdentifierArgument.id()).executes(command -> executeListRegistry(
							command.getSource().getServer().reloadableRegistries().lookup(),
							IdentifierArgument.getId(command, "registry"),
							text -> command.getSource().sendSuccess(() -> text, false)
					)))
			);
		});
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			registerCommands();
	}
}