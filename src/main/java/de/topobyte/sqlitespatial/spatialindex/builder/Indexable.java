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

public interface Indexable
{

	public int getX();

	public int getY();

	public void setNode(Node<? extends Indexable> node);

	public Node<? extends Indexable> getNode();

	public void setSid(int sid);

}
