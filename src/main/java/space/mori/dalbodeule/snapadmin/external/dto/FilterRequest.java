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


package space.mori.dalbodeule.snapadmin.external.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Describes a request that contains parameters that are used
 * to filter results.  
 *
 */
public interface FilterRequest {
	/**
	 * Converts the request to a MultiValue map that can be 
	 * later converted into a query string
	 * @return
	 */
	public MultiValueMap<String, String> computeParams();
	
	/**
	 * Empty filtering request
	 * @return an empty map
	 */
	public static MultiValueMap<String, String> empty() {
		return new LinkedMultiValueMap<>();
	}
}
