package com.xiao.databse

import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.transaction.Transaction

/**
 *
 * @author lix wang
 */
class SqlSessionWrapper(val sqlSession: SqlSession, var transaction: Transaction? = null)