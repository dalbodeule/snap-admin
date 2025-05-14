/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps

 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package space.mori.dalbodeule.snapadmin.external.dbmapping.query;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for results returned by user-provided SQL queries run via
 * the SQL console.
 */
public class DbQueryResult {
	private List<DbQueryResultRow> rows;

	public DbQueryResult(List<DbQueryResultRow> rows) {
		this.rows = rows;
	}
	
	public List<DbQueryResultRow> getRows() {
		return rows;
	}
	
	public boolean isEmpty() {
		return rows.isEmpty();
	}
	
	public List<DbQueryOutputField> getSortedFields() {
		if (isEmpty()) {
			return new ArrayList<>();
		} else {
			return rows.get(0).getSortedFields();
		}
	}
	
	public int size() {
		return rows.size();
	}
	
	public void crop(int startOffset, int endOffset) {
		rows = rows.subList(startOffset, endOffset);
	}
}
