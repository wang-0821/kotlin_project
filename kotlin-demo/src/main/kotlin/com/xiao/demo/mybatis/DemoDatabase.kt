package com.xiao.demo.mybatis

import com.xiao.databse.annotation.KtDatabase
import com.xiao.databse.MyBatisDatabase

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
class DemoDatabase : MyBatisDatabase(URL, USERNAME, PASSWORD) {
    companion object {
        const val NAME = "demo"
        const val MAPPER_PATH = "com.xiao.demo.mybatis.mapper"
        const val MAPPER_XML_PATH = "classpath*:mybatis/mapper/"
        const val DATASET_PATH = "db/demo"
        const val URL = "jdbc:mysql://localhost:3306/lix_database_demo"
        const val USERNAME = "root"
        const val PASSWORD = "123456"
    }
}