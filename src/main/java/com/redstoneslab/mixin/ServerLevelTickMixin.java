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

import com.redstoneslab.util.HalfDelay;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC < 12100
//$$ import net.minecraft.world.level.GameRules;
//$$ import net.minecraft.world.level.biome.Biome;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.level.block.SnowLayerBlock;
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif

/**
 * Doubles the ice / snow probability above a top redstone slab.
 *
 * <p>Vanilla decides the 1-in-N chance <em>before</em> the random position is sampled, so the
 * threshold cannot be chosen from the position without restructuring the method. To stay
 * compatible with MixinExtras-based mods (which hook {@code @Redirect}), this runs an additional
 * precipitation pass restricted to qualifying positions, giving approximately double the chance.
 *
 * <p>1.21.1 and later expose {@code tickPrecipitation}, so the extra pass simply reuses it. 1.19.4
 * inlines the ice/snow logic in {@code tickChunk} and has no such helper, so the pass performs the
 * same operations (freeze, snow layer growth and precipitation handling) itself.
 */
@Mixin(ServerLevel.class)
public class ServerLevelTickMixin
{
	@Inject(method = "tickChunk", at = @At("HEAD"))
	private void redstoneslab$extraPrecipitation(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci)
	{
		ServerLevel level = (ServerLevel) (Object) this;
		RandomSource random = level.getRandom();
		ChunkPos chunkPos = chunk.getPos();
		int x = chunkPos.getMinBlockX();
		int z = chunkPos.getMinBlockZ();
		//#if MC >= 12100
		if (randomTickSpeed <= 0)
		{
			return;
		}
		for (int k = 0; k < randomTickSpeed; k++)
		{
			BlockPos candidate = level.getBlockRandomPos(x, 0, z, 15);
			BlockPos topPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, candidate);
			boolean qualified = HalfDelay.isTopSlabBelow(level, topPos.below()) || HalfDelay.isTopSlabBelow(level, topPos);
			if (qualified && random.nextInt(48) == 0)
			{
				level.tickPrecipitation(candidate);
			}
		}
		//#else
		//$$ BlockPos candidate = level.getBlockRandomPos(x, 0, z, 15);
		//$$ BlockPos topPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, candidate);
		//$$ boolean qualified = HalfDelay.isTopSlabBelow(level, topPos.below()) || HalfDelay.isTopSlabBelow(level, topPos);
		//$$ if (qualified && random.nextInt(16) == 0)
		//$$ {
		//$$ 	redstoneslab$tickPrecipitation(level, topPos);
		//$$ }
		//#endif
	}

	//#if MC < 12100
	//$$ private static void redstoneslab$tickPrecipitation(ServerLevel level, BlockPos topPos)
	//$$ {
	//$$ 	BlockPos below = topPos.below();
	//$$ 	Biome biome = level.getBiome(topPos).value();
	//$$ 	if (biome.shouldFreeze(level, below))
	//$$ 	{
	//$$ 		level.setBlockAndUpdate(below, Blocks.ICE.defaultBlockState());
	//$$ 	}
	//$$ 	if (level.isRaining())
	//$$ 	{
	//$$ 		int maxHeight = level.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
	//$$ 		if (maxHeight > 0 && biome.shouldSnow(level, topPos))
	//$$ 		{
	//$$ 			BlockState state = level.getBlockState(topPos);
	//$$ 			if (state.is(Blocks.SNOW))
	//$$ 			{
	//$$ 				int layers = state.getValue(SnowLayerBlock.LAYERS);
	//$$ 				if (layers < Math.min(maxHeight, 8))
	//$$ 				{
	//$$ 					BlockState grown = state.setValue(SnowLayerBlock.LAYERS, layers + 1);
	//$$ 					Block.pushEntitiesUp(state, grown, level, topPos);
	//$$ 					level.setBlockAndUpdate(topPos, grown);
	//$$ 				}
	//$$ 			}
	//$$ 			else
	//$$ 			{
	//$$ 				level.setBlockAndUpdate(topPos, Blocks.SNOW.defaultBlockState());
	//$$ 			}
	//$$ 		}
	//$$ 		Biome.Precipitation precipitation = biome.getPrecipitationAt(below);
	//$$ 		if (precipitation != Biome.Precipitation.NONE)
	//$$ 		{
	//$$ 			BlockState state = level.getBlockState(below);
	//$$ 			state.getBlock().handlePrecipitation(state, level, below, precipitation);
	//$$ 		}
	//$$ 	}
	//$$ }
	//#endif
}
