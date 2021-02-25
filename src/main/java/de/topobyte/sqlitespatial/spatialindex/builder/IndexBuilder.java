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

import java.util.ArrayList;
import java.util.List;

public class IndexBuilder<T extends Indexable>
{

	public Node<T> build(List<T> items, int maxItems)
	{
		Node<T> node = new Node<>();
		node.init(items, maxItems);
		return node;
	}

	public List<Node<T>> getLeafs(Node<T> node)
	{
		List<Node<T>> nodes = new ArrayList<>();
		addChildren(nodes, node);
		return nodes;
	}

	private void addChildren(List<Node<T>> nodes, Node<T> node)
	{
		if (node.getType() == NodeType.LEAF) {
			nodes.add(node);
		} else {
			addChildren(nodes, node.getChild1());
			addChildren(nodes, node.getChild2());
		}
	}

}
