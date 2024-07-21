package com.yummy.naraka.data;

import com.yummy.naraka.data.worldgen.NarakaStructureSets;
import com.yummy.naraka.data.worldgen.NarakaStructures;
import com.yummy.naraka.data.worldgen.features.NarakaFeatures;
import com.yummy.naraka.data.worldgen.placement.NarakaPlacements;
import com.yummy.naraka.world.damagesource.NarakaDamageTypes;
import com.yummy.naraka.world.item.enchantment.NarakaEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;

import java.util.concurrent.CompletableFuture;

public class NarakaDatapackProvider extends RegistriesDatapackGenerator {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, NarakaDamageTypes::bootstrap)
            .add(Registries.ENCHANTMENT, NarakaEnchantments::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, NarakaFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, NarakaPlacements::bootstrap)
            .add(Registries.STRUCTURE, NarakaStructures::bootstrap)
            .add(Registries.STRUCTURE_SET, NarakaStructureSets::bootstrap);

    private final CompletableFuture<HolderLookup.Provider> fullRegistries;


    public NarakaDatapackProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, RegistryPatchGenerator.createLookup(registries, BUILDER)
                .thenApply(RegistrySetBuilder.PatchedRegistries::patches)
        );
        fullRegistries = RegistryPatchGenerator.createLookup(registries, BUILDER)
                .thenApply(RegistrySetBuilder.PatchedRegistries::full);
    }

    public CompletableFuture<HolderLookup.Provider> getRegistryProvider() {
        return fullRegistries;
    }
}
