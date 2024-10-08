package com.yummy.naraka.world.item.reinforcement;

import com.yummy.naraka.NarakaMod;
import com.yummy.naraka.util.NarakaItemUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Map;
import java.util.function.Predicate;

public class DefenceIncrease implements ReinforcementEffect {
    private static final int[] DEFENCE_POINTS = {
            1, 1, 2, 2, 2, 3, 3, 6, 12, 50
    };

    private static final Map<Predicate<ItemStack>, EquipmentSlotGroup> SLOT_GROUP_MAP = Map.of(
            itemStack -> itemStack.is(ItemTags.HEAD_ARMOR), EquipmentSlotGroup.HEAD,
            itemStack -> itemStack.is(ItemTags.CHEST_ARMOR), EquipmentSlotGroup.CHEST,
            itemStack -> itemStack.is(ItemTags.LEG_ARMOR), EquipmentSlotGroup.LEGS,
            itemStack -> itemStack.is(ItemTags.FOOT_ARMOR), EquipmentSlotGroup.FEET
    );

    private static AttributeModifier createModifier(int reinforcement) {
        reinforcement = Mth.clamp(reinforcement, 0, DEFENCE_POINTS.length);
        int defence = 0;
        for (int i = 0; i < reinforcement; i++)
            defence += DEFENCE_POINTS[i];
        return new AttributeModifier(NarakaMod.location("reinforcement_effect", "defence"), defence, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean canApply(LivingEntity entity, EquipmentSlot equipmentSlot, ItemStack itemStack, int reinforcement) {
        return itemStack.is(ItemTags.ARMOR_ENCHANTABLE);
    }

    @Override
    public void onReinforcementIncreased(ItemStack itemStack, int previousReinforcement, int currentReinforcement) {
        ItemAttributeModifiers modifiers = NarakaItemUtils.getAttributeModifiers(itemStack);
        itemStack.set(
                DataComponents.ATTRIBUTE_MODIFIERS,
                modifiers.withModifierAdded(
                        Attributes.ARMOR,
                        createModifier(currentReinforcement),
                        findSlot(itemStack)
                )
        );
    }

    private EquipmentSlotGroup findSlot(ItemStack itemStack) {
        for (Predicate<ItemStack> predicate : SLOT_GROUP_MAP.keySet()) {
            if (predicate.test(itemStack))
                return SLOT_GROUP_MAP.get(predicate);
        }
        return EquipmentSlotGroup.ARMOR;
    }
}
