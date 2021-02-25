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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Node<T extends Indexable>
{

	private NodeType type;
	private Rectangle envelope;

	// for type = LEAF
	private List<T> items;

	// for type = NODE
	private Node<T> child1, child2;

	public NodeType getType()
	{
		return type;
	}

	public Rectangle getEnvelope()
	{
		return envelope;
	}

	public List<T> getItems()
	{
		return items;
	}

	public Node<T> getChild1()
	{
		return child1;
	}

	public Node<T> getChild2()
	{
		return child2;
	}

	public void init(List<T> items, int maxItems)
	{
		// Determine the covering rectangle of all items
		T first = items.iterator().next();
		envelope = new Rectangle(first.getX(), first.getX(), first.getY(),
				first.getY());
		for (T item : items) {
			envelope.expandToInclude(item.getX(), item.getY());
			if (item.getNode() == null) {
				item.setNode(this);
			}
		}
		if (items.size() <= maxItems) {
			type = NodeType.LEAF;
			System.out.println(envelope);
			this.items = items;
			return;
		}

		type = NodeType.NODE;
		final SplitDirection direction = envelope.getWidth() > envelope
				.getHeight() ? SplitDirection.HORIZONTAL
						: SplitDirection.VERTICAL;

		System.out.println("split: " + direction);

		Collections.sort(items, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2)
			{
				if (direction == SplitDirection.HORIZONTAL) {
					return Double.compare(o1.getX(), o2.getX());
				} else {
					return Double.compare(o1.getY(), o2.getY());
				}
			}
		});

		int size = items.size();
		int middle = (size + 1) / 2;

		child1 = new Node<>();
		child2 = new Node<>();

		List<T> items1 = new ArrayList<>();
		List<T> items2 = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			T item = items.get(i);
			if (i == middle - 1) {
				items1.add(item);
				items2.add(item);
				if (item.getNode() == this) {
					item.setNode(child1);
				}
			} else if (i < middle) {
				items1.add(item);
				if (item.getNode() == this) {
					item.setNode(child1);
				}
			} else {
				items2.add(item);
				if (item.getNode() == this) {
					item.setNode(child2);
				}
			}
		}
		child1.init(items1, maxItems);
		child2.init(items2, maxItems);
	}

}
