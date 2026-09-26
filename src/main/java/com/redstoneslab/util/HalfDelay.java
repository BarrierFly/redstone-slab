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

package com.redstoneslab.util;

import com.redstoneslab.block.RedstoneSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class HalfDelay
{
	private HalfDelay()
	{
	}

	public static boolean isTopSlabBelow(BlockGetter level, BlockPos pos)
	{
		BlockState below = level.getBlockState(pos.below());
		return below.getBlock() instanceof RedstoneSlabBlock && below.getValue(RedstoneSlabBlock.TYPE) == SlabType.TOP;
	}

	public static boolean isDownwardAttached(BlockState state)
	{
		if (state.getBlock() instanceof RedstoneWallTorchBlock)
		{
			return false;
		}
		if (state.getBlock() instanceof ButtonBlock)
		{
			return state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE) == AttachFace.FLOOR;
		}
		if (state.getBlock() instanceof BaseCoralWallFanBlock)
		{
			return false;
		}
		return true;
	}

	public static int halve(int delay, RandomSource random)
	{
		if (delay <= 1)
		{
			return delay;
		}
		if ((delay & 1) == 0)
		{
			return delay / 2;
		}
		return delay / 2 + random.nextInt(2);
	}
}
