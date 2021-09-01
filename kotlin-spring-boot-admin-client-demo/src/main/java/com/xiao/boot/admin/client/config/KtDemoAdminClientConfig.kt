package com.xiao.boot.admin.client.config

import com.xiao.boot.base.property.KtConfiguration
import org.springframework.jmx.export.annotation.ManagedAttribute
import org.springframework.jmx.export.annotation.ManagedOperation
import org.springframework.jmx.export.annotation.ManagedResource

/**
 * @author lix wang
 */
@KtConfiguration
@ManagedResource("admin-demo:name=KtDemoAdminClientConfig")
class KtDemoAdminClientConfig {
    var value: String = "undefined"
        @ManagedAttribute
        get
        @ManagedAttribute(defaultValue = "kt-demo-value")
        set

    @ManagedOperation
    fun putValue(value: String) {
        this.value = value
    }
}