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

import java.util.List;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.IPreparedStatement;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.sqlitespatial.spatialindex.builder.IndexBuilder;
import de.topobyte.sqlitespatial.spatialindex.builder.Indexable;
import de.topobyte.sqlitespatial.spatialindex.builder.Node;
import de.topobyte.sqlitespatial.spatialindex.builder.Rectangle;

public class SpatialIndexTableCreator
{

	public static <T extends Indexable> void createIndex(IConnection db,
			String tableName, List<T> items, int maxNodeSize)
			throws QueryException
	{
		IndexBuilder<T> indexBuilder = new IndexBuilder<>();
		Node<T> root = indexBuilder.build(items, maxNodeSize);

		IPreparedStatement create = db.prepareStatement("create table "
				+ tableName
				+ " (id integer, minX integer, maxX integer, minY integer, maxY integer)");
		create.executeQuery();

		IPreparedStatement insert = db.prepareStatement(
				"insert into " + tableName + " values (?, ?, ?, ?, ?)");

		List<Node<T>> leafs = indexBuilder.getLeafs(root);
		for (int i = 0; i < leafs.size(); i++) {
			Node<T> leaf = leafs.get(i);

			Rectangle r = leaf.getEnvelope();

			insert.setInt(1, i);
			insert.setInt(2, r.getMinX());
			insert.setInt(3, r.getMaxX());
			insert.setInt(4, r.getMinY());
			insert.setInt(5, r.getMaxY());
			insert.executeQuery();

			List<T> leafItems = leaf.getItems();
			for (T t : leafItems) {
				if (t.getNode() == leaf) {
					t.setSid(i);
				}
			}
		}
	}

}
