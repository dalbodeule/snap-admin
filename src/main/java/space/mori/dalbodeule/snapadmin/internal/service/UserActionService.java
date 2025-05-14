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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import space.mori.dalbodeule.snapadmin.external.dto.LogsSearchRequest;
import space.mori.dalbodeule.snapadmin.external.dto.PaginatedResult;
import space.mori.dalbodeule.snapadmin.external.dto.PaginationInfo;
import space.mori.dalbodeule.snapadmin.internal.model.UserAction;
import space.mori.dalbodeule.snapadmin.internal.repository.CustomActionRepositoryImpl;
import space.mori.dalbodeule.snapadmin.internal.repository.UserActionRepository;

/**
 * Service class to retrieve user actions through the {@link CustomActionRepositoryImpl}. 
 *
 */
@Service
public class UserActionService {
	@Autowired
	private UserActionRepository repo;
	
	@Autowired
	private CustomActionRepositoryImpl customRepo;
	
	@Autowired
	private TransactionTemplate internalTransactionTemplate;
	
	public UserAction save(UserAction a) {
		return internalTransactionTemplate.execute(status -> {
			return repo.save(a);
		});
	}
	
	/**
	 * Retruns a page of results of user actions that match the given input request.
	 * @param request a request containing filtering parameters for user actions
	 * @return a page of results matching the input request
	 */
	public PaginatedResult<UserAction> findActions(LogsSearchRequest request) {
		PageRequest page = request.toPageRequest();
		
		long count = customRepo.countActions(request);
		List<UserAction> actions = customRepo.findActions(request);
		int maxPage = (int)(Math.ceil ((double)count / page.getPageSize()));
		
		return new PaginatedResult<>(
			new PaginationInfo(page.getPageNumber() + 1, maxPage, page.getPageSize(), count, null, request),
			actions
		);
	}
	
}
