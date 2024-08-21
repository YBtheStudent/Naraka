package com.yummy.naraka.world.block;

import com.mojang.serialization.MapCodec;
import com.yummy.naraka.world.block.entity.ForgingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ForgingBlock extends BaseEntityBlock {
    public static final MapCodec<ForgingBlock> CODEC = simpleCodec(ForgingBlock::new);

    public ForgingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ForgingBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof ForgingBlockEntity forgingBlockEntity) {
            if (itemStack.is(Items.MACE)) {
                forgingBlockEntity.tryReinforce();
                itemStack.hurtAndBreak(5, player, EquipmentSlot.MAINHAND);
            } else if (itemStack.isEmpty()) {
                forgingBlockEntity.dropItem();
            } else if (forgingBlockEntity.getItemStack().isEmpty()) {
                forgingBlockEntity.setItemStack(itemStack);
                itemStack.shrink(1);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
