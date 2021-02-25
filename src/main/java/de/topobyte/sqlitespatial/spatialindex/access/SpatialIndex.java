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

package de.topobyte.sqlitespatial.spatialindex.access;

import java.util.ArrayList;
import java.util.List;

import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.IPreparedStatement;
import de.topobyte.luqe.iface.IResultSet;
import de.topobyte.luqe.iface.QueryException;

public class SpatialIndex
{

	private List<SpatialIndexItem> spatialIndex;

	public SpatialIndex(IConnection db, String tableName) throws QueryException
	{
		spatialIndex = getSpatialIndex(db, tableName);
	}

	public int getSize()
	{
		return spatialIndex.size();
	}

	private static List<SpatialIndexItem> getSpatialIndex(IConnection db,
			String tableName) throws QueryException
	{
		List<SpatialIndexItem> items = new ArrayList<>();

		String stmt = "select id,minX,maxX,minY,maxY from " + tableName;
		IPreparedStatement statement = db.prepareStatement(stmt);

		IResultSet results = statement.executeQuery();
		while (results.next()) {
			int id = results.getInt(1);
			int minX = results.getInt(2);
			int maxX = results.getInt(3);
			int minY = results.getInt(4);
			int maxY = results.getInt(5);
			items.add(new SpatialIndexItem(id, minX, maxX, minY, maxY));
		}

		results.close();

		return items;
	}

	public TIntSet getSpatialIndexIds(int minX, int maxX, int minY, int maxY)
	{
		TIntSet results = new TIntHashSet();
		for (SpatialIndexItem item : spatialIndex) {
			if (intersects(item, minX, maxX, minY, maxY)) {
				results.add(item.getId());
			}
		}
		return results;
	}

	private boolean intersects(SpatialIndexItem item, int minX, int maxX,
			int minY, int maxY)
	{
		return !(item.getMinX() > maxX || item.getMaxX() < minX
				|| item.getMaxY() < minY || item.getMinY() > maxY);
	}

}
