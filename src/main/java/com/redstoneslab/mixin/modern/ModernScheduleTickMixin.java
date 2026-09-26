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
import com.redstoneslab.mixin.BlockInvoker;
import com.redstoneslab.util.DelayPolicy;
import com.redstoneslab.util.HalfDelay;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.TickPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScheduledTickAccess.class)
public interface ModernScheduleTickMixin
{
	@Inject(method = "scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V", at = @At("HEAD"), cancellable = true)
	private void redstoneslab$scheduleBlock(BlockPos pos, Block block, int delay, CallbackInfo ci)
	{
		ScheduledTickAccess self = (ScheduledTickAccess) (Object) this;
		if (!(self instanceof Level level) || !HalfDelay.isTopSlabBelow(level, pos))
		{
			return;
		}
		if (block instanceof ScaffoldingBlock)
		{
			this.redstoneslab$scaffolding(self, level, pos, block);
			ci.cancel();
			return;
		}
		if (!DelayPolicy.isHalfDelayBlock(block) || delay <= 1 || !HalfDelay.isDownwardAttached(level.getBlockState(pos)))
		{
			return;
		}
		int adjusted = HalfDelay.halve(delay, level.getRandom());
		if (adjusted != delay)
		{
			self.getBlockTicks().schedule(self.createTick(pos, block, adjusted));
			ci.cancel();
		}
	}

	@Inject(method = "scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;ILnet/minecraft/world/ticks/TickPriority;)V", at = @At("HEAD"), cancellable = true)
	private void redstoneslab$scheduleBlockPriority(BlockPos pos, Block block, int delay, TickPriority priority, CallbackInfo ci)
	{
		ScheduledTickAccess self = (ScheduledTickAccess) (Object) this;
		if (!(self instanceof Level level) || !HalfDelay.isTopSlabBelow(level, pos))
		{
			return;
		}
		if (block instanceof ScaffoldingBlock)
		{
			this.redstoneslab$scaffolding(self, level, pos, block);
			ci.cancel();
			return;
		}
		if (!DelayPolicy.isHalfDelayBlock(block) || delay <= 1 || !HalfDelay.isDownwardAttached(level.getBlockState(pos)))
		{
			return;
		}
		int adjusted = HalfDelay.halve(delay, level.getRandom());
		if (adjusted != delay)
		{
			self.getBlockTicks().schedule(self.createTick(pos, block, adjusted, priority));
			ci.cancel();
		}
	}

	@Inject(method = "scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V", at = @At("HEAD"), cancellable = true)
	private void redstoneslab$scheduleFluid(BlockPos pos, Fluid fluid, int delay, CallbackInfo ci)
	{
		ScheduledTickAccess self = (ScheduledTickAccess) (Object) this;
		if (!(self instanceof Level level) || !HalfDelay.isTopSlabBelow(level, pos) || !redstoneslab$isWaterOrLava(fluid) || delay <= 1)
		{
			return;
		}
		int adjusted = HalfDelay.halve(delay, level.getRandom());
		if (adjusted != delay)
		{
			self.getFluidTicks().schedule(self.createTick(pos, fluid, adjusted));
			ci.cancel();
		}
	}

	@Inject(method = "scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;ILnet/minecraft/world/ticks/TickPriority;)V", at = @At("HEAD"), cancellable = true)
	private void redstoneslab$scheduleFluidPriority(BlockPos pos, Fluid fluid, int delay, TickPriority priority, CallbackInfo ci)
	{
		ScheduledTickAccess self = (ScheduledTickAccess) (Object) this;
		if (!(self instanceof Level level) || !HalfDelay.isTopSlabBelow(level, pos) || !redstoneslab$isWaterOrLava(fluid) || delay <= 1)
		{
			return;
		}
		int adjusted = HalfDelay.halve(delay, level.getRandom());
		if (adjusted != delay)
		{
			self.getFluidTicks().schedule(self.createTick(pos, fluid, adjusted, priority));
			ci.cancel();
		}
	}

	private void redstoneslab$scaffolding(ScheduledTickAccess self, Level level, BlockPos pos, Block block)
	{
		if (level instanceof ServerLevel serverLevel && level.getRandom().nextBoolean())
		{
			BlockState state = level.getBlockState(pos);
			((BlockInvoker) block).redstoneslab$tick(state, serverLevel, pos, level.getRandom());
		}
		else
		{
			self.getBlockTicks().schedule(self.createTick(pos, block, 1));
		}
	}

	private static boolean redstoneslab$isWaterOrLava(Fluid fluid)
	{
		return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER || fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
	}
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$
//$$ @Mixin(net.minecraft.world.level.block.Block.class)
//$$ public abstract class ModernScheduleTickMixin
//$$ {
//$$ }
//#endif
