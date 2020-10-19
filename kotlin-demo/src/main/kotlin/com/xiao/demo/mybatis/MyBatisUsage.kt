package com.xiao.demo.mybatis

import com.xiao.databse.SqlSessionFactoryHelper
import com.xiao.databse.TransactionHelper
import com.xiao.demo.mybatis.mapper.UserMapper
import com.xiao.demo.mybatis.mapper.UserMapperV2
import org.apache.ibatis.session.SqlSessionFactory

/**
 *
 * @author lix wang
 */
internal class MyBatisUsage {
    fun queryUser(sqlSessionFactory: SqlSessionFactory) {
        val sqlSession = sqlSessionFactory.openSession()
        val transactionHelper = TransactionHelper(sqlSession)
        val userMapper = sqlSessionFactory.configuration.getMapper(UserMapper::class.java, sqlSession)
        val userMapperV2 = sqlSessionFactory.configuration.getMapper(UserMapperV2::class.java, sqlSession)

        transactionHelper.doInTransaction {
            println(userMapper.getById(1))
            userMapper.updatePasswordById(1, "password_latest")
            println(userMapperV2.getById(1))
        }
    }

    fun sqlSessionFactory(
        name: String,
        mapperXmlPath: String,
        mapperPath: String,
        databaseUrl: String,
        username: String,
        password: String
    ): SqlSessionFactory {
        if (sqlSessionFactory == null) {
            synchronized(this::class) {
                if (sqlSessionFactory == null) {
                    sqlSessionFactory = SqlSessionFactoryHelper.createSqlSessionFactory(
                        name,
                        mapperXmlPath,
                        mapperPath,
                        databaseUrl,
                        username,
                        password
                    )
                }
                return sqlSessionFactory!!
            }
        } else {
            return sqlSessionFactory!!
        }
    }

    companion object {
        @Volatile var sqlSessionFactory: SqlSessionFactory? = null
    }
}

fun main() {
    val obj = MyBatisUsage()
    val sqlSessionFactory = obj.sqlSessionFactory(
        "demo",
        "classpath*:mybatis/mapper/",
        "com.xiao.demo.mybatis.mapper",
        "jdbc:mysql://localhost:3306/lix_database_demo",
        "root",
        "123456"
    )
    obj.queryUser(sqlSessionFactory)
}