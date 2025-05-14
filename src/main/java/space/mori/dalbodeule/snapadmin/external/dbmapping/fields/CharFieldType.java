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


package space.mori.dalbodeule.snapadmin.external.dbmapping.fields;

import java.util.List;

import space.mori.dalbodeule.snapadmin.external.dto.CompareOperator;

public class CharFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "char";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		if (value.toString().isBlank()) return null;
		return value.toString().charAt(0);
	}

	@Override
	public Class<?> getJavaClass() {
		return char.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.STRING_EQ);
	}
}