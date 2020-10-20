package com.xiao.databse.testing

import com.xiao.databse.MyBatisDatabase
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class KtDataSourceTestBase  {
    private var databases: Map<KClass<out MyBatisDatabase>, MyBatisDatabase> = mapOf()
    private var databaseAnnotations: Map<KClass<out MyBatisDatabase>, KtTestDatabase> = mapOf()

    @Order(0)
    @BeforeAll
    fun registerDatabase() {
        val annotations = mutableListOf<KtTestDatabase>()
        javaClass.getAnnotation(KtTestDatabase::class.java)?.let {
            annotations.add(it)
        }
        javaClass.getAnnotation(KtTestDatabases::class.java)?.let {
            annotations.addAll(it.value)
        }
        databaseAnnotations = annotations.associateBy { it.database }
        databases = annotations.associate { it.database to it.database.java.newInstance() }
    }

    fun database(kClass: KClass<out MyBatisDatabase>): MyBatisDatabase {
        return databases[kClass] ?: throw IllegalArgumentException("Database ${kClass.simpleName} not registered.")
    }

    @BeforeEach
    fun migration() {
        // TODO This way to migrate database is not concise and beautiful. Need improve later.
        val locations = mutableListOf("db/migration")
        locations.addAll(databases.map { it.value.datasetPath() })
        Flyway.configure()
            .dataSource("jdbc:mysql://localhost:3306?characterEncoding=UTF8", "root", "123456")
            .locations(*locations.toTypedArray())
            .sqlMigrationSuffixes(".sql")
            .schemas("lix_database_demo")
            .load()
            .migrate()
    }
}
