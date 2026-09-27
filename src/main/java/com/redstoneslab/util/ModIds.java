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

package com.redstoneslab.util;

import com.redstoneslab.RedstoneSlabMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModIds
{
	private ModIds()
	{
	}

	//#if MC >= 12111
	public static net.minecraft.resources.Identifier id(String path)
	{
		return net.minecraft.resources.Identifier.fromNamespaceAndPath(RedstoneSlabMod.MOD_ID, path);
	}
	//#elseif MC >= 12100
	//$$ public static net.minecraft.resources.ResourceLocation id(String path)
	//$$ {
	//$$ 	return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RedstoneSlabMod.MOD_ID, path);
	//$$ }
	//#else
	//$$ public static net.minecraft.resources.ResourceLocation id(String path)
	//$$ {
	//$$ 	return new net.minecraft.resources.ResourceLocation(RedstoneSlabMod.MOD_ID, path);
	//$$ }
	//#endif

	public static ResourceKey<Block> block(String path)
	{
		return ResourceKey.create(Registries.BLOCK, id(path));
	}

	public static ResourceKey<Item> item(String path)
	{
		return ResourceKey.create(Registries.ITEM, id(path));
	}

	public static ResourceKey<EntityType<?>> entity(String path)
	{
		return ResourceKey.create(Registries.ENTITY_TYPE, id(path));
	}
}
