package space.mori.dalbodeule.debug.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="test_table")
data class TestTable(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @Column(nullable = false, length = 32)
    var name: String
) {
    constructor(): this(null, "")
}