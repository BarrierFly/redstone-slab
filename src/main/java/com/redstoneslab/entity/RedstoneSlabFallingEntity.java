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

package com.redstoneslab.entity;

import com.redstoneslab.RedstoneSlabMod;
import com.redstoneslab.block.RedstoneSlabBlock;
import com.redstoneslab.mixin.FallingBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RedstoneSlabFallingEntity extends FallingBlockEntity
{
	public static final int MAX_TIME = 600;

	public RedstoneSlabFallingEntity(EntityType<? extends FallingBlockEntity> entityType, Level level)
	{
		super(entityType, level);
	}

	//#if MC >= 12110
	@Override
	protected AABB makeBoundingBox(Vec3 position)
	{
		return this.redstoneslab$slabBox(position);
	}
	//#else
	//$$ @Override
	//$$ protected AABB makeBoundingBox()
	//$$ {
	//$$ 	return this.redstoneslab$slabBox(this.position());
	//$$ }
	//#endif

	private AABB redstoneslab$slabBox(Vec3 position)
	{
		return new AABB(
				position.x - 0.49, position.y + 0.5, position.z - 0.49,
				position.x + 0.49, position.y + 1.0, position.z + 0.49);
	}

	public static RedstoneSlabFallingEntity fall(Level level, BlockPos pos, BlockState state)
	{
		RedstoneSlabFallingEntity entity = new RedstoneSlabFallingEntity(RedstoneSlabMod.FALLING_REDSTONE_SLAB, level);
		((FallingBlockEntityAccessor) (Object) entity).redstoneslab$setBlockState(state.setValue(RedstoneSlabBlock.WATERLOGGED, false));
		entity.blocksBuilding = true;
		entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		entity.setDeltaMovement(Vec3.ZERO);
		entity.xo = entity.getX();
		entity.yo = entity.getY();
		entity.zo = entity.getZ();
		level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
		level.addFreshEntity(entity);
		return entity;
	}

	@Override
	public void tick()
	{
		if (this.getBlockState().isAir())
		{
			this.discard();
			return;
		}

		this.time++;

		//#if MC >= 12005
		this.applyGravity();
		//#else
		//$$ this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
		//#endif
		this.move(MoverType.SELF, this.getDeltaMovement());

		this.tickLogic();

		this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
	}

	private BlockPos slabPos()
	{
		return BlockPos.containing(this.getX(), this.getY() + 0.5, this.getZ());
	}

	private void tickLogic()
	{
		Level level = this.level();
		ServerLevel serverLevel = level instanceof ServerLevel && !level.isClientSide() ? (ServerLevel) level : null;
		BlockPos pos = this.slabPos();
		BlockState current = level.getBlockState(pos);

		if (current.getBlock() instanceof RedstoneSlabBlock && current.getValue(RedstoneSlabBlock.TYPE) == SlabType.BOTTOM)
		{
			double planeY = pos.getY() + 0.625;
			if (this.getY() + 0.5 <= planeY && this.getDeltaMovement().y <= 0.0)
			{
				LivingEntity blocker = this.findBlocker(level, pos);
				if (blocker != null)
				{
					this.setPos(this.getX(), planeY - 0.5, this.getZ());
					this.setDeltaMovement(Vec3.ZERO);
					this.time = 0;
					if (serverLevel != null)
					{
						this.hurtAndReset(serverLevel, pos, blocker);
					}
				}
				else if (serverLevel != null)
				{
					serverLevel.setBlock(pos, current.setValue(RedstoneSlabBlock.TYPE, SlabType.DOUBLE).setValue(RedstoneSlabBlock.WATERLOGGED, false), 3);
					this.discard();
				}
			}
			return;
		}

		if (this.onGround())
		{
			if (serverLevel != null)
			{
				this.solidify(serverLevel, pos);
			}
			return;
		}

		if (this.time > MAX_TIME || this.belowWorld(level))
		{
			this.discard();
		}
	}

	private boolean belowWorld(Level level)
	{
		//#if MC >= 12110
		return this.getY() < level.getMinY() - 64;
		//#else
		//$$ return this.getY() < level.getMinBuildHeight() - 64;
		//#endif
	}

	private void solidify(ServerLevel level, BlockPos pos)
	{
		if (level.getBlockState(pos).canBeReplaced())
		{
			level.setBlock(pos, this.getBlockState().setValue(RedstoneSlabBlock.TYPE, SlabType.BOTTOM).setValue(RedstoneSlabBlock.WATERLOGGED, false), 3);
			this.discard();
		}
		else
		{
			this.discard();
			//#if MC >= 12110
			this.spawnAtLocation(level, this.getBlockState().getBlock());
			//#else
			//$$ this.spawnAtLocation(this.getBlockState().getBlock());
			//#endif
		}
	}

	private LivingEntity findBlocker(Level level, BlockPos pos)
	{
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos));
		for (LivingEntity entity : entities)
		{
			if (!entity.isAlive() || entity.isInvulnerable() || entity instanceof net.minecraft.world.entity.decoration.ArmorStand)
			{
				continue;
			}
			double eye = entity.getEyeY();
			if (eye >= pos.getY() && eye < pos.getY() + 1.0)
			{
				return entity;
			}
		}
		return null;
	}

	private void hurtAndReset(ServerLevel level, BlockPos pos, LivingEntity blocker)
	{
		blocker.hurt(level.damageSources().generic(), 1.0F);
		List<RedstoneSlabFallingEntity> falling = level.getEntitiesOfClass(RedstoneSlabFallingEntity.class, new AABB(pos));
		for (RedstoneSlabFallingEntity entity : falling)
		{
			entity.time = 0;
		}
	}
}
