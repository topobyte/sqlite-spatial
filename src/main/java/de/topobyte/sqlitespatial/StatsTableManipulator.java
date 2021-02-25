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

package de.topobyte.sqlitespatial;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsTableManipulator
{

	final static Logger logger = LoggerFactory
			.getLogger(StatsTableManipulator.class);

	private final static String STAT_TABLE_NAME = "sqlite_stat1";
	private final static String SPATIAL_INDEX_PREFIX = "sidx_";

	public void manipulateStatsTable(Connection connection) throws SQLException
	{
		logger.info("Manipulating stats table");

		ResultSet results = connection.createStatement()
				.executeQuery("select distinct tbl from " + STAT_TABLE_NAME
						+ " where idx like '" + SPATIAL_INDEX_PREFIX + "%'");

		List<String> tables = new ArrayList<>();
		while (results.next()) {
			tables.add(results.getString(1));
		}
		logger.info("Tables with spatial indices: " + tables);

		for (String table : tables) {
			manipulateStatsForTable(connection, table);
		}
	}

	private void manipulateStatsForTable(Connection connection, String table)
			throws SQLException
	{
		ResultSet results = connection.createStatement()
				.executeQuery("select idx, stat from " + STAT_TABLE_NAME
						+ " where tbl='" + table + "'");

		logger.info("Manipulating rows for table: " + table);

		List<Row> rows = new ArrayList<>();
		while (results.next()) {
			String name = results.getString(1);
			String values = results.getString(2);

			Pattern pattern = Pattern.compile("(\\d+) (\\d+)");
			Matcher matcher = pattern.matcher(values);
			if (matcher.matches()) {
				int a = Integer.valueOf(matcher.group(1));
				int b = Integer.valueOf(matcher.group(2));
				rows.add(new Row(name, a, b));
			}
		}

		for (Row row : rows) {
			int newB = row.b;
			if (row.name.startsWith(SPATIAL_INDEX_PREFIX)) {
				newB = 1;
			} else if (newB == 1) {
				newB = 2;
			} else {
				logger.info("Not touching index: '" + row.name + "': '" + row.a
						+ " " + row.b + "'");
				continue;
			}
			String oldValue = String.format("%d %d", row.a, row.b);
			String newValue = String.format("%d %d", row.a, newB);
			logger.info("Changing index '" + row.name + "': '" + oldValue
					+ "' -> '" + newValue + "'");
			connection.createStatement()
					.executeUpdate("update " + STAT_TABLE_NAME + " set stat='"
							+ newValue + "' where tbl='" + table + "' and idx='"
							+ row.name + "'");
		}
	}

	private class Row
	{
		String name;
		int a, b;

		public Row(String name, int a, int b)
		{
			this.name = name;
			this.a = a;
			this.b = b;
		}
	}

}
