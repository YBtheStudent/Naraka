package com.yummy.naraka.jade;

import com.yummy.naraka.data.lang.NarakaJadeProviders;
import com.yummy.naraka.data.lang.NarakaLanguageProvider;
import com.yummy.naraka.world.entity.data.DeathCountHelper;
import com.yummy.naraka.world.entity.data.Stigma;
import com.yummy.naraka.world.entity.data.StigmaHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class NarakaEntityDataProvider implements IEntityComponentProvider {
    public static final NarakaEntityDataProvider INSTANCE = new NarakaEntityDataProvider();

    @Override
    public ResourceLocation getUid() {
        return NarakaJadeProviders.ENTITY_DATA.location;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor entityAccessor, IPluginConfig pluginConfig) {
        Entity entity = entityAccessor.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            Stigma stigma = StigmaHelper.get(livingEntity);
            if (stigma.getStigma() > 0)
                tooltip.add(Component.translatable(NarakaLanguageProvider.JADE_STIGMA_KEY, stigma.getStigma()));
            if (DeathCountHelper.isDeathCounted(livingEntity))
                tooltip.add(Component.translatable(NarakaLanguageProvider.JADE_DEATH_COUNT_KEY, DeathCountHelper.get(livingEntity)));
        }
    }
}