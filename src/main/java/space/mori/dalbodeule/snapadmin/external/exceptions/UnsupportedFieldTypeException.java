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

package space.mori.dalbodeule.snapadmin.external.exceptions;

/**
 * Thrown when a field of an `@Entity` class has a type that is not
 * supported.
 */
public class UnsupportedFieldTypeException extends SnapAdminException {
	private static final long serialVersionUID = -8891734807568233099L;
	
	public UnsupportedFieldTypeException() {
	}
	
	public UnsupportedFieldTypeException(String msg) {
		super(msg);
	}

}
