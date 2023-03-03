* [1.Sentinel执行流程](#1)
* 2.[规则种类](#2)

<h2 id="1">1.Sentinel执行流程</h2>
&emsp;&emsp; sentinel所有的资源都对应一个Entry，Entry可以通过对主流框架的适配自动创建，
也可以通过注解的方式或调用API显式创建，每个Entry创建时，会创建一些列功能插槽(slot chain)。

    NodeSelectorSlot    负责收集资源的路径，并将资源的调用路径，以树状结构存储起来，用于根据调用路径来限流降级。
    ClusterBuilderSlot  用于存储资源的统计信息及调用者信息，这些信息将用作多维度限流，降级的依据。
    StatisticSlot       用于记录、统计不同维度的运行时指标监控信息。
    FlowSlot            用于根据预设的限流规则以及前面slot统计的状态，来进行流量控制。
    AuthoritySlot       根据配置的黑白名单和调用来源信息，做黑白名单控制。
    DegradeSlot         通过统计信息以及预设的规则，来做熔断降级。
    SystemSlot          通过系统的状态，来控制总的入口流量。

    ----------------> NodeSelectorSlot ----------> StatisticsSlot -------> ParamFlowSlot
    外部请求            调用链路构建      统计簇点构建    监控统计                热点参数限流
    Slot Chain Entry

    -------------> SystemSlot --------> AuthoritySlot --------> FlowSlot -------> DegradeSlot -----> Action
                    系统保护               来源访问控制            流量控制            熔断降级

<h2 id="2">2.规则种类</h2>
&emsp;&emsp; sentinel支持以下规则：流量控制规则、熔断降级规则、系统保护规则、来源访问控制规则、热点参数规则。
同一个资源可以同时有多个降级规则。

    FlowRule 流量控制规则，属性；
        resource        资源名，限流规则的作用对象
        count           限流阈值
        grade           限流阈值类型，QPS或线程数模式        默认QPS模式
        limitApp        流控针对的调用来源                  默认default，不区分调用来源
        strategy        调用关系限流策略：直接、链路、关联     默认直接
        controlBehavior 流控效果(直接拒绝/排队等待/慢启动模式) 默认直接拒绝

    private static void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource(resource);
        // Set max qps to 20
        rule1.setCount(20);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rules.add(rule1);
        FlowRuleManager.loadRules(rules);
    }

    DegradeRule 熔断降级规则，属性：
        resource            资源名，规则的作用对象
        grade               熔断策略，支持慢调用比例/异常比例/异常数策略     默认慢调用比例
        count               慢调用比例模式下为慢调用临界RT
        timeWindow          熔断时长，单位为s
        minRequestAmount    熔断触发的最小请求数，请求数小于该值时即使异常比率超出阈值也不会熔断  默认5
        statIntervalMs      统计时长，单位ms       默认1000ms
        slowRatioThreshold  慢调用比例阈值

    private static void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule(resource);
            .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
            .setCount(0.7); // Threshold is 70% error ratio
            .setMinRequestAmount(100)
            .setStatIntervalMs(30000) // 30s
            .setTimeWindow(10);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }


        