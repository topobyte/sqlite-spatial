
// Copyright 2021 Sebastian Kuerten
//
// This file is part of sqlite-spatial.
//
// sqlite-spatial is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// sqlite-spatial is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with sqlite-spatial. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.sqlitespatial.spatialindex.builder;

public class Rectangle
{

	private int minX;
	private int maxX;
	private int minY;
	private int maxY;

	public Rectangle(int minX, int maxX, int minY, int maxY)
	{
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public int getMinX()
	{
		return minX;
	}

	public int getMaxX()
	{
		return maxX;
	}

	public int getMinY()
	{
		return minY;
	}

	public int getMaxY()
	{
		return maxY;
	}

	public int getWidth()
	{
		return maxX - minX;
	}

	public int getHeight()
	{
		return maxY - minY;
	}

	public void expandToInclude(int x, int y)
	{
		if (x > maxX) {
			maxX = x;
		}
		if (x < minX) {
			minX = x;
		}
		if (y > maxY) {
			maxY = y;
		}
		if (y < minY) {
			minY = y;
		}
	}

	@Override
	public String toString()
	{
		return String.format("%d : %d, %d : %d", minX, maxX, minY, maxY);
	}

}
