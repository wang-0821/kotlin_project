package com.xiao.databse.testing

import com.xiao.databse.BaseDatabase
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import java.util.regex.Pattern
import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class KtTestDataSourceBase  {
    private var databases: Map<KClass<out BaseDatabase>, BaseDatabase> = mapOf()
    private var databaseAnnotations: Map<KClass<out BaseDatabase>, KtTestDatabase> = mapOf()

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
        databases = annotations.associate { it.database to getDatabase(it.database) }
    }

    fun database(kClass: KClass<out BaseDatabase>): BaseDatabase {
        return databases[kClass] ?: throw IllegalArgumentException("Database ${kClass.simpleName} not registered.")
    }

    @BeforeEach
    fun migration() {
        val params = buildMigrationParams(databases.values)
            params.forEach { param ->
                val flyway = Flyway
                    .configure()
                    .dataSource(param.url, param.username, param.password)
                    .locations(*(param.locations + "db/migration").toTypedArray())
                    .sqlMigrationSuffixes(".sql")
                    .schemas(*param.schemas.toTypedArray())
                    .load()
                flyway.clean()
                flyway.migrate()
            }
    }

    protected fun getDatabase(database: KClass<out BaseDatabase>): BaseDatabase {
        return database.java.newInstance()
    }

    private fun buildMigrationParams(databases: Collection<BaseDatabase>): List<MigrationParams> {
        val regex = Pattern.compile("(.*://.*)/(.*)")
        return databases
            .map {
                val matcher = regex.matcher(it.url)
                if (matcher.find() && matcher.groupCount() > 1) {
                    return@map MigrationParams(
                        matcher.group(1),
                        it.username,
                        it.password,
                        setOf(it.datasetPath()),
                        setOf(matcher.group(2))
                    )
                } else {
                    throw IllegalArgumentException("Database $it url doesn't match pattern.")
                }
            }
            .groupBy {
                it.url
            }
            .map {
                val locations = it.value.flatMap { it.locations }.toSet()
                val schemas = it.value.flatMap { it.schemas }.toSet()
                val database = it.value.first()
                MigrationParams(database.url, database.username, database.password, locations, schemas)
            }
    }

    @Suppress("ArrayInDataClass")
    private data class MigrationParams(
        val url: String,
        val username: String,
        val password: String,
        val locations: Set<String>,
        val schemas: Set<String>
    )
}
