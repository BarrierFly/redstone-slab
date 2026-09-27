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

public final class WireReadGuard
{
	private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);

	private WireReadGuard()
	{
	}

	public static boolean isReadingWireInput()
	{
		return DEPTH.get()[0] > 0;
	}

	public static void push()
	{
		DEPTH.get()[0]++;
	}

	public static void pop()
	{
		int[] depth = DEPTH.get();
		if (depth[0] > 0)
		{
			depth[0]--;
		}
	}
}
