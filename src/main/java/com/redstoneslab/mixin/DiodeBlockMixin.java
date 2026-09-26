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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12100
import net.minecraft.world.level.SignalGetter;
//#else
//$$ import net.minecraft.world.level.LevelReader;
//#endif

/**
 * Implements the comparator "7.5" side input: a single redstone slab on a side of a comparator
 * contributes 8 instead of the strong value 7. The method is declared on {@link DiodeBlock}, not
 * on {@link ComparatorBlock}, so targeting {@code DiodeBlock} is the only way to reliably remap it
 * on obfuscated versions. Injected via {@code @Inject} at return rather than overriding the method,
 * because mixins cannot override a method declared by the target's superclass.
 */
@Mixin(DiodeBlock.class)
public abstract class DiodeBlockMixin
{
	@Inject(method = "getAlternateSignal", at = @At("RETURN"), cancellable = true)
	private void redstoneslab$slabSideSignal(
			//#if MC >= 12100
			SignalGetter level,
			//#else
			//$$ LevelReader level,
			//#endif
			BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir)
	{
		if (!((Object) this instanceof ComparatorBlock))
		{
			return;
		}
		int signal = cir.getReturnValueI();
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		Direction clockwise = facing.getClockWise();
		Direction counterClockwise = facing.getCounterClockWise();
		signal = Math.max(signal, redstoneslab$singleSlabSide(level, pos.relative(clockwise), clockwise));
		signal = Math.max(signal, redstoneslab$singleSlabSide(level, pos.relative(counterClockwise), counterClockwise));
		cir.setReturnValue(signal);
	}

	private static int redstoneslab$singleSlabSide(BlockGetter level, BlockPos neighborPos, Direction readerToNeighbor)
	{
		BlockState neighbor = level.getBlockState(neighborPos);
		if (neighbor.getBlock() instanceof RedstoneSlabBlock
				&& neighbor.getValue(RedstoneSlabBlock.TYPE) != SlabType.DOUBLE
				&& neighbor.getDirectSignal(level, neighborPos, readerToNeighbor) > 0)
		{
			return 8;
		}
		return 0;
	}
}
