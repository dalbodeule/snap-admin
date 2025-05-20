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

package space.mori.dalbodeule.snapadmin.internal.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import space.mori.dalbodeule.snapadmin.internal.model.ConsoleQuery;
import space.mori.dalbodeule.snapadmin.internal.repository.ConsoleQueryRepository;

@Service
public class ConsoleQueryService {
	@Autowired
	private TransactionTemplate internalTransactionTemplate;
	
	@Autowired
	private ConsoleQueryRepository repo;

	private final Logger logger = Logger.getLogger(ConsoleQueryService.class.getName());

	public ConsoleQuery save(ConsoleQuery q) {
		try {
			return internalTransactionTemplate.execute((status) -> {
				return repo.save(q);
			});
		} catch(Exception e) {
			logger.severe("Error while saving console query: " + e.getMessage());
			throw e;
		}
	}
	
	public void delete(String id) {
		internalTransactionTemplate.executeWithoutResult((status) -> {
			repo.deleteById(id);
		});
	}
	
	public List<ConsoleQuery> findAll() {
		return repo.findAll();
	}
	
	public Optional<ConsoleQuery> findById(String id) {
		return repo.findById(id);
	}
}
