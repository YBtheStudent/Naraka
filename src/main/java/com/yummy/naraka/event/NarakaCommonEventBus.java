package com.yummy.naraka.event;

import com.yummy.naraka.NarakaContext;
import com.yummy.naraka.NarakaMod;
import com.yummy.naraka.attachment.AttachmentSyncHelper;
import com.yummy.naraka.attachment.DeathCountHelper;
import com.yummy.naraka.attachment.StigmaHelper;
import com.yummy.naraka.core.NarakaRegistries;
import com.yummy.naraka.network.payload.ChangeDeathCountVisibilityPayload;
import com.yummy.naraka.network.payload.SyncEntityIntAttachmentPayload;
import com.yummy.naraka.world.block.NarakaBlocks;
import com.yummy.naraka.world.block.NectariumBlock;
import com.yummy.naraka.world.block.entity.SoulCraftingBlockEntity;
import com.yummy.naraka.world.entity.Herobrine;
import com.yummy.naraka.world.entity.NarakaEntityTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = NarakaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NarakaCommonEventBus {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AttachmentSyncHelper.initialize();
            NarakaContext.initialize();
            NarakaBlocks.setFlammableBlocks();
        });
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(new RegistryBuilder<>(NarakaRegistries.STRUCTURE_PIECE_FACTORY)
                .sync(true)
                .defaultKey(NarakaMod.location("empty"))
                .maxId(128)
                .create()
        );
    }

    @SubscribeEvent
    public static void loadConfig(ModConfigEvent.Loading event) {
        DeathCountHelper.loadConfig();
        StigmaHelper.loadConfig();
        NectariumBlock.loadConfig();
        SoulCraftingBlockEntity.loadConfig();
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("3");
        registrar.playBidirectional(
                SyncEntityIntAttachmentPayload.TYPE,
                SyncEntityIntAttachmentPayload.CODEC,
                SyncEntityIntAttachmentPayload::handle
        );
        registrar.playToClient(
                ChangeDeathCountVisibilityPayload.TYPE,
                ChangeDeathCountVisibilityPayload.CODEC,
                ChangeDeathCountVisibilityPayload::handle
        );
    }

    @SubscribeEvent
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(NarakaEntityTypes.HEROBRINE.get(), Herobrine.getAttributeSupplier());
    }
}
