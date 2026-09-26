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

package com.redstoneslab.block;

import com.redstoneslab.entity.RedstoneSlabFallingEntity;
import com.redstoneslab.util.WireReadGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

//#if MC >= 12110
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.redstone.Orientation;
//#endif

public class RedstoneSlabBlock extends SlabBlock
{
	public RedstoneSlabBlock(BlockBehaviour.Properties properties)
	{
		super(properties);
	}

	@Override
	public boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		SlabType type = state.getValue(TYPE);
		if (type == SlabType.DOUBLE)
		{
			return 15;
		}
		if (direction.getAxis().isHorizontal())
		{
			return 7;
		}
		if (type == SlabType.TOP)
		{
			return direction == Direction.DOWN ? 15 : 0;
		}
		return direction == Direction.UP ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		if (WireReadGuard.isReadingWireInput())
		{
			return 0;
		}
		return this.getSignal(state, level, pos, direction);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston)
	{
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide())
		{
			this.notifyRedstone(level, pos);
			this.scheduleFallIfNeeded(level, pos, state);
		}
	}

	//#if MC >= 12110
	@Override
	public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston)
	{
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
		if (!movedByPiston)
		{
			this.notifyRedstone(level, pos);
		}
	}
	//#else
	//$$ @Override
	//$$ public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
	//$$ {
	//$$ 	if (!state.is(newState.getBlock()))
	//$$ 	{
	//$$ 		super.onRemove(state, level, pos, newState, movedByPiston);
	//$$ 		if (!level.isClientSide())
	//$$ 		{
	//$$ 			this.notifyRedstone(level, pos);
	//$$ 		}
	//$$ 	}
	//$$ }
	//#endif

	//#if MC >= 12110
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block neighborBlock, Orientation orientation, boolean movedByPiston)
	{
		super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
		if (!level.isClientSide())
		{
			this.scheduleFallIfNeeded(level, pos, state);
		}
	}
	//#else
	//$$ @Override
	//$$ public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	//$$ {
	//$$ 	super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
	//$$ 	if (!level.isClientSide())
	//$$ 	{
	//$$ 		this.scheduleFallIfNeeded(level, pos, state);
	//$$ 	}
	//$$ }
	//#endif

	//#if MC >= 12110
	@Override
	public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random)
	{
		if (state.getValue(TYPE) == SlabType.TOP)
		{
			scheduledTickAccess.scheduleTick(pos, this, 2);
		}
		return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
	}
	//#else
	//$$ @Override
	//$$ public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
	//$$ {
	//$$ 	if (state.getValue(TYPE) == SlabType.TOP)
	//$$ 	{
	//$$ 		level.scheduleTick(pos, this, 2);
	//$$ 	}
	//$$ 	return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	//$$ }
	//#endif

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		if (state.getValue(TYPE) == SlabType.TOP && FallingBlock.isFree(level.getBlockState(pos.below())))
		{
			RedstoneSlabFallingEntity.fall(level, pos, state);
		}
	}

	private void scheduleFallIfNeeded(Level level, BlockPos pos, BlockState state)
	{
		if (state.getValue(TYPE) == SlabType.TOP)
		{
			level.scheduleTick(pos, this, 2);
		}
	}

	private void notifyRedstone(Level level, BlockPos pos)
	{
		level.updateNeighborsAt(pos, this);
		for (Direction direction : Direction.values())
		{
			level.updateNeighborsAt(pos.relative(direction), this);
		}
	}
}
