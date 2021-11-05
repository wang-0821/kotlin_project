### Maven生命周期
&emsp;&emsp; Maven执行一个阶段的时候，首先会有序的执行前面的所有阶段，直到命令行指定的那个阶段为止。
每个阶段对应了零个或多个目标。

                                    validate 验证项目是否正确及构建信息完整性
                                        |
                                        V
                                generate-sources 生成所有需要包含在编译过程中的源代码
                                        |
                                        V
                                process-sources 处理源代码，如过滤一些值
                                        |
                                        V
                                generate-resources 生成所有需要包含在打包过程中的资源文件
                                        |
                                        V
                                process-resources 复制并处理资源文件到目标目录，准备打包
                                        |
                                        V
                                      compile 编译项目的源代码
                                        |
                                        V
                                 process-classes 处理编译生成的文件，如字节码增强
                                        |
                                        V
                              generate-test-sources 生成所有包含在测试编译过程中的测试源码
                                        |
                                        V
                                process-test-sources 处理测试源码，比如过滤一些值
                                        |
                                        V
                              generate-test-resources 生成测试需要的资源文件
                                        |
                                        V
                              process-test-resources 复制并处理测试资源文件至测试目标目录
                                        |
                                        V
                                    test-compile 编译测试源码至测试目标目录
                                        |
                                        V
                                       test 使用单元测试框架运行测试
                                        |
                                        V
                                 prepare-package 在打包前，执行一些打包必要的操作
                                        |
                                        V
                                     package 将编译好的代码打包成可分发的格式，如JAR
                                        |
                                        V
                                pre-integration-test 执行一些在测试运行之前需要的动作，如建立集成测试需要的环境
                                        |
                                        V
                                integration-test 处理包并发布至集成测试可以运行的环境
                                        |
                                        V
                                post-integration-test 执行一些在集成测试运行后需要的动作，如清理集成测试环境
                                        |
                                        V
                                     verify 执行所有检查，验证包是有效的，符合质量规范的
                                        |
                                        V
                                     install 安装包至本地仓库，以备本地其它项目作为依赖使用
                                        |
                                        V
                                      deploy 复制最终的包至远程仓库，共享给其它开发人员和项目

        
        maven可以运行单个插件目标，如：mvn archetype:create。插件目标是绑定在生命周期上的。
        resources:resources 表示Resources插件的resources目标绑定到了resources阶段。
            这个目标复制src/main/resources下的所有资源和其它任何配置的资源目录，到输出目录。
        compiler:compile 表示Compiler插件的compile目标绑定到compile阶段。
            这个目标复制src/main/java下的所有源代码和其它任何配置的资源目录，到输出目录。
        resources:testResources 表示Resources插件的testResources目标绑定到了test-resources阶段。
            这个目标复制src/test/resources下的所有资源和其它任何的配置的测试资源目录，到测试输出目录。
        compiler:testCompile 表示Compiler插件的testCompile目标绑定到了test-compile阶段。
            这个目标便已src/test/java下的测试用例和其它任何的配置的测试资源目录，到测试输出目录。
        surefire:test 表示Surefire插件的test目标绑定到了test阶段。
            这个阶段运行所有的测试并创建那些捕捉详细测试结果的输出文件。默认情况下，如果有测试失效，这个目标会终止。
        jar:jar 表示Jar插件的jar目标绑定到了package阶段。这个目标把输出目录打包成JAR文件。

### Maven坐标
&emsp;&emsp; 当打包JAR文件时，maven需要知道这个Jar文件的名字。POM提供了项目的一组唯一坐标，
并通过依赖，父和先决条件来定义和其它项目的关系。
    
        packaging类型：jar、war、pom。所有父级项目的packaging类型都是pom，
            子项目的packaging值只能是jar或者war，默认是jar。
        
        <groupId></groupId>           团队标识
        <artifactId></artifactId>     一个单独项目的唯一标识
        <packaging></packaging>       版本号
        <version></version>           项目类型，描述了项目打包后的输出
        
        classifier 如果要发布同样的代码，但由于技术原因需要生成两个单独的构件，
            那么就要使用分类器(classifier)。

### Maven命令

        mvn install 将项目本身编译并打包到本地仓库。

        mvn install -X 调试模式，会打印出DEBUG级日志。

        mvn install -Dmaven.test.skip=true 设置跳过测试构建。
        <plugin>
            <configuration>
                <skip>true</skip>
            </configuration>
        </plugin>

        mvn dependency:resolve 打印已经解决依赖的列表。

        mvn dependency:tree 打印依赖树。

        添加测试范围依赖：
        <dependency>
            <scope>test</scope>
        </dependency>

        mvn test 执行测试单元。
        mvn test -Dmaven.test.failure.ignore=true 通过-D参数设置忽略测试失败。
        <plugin>
            <configuration>
                <testFailureIgnore>true</testFailureIgnore>
            </configuration>
        </plugin>

        mvn compile 编译项目。

        mvn site 站点生成和报告。

        mvn dependency:analyze 用来分析项目的依赖，包括使用并声明的、使用未声明的、未使用但声明的。

        mvn help:effective-pom 查看项目的有效POM。

        

