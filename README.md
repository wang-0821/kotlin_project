[读书笔记](ReadingNotes.md)

* [1.项目简介](#1)
* [2.IO](#3)
* [8.代码规范及测试](#8)

<h2 id="1">1.项目简介</h2>
&emsp;&emsp; 本项目中包含一些源码阅读笔记和技术书阅读笔记。本项目分为12个子模块，两个大类，一类单纯做了一些功能，
没有集成SpringBoot，另一类是基于SpringBoot实现了一些功能。本项目最终目标是全面运用kotlin协程，
包括：数据库交互、RPC调用、HttpServlet处理、Redis交互，凡是涉及到IO的地方都希望能使用Kotlin协程。

        本项目模块：
            kotlin-base：
                实现了一些基本的功能，如异步队列、基于堆外内存的基本数据类型数组、slfj4日志、
                基于ScheduledExecutorService的定时任务、自定义FastThreadLocal、CoroutineFastThreadLocal等。
                
            kotlin-beans：
                仿spring-beans实现了一个简单的对象管理及依赖注入的容器。
            
            kotlin-database：
                基于MyBatis简化了mapper interface和xml的扫描，实现了一个简单的TransactionHelper，
                可以实现一个transaction block中多个dataSource的事务提交和回滚，并且基于Junit5 Extension 
                和Flyway实现了测试时自动执行数据库迁移。
            
            kotlin-demo：
                kotlin模块使用demo。
            
            kotlin-http：
                不借助框架，仿OkHttp实现的一个Http client，用以加深Http理解。本Http Client支持Transfer-Encoding: gzip, chunked，
                支持HTTP/1.0、HTTP/1.1，支持Socket连接池，支持域名解析InetAddress复用，支持Kotlin协程，不支持IO多路复用。
                
            kotlin-log4j2: 
                本项目全局使用slf4j，因此把log4j2单独拆分为一个模块，可以根据需求替换成其他的日志框架。
            
            kotlin-metrics：
                利用kotlin-base中的定时任务及堆外内存基本数据类型数组，实现了简单的运行指标的间隔输出功能。
                
            kotlin-redis：
                基于Lettuce实现支持Kotlin协程的RedisService，IO多路复用使用Epoll或KQueue，并且实现基于Redis的分布式锁。
            
            kotlin-spring-boot-base：
                实现基于注解的环境变量配置，自定义以下Bean：AutoConfigurationImportFilter 处理自动配置、
                DefaultTestExecutionListenersPostProcessor处理SpringBoot Test中的TestExecutionListener。
                SpringBoot中很多AutoConfiguration并不符合我们的业务需求，例如：FlywayAutoConfiguration，
                我们可以根据业务需求，自定义AutoConfiguration。SpringBoot的EnableAutoConfiguration机制是核心。
                
            kotlin-spring-boot-mybatis：
                基于MyBatis和SpringBoot @Import，实现数据源自动配置，基于注解和Flyway实现数据自动迁移，
                改造测试环境MyBatis的XMLLanguageDriver及SqlSource，实现自动监控缺失的数据迁移。
                
            kotlin-spring-boot-servre-base：
                提供了WebServer支持Kotlin协程的能力，利用@RestControllerAdvice实现了全局异常处理，
                自定义RequestMappingHandlerAdapter、ServletInvocableHandlerMethod，实现支持Http Servlet协程执行。
                
            kotlin-spring-boot-server-undertow：
                利用全局线程池替换掉Undertow默认的线程池，用来提升性能。自定义WebServerFactoryCustomizer
                和UndertowRootInitialHttpHandler，实现利用Kotlin协程来执行请求。

### Kotlin协程是什么？解决了什么问题？
&emsp;&emsp; Kotlin协程基于状态机的原理实现，将协程挂起恢复后要执行的逻辑，都封装到了resumeWith方法中，根据不同的状态执行不同的逻辑。
Java异步导致的问题在于：异步执行一个方法，后续等待获取结果时，通常使用Future.get()，这个方法会使CPU自旋等待异步任务结束，
这会导致CPU执行效率会降低。如果想要解决这个问题，可以每一步都采用callback回调，但Java中全局使用callback回调，会导致代码非常复杂。
Kotlin协程通过挂起和恢复简化了回调的复杂度，并且Kotlin是完全非阻塞的，不会导致CPU自旋，从而来提升CPU效率。

<h2 id="2">2.IO</h2>
&emsp;&emsp; 

<h2 id="8">8.代码规范及测试</h2>
&emsp;&emsp; 本项目使用ktlint来进行代码格式校验及自动纠正。定义gradle ktlintCheck 任务来校验kotlin代码格式，并将ktlintCheck任务放置在
verification check任务之前，那么在执行gradle build之前就会先执行ktlintCheck。还定义了一个 gradle ktlintFormat 任务，这个任务是单独的，
执行这个任务可以根据代码规范，自动进行格式纠正。

    ```Groovy
    task ktlintCheck(type: JavaExec, group: "verification") {
        description = "Gradle check kotlin verification."
        classpath = configurations.ktlint
        main = "com.pinterest.ktlint.Main"
        args "src/**/*.kt"
    }

    check.dependsOn ktlintCheck

    task ktlintFormat(type: JavaExec, group: "formatting") {
        description = "Gradle check kotlin formatting."
        classpath = configurations.ktlint
        main = "com.pinterest.ktlint.Main"
        args "-F", "src/**/*.kt"
    }
    ```

### 测试
&emsp;&emsp; 本项目使用Github Action配合Junit5执行测试。单测很重要，通过单测能够发现bug，
更重要的是当需要重构你的代码或者业务变更时，单测在确保逻辑正确方面能发挥很大的作用。
通常建议在开发完功能后，立即完成所有逻辑分支的单测编写。在我们实际开发过程中，鉴于时间关系，
通常直接以API为切入点进行单测的编写，对于复杂的方法或者Mapper这种API单测可能覆盖不到的，
才会逐个进行测试。对于紧急需求，可以先不写单测，但需要有个时间节点来补上。本项目单测覆盖率100%。
    
    Github workflow CI：
    ```Groovy
    name: Build CI

    # Controls when the action will run. 
    on: [push, pull_request]

    # Run jobs automatically.
    # A workflow run is made up of one or more jobs that can run sequentially or in parallel
    jobs:
      # This workflow contains a single job called "build"
      build:
        # The type of runner that the job will run on
        runs-on: ubuntu-latest

        # Service containers to run with the job
        services:
          # mysql service
          mysql:
            image: mysql:5.7
            env:
              MYSQL_ROOT_PASSWORD: 123456
            ports:
              - "3306:3306"
            options: >-
              --health-cmd "mysqladmin ping"
              --health-interval 10s
              --health-timeout 5s
              --health-retries 3

          # redis service
          redis:
            # Docker Hub image
            image: redis
            ports:
              - "6379:6379"
            # Set health checks to wait until redis has started
            options: >-
              --health-cmd "redis-cli ping"
              --health-interval 10s
              --health-timeout 5s
              --health-retries 5

        # Steps represent a sequence of tasks that will be executed as part of the job
        steps:
          # Checks-out your repository under $GITHUB_WORKSPACE, so your job can access it
          - uses: actions/checkout@v2

          # Set up jdk version
          - uses: actions/setup-java@v1
            with:
              java-version: 1.8

          # use dependencies cache to speed up
          - uses: actions/cache@v2
            with:
              path: |
                ~/.gradle/caches
                ~/.gradle/wrapper
              key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
              restore-keys: |
                ${{ runner.os }}-gradle-

          # Runs build
          - run: |
              echo "Build start..."
              ./gradlew build
              echo "Bulid finished."

          # cleanup gradle cache
          # Remove some files from the Gradle cache, so they aren't cached by GitHub Actions.
          # Restoring these files from a GitHub Actions cache might cause problems for future builds.
          - run: |
              rm -f ~/.gradle/caches/modules-2/modules-2.lock
              rm -f ~/.gradle/caches/modules-2/gc.properties
    ```
