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

import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

/**
 * Tests whether a moved block state depends on the block below it for support.
 * <p>
 * Only blocks that require a downward support are considered attached: rails, floor torches,
 * floor buttons, pressure plates, diodes, carpets, coral, fire, snow layers and plants. Wall
 * mounted variants (wall torch, wall button, wall coral fan) and plain full blocks are not.
 */
public final class DownwardSupport
{
	private DownwardSupport()
	{
	}

	public static boolean requiresDownwardSupport(BlockState state)
	{
		Block block = state.getBlock();
		if (block instanceof BaseRailBlock)
		{
			return true;
		}
		if (block instanceof BaseCoralWallFanBlock)
		{
			return false;
		}
		if (block instanceof BaseCoralPlantTypeBlock)
		{
			return true;
		}
		if (block instanceof CarpetBlock)
		{
			return true;
		}
		if (block instanceof WallTorchBlock || block instanceof RedstoneWallTorchBlock)
		{
			return false;
		}
		if (block instanceof TorchBlock || block instanceof RedstoneTorchBlock)
		{
			return true;
		}
		if (block instanceof FaceAttachedHorizontalDirectionalBlock)
		{
			return state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE) == AttachFace.FLOOR;
		}
		if (block instanceof BasePressurePlateBlock)
		{
			return true;
		}
		if (block instanceof DiodeBlock)
		{
			return true;
		}
		if (block instanceof BaseFireBlock)
		{
			return true;
		}
		if (block instanceof SnowLayerBlock)
		{
			return true;
		}
		if (block instanceof BushBlock)
		{
			return true;
		}
		return false;
	}
}