### POM文件基本项目信息

        <licenses><license></license></licenses> 许可
        <organization></organization> 机构
        <developers><developer></developer></developers> 开发人员

### POM依赖

        <project>
            <dependencies>
                <dependency>
                </dependency>
            </dependencies>
        </project>

### Maven Exec插件
&emsp;&emsp; Maven Exec插件可以运行Java类和其它脚本。
    
        mvn help:describe -Dplugin=exec -Dfull 查看Exec插件完整描述。
        mvn exec:java -Dexec.mainClass=org...Main 使用Exec执行Main类。

### 依赖范围
&emsp;&emsp; 测试范围依赖只在测试编译阶段和测试运行时在classpath中有效的依赖。
依赖的范围可以由scope配置，scope取值：compile、provided、runtime、test、system。

        compile：默认的依赖范围，此种依赖在编译、运行、测试时均有效。
        provided：在测试、编译时有效，运行时无效。如servlet-api，容器会提供，就不用maven重复引入了。
        runtime：在运行、测试时有效，编译时无效。如JDBC驱动实现，编译时只需要JDK提供的JDBC接口。
        test：只在测试时有效。如JUnit。
        system：在编译、测试时有效，但在运行时无效。和provided的区别是，使用system范围的依赖时，
            必须通过systemPath元素显式的指定依赖文件的路径。由于此类依赖不是通过Maven仓库解析的，
            而往往与本机系统绑定，可能造成构建的不可移植，因此应该慎用。

        <dependency>
            <scope>test</scope>
        </dependency>

        Maven Surefire插件有一个test目标，该目标被绑定在了test阶段。如果你希望Surefire插件
        遇到了失败的单元测试，还是继续构建，那么需要设置忽略测试失败。
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId> 
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <testFailureIgnore>true</testFailureIgnore>
                    </configuration>
                </plugin>
            </plugins>
        </build>

### POM配置插件

        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>...</groupId>
                        <artifactId>...</artifactId>
                    </plugin>
                </plugins>
            </build>
        </project>

### Maven多模块项目
&emsp;&emsp; 一个多模块项目通过一个父POM引用一个或多个子模块来定义。

    父模块POM配置：
        <project>
            <modelVersion>4.0.0</modelVersion>

            <groupId>org.demo</groupId>
            <artifactId>project-parent</artifactId>
            <packaging>pom<packaging>
            <version>1.0</version>
            <name>...</name>

            <modules>
                <module>project1</module>
                <module>project2</module>
            </modules>

            <build>
                <pluginManagement>
                    <plugins>
                        <plugin>
                            ......
                        </plugin>
                    </plugins>
                </pluginManagement>
            </build>

            <dependencies>
                <dependency>
                    ......
                </dependency>
            </dependencies>
        </project>

    子模块POM配置：
        <project>
            <modelVersion>4.0.0</modelVersion>

            <parent>
                <groupId>org.demo</groupId>
                <artifactId>project-parent</artifactId>
                <version>1.0</version>
            </parent>

            <artifactId>project1</artifactId>
            <packaging>jar</packaging>
            <name>...</name>

            <dependencies>
                <dependency>
                    ......
                </dependency>
            </dependencies>

            <build>
                <plugins>
                    ......
                </plugins>
            </build>
        </project>

### POM依赖管理
&emsp;&emsp; 可以在父POM中使用dependencyManagement来管理依赖版本，然后在子项目中，
可以直接引入依赖而不用再指定依赖版本，如果指定依赖版本会覆盖父POM中的版本。
        
        <project>
            <dependencyManagement>
                <dependencies>
                    <dependency>
                        ......
                    </dependency>
                </dependencies>
            </dependencyManagement>
        </project>

### POM去除依赖的依赖

        <dependency>
            <exclusions>
                <exclusion>
                    <groupId>...</groupId>
                    <artifactId>...</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

### 清理生命周期
&emsp;&emsp; mvn clean将调用清理生命周期，清理生命周期包含了三个生命周期阶段：pre-clean、clean、post-clean。

    <build>
        <executions>
            <execution>
                <id>...</id>
                <phase>pre-clean</phase>
                <goals>
                    <goal>run</goal>
                </goals>

                <configuration>
                    <tasks>
                        ......
                    </tasks>
                </configuration>
            </execution>
        </executions>
    </build>

### 站点生命周期
&emsp;&emsp; mvn site。站点生命周期包含四个阶段：pre-site、site、post-site、site-deploy。

### Maven Profile
&emsp;&emsp; 使用Maven Profile能为一个特殊的环境自定义一个特殊的构建。

        <project>
            <profiles>
                <profile>
                    <build>
                        <plugins>
                            ......
                        </plugins>
                    </build>

                    <modules>
                        ......
                    </modules>

                    <dependencies>
                        ......
                    </dependencies>

                    <activation>
                        ......
                    </activation>
                </profile>
            </profiles>
        </project>

