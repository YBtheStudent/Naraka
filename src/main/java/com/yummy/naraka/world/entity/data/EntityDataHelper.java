package com.yummy.naraka.world.entity.data;

import com.yummy.naraka.core.registries.NarakaRegistries;
import com.yummy.naraka.network.SyncEntityDataPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class EntityDataHelper {
    private static final Map<UUID, EntityDataContainer> ENTITY_DATA_MAP = new HashMap<>();
    private static final Map<EntityDataType<?>, List<EntityDataChangeListener>> DATA_CHANGE_LISTENERS = new HashMap<>();

    public static <T> void registerDataChangeListener(EntityDataType<T> entityDataType, DataChangeListener<T> listener) {
        List<EntityDataChangeListener> listeners = DATA_CHANGE_LISTENERS.computeIfAbsent(entityDataType, _entityDataType -> new ArrayList<>());
        listeners.add(wrap(entityDataType, listener));
    }

    private static <T> EntityDataHelper.EntityDataChangeListener wrap(EntityDataType<T> entityDataType, EntityDataHelper.DataChangeListener<T> listener) {
        return (livingEntity, _entityDataType, from, to) -> {
            Class<T> type = entityDataType.getValueType();
            listener.onChange(livingEntity, entityDataType, type.cast(from), type.cast(to));
        };
    }

    public static void syncEntityData(LivingEntity livingEntity, EntityDataType<?> entityDataType) {
        Holder<EntityDataType<?>> holder = EntityDataTypes.holder(entityDataType);
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            CompoundTag data = new CompoundTag();
            saveEntityData(livingEntity, data);
            for (ServerPlayer player : serverLevel.players())
                ServerPlayNetworking.send(player, new SyncEntityDataPayload(livingEntity, holder, data));
        }
    }

    public static void syncEntityData(LivingEntity livingEntity) {
        HolderSet<EntityDataType<?>> holders = HolderSet.direct(
                NarakaRegistries.ENTITY_DATA_TYPE.holders()
                        .filter(holder -> hasEntityData(livingEntity, holder.value()))
                        .toList()
        );
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            CompoundTag data = new CompoundTag();
            saveEntityData(livingEntity, data);
            for (ServerPlayer player : serverLevel.players())
                ServerPlayNetworking.send(player, new SyncEntityDataPayload(livingEntity, holders, data));
        }
    }

    public static void setEntityData(LivingEntity livingEntity, EntityDataType<?> entityDataType, Object value) {
        EntityDataContainer container = ENTITY_DATA_MAP.computeIfAbsent(livingEntity.getUUID(), uuid -> new EntityDataContainer());
        Object original = container.getEntityData(entityDataType);
        container.setEntityData(entityDataType, value);
        syncEntityData(livingEntity, entityDataType);
        for (EntityDataChangeListener listener : DATA_CHANGE_LISTENERS.computeIfAbsent(entityDataType, type -> new ArrayList<>()))
            listener.onChange(livingEntity, entityDataType, original, value);
    }

    public static <T> T getEntityData(LivingEntity livingEntity, EntityDataType<T> entityDataType) {
        if (!ENTITY_DATA_MAP.containsKey(livingEntity.getUUID()))
            return entityDataType.getDefaultValue();
        EntityDataContainer container = ENTITY_DATA_MAP.get(livingEntity.getUUID());
        return container.getEntityData(entityDataType);
    }

    public static void removeEntityData(LivingEntity entity) {
        ENTITY_DATA_MAP.remove(entity.getUUID());
    }

    public static void saveEntityData(LivingEntity livingEntity, CompoundTag compoundTag) {
        RegistryAccess registries = livingEntity.level().registryAccess();
        EntityDataContainer container = ENTITY_DATA_MAP.get(livingEntity.getUUID());
        if (container != null)
            container.save(compoundTag, registries);
    }

    public static void readEntityData(LivingEntity livingEntity, CompoundTag compoundTag) {
        RegistryAccess registries = livingEntity.level().registryAccess();
        EntityDataContainer container = ENTITY_DATA_MAP.computeIfAbsent(livingEntity.getUUID(), uuid -> new EntityDataContainer());
        if (livingEntity instanceof Player)
            container.loadDefault();
        container.read(compoundTag, registries);
    }

    public static boolean hasEntityData(LivingEntity livingEntity) {
        return ENTITY_DATA_MAP.containsKey(livingEntity.getUUID());
    }

    public static boolean hasEntityData(LivingEntity livingEntity, EntityDataType<?> entityDataType) {
        if (hasEntityData(livingEntity)) {
            return ENTITY_DATA_MAP.get(livingEntity.getUUID())
                    .hasEntityData(entityDataType);
        }
        return false;
    }

    private interface EntityDataChangeListener {
        void onChange(LivingEntity livingEntity, EntityDataType<?> entityDataType, Object from, Object to);
    }

    public interface DataChangeListener<T> {
        void onChange(LivingEntity livingEntity, EntityDataType<T> entityDataType, T from, T to);
    }
}
