package com.yummy.naraka;

import com.yummy.naraka.attachment.NarakaAttachments;
import com.yummy.naraka.block.NarakaBlocks;
import com.yummy.naraka.entity.NarakaEntities;
import com.yummy.naraka.item.NarakaCreativeModTabs;
import com.yummy.naraka.item.NarakaItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(NarakaMod.MOD_ID)
public class NarakaMod
{
    public static final String MOD_ID = "naraka";

    public NarakaMod(IEventBus modEventBus, ModContainer modContainer)
    {
        NarakaItems.register(modEventBus);
        NarakaBlocks.register(modEventBus);
        NarakaCreativeModTabs.register(modEventBus);
        NarakaEntities.register(modEventBus);
        NarakaAttachments.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, NarakaConfig.SPEC);
    }

    public static ResourceLocation neoLocation(String path) {
        return new ResourceLocation("neoforge", path);
    }

    /**
     * Returns mod's resource location
     *
     * @param path Resource path
     * @return {@linkplain ResourceLocation} with namespace {@linkplain NarakaMod#MOD_ID}
     */
    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ResourceLocation location(String prefix, String path) {
        return location("%s/%s".formatted(prefix, path));
    }

    public static NarakaConfig config() {
        return NarakaConfig.getInstance();
    }
}
