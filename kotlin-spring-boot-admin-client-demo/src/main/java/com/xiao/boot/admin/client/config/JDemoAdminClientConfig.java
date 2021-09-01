package com.xiao.boot.admin.client.config;

import com.xiao.boot.base.property.KtConfiguration;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author lix wang
 */
@KtConfiguration
@ManagedResource("admin-demo:name=JDemoAdminClientConfig")
public class JDemoAdminClientConfig {
    private String value = "undefined";

    @ManagedAttribute
    public String getValue() {
        return value;
    }

    @ManagedAttribute(defaultValue = "j-demo-value")
    public void setValue(String value) {
        this.value = value;
    }

    @ManagedOperation
    public void putValue(String value) {
        this.value = value;
    }
}
