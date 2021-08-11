package xiao.boot.mybatis.testing

/**
 *
 * @author lix wang
 */
object TestMigrationUtils {
    val migratedTables = HashMap<String, MutableSet<String>>()

    internal fun addMigratedTable(databaseName: String, tableName: String) {
        val tables = migratedTables[databaseName] ?: mutableSetOf()
        tables.add(tableName)
        migratedTables[databaseName] = tables
    }
}