package com.xiao.database

import com.xiao.databse.annotation.KtDatabase
import com.xiao.databse.BaseDatabase

/**
 *
 * @author lix wang
 */
@KtDatabase(
    name = DemoDatabase.NAME,
    mapperPath = DemoDatabase.MAPPER_PATH,
    mapperXmlPath = DemoDatabase.MAPPER_XML_PATH,
    dataSetPath = DemoDatabase.DATASET_PATH
)
class DemoDatabase : BaseDatabase(URL, USERNAME, PASSWORD) {
    companion object {
        const val NAME = "demo"
        const val MAPPER_PATH = "com.xiao.database.mybatis.mapper"
        const val MAPPER_XML_PATH = "classpath*:mybatis/mapper/"
        const val DATASET_PATH = "db/common"
        const val URL = "jdbc:mysql://localhost:3306/lix_database_common"
        const val USERNAME = "root"
        const val PASSWORD = "123456"
    }
}