* [1.概述](#1)
* [2.XML配置](#2)
* [3.XML映射文件](#3)
* [4.动态SQL](#4)

<h2 id="1">1.概述</h2>
### SqlSessionFactoryBuilder
&emsp;&emsp; 这个类可以被实例化、使用和丢弃，一旦创建了SqlSessionFactory就不需要它了。SqlSessionFactoryBuilder实例的最佳作用域是
方法作用域(局部方法变量)，可以用来创建多个SqlSessionFactory。最好不要一致保留它。

### SqlSessionFactory
&emsp;&emsp; 每个基于MyBatis的应用都是以一个SqlSessionFactory的实例为核心的。SqlSessionFactory的实例可以通过SqlSessionFactoryBuilder获得。
SqlSessionFactoryBuilder可以从XML配置文件或者一个预先配置的Configuration示例来构建出SqlSessionFactory实例。

<br>
&emsp;&emsp; 一旦创建就应该在应用的运行期一直存在，最佳实践是在应用运行期不要重复创建多次，SqlSessionFactory最佳作用域是应用作用域。
可以使用单例模式或者静态单例模式。
    
    String resource = "org/mybatis/example/mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    
    // xml 配置文件示例
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
      PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
      "http://mybatis.org/dtd/mybatis-3-config.dtd">
    <configuration>
      <environments default="development">
        <environment id="development">
          <transactionManager type="JDBC"/>
          <dataSource type="POOLED">
            <property name="driver" value="${driver}"/>
            <property name="url" value="${url}"/>
            <property name="username" value="${username}"/>
            <property name="password" value="${password}"/>
          </dataSource>
        </environment>
      </environments>
      <mappers>
        <mapper resource="org/mybatis/example/BlogMapper.xml"/>
      </mappers>
    </configuration>

&emsp;&emsp; SqlSessionFactory中可以获取SqlSession实例，SqlSession提供了在数据库执行SQL命令所需的所有方法。
可以通过SqlSession实例来直接执行已映射的SQL语句。

    // 基于XML映射语句的示例
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTO mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="org.mybatis.example.BlogMapper">
        <select id="selectBlog" resultType="Blog">
            SELECT * 
            FROM blog
            WHERE id = #{id}
        </select>
    </mapper>
    
    SqlSession session = sqlSessionFactory.openSession();
    Blog blog = (Blog) session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
    // 也可以使用下面方式调用
    BlogMapper mapper = session.getMapper(BlogMapper.class);
    Blog blog = mapper.selectBlog(101);
    
&emsp;&emsp; 全限定名("com.packagename.MyMapper.selectThings")将被直接用于查找和使用。短名称如果全局唯一也可以作为一个单独的引用，
如果不唯一，有两个或两个以上的相同名称，那么使用时就会产生"短名称不唯一"的错误，这种情况下需要使用全限定名。还可以使用注解(例如@Select)的
方式完成语句映射，但是对于稍微复杂的语句，Java注解会力不从心，而且会让本就复杂的SQL语句混乱不堪，因此如果需要做一些复杂的操作，最好使用XML来映射语句。

### SqlSession
&emsp;&emsp; 每个线程都应该有自己的SqlSession实例，SqlSession的实例不是线程安全的，因此不能被共享，所以最佳的作用域是请求或方法作用域。

### 映射器实例
&emsp;&emsp; 映射器是一些绑定映射语句的接口。映射器接口的实例是从SqlSession中获得的。

    try(SqlSession session = sqlSessionFactory.openSession()) {
        BlogMapper mapper = session.getMapper(BlogMapper.class);
    }
    
<h2 id="2">2.XML配置</h2>
### 属性
&emsp;&emsp; 如果一个属性在不同地方进行了配置，那么MyBatis将按照下列顺序加载：1，首先读取properties元素内指定的属性。2，然后根据properties
元素中的resource属性读取类路径下属性文件，或按照url属性指定的路径读取属性文件，并覆盖之前读取过的同名属性。3，最后读取作为方法参数传递的属性，
并覆盖之前读取过的同名属性。

    // 从MyBatis3.4.2开始可以为占位符指定一个默认值。
    <dataSource type="POOLED">
        <!-- ... -->
        <property name="username" value="${username:ut_user}"> <!-- username属性默认值为ut_user -->
    </dataSource>
    
    // 这个特性默认是关闭的，如果要开启，需要添加特定属性来开启。
    <properties resource="org/.../config.properties">
        <property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true"> <!-- 启用默认值特性 -->
    </properties>
    
### 设置
&emsp;&emsp; 这是MyBatis中调整设置，会改变MyBatis的运行时行为。

    cacheEnabled            全局性的开启或关闭所有映射器配置文件中已配置的任何缓存。 默认true
    lazyLoadingEnabled      延迟加载的全局开关。当开启时，所有关联对象都延迟加载。可通过fetchType覆盖该项开关。  默认false
    aggressiveLazyLoading   开启时，任一方法的调用都会加载该对象的所有延迟加载属性。否则每个延迟加载属性会按需加载。    默认false
    multipleResultSetsEnabled   是否允许单个语句返回多结果集。需要数据库驱动支持。 默认true
    useColumnLabel          使用列标签代替列名。依赖数据库驱动。  默认true
    useGeneratedKeys        允许JDBC支持自动生成主键，需要数据库驱动支持。设置为true强制使用自动生成主键。 默认false
    autoMappingBehavior     指定MyBatis如何自动映射列到字段或属性。NONE表示关闭自动映射。PARTIAL自会自动映射没有定义嵌套结果映射的字段。
                            FULL会自动映射任何复杂的结果集。  默认PARTIAL
    autoMappingUnknownColumnBehavior    指定发现自动映射目标未知列的行为。NONE：不做任何反应。WARNING：输出警告日志。
                            FAILING：映射失败，抛出SqlSessionException。 默认NONE
    defaultExecutorType     配置默认的执行器。SIMPLE：普通的执行器。REUSE：执行器会重用预处理语句(PreparedStatement)。
                            BATCH：执行器不仅重用语句还会执行批量更新。    默认SIMPLE
    defaultFetchSize        为驱动的结果集获取数量设置一个建议值。此参数只可以在查询设置中被覆盖。 默认NULL
    defaultResultSetType    指定语句默认的滚动策略。    FORWARD_ONLY、SCROLL_SENSITIVE、SCROLL_INSENSITIVE、
                            DEFAULT(等同于未设置)。    默认未设置(NULL)
    safeResultHandlerEnabled 是否允许在嵌套语句中使用结果处理器（ResultHandler）。如果允许使用则设置为 false。 默认为true
    mapUnderscoreToCamelCase 是否开启驼峰命名自动映射，即从经典数据库列名 A_COLUMN 映射到经典 Java 属性名 aColumn。 默认为false
    localCacheScope         MyBatis 利用本地缓存机制（Local Cache）防止循环引用和加速重复的嵌套查询。默认值为 SESSION，
                            会缓存一个会话中执行的所有查询。 若设置值为 STATEMENT，本地缓存将仅用于执行语句，
                            对相同 SqlSession 的不同查询将不会进行缓存。 默认值为SESSION
    jdbcTypeForNull         当没有为参数指定特定的 JDBC 类型时，空值的默认 JDBC 类型。 某些数据库驱动需要指定列的 JDBC 类型，
                            多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER。  默认值为OTHER
    lazyLoadTriggerMethods  指定对象的哪些方法触发一次延迟加载。	用逗号分隔的方法列表。	默认值为equals,clone,hashCode,toString
    defaultScriptingLanguage 指定动态 SQL 生成使用的默认脚本语言。	默认值为org.apache.ibatis.scripting.xmltags.XMLLanguageDriver
    defaultEnumTypeHandler  指定 Enum 使用的默认 TypeHandler。 默认值为org.apache.ibatis.type.EnumTypeHandler
    callSettersOnNulls      指定当结果集中值为 null 的时候是否调用映射对象的 setter（map 对象时为 put）方法，这在依赖于 Map.keySet() 
                            或 null 值进行初始化时比较有用。注意基本类型（int、boolean 等）是不能设置成 null 的。  默认值为false
    returnInstanceForEmptyRow 当返回行的所有列都是空时，MyBatis默认返回 null。 当开启这个设置时，MyBatis会返回一个空实例。 
                            请注意，它也适用于嵌套的结果集（如集合或关联）。默认为false
    logPrefix               指定 MyBatis 增加到日志名称的前缀。	未设置默认值。
    logImpl                 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。SLF4J、LOG4J、LOG4J2、JDK_LOGGING、COMMONS_LOGGING、
                            STDOUT_LOGGING、NO_LOGGING。  未设置默认值
    proxyFactory            指定 Mybatis 创建可延迟加载对象所用到的代理工具。	CGLIB、JAVASSIST。    默认值为JAVASSIST
    vfsImpl                 指定 VFS 的实现。 未设置默认值
    useActualParamName      允许使用方法签名中的名称作为语句参数名称。 为了使用该特性，你的项目必须采用 Java 8 编译，
                            并且加上 -parameters 选项。 默认值true
    configurationFactory    指定一个提供 Configuration 实例的类。 这个被返回的 Configuration 实例用来加载被反序列化对象的延迟加载属性值。 
                            这个类必须包含一个签名为static Configuration getConfiguration() 的方法。 未设置默认值。
    shrinkWhitespacesInSql  从SQL中删除多余的空格字符。请注意，这也会影响SQL中的文字字符串。 默认值为false
    defaultSqlProviderType  指定一个包含提供方法的sql提供类，当参数缺省时，这个类可以被用作@SelectProvider注解的type或value参数值。
    
    // 用法
    <settings>
        <setting name="xxx" value="xxx"/>
        ...
    </settings>
    
### 类型处理器
&emsp;&emsp; MyBatis本身有很多类型处理器，也可以自定义类型处理器。通过类型处理器的泛型，MyBatis可以得知该类型处理器处理的Java类型，
不过这种行为可以通过两种方法改变：1，在类型处理器的配置元素上增加一个javaType属性。2，在类型处理器的类上增加@MappedTypes注解指定与其
关联的Java类型列表。可以通过两种方法指定关联的JDBC类型：1，在类型处理器配置元素上增加jdbcType属性。2，在类型处理器类上增加一个@MappedJdbcTypes注解。

    // 用来处理BigDecimal Java与JDBC值的互相映射
    @MappedJdbcTypes(JdbcType.TIMESTAMP)
    public class BigDecimalTypeHandler extends BaseTypeHandler<BigDecimal> {
    
      @Override
      public void setNonNullParameter(PreparedStatement ps, int i, BigDecimal parameter, JdbcType jdbcType)
          throws SQLException {
        ps.setBigDecimal(i, parameter);
      }
    
      @Override
      public BigDecimal getNullableResult(ResultSet rs, String columnName)
          throws SQLException {
        return rs.getBigDecimal(columnName);
      }
    
      @Override
      public BigDecimal getNullableResult(ResultSet rs, int columnIndex)
          throws SQLException {
        return rs.getBigDecimal(columnIndex);
      }
    
      @Override
      public BigDecimal getNullableResult(CallableStatement cs, int columnIndex)
          throws SQLException {
        return cs.getBigDecimal(columnIndex);
      }
    }
    
### 对象工厂
&emsp;&emsp; 每次MyBatis创建结果对象的新实例时，都会使用一个对象工厂(ObjectFactory)实例来完成实例化工作。如果想覆盖对象工厂的默认行为，
可以通过创建自己的对象工厂来实现。

### 插件
&emsp;&emsp; MyBatis允许在映射语句执行过程中的某一点进行拦截使用。只需要实现Interceptor接口，并指定想要拦截的方法签名即可。

    // 默认情况下，MyBatis允许使用插件来拦截的方法调用包括：
    Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
    ParameterHandler (getParameterObject, setParameters)
    ResultSetHandler (handleResultSets, handleOutputParameters)
    StatementHandler (prepare, parameterize, batch, update, query)
    
    // 插件使用方式
    @Intercepts({
        @Signature(type= Executor.class, method = "update", args = {MappedStatement.class,Object.class})
    })
    public class ExamplePlugin implements Interceptor {
      private Properties properties = new Properties();
      public Object intercept(Invocation invocation) throws Throwable {
        // implement pre processing if need
        Object returnObject = invocation.proceed();
        // implement post processing if need
        return returnObject;
      }
      public void setProperties(Properties properties) {
        this.properties = properties;
      }
    }
    
### 环境配置
&emsp;&emsp; MyBatis可以配置成适应多种环境。但是每个SqlSessionFactory实例只能选择一种环境。每个数据库对应于一个SqlSessionFactory。
每个SqlSessionFactory对应一个Environment。每个环境对应一个TransactionFactory和一个DataSource。

<br>
&emsp;&emsp; 事务管理器(transactionManager)，在MyBatis中有两种类型的事务管理器(JDBC || MANAGED)，JDBC-这个配置直接使用了JDBC的提交和回滚设施，
它依赖从数据源获得的连接来管理事务作用域。MANAGED-这个配置几乎没做什么，从不提交或回滚一个连接，而是让容器来管理事务的整个生命周期，
默认情况下会关闭连接，然而一些容器并不希望被关闭，因此要将closeConnection设置为false。可以使用TransactionFactory和Transaction接口，
自定义MyBatis对事务的处理。

<br>
&emsp;&emsp; 数据源(dataSource)，dataSource元素使用标准的JDBC数据源接口来配置JDBC连接对象的资源。有三种内建的数据源类型: UNPOOLED、
POOLED、JNDI。UNPOOLED：每次请求时都打开和关闭连接。POOLED: 使用线程池。JNDI：为了能在如EJB或应用服务器这类容器中使用。

    // UNPOOLED配置属性
    driver – 这是 JDBC 驱动的 Java 类全限定名（并不是 JDBC 驱动中可能包含的数据源类）。
    url – 这是数据库的 JDBC URL 地址。
    username – 登录数据库的用户名。
    password – 登录数据库的密码。
    defaultTransactionIsolationLevel – 默认的连接事务隔离级别。
    defaultNetworkTimeout – 等待数据库操作完成的默认网络超时时间（单位：毫秒）。
    
    // POOLED配置属性。包含UNPOOLED配置属性。
    poolMaximumActiveConnections – 在任意时间可存在的活动（正在使用）连接数量，默认值：10
    poolMaximumIdleConnections – 任意时间可能存在的空闲连接数。
    poolMaximumCheckoutTime – 在被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒（即 20 秒）
    poolTimeToWait – 这是一个底层设置，如果获取连接花费了相当长的时间，连接池会打印状态日志并重新尝试获取一个连接。默认20秒。
    poolMaximumLocalBadConnectionTolerance – 这是一个关于坏连接容忍度的底层设置， 作用于每一个尝试从缓存池获取连接的线程。
        如果这个线程获取到的是一个坏的连接，那么这个数据源允许这个线程尝试重新获取一个新的连接，但是这个重新尝试的次数
        不应该超过 poolMaximumIdleConnections 与 poolMaximumLocalBadConnectionTolerance 之和。 默认值：3
    poolPingQuery – 发送到数据库的侦测查询，用来检验连接是否正常工作并准备接受请求。默认是“NO PING QUERY SET”，
        这会导致多数数据库驱动出错时返回恰当的错误消息。
    poolPingEnabled – 是否启用侦测查询。若开启，需要设置 poolPingQuery 属性为一个可执行的 SQL 语句，默认值：false。
    poolPingConnectionsNotUsedFor – 配置 poolPingQuery 的频率。可以被设置为和数据库连接超时时间一样，来避免不必要的侦测，默认值：0
    
### 数据库厂商标识(databaseIdProvider)
&emsp;&emsp; MyBatis可以根据不同的数据库厂商执行不同的语句，这种多厂商的支持是基于映射语句中的databaseId属性。
可以通过实现接口DatabaseIdProvider并注册的方式来构建自己的DatabaseIdProvider。

<h2 id="3">3.XML映射文件</h2>
&emsp;&emsp; MyBatis真正的强大在于它的语句映射。SQL映射文件只有下面很少的几个顶级元素。

    cache – 该命名空间的缓存配置。
    cache-ref – 引用其它命名空间的缓存配置。
    resultMap – 描述如何从数据库结果集中加载对象，是最复杂也是最强大的元素。
    sql – 可被其它语句引用的可重用语句块。
    insert – 映射插入语句。
    update – 映射更新语句。
    delete – 映射删除语句。
    select – 映射查询语句。
    
### select
&emsp;&emsp; select元素的属性如下。

     id	            在命名空间中唯一的标识符，可以被用来引用这条语句。
     parameterType	参数的类全限定名或别名。这个属性是可选的，因为 MyBatis 可以通过类型处理器（TypeHandler）推断出具体传入语句的参数。
     resultType	    返回结果的类全限定名或别名。如果返回的是集合，那应该设置为集合元素的类型。 resultType 和 resultMap 不能同时使用。
     resultMap	    对外部 resultMap 的命名引用。结果映射是 MyBatis 最强大的特性，如果你对其理解透彻，许多复杂的映射问题都能迎刃而解。
     flushCache	    将其设置为 true 后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：false。
     useCache	    将其设置为 true 后，将会导致本条语句的结果被二级缓存缓存起来，默认值：对 select 元素为 true。
     timeout	    这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置（unset）（依赖数据库驱动）。
     fetchSize	    这是一个给驱动的建议值，尝试让驱动程序每次批量返回的结果行数等于这个设置值。 默认值为未设置（unset）（依赖驱动）。
     statementType	STATEMENT，PREPARED(默认)、CALLABLE。MyBatis会分别使用Statement，PreparedStatement 或 CallableStatement。
     resultSetType	FORWARD_ONLY，SCROLL_SENSITIVE, SCROLL_INSENSITIVE 或 DEFAULT(== unset)，默认值为 unset。
     databaseId	    如果配置了databaseIdProvider，MyBatis 会加载所有不带 databaseId 或匹配当前 databaseId 的语句；
                    如果带和不带的语句都有，则不带的会被忽略。
     resultOrdered	这个设置仅针对嵌套结果 select 语句：如果为 true，将会假设包含了嵌套结果集或是分组，当返回一个主结果行时，
                    就不会产生对前面结果集的引用。 这就使得在获取嵌套结果集的时候不至于内存不够用。默认值：false。
     resultSets	    这个设置仅适用于多结果集的情况。它将列出语句执行后返回的结果集并赋予每个结果集一个名称，多个名称之间以逗号分隔。
     
### insert、update、delete
&emsp;&emsp; insert、update、delete的实现很接近。三者元素的属性如下。

    id、parameterType、flushCache、timeout、statementType、databaseId的属性含义同select。
    useGeneratedKeys    （仅用于insert 和 update）这会令 MyBatis 使用 JDBC 的 getGeneratedKeys 方法来取出由数据库内部生成的主键。
    keyProperty	        （仅用于 insert 和 update）指定能够唯一识别对象的属性，MyBatis会使用getGeneratedKeys的返回值或insert语句
                         的 selectKey 子元素设置它的值，默认值：未设置（unset）。如果生成列不止一个，可以用逗号分隔多个属性名称。
    keyColumn	        （仅用于 insert 和 update）设置生成键值在表中的列名，在某些数据库（像 PostgreSQL）中，
                         当主键列不是表中的第一列的时候，是必须设置的。如果生成列不止一个，可以用逗号分隔多个属性名称。
                         
     // 可以使用集合插入
     <insert id="insertAuthor" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO Author
            (username, password, email, bio)
        VALUES
            <foreach item="item" collection="list" seperator=",">
                (#{item.username}, #{item.password}, #{item.email}, #{item.bio})
            </foreach>
     </insert>
     
    // selectKey元素的属性
    keyProperty	    selectKey 语句结果应该被设置到的目标属性。如果生成列不止一个，可以用逗号分隔多个属性名称。
    keyColumn	    返回结果集中生成列属性的列名。如果生成列不止一个，可以用逗号分隔多个属性名称。
    resultType	    结果的类型。MyBatis 允许将任何简单类型用作主键的类型，包括字符串。如果生成列不止一个，
                    则可以使用包含期望属性的 Object 或 Map。
    order	        可以设置为 BEFORE 或 AFTER。如果设置为 BEFORE，那么它首先会生成主键，设置 keyProperty 再执行插入语句。
                    如果设置为 AFTER，那么先执行插入语句，然后是 selectKey 中的语句 - 在插入语句内部可能有嵌入索引调用。
    statementType	同上。
    
<h2 id="4">4.动态SQL</h2>
&emsp;&emsp; 动态SQL是MyBatis的强大特性之一。MyBatis 3 替换了之前的大部分元素，现在要学习的元素种类比原来的一半还少。

    if
    choose(when、otherwise)
    trim(where、set)
    foreach
    
### if
&emsp;&emsp; 使用动态SQL最常见的情景是根据条件中包含where子句的一部分。
    
    <select id="findWithTitle" resultType="Blog">
        SELECT * FROM blog
        WHERE state = 'ACTIVE'
        <if test="title != null">
            AND title LIKE #{title}
        </if>        
    </select>
    
### choose、when、otherwise
&emsp;&emsp; 有时候我们不想使用所有的条件，而是从众多条件中选择一个使用，针对这种情况MyBatis提供了choose元素。choose有点像Java中的switch。

    <select id="findActiveBlogLike" resultType="Blog">
        SELECT * FROM blog
        WHERE state = 'ACTIVE'
        <choose>
            <when test="title != null">
                AND title LIKE #{title}
            </when>
            <when test="author != null and author.name != null">
                AND author_name LIKE #{author.name}
            </when>
            <otherwise>
                AND featured = 1
            </otherwise>
        </choose>
    </select>
    
### trim、where、set
&emsp;&emsp; where元素只会在子元素返回任何内容的情况下，才插入"WHERE"子句。而且若子句的开头为"AND"或"OR"，where元素也会将它们去除。
如果where元素与期望不同，可以通过自定义trim元素来定制where元素的功能。

    <select id="findActiveBlogLike" resultType="Blog">
        SELECT * FROM blog
        <where>
            <if test="state != null">
                state = #{state}
            </if>
            <if tset="title != null">
                AND title LIKE #{title}
            </if>
            <if test="author != null and author.name != null">
                AND author_name LIKE #{author.name}
            </if>
        </where>
    </select>

    // 下面trim元素与where元素等价。
    <trim prefix="WHERE" prefixOverrides="AND |OR">
        ...
    </trim>
    
    // 用于动态更新语句的类似解决方案叫set，set元素可以用于动态包含需要更新的列，忽略其他不更新的列。
    // set元素会动态的在行首插入SET关键字，并删除额外的逗号。
    <update id="updateAuthorIfNecessary">
        UPDATE author
            <set>
                <if test="username != null">
                    username=#{username},
                </if>
                <if test="password != null">
                    password=#{password},
                </if>
            </set>
        WHERE
            id = #{id}
    </update>
    
    // 下面trim元素与set元素等价。
    <trim prefix="SET" suffixOverrides=",">
        ...
    </trim>
    
### foreach
&emsp;&emsp; 动态SQL另一个常见使用场景是对集合进行遍历。

    <select id="selectPostIn" resultType="domain.blog.Post">
      SELECT *
      FROM POST P
      WHERE ID in
      <foreach item="item" index="index" collection="list"
          open="(" separator="," close=")">
            #{item}
      </foreach>
    </select>
    
### script
&emsp;&emsp; 要在带注解的映射器接口中使用动态SQL，可以使用script元素。

    @Update({"<script>",
          "update Author",
          "  <set>",
          "    <if test='username != null'>username=#{username},</if>",
          "    <if test='password != null'>password=#{password},</if>",
          "    <if test='email != null'>email=#{email},</if>",
          "    <if test='bio != null'>bio=#{bio}</if>",
          "  </set>",
          "where id=#{id}",
          "</script>"})
          
### bind
&emsp;&emsp; bind元素允许在OGNL表达式之外创建一个变量，并将其绑定到当前的上下文。

    <select id="selectBlogsLike" resultType="Blog">
      <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
      SELECT * FROM BLOG
      WHERE title LIKE #{pattern}
    </select>
    
### 多数据库支持与脚本语言
&emsp;&emsp; 如果配置了databaseIdProvider，那么可以在动态代码中使用名为"_databaseId"的变量为不同的数据库构建特定的语句。
MyBatis支持插入脚本语言，可以使用lang属性为特定的语句指定语言，也可以在mapper接口上使用@Lang注解。