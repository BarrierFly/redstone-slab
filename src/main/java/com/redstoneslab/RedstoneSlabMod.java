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

package com.redstoneslab;

import com.mojang.logging.LogUtils;
import com.redstoneslab.block.RedstoneSlabBlock;
import com.redstoneslab.entity.RedstoneSlabFallingEntity;
import com.redstoneslab.util.ModIds;
import net.fabricmc.api.ModInitializer;
//#if MC >= 260000
//$$ import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
//#else
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
//#endif
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.slf4j.Logger;

//#if MC < 12000
//$$ import net.minecraft.world.level.material.Material;
//#endif

public class RedstoneSlabMod implements ModInitializer
{
	public static final String MOD_ID = "redstoneslab";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final RedstoneSlabBlock REDSTONE_SLAB = Registry.register(
			BuiltInRegistries.BLOCK,
			ModIds.block("redstone_slab"),
			new RedstoneSlabBlock(createBlockProperties())
	);

	public static final Item REDSTONE_SLAB_ITEM = Registry.register(
			BuiltInRegistries.ITEM,
			ModIds.item("redstone_slab"),
			createBlockItem()
	);

	public static final EntityType<RedstoneSlabFallingEntity> FALLING_REDSTONE_SLAB = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ModIds.entity("falling_redstone_slab"),
			EntityType.Builder.of(RedstoneSlabFallingEntity::new, MobCategory.MISC)
					.sized(0.98F, 0.98F)
					.clientTrackingRange(10)
					.updateInterval(2)
					//#if MC >= 12110
					.build(ModIds.entity("falling_redstone_slab"))
					//#else
					//$$ .build(MOD_ID + ":falling_redstone_slab")
					//#endif
	);

	private static BlockBehaviour.Properties createBlockProperties()
	{
		return
				//#if MC >= 12000
				BlockBehaviour.Properties.of()
						//#else
						//$$ BlockBehaviour.Properties.of(Material.METAL)
						//#endif
						//#if MC >= 12110
						.setId(ModIds.block("redstone_slab"))
						//#endif
						.mapColor(MapColor.FIRE)
						.requiresCorrectToolForDrops()
						.strength(5.0F, 6.0F)
						.sound(SoundType.METAL)
						.isRedstoneConductor((state, level, pos) -> false)
						//#if MC >= 260000
						//$$ .pushReaction(PushReaction.PUSH_PULL);
						//#elseif MC >= 12000
						.pushReaction(PushReaction.NORMAL);
						//#else
						//$$ ;
						//#endif
	}

	private static Item createBlockItem()
	{
		return new BlockItem(
				REDSTONE_SLAB,
				//#if MC >= 12110
				new Item.Properties().setId(ModIds.item("redstone_slab")).useBlockDescriptionPrefix()
				//#else
				//$$ new Item.Properties()
				//#endif
		);
	}

	@Override
	public void onInitialize()
	{
		//#if MC >= 260000
		//$$ CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
		//$$ 		.register(output -> output.accept(new ItemStack(REDSTONE_SLAB_ITEM)));
		//$$ CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
		//$$ 		.register(output -> output.accept(new ItemStack(REDSTONE_SLAB_ITEM)));
		//#else
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
				.register(entries -> entries.accept(new ItemStack(REDSTONE_SLAB_ITEM)));
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS)
				.register(entries -> entries.accept(new ItemStack(REDSTONE_SLAB_ITEM)));
		//#endif
	}
}
