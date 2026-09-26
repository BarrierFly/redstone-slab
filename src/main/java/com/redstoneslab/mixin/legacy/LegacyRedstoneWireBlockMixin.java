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

package com.redstoneslab.mixin.legacy;

import com.redstoneslab.util.WireReadGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC < 12102
//$$ @Mixin(net.minecraft.world.level.block.RedStoneWireBlock.class)
//$$ public abstract class LegacyRedstoneWireBlockMixin
//$$ {
//$$ 	@Inject(method = "calculateTargetStrength", at = @At("HEAD"))
//$$ 	private void redstoneslab$enterWireRead(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir)
//$$ 	{
//$$ 		WireReadGuard.push();
//$$ 	}
//$$
//$$ 	@Inject(method = "calculateTargetStrength", at = @At("RETURN"))
//$$ 	private void redstoneslab$exitWireRead(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir)
//$$ 	{
//$$ 		WireReadGuard.pop();
//$$ 	}
//$$ }
//#else
@Mixin(net.minecraft.world.level.block.Block.class)
public abstract class LegacyRedstoneWireBlockMixin
{
}
//#endif
