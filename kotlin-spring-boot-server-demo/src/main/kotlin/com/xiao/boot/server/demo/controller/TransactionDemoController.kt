package com.xiao.boot.server.demo.controller

import com.xiao.boot.server.demo.service.TransactionDemoService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author lix wang
 */
@RestController
@RequestMapping("/api/v1/demo/transaction")
class TransactionDemoController(
    private val transactionDemoService: TransactionDemoService
) {
    @PostMapping("/testRollback")
    fun rollback(@RequestParam("id") id: Long, @RequestParam("username") username: String) {
        transactionDemoService.updateUsernameInTransaction(id, username)
    }

    @PostMapping("/testTransactionServiceRollback")
    fun transactionServiceRollback(
        @RequestParam("id") id: Long,
        @RequestParam("username") username: String
    ) {
        transactionDemoService.rollbackOnTransactionService(id, username)
    }
}