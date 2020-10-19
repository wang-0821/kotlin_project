package com.xiao.databse

import com.xiao.base.logging.Logging
import org.apache.ibatis.session.SqlSession

/**
 *
 * @author lix wang
 */
class TransactionHelper(val sqlSession: SqlSession) {
    inline fun doInTransaction(block: () -> Unit) {
        try {
            block()
        } catch (e: Throwable) {
            sqlSession.rollback()
            log.error("Transaction call failed. ${e.message}", e)
            throw e
        }
        sqlSession.commit()
    }

    companion object : Logging()
}