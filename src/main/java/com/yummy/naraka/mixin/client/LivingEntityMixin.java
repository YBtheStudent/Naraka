package com.yummy.naraka.mixin.client;

import com.yummy.naraka.core.registries.NarakaRegistries;
import com.yummy.naraka.network.RequestEntityDataPayload;
import com.yummy.naraka.world.entity.data.EntityDataType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.HolderSet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "recreateFromPacket", at = @At("RETURN"))
    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket, CallbackInfo ci) {
        HolderSet<EntityDataType<?>> entityDataTypes = HolderSet.direct(NarakaRegistries.ENTITY_DATA_TYPE.holders().toList());
        ClientPlayNetworking.send(new RequestEntityDataPayload(self(), entityDataTypes));
    }

    @Unique
    private LivingEntity self() {
        return (LivingEntity) (Object) this;
    }
}
