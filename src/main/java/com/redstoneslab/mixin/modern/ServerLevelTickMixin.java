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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Doubles the ice / snow probability above a top redstone slab.
 *
 * <p>Vanilla decides the 1-in-48 chance <em>before</em> the random position is sampled, so the
 * threshold cannot be chosen from the position without restructuring the method. To stay
 * compatible with MixinExtras-based mods (which hook {@code @Redirect}), this runs an additional
 * 1-in-48 precipitation pass restricted to qualifying positions. The combined probability for a
 * qualifying position is therefore approximately 2-in-48 = 1-in-24.
 */
@Mixin(ServerLevel.class)
public class ServerLevelTickMixin
{
	@Inject(method = "tickChunk", at = @At("HEAD"))
	private void redstoneslab$extraPrecipitation(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci)
	{
		if (randomTickSpeed <= 0)
		{
			return;
		}
		ServerLevel level = (ServerLevel) (Object) this;
		RandomSource random = level.getRandom();
		ChunkPos chunkPos = chunk.getPos();
		int x = chunkPos.getMinBlockX();
		int z = chunkPos.getMinBlockZ();
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
