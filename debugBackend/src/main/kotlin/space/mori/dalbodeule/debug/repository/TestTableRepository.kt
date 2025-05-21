package space.mori.dalbodeule.debug.repository

import org.springframework.data.jpa.repository.JpaRepository
import space.mori.dalbodeule.debug.model.TestTable

interface TestTableRepository: JpaRepository<TestTable, String> {
}