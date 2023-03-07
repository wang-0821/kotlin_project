package com.xiao.cloud.sentinel

import com.alibaba.csp.sentinel.Entry
import com.alibaba.csp.sentinel.SphU
import com.alibaba.csp.sentinel.slots.block.BlockException
import com.alibaba.csp.sentinel.slots.block.RuleConstant
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager
import org.junit.jupiter.api.Test
import kotlin.math.min

/**
 *
 * @author lix wang
 */
class SentinelDemo {
    @Test
    fun doTestFlowRule() {
        initFlowRules()
        while (true) {
            var entry: Entry? = null
            try {
                entry = SphU.entry(RESOURCE_NAME)
//                println("hello world loop")
            } catch (ex: BlockException) {
                println("blocked!")
            } finally {
                entry?.exit()
            }
        }
    }

    @Test
    fun doTestDegradeRule() {
        initDegradeRules()
        (0..Long.MAX_VALUE).forEach {
            var entry: Entry? = null
            try {
                entry = SphU.entry(RESOURCE_NAME)
                Thread.sleep(min(it, 50L))
                println("$it hello world degrade loop")
            } catch (ex: BlockException) {
                println("$it degrade blocked!")
            } finally {
                entry?.exit()
            }
        }
    }

    companion object {
        const val RESOURCE_NAME = "HelloWorld"

        @JvmStatic
        fun initFlowRules() {
            FlowRule().apply {
                resource = RESOURCE_NAME
                grade = RuleConstant.FLOW_GRADE_QPS
                // Set limit QPS to 20
                count = 20.0
            }.also {
                FlowRuleManager.loadRules(listOf(it))
            }
        }

        @JvmStatic
        fun initDegradeRules() {
            DegradeRule().apply {
                resource = RESOURCE_NAME
                grade = CircuitBreakerStrategy.SLOW_REQUEST_RATIO.type
                count = 40.0
                timeWindow = 5
                minRequestAmount = 10
                statIntervalMs = 2000
                slowRatioThreshold = 0.90
            }.also {
                DegradeRuleManager.loadRules(listOf(it))
            }
        }
    }
}

fun main() {
    SentinelDemo().run {
        doTestFlowRule()
//        doTestDegradeRule()
    }
}