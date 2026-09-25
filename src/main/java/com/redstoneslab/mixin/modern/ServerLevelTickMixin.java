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
import com.redstoneslab.util.HalfDelay;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelTickMixin
{
	@Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
	private int redstoneslab$alwaysSample(RandomSource random, int bound)
	{
		return 0;
	}

	@Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickPrecipitation(Lnet/minecraft/core/BlockPos;)V"))
	private void redstoneslab$thresholdPrecipitation(ServerLevel level, BlockPos pos)
	{
		BlockPos topPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
		boolean qualified = HalfDelay.isTopSlabBelow(level, topPos.below()) || HalfDelay.isTopSlabBelow(level, topPos);
		int threshold = qualified ? 24 : 48;
		if (level.getRandom().nextInt(threshold) == 0)
		{
			level.tickPrecipitation(pos);
		}
	}
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$
//$$ @Mixin(net.minecraft.world.level.block.Block.class)
//$$ public abstract class ServerLevelTickMixin
//$$ {
//$$ }
//#endif
