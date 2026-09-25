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

import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;

public final class DelayPolicy
{
	private DelayPolicy()
	{
	}

	public static boolean isHalfDelayBlock(Block block)
	{
		return block instanceof RepeaterBlock
				|| block instanceof ComparatorBlock
				|| block instanceof RedstoneTorchBlock
				|| block instanceof ButtonBlock
				|| block instanceof DetectorRailBlock
				|| block instanceof BasePressurePlateBlock
				|| block instanceof CoralBlock
				|| block instanceof BaseCoralPlantTypeBlock
				|| block instanceof FireBlock;
	}
}
