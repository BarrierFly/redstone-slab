/*
 * This file is part of the Redstone Slab project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  BarrierFly and contributors
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
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12100
import net.minecraft.world.level.SignalGetter;
//#else
//$$ import net.minecraft.world.level.LevelReader;
//#endif

import java.util.List;

/**
 * Implements the comparator "7.5" semantics for redstone slabs.
 *
 * <p>{@code getAlternateSignal} is declared on {@link DiodeBlock}, not on {@link ComparatorBlock}, so
 * this mixin targets {@code DiodeBlock} and injects at return. Overriding the method was not
 * possible: the target's superclass method is not remapped for a mixin on the subclass.
 *
 * <p>A single slab on a comparator side contributes {@code 8} (ceil of 7.5). When both the front and
 * a side already read 7.5 and no side reaches 8, the side is encoded as {@code 7} instead, so the
 * vanilla integer comparison treats the two 7.5 sources as equal (COMPARE outputs 7, SUBTRACT 0).
 * This only happens when the other side has no input, is also a single slab, or has an input below
 * 8; a side input of 8 or more takes precedence and the normal integer path is used.
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
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		Direction clockwise = facing.getClockWise();
		Direction counterClockwise = facing.getCounterClockWise();
		boolean clockwiseSlab = redstoneslab$isSingleSlab(level.getBlockState(pos.relative(clockwise)));
		boolean counterClockwiseSlab = redstoneslab$isSingleSlab(level.getBlockState(pos.relative(counterClockwise)));
		if (!clockwiseSlab && !counterClockwiseSlab)
		{
			return;
		}
		int vanilla = cir.getReturnValueI();
		if (vanilla < 8 && redstoneslab$frontIsSevenPointFive(level, pos, state))
		{
			cir.setReturnValue(7);
			return;
		}
		int signal = vanilla;
		if (clockwiseSlab)
		{
			signal = Math.max(signal, 8);
		}
		if (counterClockwiseSlab)
		{
			signal = Math.max(signal, 8);
		}
		cir.setReturnValue(signal);
	}

	private static boolean redstoneslab$isSingleSlab(BlockState state)
	{
		return state.getBlock() instanceof RedstoneSlabBlock && state.getValue(RedstoneSlabBlock.TYPE) != SlabType.DOUBLE;
	}

	/**
	 * Whether the front input of the comparator reads 7.5: either the front block is a single slab,
	 * or it is a redstone conductor whose strongest incoming charge is a single slab's horizontal
	 * side charge of 7 while nothing reaches 8. If a container or item frame is read through the
	 * front (capacity), that wins over the signal and is not treated as 7.5.
	 */
	private static boolean redstoneslab$frontIsSevenPointFive(BlockGetter level, BlockPos pos, BlockState state)
	{
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		BlockPos frontPos = pos.relative(facing);
		BlockState front = level.getBlockState(frontPos);
		if (redstoneslab$isSingleSlab(front))
		{
			return true;
		}
		if (!front.isRedstoneConductor(level, frontPos) || front.hasAnalogOutputSignal())
		{
			return false;
		}
		BlockPos behind = frontPos.relative(facing);
		if (level.getBlockState(behind).hasAnalogOutputSignal() || redstoneslab$hasFacingItemFrame(level, behind, facing))
		{
			return false;
		}
		return redstoneslab$strongestCharge(level, frontPos) == 7 && redstoneslab$hasHorizontalSingleSlab(level, frontPos);
	}

	private static int redstoneslab$strongestCharge(BlockGetter level, BlockPos pos)
	{
		int max = 0;
		for (Direction direction : Direction.values())
		{
			BlockPos neighborPos = pos.relative(direction);
			max = Math.max(max, level.getBlockState(neighborPos).getDirectSignal(level, neighborPos, direction));
			if (max >= 15)
			{
				return 15;
			}
		}
		return max;
	}

	private static boolean redstoneslab$hasHorizontalSingleSlab(BlockGetter level, BlockPos pos)
	{
		for (Direction direction : Direction.Plane.HORIZONTAL)
		{
			if (redstoneslab$isSingleSlab(level.getBlockState(pos.relative(direction))))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean redstoneslab$hasFacingItemFrame(BlockGetter level, BlockPos pos, Direction facing)
	{
		if (!(level instanceof Level realLevel))
		{
			return false;
		}
		List<ItemFrame> frames = realLevel.getEntitiesOfClass(ItemFrame.class,
				new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
				frame -> frame.getDirection() == facing);
		return frames.size() == 1;
	}
}
