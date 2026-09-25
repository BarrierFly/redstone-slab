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

package com.redstoneslab.client;

import com.redstoneslab.RedstoneSlabMod;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
//#if MC >= 12110
import net.minecraft.client.renderer.entity.EntityRenderers;
//#else
//$$ import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
//#endif

public class RedstoneSlabModClient implements ClientModInitializer
{
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void onInitializeClient()
	{
		//#if MC >= 12110
		EntityRenderers.register(
				RedstoneSlabMod.FALLING_REDSTONE_SLAB,
				(EntityRendererProvider) context -> (EntityRenderer) new FallingBlockRenderer(context)
		);
		//#else
		//$$ EntityRendererRegistry.register(
		//$$ 		RedstoneSlabMod.FALLING_REDSTONE_SLAB,
		//$$ 		(EntityRendererProvider) context -> (EntityRenderer) new FallingBlockRenderer(context)
		//$$ );
		//#endif
	}
}
