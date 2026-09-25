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

package com.redstoneslab.mixin.modern;

//#if MC >= 12110
import com.redstoneslab.block.RedstoneSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin
{
	@SuppressWarnings("override")
	protected int getAlternateSignal(SignalGetter level, BlockPos pos, BlockState state)
	{
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		Direction clockwise = facing.getClockWise();
		Direction counterClockwise = facing.getCounterClockWise();
		int signal = Math.max(
				level.getControlInputSignal(pos.relative(clockwise), clockwise, false),
				level.getControlInputSignal(pos.relative(counterClockwise), counterClockwise, false)
		);
		signal = Math.max(signal, redstoneslab$slabSideSignal(level, pos.relative(clockwise), clockwise));
		signal = Math.max(signal, redstoneslab$slabSideSignal(level, pos.relative(counterClockwise), counterClockwise));
		return signal;
	}

	private static int redstoneslab$slabSideSignal(SignalGetter level, BlockPos neighborPos, Direction readerToNeighbor)
	{
		if (level.getBlockState(neighborPos).getBlock() instanceof RedstoneSlabBlock)
		{
			if (level.getSignal(neighborPos, readerToNeighbor) > 0)
			{
				return 8;
			}
		}
		return 0;
	}

	@Inject(method = "calculateOutputSignal", at = @At("RETURN"), cancellable = true)
	private void redstoneslab$calculateOutputSignal(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir)
	{
		if (redstoneslab$bothSlabs(level, pos, state) && state.getValue(ComparatorBlock.MODE) == ComparatorMode.COMPARE)
		{
			cir.setReturnValue(7);
		}
	}

	@Inject(method = "shouldTurnOn", at = @At("RETURN"), cancellable = true)
	private void redstoneslab$shouldTurnOn(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir)
	{
		if (!cir.getReturnValue() && redstoneslab$bothSlabs(level, pos, state) && state.getValue(ComparatorBlock.MODE) == ComparatorMode.COMPARE)
		{
			cir.setReturnValue(true);
		}
	}

	private static boolean redstoneslab$bothSlabs(Level level, BlockPos pos, BlockState state)
	{
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		boolean front = level.getBlockState(pos.relative(facing)).getBlock() instanceof RedstoneSlabBlock;
		boolean side = level.getBlockState(pos.relative(facing.getClockWise())).getBlock() instanceof RedstoneSlabBlock
				|| level.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock() instanceof RedstoneSlabBlock;
		return front && side;
	}
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$
//$$ @Mixin(net.minecraft.world.level.block.Block.class)
//$$ public abstract class ComparatorBlockMixin
//$$ {
//$$ }
//#endif
