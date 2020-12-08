package com.xiao.databse.testing

import com.xiao.databse.BaseDatabase
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import java.util.regex.Pattern
import javax.sql.DataSource
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
        databases = annotations.associate { it.database to it.database.java.newInstance() }
    }

    fun database(kClass: KClass<out BaseDatabase>): BaseDatabase {
        return databases[kClass] ?: throw IllegalArgumentException("Database ${kClass.simpleName} not registered.")
    }

    @BeforeEach
    fun migration() {
        val params = buildMigrationParams(databases.values)
            params.forEach { param ->
                Flyway
                    .configure()
                    .dataSource(param.dataSource)
                    .locations(*(param.locations + "db/migration").toTypedArray())
                    .sqlMigrationSuffixes(".sql")
                    .schemas(*param.schemas.toTypedArray())
                    .load()
                    .migrate()
            }
    }

    private fun buildMigrationParams(databases: Collection<BaseDatabase>): List<MigrationParams> {
        val regex = Pattern.compile("(.*://.*)/(.*)")
        return databases
            .groupBy {
                it.dataSource()
            }
            .map { (dataSource, databases) ->
                val locations = databases
                    .map {
                        it.datasetPath()
                    }
                    .toSet()
                val schemas = databases
                    .map {
                        val matcher = regex.matcher(it.url)
                        if (matcher.find() && matcher.groupCount() > 1) {
                            return@map matcher.group(2).substringBefore("?")
                        } else {
                            throw IllegalArgumentException("Migrate database ${it.name()} failed.")
                        }
                    }
                    .toSet()
                MigrationParams(dataSource, locations, schemas)
            }
    }

    @Suppress("ArrayInDataClass")
    private data class MigrationParams(
        val dataSource: DataSource,
        val locations: Set<String>,
        val schemas: Set<String>
    )
}
