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

import com.redstoneslab.util.DownwardSupport;
import com.redstoneslab.util.HalfDelay;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin
{
	@ModifyVariable(method = "tick", at = @At(value = "STORE"), ordinal = 0)
	private static float redstoneslab$fasterProgress(float value, Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity)
	{
		if (HalfDelay.isTopSlabBelow(level, pos) && DownwardSupport.requiresDownwardSupport(blockEntity.getMovedState()))
		{
			return value + 0.5F;
		}
		return value;
	}
}
