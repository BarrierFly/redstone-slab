/*
 * This file is part of the Redstone Slab project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  GTC and contributors
 *
 * Redstone Slab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Redstone Slab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Redstone Slab.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.redstoneslab.mixin;

import com.redstoneslab.block.RedstoneSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Comparator "7.5" special case: when both the front and a side of the comparator are single
 * redstone slabs, COMPARE mode outputs 7 and the comparator turns on, instead of the vanilla
 * result 0 (front 7, side 8). Double slabs are excluded: they emit 15 on every face and are
 * handled by the normal integer logic.
 *
 * <p>The side signal itself (7 -> 8) is implemented in {@link DiodeBlockMixin}, because
 * {@code getAlternateSignal} is declared on {@code DiodeBlock}.
 */
@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin
{
	@Inject(method = "calculateOutputSignal", at = @At("RETURN"), cancellable = true)
	private void redstoneslab$calculateOutputSignal(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir)
	{
		if (redstoneslab$bothSingleSlabs(level, pos, state) && state.getValue(ComparatorBlock.MODE) == ComparatorMode.COMPARE)
		{
			cir.setReturnValue(7);
		}
	}

	@Inject(method = "shouldTurnOn", at = @At("RETURN"), cancellable = true)
	private void redstoneslab$shouldTurnOn(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir)
	{
		if (!cir.getReturnValueZ() && redstoneslab$bothSingleSlabs(level, pos, state) && state.getValue(ComparatorBlock.MODE) == ComparatorMode.COMPARE)
		{
			cir.setReturnValue(true);
		}
	}

	private static boolean redstoneslab$bothSingleSlabs(Level level, BlockPos pos, BlockState state)
	{
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		return redstoneslab$isSingleSlab(level.getBlockState(pos.relative(facing)))
				&& (redstoneslab$isSingleSlab(level.getBlockState(pos.relative(facing.getClockWise())))
				|| redstoneslab$isSingleSlab(level.getBlockState(pos.relative(facing.getCounterClockWise()))));
	}

	private static boolean redstoneslab$isSingleSlab(BlockState state)
	{
		return state.getBlock() instanceof RedstoneSlabBlock && state.getValue(RedstoneSlabBlock.TYPE) != SlabType.DOUBLE;
	}
}
