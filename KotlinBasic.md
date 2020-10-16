* [1.惯用法](#1)
* [2.基础](#2)
* [3.类与对象](#3)
* [4.函数与Lambda表达式](#4)
* [5.集合](#5)
* [6.协程](#6)
* [7.多平台程序设计](#7)
* [8.语言结构](#8)

<h2 id="1">1.惯用法</h2>
&emsp;&emsp; 创建DTO类(POJO)，以data开头，会自动包含以下功能equals()、hashCode()、toString()、copy()。

    <p>
        data class Customer(val name: String)
    </p>
    
<br>
&emsp;&emsp; Lazy延迟计算属性，当第一次使用变量p时，会调用lambda块中的方法进行赋值。
    
    <p>
        val p: String by lazy {
            // do compute
        }
    </p>
    
<br>
&emsp;&emsp; 拓展函数。

    <p>
        fun String.spaceToCamelCase() { ... }
    </p>

<br>
&emsp;&emsp;  创建单例。
    
    <p>
        object Resource {
            val name = "Name"
        }
    </p>
    
<br>
&emsp;&emsp; 可以在一个对象实例上调用多个方法，使用with语句实现。

    <p>
        class Turtle {
            fun penDown()
            fun penUp()
            fun turn(degrees: Double)
            fun forward(pixels: Double)
        }
        
        val myTurtle = Turtle()
        with(myTurtle) {
            penDown()
            for (i in 1..4) {
                forward(100.0)
                turn(90.0)
            }
            penup()
        }
    </p>
    
<br>
&emsp;&emsp; 针对资源的try语句
    
    <p>
        val stream = File.newInputStream(Path.get("/xxx/xxx.txt"))
        stream.buffered().reader().use { println(it.readText) }
    </p>
    
<br>
&emsp;&emsp; 针对需要泛型类型信息的函数，可以简化使用方式，reified用来限定类型参数。

    <p>
        public final class Gson {
            public <T> T fromJson(JsonElement json, Class<T> clazz) { ... }
        }
        
        // 简化写法
        inline fun <reified T: Any> Gson.fromJson(json: JsonElement): T = this.fromJson(json, A::class.java)
    </p>
    
 <br>
 &emsp;&emsp; 当Boolean类型的值为null时， a == true 返回false
 
 <br>
 &emsp;&emsp; 交换两个变量的值： 
 
    <p>
        var a = 1
        var b = 2
        a = b.also { b = a }
    </p> 
    

<h2 id="2">2.基础</h2>
### 基本类型
&emsp;&emsp; Double 64位，Float 32位，Long 64位，Int 32位，Short 16位，Byte 8位。kotlin 浮点型的数据，无符号标识默认为Double，如：123.4。
kotlin 1.1开始支持数字字面值中使用下划线，如：1_000_000。当使用可为null或者泛型的数值时，这种情况下数值会被装箱为数值对象。

    <p>
        fun main() {
            val a: Int = 10000
            println(a === a) // true
            val boxedA: Int? = a
            val anotherBoxedA: Int? = a
            // 此时引用不同
            println(boxedA === anotherBoxedA) // false
            // 对象内容相等
            println(boxedA == anotherBoxedA) // true
        }
    </p>
    
<br>
&emsp;&emsp; kotlin不支持隐式的将较小的数据转换为较大的数据，也就是说必须显式的进行转换。

    <p>
        fun main() {
            val b: Byte = 1
            val i: Int = b.toInt()
        }
    </p>
    
<br>
&emsp;&emsp; 位运算：kotlin没有使用特别的字符来表示位运算，而是有名称的普通函数。
    
    <p>
        // 只适用与Int和Long
        shl(bits) - 带符号左移，相当于<<
        shr(bits) - 带符号右移，相当于>>
        ushr(bits)- 无符号右移，相当于 >>>
        and(bits) - 按位与(and)
        or(bits) - 按位或(or)
        xor(bits) - 按位异或(xor)
        inv() - 按位取反
    </p>

<br>
&emsp;&emsp; 字符适用Char表示，不能直接当作数值使用，需要显式的转换，如：'0'.toInt()。

<br>
&emsp;&emsp; 数组，可以使用arrayOf(1, 2, 3) 创建一个数组，还可以使用Array构造函数，第一个参数为数组大小，第二个参数为另一个函数，
这个函数接受数组下标作为自己的输入参数，然后返回这个下标对应的数组的初始值。

    <p>
        kotlin数组的类型是不可变的，所以不允许将Array<String>赋值给Array<Any>, 但是可以使用Array<out Any> (类型投射)。
        fun main() {
            val asc = Array(5, { i -> (i * i).toString() })
            asc.forEach{ println(it) } // 打印出的结果为 ["0", "1", "4", "9", "16"]
        }
    </p> 

<br>
&emsp;&emsp; kotlin中有专门的类来表达基本数据类型的数组，ByteArray、ShortArray、IntArray等，这些数组可以避免数值对象装箱带来的性能损耗。

### 控制流
&emsp;&emsp; kotlin使用when作为switch操作符。
    
    <p>
        when (x) {
            1, 2 -> println("aaa")
            else -> print("otherwise")
        }
        
        when (x) {
            parseInt(s) -> print("s encodes x")
            else -> print("s does not encodes x")
        }
        
        when (x) {
            in 0..10 -> print("x is it range")
            in validNumbers -> print("x is valid")
            !in 10..20 -> print("x is outside range") 
            else -> print("none of the above")
        }
    </p>
    
<br>
&emsp;&emsp; for循环。
    
    <p>
        fun main() {
            for (i in 1..3) {
                println(i)
            }
            
            for (i in 6 downTo 0 step 2) {
                println(i)
            }
            
            // 使用下标变量来遍历数组或者List
            val array = arrayOf("a", "b", "c")
            for (i in array.indices) {
                println(array[i])
            }
            
            // 可以使用withIndex库函数
            for((index, value) in array.withIndex()) {
                println("element at $index is $value")
            }
        }
    </p>
    
### 返回与跳转
&emsp;&emsp; return 从最内层的函数或匿名函数中返回，break 结束最内层的循环，continue 在最内层的循环中，跳转到下一次循环。
当return语句指定了返回值时，源代码解析器会将这样的语句优先识别为使用标签限定的return语句，如return@a 1 表示返回到标签@a处，返回值为1。

    <p>
        val s = person.name ?: return
        
        // 通过标签进行break跳转
        loop@ for (i in 1..100) {
            for (j in 1..100) {
                if (...) break@loop
            }
        }
        
        // 通过标签进行return返回
        fun foo() {
            listof(1, 2, 3, 4).forEach lit@{
                if (it == 3) return@lit
                print(it)
            }
            printl("done with explicit label")
        }
        
        // 通过匿名函数进行return返回
        fun foo() {
            listof(1, 2, 3, 4).forEach(fun (value: Int) {
                if (value == 3) return
                print(value)
            })
            print("done with anonymous function")
        }
        
        fun foo() {
            run loop@{
                listof(1, 2, 3, 4).forEach {
                    if (it == 3) return@loop
                    println(it)
                }
            }
            print("done with nested loop")
        }
    </p>
    
<h2 id="3">3.类与对象</h2>
### 类与继承
&emsp;&emsp; kotlin的类可以有一个主构造器，以及一个或多个次构造器，主构造器位于类名称之后。如果主构造器没有任何注解也没有任何可见度修饰符，
那么constructor关键字可省略。

    <p>
        class Person constructor(name: String) { ... }
        
        class Person (name: String) { ... }
        
        // 主构造器不能包含任何代码，初始化代码可以放在初始化代码段，初始化代码以init关键字作为前缀。
        class InitOrderDemo(name: String) {
            val firstProperty = "First property: $name".also(::println)
            init {
                println(...)
            }
            val sendProperty = ...
            init {
                ...
            }
        }
        
        // 主构造器中定义的属性可以是val，也可以是var
        class Person(val name: String, var age: Int) { ... }
        
        // 如果主构造器有注解或者可见度修饰符，那么constructor关键字不能省略。
        class Customer public @Inject constructor(name: String) { ... }
    </p>
    
<br>
&emsp;&emsp; 类的次级构造器，以constructor关键字修饰。init初始化代码实际上会成为主构造器的一部分，在次级构造器执行前，初始化代码将被先执行。

    <p>
        class Person {
            constructor(parent: Person) {
                parent.children.add(this)
            }
        }
        
        // 如果类有主构造器，那么每个次级构造器都要委托给主构造器
        
        class Person(name: String) {
            constructor(name: String, parent: Person) : this(name) {
                paren.children.add(this)
            }
        }
    </p>

<br>
&emsp;&emsp; kotlin 如果子类有主构造器，那么必须在主构造器中使用主构造器的参数来初始化基类。kotlin 要求使用明确的修饰符(open)标识允许被子类覆盖的成员。
final 可以禁止覆盖。可以使用一个var属性覆盖一个val属性，但是不能用val覆盖一个var属性，因为val相当于只定义了一个get方法，用var覆盖，只是向后代类中添加了一个set方法。

    <p>
        open class Base(p: Int) { 
            open val x: Int get() { ... }
            open fun v() { ... }
            fun nv() { ... }
        }
        
        class Derived(p: Int) : Base(p) {
            override fun v() { ... } 
        }
        
        // 可以在主构造器的属性声明中使用override关键字。
        interface Foo {
            val count: Int
        }
        
        class Bar1(override val count: Int) : Foo { ... }
        class Bar2 : Foo {
            override var count: Int = 0
        }
    </p>
    
<br>
&emsp;&emsp; 在内部类中，可以使用super关键字加上外部类名称限定符: super@Outer 来访问外部类的超类。

    <p>
        class Bar : Foo() {
            override fun f() { ... }
            override val x: Int get() = 0
            
            inner class Baz {
                fun g() {
                    super@Bar.f()
                    println(super@Bar.x)
                }
            }
        }
    </p>
    
<br>
&emsp;&emsp; 如果一个类从直接超类中继承了一个成员的多个实现，那么子类必须覆盖这个成员，并提供一个自己的实现，

    <p>
        open class A {
            open fun f() { ... }
            fun a() { ... }
        }
        
        interface B {
            // 接口的成员默认是open的
            fun f() { ... }
            fun b() { ... }
        }
        
        class C : A(), B {
            override fun f() {
                super<A>.f()
                super<B>.f()
            }
        }
    </p>
    
<br>
&emsp;&emsp; 抽象类，默认是open的。抽象类以abstract修饰。

    <p>
        open class Base {
            open fun f() { ... }
        }
        
        abstract class Derived : Base() {
            override abstract fun f()
        }
    </p>
    
<br>
&emsp;&emsp; 伴生对象(Companion Object)，如果希望使用者不通过类的实例调用，但又需要访问类的内部信息，可以将一个函数写为这个类的一个对象声明的成员。
如果在类中声明一个伴生对象，那么只需要像静态方法一样对伴生对象成员进行调用。

### 接口
&emsp;&emsp; kotlin接口可以包含抽象方法的声明，也可以包含方法的实现。接口与抽象类的区别在于，接口不能存储状态数据，接口可以有属性，
但属性必须是抽象的，或者必须提供访问器的自定义实现。

    <p>
        interface MyInterface {
            val prop: Int // 抽象属性
            val propertyWithImplementation: String
                get() = "foo"
                
            fun foo() {
                print(prop)
            }
        }
        
        class Child : MyInterface {
            override val prop: Int = 20
        }
    </p>
    
### 可见度修饰符
&emsp;&emsp; kotlin存在4种可见度修饰符：private、protected、internal、public。默认的可见度为public。如果标记为internal，
那么将允许在同一个模块module内访问。在kotlin中，外部类不能访问其内部类的private成员。

### 扩展
&emsp;&emsp; kotlin支持扩展函数和扩展属性，这个感觉跟ruby中打开类的用法相同，都是支持自定义拓展。

    <p>
        fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
            val temp = this[index1]
            this[index1] = this[index2]
            this[index2] = temp
        }
        
        // 如果在类中存在成员函数，同时又在类上定义了同名的扩展函数，那么总是会优先使用成员函数。
        class C {
            fun foo() {
                println("member")
            }
        }
        
        fun C.foo() {
            println("extension")
        }
        
        如果调用c.foo()，此时会打印member而不是extension。
    </p>

<br>
&emsp;&emsp; 当派发接受者与扩展接受者的成员名称发生冲突时，扩展接受者的成员会优先被使用，如果想要使用派发接受者的成员，需要使用带限定符的this语法。

    <p>
        class C {
            toString()        // 这里会调用D.toString()
            this@C.toString() // 这里会调用C.toString()
        }
    </p>
    
<br>
&emsp;&emsp; 以成员的形式定义的扩展函数，可以声明为open，而且可以在子类中覆盖，也就是说在这类拓展函数的派发过程中，针对派发接受者是虚拟的，
但针对扩展接受者是静态的。扩展方法的定义所在的类的实例，称为派发接受者，扩展方法的目标类型的实例称为扩展接受者。

    <p>
        open class D {}
        
        class D1 : D() {}
        
        open class C {
            open fun D.foo() {
                println("D.foo in C")
            }
            
            open fun D1.foo() {
                println("D1.foo in C")
            }
            
            fun caller(d: D) {
                d.foo()
            }
        }
        
        class C1 : C() {
            override fun D.foo() {
                println("D.foo in C1")
            }
            
            override fun D1.foo() {
                println("D1.foo in C1")
            }
        }
        
        fun main() {
            C().caller(D())   // 打印 D.foo in C
            C1().caller(D())   // 打印 D.foo in C1  派发接受者解析过程是虚拟的 virtual
            C().caller(D1())   // D.foo in C        扩展接受者的解析过程是静态的 static
        }
    </p>
    
### 数据类
&emsp;&emsp; 通常会创建一些类用来保存数据，这种类称为数据类，用data来标记。data类需要满足：1.主构造器至少要一个参数。2.主构造器所有参数必须标记为val或var。
3.数据类不能是抽象类，open类，封闭类(sealed)，内部类。在JVM上，如果自动生成的类需要拥有一个无参的构造函数，那么需要为所有的属性指定默认值。

<br>
&emsp;&emsp; 编译器对自动生成的函数，只使用主结构中定义的属性，如果想排除某个属性，那么可以将它声明在类的主体部。

    <p>
        data class Person(val name: String) {
            var age: Int = 0
        }
        
        fun main() {
            val person = Person("john")
            person.age = 10
            println(person.toString()) // 打印结果为Person(name=john)
        }
    </p>
 
<br>
&emsp;&emsp; 数据类中成员数据解析。kotlin标准库中提供了Pair和Triple类可使用，但大多数情况下使用有具体名称的数据类是更好的设计方式。
    
    <p>
        val jane = User("jane", 35)
        val (name, age) = jane
    </p>
    
### 封闭类
&emsp;&emsp; 封闭类需要将sealed修饰符放在类名之前，封闭类可以有子类，但所有的子类声明都必须定义在封闭类所在的同一个源代码文件内。
封闭类本身是抽象类，不允许直接创建实例，而且封闭类可以拥有abstract成员。封闭类不允许拥有非private的构造器。

### 泛型
&emsp;&emsp; kotlin中不存在java中的通配符类型，而是使用：声明处类型变异和类型投射。通常java会使用extends、super这种通配符，这些理解起来很麻烦。
kotlin的泛型类型，也只是在编译期进行类型安全性检查，在运行期，泛型类型的实例不保存关于其类型参数的任何信息，这叫做类型擦除。

    <p>
        void com.xiao.demo(Source<String> strs) {
            Source<? extends Object> objects = strs; // 在java中需要这么声明
        }
        
        
        // kotlin 通过对source的类型参数T添加注解，确保Source<T> 成员函数只返回T类型，而不会消费T类型。
        // out 修饰符为协变注解，这个注解出现在类型参数的声明处，因此称为声明处的类型变异，与java中的使用处类型变异相反。
        // in 这个注解导致参数类型反向变异，这个类型只能被消费，不能被生产。
        
        interface Source<out T> {
            abstract fun nextT(): T
        }
        
        fun com.xiao.demo(strs: Source<String>) {
            val objects: Source<Any> = strs   
        }
        
        interface Comparable<in T> {
            operator fun compareTo(other: T): Int
        }
        
        fun com.xiao.demo(x: Comparable<Number>) {
            x.compareTo(1.0)
            val y: Comparable<Double> = x
        }
    </p>
    
<br>
&emsp;&emsp; 类型投射。对于不知道类型参数的任何信息，可以使用星号投射(*)。

    <p>
        fun copy(from: Array<Any>, to: Array<Any>) {
            for (i in from.indices) {
                to[i] = from[i]
            }
        }
        
        val ints = arrayOf(1, 2, 3)
        val any = Array<Any>(3) {""}
        copy(ints, any)       // 这里会报错
        
        fun copy(from: Array<out Any>, to: Array<Any>) { ... } // copy函数声明成这样就能够运行。这种方式称为类型投射。
        
        fun fill(dest: Array<in String>, value: String) { ... }
    </p>
    
<br>
&emsp;&emsp; 泛型函数。

    <p>
        fun <T> singletonList(item: T): List<T> {
            ...
        }
        
        fun <T> T.basicToString(): String {
            ...
        }
    </p>
  
<br>
&emsp;&emsp; 泛型约束。没有指定，默认的上界是Any?，在定义类型参数的尖括号内，只允许定义唯一一个上界，如果同一个类型参数要指定多个上界，
需要使用where子句。
    
    <p>
        // 冒号之后指定的类型就是类型参数的上界，对于类型参数T，只允许使用Comparable<T> 的子类型
        fun <T : Comparable<T>> sort(list: List<T>) { ... }
        
        // T类型必须同时实现CharSequence和Comparable接口。
        fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String> {
            where T : CharSequence,
                  T : Comparable<T> {
                    return list.filter { it > threshold }.map { it.toString() }
                  }
        }
    </p>
    
### 枚举类
&emsp;&emsp; 枚举类可以实现接口，但是不能继承其他类。可以使用EnumClass.valueOf(value: String): EnumClass

    <p>
        enum class Color(val rgb: Int) {
            RED(0xFF0000),
            GREEN(0x00FF00),
            BLUE(0x0000FF)
        }
    </p>
    
### 对象
&emsp;&emsp; 有时候我们需要创建一个对象，这个对象在某个类上做了简略的修改，我们不希望为了一点点修改而明确声明一个类，java通过匿名内部类实现，
kotlin使用对象表达式和对象声明来实现。

    <p>
        // 对象表达式
        window.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) { ... }
        })
        
        // 对象表达式
        open class A(x: Int) { ... }
        val ab: = object : A(1) { ... }
        
        // 不需要继承任何基类
        val adHoc = object {
            var x: Int = 0
            var y: Int = 0
        }
    </p>
    
<br>
&emsp;&emsp; 只有在局部且私有的声明范围内，匿名对象才可被用作类型，如果将匿名对象作为公开函数的返回类型，或者用作公开属性的类型，
那么这个函数或属性的真实类型会被声明为这个匿名对象的超类，如果没有超类则是Any。

<br>
&emsp;&emsp; 对象声明可以很便利的声明一个单例。

    <p>
        class MyClass {
            // 伴生对象的名称可以省略，如果省略，使用默认名称Companion
            companion object Factory {
                fun create(): MyClass = MyClass()
            }
        }
        
        val instance = MyClass.create()
    </p>
    
### 内联类
&emsp;&emsp; 对于一些业务逻辑来说，有时会需要创建一些包装类，但是会产生堆上的内存分配，带来运行时性能损失，kotlin引入了内联类，
在类前面加inline修饰符，内联类只能有一个属性，并在初始化器中使用这个属性，在运行期会使用这个唯一的属性来表达内联类的实例。

    <p>
        inline class Password(val name: String)
        
        // 在运行期，实际并不会产生一个Password对象，而是securePassword只存在一个字符串。
        val securePassword = Password("xxxxxx")
        
        // 在gradle中启动内联类
        compileKotlin {
            kotlinOptions.freeCompilerArgs += ["-XXLanguage:+InlineClasses"]
        }
    </p>
    
### 委托
&emsp;&emsp; 通过委托实现接口。使用 by实现委托。

    <p>
        interface Base {
            fun print()
        }
        
        class BaseImpl(val x: Int) : Base {
            override fun print() {
                print(x)
            }
        }
        
        class Derived(b: Bse) : Base by b
        
        fun main() {
            // 这里实际上是把 Base中的接口方法，委托给了b来实现。在Derived中，所有继承自Base接口的方法调用，都转发给了b处理。
            val b = BaseImpl(10)
            Derived(b).print()
        }
    </p>
    
### 委托属性
&emsp;&emsp; 使用by实现委托属性。

    <p>
        class C {
            var prop: String by MyDelegate()
        }
        
        // 在编译器生成的代码中，会调用provideDelegate方法，用来初始化辅助属性prop$delegate，在对prop获取设置值时，
        ／／ 会调用delegate的getValue，setValue方法。
        class C {
            private val prop$delegate = MyDelegate().provideDelegate(this, this::prop)
            var prop: String
                get() = prop$delegate.getValue(this, this::prop)
                set(value: String) = prop$delegate.setValue(this, this::prop, value)
        }
    </p>
    
<br>
&emsp;&emsp; 属性延迟加载。使用by lazy实现属性延迟加载，第一次调用时会执行lazy函数的lambda表达式，对该属性进行赋值。

    <p>
        val lazyValue: String by lazy {
            println("computed!")
            "Hello"
        }
    </p>
    
<br>
&emsp;&emsp; 可观察属性，使用Delegates.observable() 在每次向属性赋值时，在属性赋值处理完成之后，响应器会被调用。vetoable()函数在赋值处理完成前被调用。
    
    <p>
        class User {
            var name: String by Delegates.observalbe("<no name>") {
                prop, old, new ->
                println("$old -> $new")
            }
        }
    </p>
    
<br>
&emsp;&emsp; 将多个属性保存在一个map中。在解析json或者执行某些动态任务时，可以使用map实例本身作为属性的委托。

    <p>
        class User(val map: Map<String, Any?>) {
            val name: String by map
            val age: Int by map
        }
    </p>
    
<h2 id="4">4.函数与Lambda表达式</h2>
### 函数
&emsp;&emsp; 在数组前加 *，是伸展操作符。可以用作vararg参数传递。
    
    <p>
        fun <T> asList(vararg ts: T): List<T> {
            val result = ArrayList<T>()
            for (t in ts) {
                result.add(t)
            }
            return result
        }
        
        val a = arrayOf(1, 2, 3)
        val list = asList(-1, 0, *a, 4)
    </p>
    
<br>
&emsp;&emsp; infix关键字用来表示中缀表示法。中缀表示法必须只有一个参数，必须是成员函数或扩展函数，参数不能是可变参数，参数不能有默认值。

    <p>
        infix fun Int.shl(x: Int): Int { ... }
        
        1 shl 2
        
        class MyStringCollection {
            infix fun add(s: String) { ... }
            
            fun build() {
                this add "abc"
                add("abc")
            }
        }
    </p>
    
<br>
&emsp;&emsp; 尾递归函数。允许一些常用循环写的算法改用递归函数来写，而无堆栈溢出的风险。当函数用tailrec修饰符时，编译器会优化这个递归。
要使用tailrec修饰符，函数必须将自身调用作为执行的最后一个操作。不能在try／catch／finally中使用。目前在Kotlin for JVM与Kotlin/Native中支持。

    <p>
        tailrec fun findFixPoint(x: Double = 1.0): Double = if (Math.abs(x - Math.cos(x)) < eps) x else findFixPoint(Math.cos(x))
    </p>
    
 ### Lambda表达式
 &emsp;&emsp; 高阶函数是将函数作为参数或返回值的函数。
 
    <p>
        fun <T, R> Collection<T>.fold(initial: R, combine: (acc: R, nextElement: T) -> R): R {
            var accumulator: R = initial
            for(element : T in this) {
                accumulator = combine(accumulator, element)
            }
            return accumulator
        }
        
        val items = listOf(1, 2, 3, 4, 5)
        
        val joinedToString = items.fold("Elements", { acc, i -> acc + " " + i }) // 这里acc，i的类型可以推断出来，因此可以省略。
    </p>

<br>
 &emsp;&emsp; 带与不带接收者的函数类型非字面值可以互换，其中一个接收者可以替代第一个参数，例如，(A, B) -> C 类型的值可以传给或赋值给期待
 A.(B) -> C的地方，反之亦然。
 
    <p>
        val repeatFun: String.(Int) -> String = { times -> this.repeat(times) }
        val twoParameters: (String, Int) -> String = repeatFun
        
        fun runTransformation(f: (String, Int) -> String): String {
            return f("hello", 3)
        }
        
        val result = runTransformation(repeatFun)
    </p>

<br>
 &emsp;&emsp; 函数类型的值可以通过其invoke()操作符调用，f.invoke(x) 或者 f(x)
 
    <p>
        val stringPlus: (String, String) -> String = String::plus
        val intPlus: Int.(Int) -> Int = Int::plus
        
        println(stringPlus.invoke("<-", "->"))
        println(stringPlus("hello", "world"))
        println(2.intPlus(3))
    </p>
    
<br>
 &emsp;&emsp; Lambda表达式总是在花括号中，如果推断出lambda的返回类型不是Unit，那么lambda主体的最后一个表达式会视为返回值。
 Lambda表达式如果只有一个，那么该参数会隐式的声明为it。Lambda表达式与匿名函数之间的区别是非局部返回的行为，一个不带标签的return语句，
 总是在用fun关键字声明的函数中返回。意味着lambda表达式中的return将从包含它的函数返回，
 
    <p>
        // 下面这两种表达是等价的。
        
        ints.filter {
            val shouldFilter = it > 0
            shouldFilter
        }
        
        ints.filter {
            val shouldFilter = it > 0
            return@filter shouldFilter
        }
    </p>
    
<br>
 &emsp;&emsp; 如果lambda表达式的参数未使用，那么可以用下划线取代名称。Lambda表达式或者匿名函数以及局部函数和对象表达式可以访问其闭包，
 即在外部作用域中声明的变量。
    
    <p>
        map.forEach { _, value -> println("$value") }
        
        var sum = 0
        ints.filter { it > 0 }.forEach { sum += it }
        println(sum)
    </p>
    
### 内联函数
 &emsp;&emsp; 使用高阶函数会带来运行时的效率损失，每个函数都是一个对象，并且会捕获一个闭包。即那些在函数体中会访问到的变量，
 内存分配和虚拟调用会增加运行时间开销。通过内联化lambda可以消除开销。使用inline可以进行内联。可以内联的lambda表达式只能在内联函数内部调用，
 或者作为可内联的参数传递，但是noinline的可以以任何我们喜欢的方式操作。如果lambda表达式传给的函数是内联的，那么return也可以内联，只会退出lambda。
    
    <p>
        inline fun <T> foo(inlined: () -> Unit, noinline notinlined: () -> Unit): T { ... }
    </p>

<br>
 &emsp;&emsp; 有些内联函数，调用传给它们的不是直接来自函数体，而是来自另一个执行上下文的lambda表达式参数，这种情况下该lambda表达式需要使用
 crossinline修饰符标记。 通常来讲如果一个函数是内联函数，那么它的形參也是inline的，例如下面的body参数，内联函数是可以直接return的，
 也就是如果在body中return，会直接结束掉f()整个函数，使用 crossinline，能禁止直接return，必须使用return@body。
 
    <p>
        inline fun f(corssinline body() -> Unit) {
            val f = object: Runnable {
                override fun run = body()
            }
        }
    </p>
    
 <br>
 &emsp;&emsp; 内联函数支持具体化的类型参数。未标记为内联函数的，不能有具体化参数。不具有运行时表示的类型，不能作为具体化的类型参数的实参。
    
    <p>
        inline fun <reified T> TreeNode.findParentOfType(): T? {
            var p = parent
            while(p != null && p !is T) {
                p = p.parent
            }
            return p as T?
        }
        
        inline fun <reified T> membersOf() = T::class.members
        
        fun main() {
            println(membersOf<StringBuilder>().joinToString("\n))
        }
    </p>
    
<h2 id="5">5.集合</h2>
 &emsp;&emsp; List是一个有序集合，可以通过索引访问元素，元素可以重复。Set是唯一元素集合，包含一组无重复的对象。Map是键值对。
 map中经常用到的 to，也是一个中缀符，这个符号会创建一个短时存活的Pair对象，因此仅在性能不重要时使用，为避免过多的内存消耗，
 可以使用apply函数帮助保持初始化流畅。
    
    <p>
        val numberMap = mutableMapOf<String, String>().apply{
            this["one"] = "1"
            this["two"] = "2"
        }
    </p>
    
### 区间
&emsp;&emsp; 可以通过rangeTo()函数，及其操作符形式的..创建两个值的区间。1..4相当于 1 <= i && i <= 4。反向迭代数字，使用downTo。
可以通过任意步长迭代数字，通过step函数完成。要迭代不包含结束元素的数字区间，使用util函数。
 
 ### 序列
&emsp;&emsp; kotlin使用Sequence<T>，提供与Iterable相同的函数。当Iterable处理包含多个步骤的时候，会每个处理步骤完成后返回结果，
在这个集合上执行下一个步骤。序列的多步处理在可能的情况下会延迟执行：仅当请求整个处理链的结果时才会进行实际计算。Sequence对每个元素逐个执行所有的步骤，
而Iterable对集合每个步骤进行处理，再执行下一步骤。
  
    <p>
        list.map { it + 1 }.first { it % 100 == 0 } // 对于list，这一步会先执行map，再用集合结果再次执行first，两次while循环。
        list.asSequence().map { it + 1 }.first { it % 100 == 0 } // 对于序列，会对每个元素先后执行map、first，一次while循环。
    </p>
    
### 集合转换
&emsp;&emsp; 双路合并是根据两个集合中，具有相同位置的元素构建配对。使用zip()扩展函数完成。也可以使用unzip()，构建两个列表。

    <p>
        val colors = listOf("red", "brown", "grey")
        val twoAnimals = listOf("fox", "bear")
        
        colors.zip(twoAnimals)  // [(red, fox), (brown, bear)]
    </p>
    
<br>
&emsp;&emsp; 利用associateWith()，创建一个Map，其中原始集合的元素是键，并通过给定的转换函数从中产生值。如果两个元素相等，
那么最后一个保留在map中。associateBy() 根据元素的值返回键，如果两个元素相等，那么最后一个保留在Map中。

    <p>
        val numbers = listOf("one", "two", "three")
        println(numbers.associateWith { it.length })
        
        println(numbers.associateBy { it.first().toUpperCase() })
    </p>
    
<br>
&emsp;&emsp; 使用flatten()，这个函数返回嵌套集合中的所有元素的一个list。flatMap返回单个列表其中包含所有元素的值。

<br>
&emsp;&emsp; joinToString()根据提供的参数，从集合元素构建单个String。joinTo()执行同样的操作，但是将结果附加到给定的Appendable对象。
可以自定义separator来设置要构建的字符串格式，可以指定limit，大小超出limit，所有其他的元素将被truncated参数的单个值替换。

    val numbers = listOf("one", "two", "three")
    println(numbers.joinToString()) // one, two, three
    
    val listString = Stringbuffer("The list of numbers: ")
    numbers.joinTo(listString) // The list of numbers: one, two, three
    
    val numbers2 = (1..100).toList()
    println(numbers2.joinToString(limit = 5, truncated = "<...>")) // 1, 2, 3, 4, 5, <...>
    
    println(numbers.joinToString { "Element: ${it.toUpperCase()}" })

<br>
&emsp;&emsp; partition() 通过一个谓词过滤集合，并且将不匹配的元素存放在一个单独的列表中。
    
    val numbers = listOf("one", "two", "three")
    val (match, reset) = numbers.partition { it.length > 3 }
    
### 分组
&emsp;&emsp; kotlin的groupBy()使用一个lambda函数，并返回一个Map。在带有两个lambda的groupBy()中，由keySelector函数生成的键映射到值转换函数的结果，
而不是原始元素。groupBy实际已经对每个元素进行了遍历，通过keySelector构建了一个LinkedHashMap<K, MutableList<T>>()。此时分组全操作已经完成。
    
       val numbers = listOf("one", "two", "three", "four", "five")
       println(numbers.groupBy { it.first.toUpperCase() }) 
       // { O=[one], T=[two, three], F=[four, five] }
       
       println(numbers.groupBy(keySelector = { it.first() }, valueTransform = { it.toUpperCase() }))
       // { o=[ONE], t=[TWO, THREE], f=[FOUR, FIVE] }

<br>
&emsp;&emsp; 可以使用groupingBy()函数进行分组，这个函数实际返回的是一个Grouping类型的实例，此时只是产生了一个Grouping类型实例，
并没有实际遍历元素进行分组操作。Grouping类型实例，支持eachCount()、fold()、reduce()、aggregate()操作，也就是在调用这些函数时，
才实际执行了遍历分组操作。

    val numbers = listOf("one", "two", "three", "four", "five")
    println(numbers.groupingBy { it.first() }.eachCount() )
    // { o=1, t=2, f=2, s=1 }
    
### 取集合的一部分
&emsp;&emsp; 使用slice()，返回具有给定索引的集合元素列表。从头开始获取指定数量的元素使用take()，从尾开始获取指定数量的元素使用takeLast()。
要从头或尾去除给定数量的元素，使用drop()或dropLast()。还可以使用takeWhile()、takeLastWhile()、dropWhile()、dropLastWhile()，定义要获取或去除的元素直到与谓词匹配。
    
        val numbers = listOf(1, 2, 3, 4, 5)
        println(numbers.slice(0..4 step 2))
        println(numbers.take(2))
        println(numbers.takeLast(2))
        println(numbers.drop(2))
        println(numbers.dropLast(2))
        
<br>
&emsp;&emsp; chunked()函数可以将集合分解为给定大小的块。windowed() 返回滑动窗口，与chunked()不同。zipWithNext() 相当于windowed()窗口大小为2。

    val numbers = (0..13).toList()
    println(numbers.chunked(3)) // [[0, 1, 2], ... , [12, 13]]
    
    println(numbers.chunked(3) { it.sum() }) // [3, 12, 21, 30, 25]

### 取单个元素
&emsp;&emsp; 可以使用elementAt()取特定位置的元素，在使用list的情况下，使用get()或[] 更习惯。还可以使用elementAtOrNull() 
或者elementAtOrElse()。还可以使用first()、last()、firstOrNull()、lastOrNull()。可以使用find()代替firstOrNull()，使用findLast()代替lastOrNull()。

<br>
&emsp;&emsp; 可以使用random()，来获取集合中的一个随机元素。使用contains()判断集合中是否存在某个元素，如果一个集合元素等于equals()那么返回true。
可以使用in 调用contains()，一次检查多个实例的存在，可以使用containsAll()。使用isEmpty()、isNotEmpty()来判断集合中是否包含任何元素。

    val numbers = listOf(1, 2, 3, 4)
    println(numbers.random()) // 1
    
    val numbers = listOf("one", "two")
    println(numbers.contains("one"))
    println("zero" in numbers) // in 实际上是调用了contains() 函数。
    
### 排序
&emsp;&emsp; 如需为用户定义的类型定义一个自然顺序，那么需要让这个类继承Comparable接口，实现compareTo()函数。如果要为类型定义自定义顺序，
可以为其创建一个Comparator，定义一个Comparator简短的方式是标准库中的compareBy()函数。基本的函数sorted()和sortedDescending()返回集合的元素，
这些元素按照自然顺序升序和降序排列，这些函数适用于Comparable元素的集合。

    val lengthComparator = Comparator { str1: String, str2: String -> str1.length - str2.length }
    println(listOf("aaa", "bb", "c").sortedWith(lengthComparator))
    println(listOf("aaa", "bb", "c").sortedWith(compareBy { it.length }))
    
<br>
&emsp;&emsp; 为了按照自定义顺序排列，可以使用函数sortedBy()和sortedByDescending()它们将接受一个将集合元素映射为Comparable值的选择器函数，
并以该值的自然顺序对集合排序。倒序使用reversed()函数，随机顺序使用shuffled()函数。

    val numbers = listOf("one", "two", "three")
    val sortedNumbers = numbers.sortedBy { it.length }
    
<h2 id="6">6.协程</h2>
&emsp;&emsp; 开启一个协程，实现Hello，World！输出。

    val job = GlobalScope.launch { // 启动一个新的协程，并且保持对这个Job的引用
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    job.join() // 等待直到子协程执行结束
    
<br>
&emsp;&emsp; 当使用GlobalScope.launch时，会创建一个顶层协程，如果忘记保持对新启动的协程的引用，它还会继续运行。我们可以使用结构化并发，
这样就不用显式的join，因为外部协程直到在其作用域中启动的所有协程都执行完毕后才会结束。

    fun main() = runBlocking {  // this: CoroutineScope
        launch {                // 在runBlocking作用域中启动一个新协程。
            delay(1000L)
            println("World!")
        }
        println("Hello,")
    }
    
<br>
&emsp;&emsp; 可以使用coroutineScope构建自己的作用域，会创建一个协程作用域并且在所有以启动子协程执行完毕前不会结束。runBlocking与coroutineScope看起来很像，
都会等待其协程体及所有子协程结束，区别在于runBlocking会阻塞当前线程等待，而coroutineScope只是挂起。会释放底层线程用于其他用途。runBlocking是常规函数，
coroutineScope是挂起函数。在GlobalScope中启动的活动协程不会使进程保活，它们就像是守护线程。
<br>
&emsp;&emsp; 这里很多人奇怪，为什么明明coroutineScope是挂起函数，最后打印的却不是"Task from nested launch" 而是"Coroutine scope is over"，
因为runBlocking是会阻塞当前线程的，首先第一个地方使用launch创建了协程，执行了start，此时视为它已经执行完，然后coroutineScope创建了作用域，在作用域中创建了协程，
执行start，此时coroutineScope.launch也视为执行完毕。此时实际还有两个协程任务没执行完(想象下Thread.start() 线程启动了，但具体任务还需要在后台隐式执行)，
这两个协程任务都处于delay状态，由于coroutineScope遇到delay会挂起，于是可以继续向下执行，此时遇到第三个delay，由于coroutineScope要所有子协程执行完才算结束，
因此即时此时有3处delay，还是没办法执行"Coroutine scope is over"，于是根据delay时间，先后打印出不同结果。等所有子协程执行完，coroutineScope才算执行完毕，
此时可以继续向下执行，因此最后打印的一定是"Coroutine scope is over"。

    fun main() = runBlocking { // this: CoroutineScope
        launch { 
            delay(200L)
            println("Task from runBlocking")
        }
        
        coroutineScope { // 创建一个协程作用域
            launch {
                delay(500L) 
                println("Task from nested launch")
            }
        
            delay(100L)
            println("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
        }
        
        println("Coroutine scope is over") // 这一行在内嵌 launch 执行完毕后才输出
    }
    
    // 输出结果为：
    Task from coroutine scope
    Task from runBlocking
    Task from nested launch
    Coroutine scope is over
    
### 取消与超时
&emsp;&emsp; 协程的launch函数返回了一个可以用来被取消运行中的协程的job，通过job可以取消协程。可以使用cancelAndJoin代替cancel和join。
    
    val job = launch {
        repeat(1000) { i ->
            println("job: I'm sleeping $i...")
            delay(500L)
        }
    }
    delay(1300L)
    println("main: I'm tired of waiting!")
    job.cancel() // 取消该作业
    job.join() // 等待该作业执行结束
    println("main: Now I can quit.")

<br>
&emsp;&emsp; 协程取消是协作的，一段协程代码必须协作才能被取消。所有kotlinx.coroutines中的挂起函数都是可以被取消的，它们会检查协程的取消，
并在取消时抛出CancellationException。但是如果协程正在执行计算任务，并且没有检查取消的话，那么是不能被取消的。可以使用yield和显示的检查取消状态，
来使执行计算的代码被取消。如果想要挂起一个被取消的协程，那么需要将相应的代码包装在withContext(NonCancellable) {} 中。

    fun cancelUseFinally() = runBlocking {
        val job = launch {
            try {
                repeat(1000) {
                    println("job: I'm sleeping $it...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000L)
                    println("job: Anf I've just delayed for 1 sec because I'm non-cancellable")
                }
                // 加了这个delay下面不会再继续执行，因为delay是挂起函数，会检查结束状态，从而不会继续往下执行，会直接cancel接下来的操作。
                delay(200L)
                println("job: I'm running finally2")
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }
    
<br>
&emsp;&emsp; 在实践中绝大多数取消一个协程的理由是它可能超时，使用withTimeout函数，如果超时会抛出TimeoutCancellationException。
如果需要做一些各类使用超时的特别的额外操作，那么可以使用withTimeoutOrNull函数，withTimeoutOrNull通过返回null来进行超时操作，
从而来替代抛出一个异常。实际上withTimeoutOrNull执行方式与withTimeout一样，只是在抛出异常后捕获了异常，然后返回了null。

    val result = withTimeoutOrNull(1300L) {
        repeat(1000) {
            println("I'm sleeping $it...")
            delay(500L)
        }
        "Done"
    }
    println("Result is $result")
    
### 组合挂起函数
&emsp;&emsp; async类似与launch，launch返回一个Job且不带任何结果附加值，async返回一个轻量级的非阻塞Deferred，Deferred也是一个Job，可以取消。
async也可以将start参数设置为CoroutineStart.LAZY，此时只有在await时或者显示的执行start时才会启动。async被定义为了CoroutineScope上的扩展，
我们使用async的结构化并发时，需要将async写在作用域中。

    // 此时如果在computeSum函数内部出现了错误，并且抛出了一个异常，那么所有在作用域中启动的协程都会被取消。
    suspend fun computeSum(): Int = coroutineScope {
        val one = async { doSomethingOne() }
        val two = async { doSomethingTwo() }
        one.await() + two.await()
    }
    
### 协程上下文与调度器
&emsp;&emsp; 协程运行在以CoroutineContext类型为代表的上下文中。协程上下文是不同元素的集合，主元素是协程中的Job。协程上下文包含一个协程调度器。
当调用launch { ... } 不传参数时，它会从启动了它的CoroutineScope中继承上下文及调度器。当使用EmptyCoroutineContext时，
会返回Dispatchers.Default CoroutineContext。默认调度器使用共享的后台线程池。调度器有Dispatchers.Uncofined、Dispatchers.Default、
newSingleThreadContext。newSingleThreadContext为协程的运行启动了一个线程，一个专用的线程是很昂贵的资源，在真实的应用程序中两者都必须被释放。

<br>
<br>
&emsp;&emsp; Dispatchers.Unconfined协程调度器在调用它的线程启动了一个协程，但它只是运行到第一个挂起点。挂起后，它恢复线程中的协程。
也就是非限制协程调度器(Unconfined)会立即被调度执行，运行在当前线程上。

    launch(Dispatchers.Unconfined) { // 非受限的——将和主线程一起工作
        println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
        delay(500)
        println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    }
    launch { // 父协程的上下文，主 runBlocking 协程
        println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
        delay(1000)
        println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
    }

<br>
&emsp;&emsp; 当在一个协程中使用launch启动一个子协程时，子协程会继承父协程的上下文coroutineContext，如果是GlobalScope启动，
那么这个协程没有父协程，会使用独立的作用域。当父协程被取消，那么所有它的子协程也会被递归取消。

<br>
&emsp;&emsp; 我们可以使用 + 操作符来在协程上下文中定义多个元素。
    
    launch(Dispatchers.Default + CoroutineName("test")) {
        println("I'm working in thread ${Thread.currentThread().name()}")
    }
    
<br>
&emsp;&emsp; 线程局部数据，可以使用ThreadLocal、asContextElement可以创建额外的上下文元素，保留给定ThreadLocal的值，
并在每次协程切换其上下文时回复它。

### 异步流
&emsp;&emsp; flow { ... } 构建块中的代码可以挂起，流使用emit函数发射值，流使用collect函数收集值。Flow是一种类似序列的冷流，
flow构建器中的代码直到流被收集的时候才运行。flowOf构建器定义了一个发射固定值集的流。使用asFlow()可以将各种集合与序列转换为流。
可以使用transform对流进行转换。可以使用take操作符，来限制操作符。flow与stream、Iterable<T>.filter等不同，flow只循环一次，
而另外的每个操作都循环一次生成一个新的集合。

    (1..3).asFlow().collect{ value -> println(value) }
    
    // transform 对流进行转换
    runBlocking {
        listOf(1, 2, 3).asFlow().transform { request ->
            emit("Marking request $request")
        }.collect { response -> println(response) }
    }
    
    // 只会执行前N个emit函数
    runBlocking {
            flow {
                try {
                    emit(1)
                    emit(2)
                    println("This line will not execute")
                    emit(3)
                } finally {
                    println("Finally in numbers")
                }
            }.take(2).collect { value -> println(value) }
        }
    
<br>
&emsp;&emsp; flow { ... } 构建器中的代码必须遵循上下文保存属性，不允许从其他上下文中发射(emit)。可以通过flowOn函数，更改流发射的上下文。
可以使用buffer操作符来并发运行flow中发射元素的代码以及收集代码，而不是顺序运行。当收集器很慢时，可以使用conflate操作符，跳过中间值。
当发射器和收集器都很慢时，合并是加快处理速度的一种方式，通过删除发射值来实现；另一种方式是取消缓慢的收集器，并在每次发射新值的时候重新启动它。
    
    fun foo(): Flow<Int> = flow {
        // 在流构建器中更改消耗 CPU 代码的上下文的错误方式
            kotlinx.coroutines.withContext(Dispatchers.Default) {
                for (i in 1..3) {
                    Thread.sleep(100) // 假装我们以消耗 CPU 的方式进行计算
                    emit(i) // 发射下一个值
                }
            }
    }
    
    //   Collecting 1
    //   Collecting 2
    //   Collecting 3
    //   Done 3
    //   Collected in 708 ms
    // 说明每次产生发射新值的时候，收集器都被取消了。
    val time = measureTimeMillis {
        foo()
            .collectLatest { value -> // 取消并重新发射最后一个值
                println("Collecting $value") 
                delay(300) // 假装我们花费 300 毫秒来处理它
                println("Done $value") 
            } 
    }   
    println("Collected in $time ms")
    
    // 此时会抛异常，因为更改了上下文
    foo().collect { value -> println(value) } 
    
    fun foo(): Flow<Int> = flow {
        for (i in 1..3) {
            Thread.sleep(100) // 假装我们以消耗 CPU 的方式进行计算
            log("Emitting $i")
            emit(i) // 发射下一个值
        }
    }.flowOn(Dispatchers.Default) // 在流构建器中改变消耗 CPU 代码上下文的正确方式
    
    foo().collect { value ->
        log("Collected $value") 
    } 
    
<br>
&emsp;&emsp; 使用zip操作符来合并两个流中的相关值。每当上游流产生值时都需要重新计算，这种操作使用combine。使用flatMapConcat在等待内部流完成之前，
开始收集下一个值。
    
    val nums = (1..3).asFlow() // 数字 1..3
    val strs = flowOf("one", "two", "three") // 字符串
    nums.zip(strs) { a, b -> "$a -> $b" } // 组合单个字符串
        .collect { println(it) } // 收集并打印
    // 结果 
    // 1 -> one
    // 2 -> two
    // 3 -> three
    
    val nums = (1..3).asFlow().onEach { delay(300) } // 发射数字 1..3，间隔 300 毫秒
    val strs = flowOf("one", "two", "three").onEach { delay(400) } // 每 400 毫秒发射一次字符串
    val startTime = System.currentTimeMillis() // 记录开始的时间
    nums.combine(strs) { a, b -> "$a -> $b" } // 使用“combine”组合单个字符串
        .collect { value -> // 收集并打印
            println("$value at ${System.currentTimeMillis() - startTime} ms from start") 
        } 
    // 结果   
    // 1 -> one at 452 ms from start
    // 2 -> one at 651 ms from start
    // 2 -> two at 854 ms from start
    // 3 -> two at 952 ms from start
    // 3 -> three at 1256 ms from start
    
<br>
&emsp;&emsp; 流异常时，可以使用catch{ ... }。 流完成时，可以使用onCompletion。启动流可以使用onEach。

### 通道
&emsp;&emsp; Channel是一个和BlockingQueue非常相似的概念。替代了阻塞的put操作，提供了挂起的send，还替代了阻塞的take操作，并提供了挂起的receive。

    val channel = Channel<Int>()
    launch {
        // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
        for (x in 1..5) channel.send(x * x)
    }
    // 这里我们打印了 5 次被接收的整数：
    repeat(5) { println(channel.receive()) }
    println("Done!")
    
<br>
&emsp;&emsp; 一个通道可以通过被关闭来表明没有更多的元素会进入通道，可以创建带缓冲区大小的通道，允许发送者在被挂起前发送多个元素。
通道遵循先进先出的原则。可以使用工厂方法ticker创建计时器通道，计时器通道每经过特定的延迟都会从该通道进行消费并产生Unit

    val channel = Channel<Int>(4) // 启动带缓冲的通道
    val sender = launch { // 启动发送者协程
        repeat(10) {
            println("Sending $it") // 在每一个元素发送前打印它们
            channel.send(it) // 将在缓冲区被占满时挂起
        }
    }
    // 没有接收到东西……只是等待……
    delay(1000)
    sender.cancel() // 取消发送者协程
    
### 共享的可变状态与并发
&emsp;&emsp; 在kotlin中volatile是一种注解。volatile只能保证内存可见性，被volatile修饰的变量，被赋值后，会多执行一行"lock addl $0x0, (%esp)"，
这个操作相当于内存屏障，重排序时不能把后面的指令重排序到内存屏障之前，因此能保证内存可见性。可以使用原子操作，保证线程安全。

    @Volatile // 在 Kotlin 中 `volatile` 是一个注解
    var counter = 0
    
    suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100  // 启动的协程数量
        val k = 1000 // 每个协程重复执行同一动作的次数
        val time = measureTimeMillis {
            coroutineScope { // 协程的作用域
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
        println("Completed ${n * k} actions in $time ms")    
    }
    
    // 结果counter可能并不是100000，因为大量自增操作时，volatile并不能提供原子性。
    fun main() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                counter++
            }
        }
        println("Counter = $counter")
    }
    
<br>
&emsp;&emsp; 以细粒度限制线程可以解决共享可变状态。这种方式将对特定共享状态的所有访问权限制在单个线程中。

    // 限定这个上下文只运行在一个线程中
    val counterContext = newSingleThreadContext("CounterContext")
    val counter = 0
    fun main() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                // 将每次自增操作限制在单线程上下文中
                withContext(counterContext) {
                    counter++
                }
            }
        }
        println("Counter = $counter")
    }
    
<br>
&emsp;&emsp; 以粗粒度限制线程，相比上面细粒度限制线程，运行更快。
    
    val counterContext = newSingleThreadContext("CounterContext")
    var counter = 0
    
    fun main() = runBlocking {
        // 将一切都限制在单线程上下文中
        withContext(counterContext) {
            massiveRun {
                counter++
            }
        }
        println("Counter = $counter")
    }
    
<br>
&emsp;&emsp; 在协程中可以使用Mutex来代替synchronized或者ReentrantLock。Mutex具有lock和unlock方法，Mutex.lock()是挂起函数，不会阻塞线程。
Mutex还有withLock扩展函数，用来代替日常用的mutex.lock() try { ... } finally { mutex.unlock() } 模式。

    val mutex = Mutex()
    var counter = 0
    
    fun main() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                // 用锁保护每次自增
                mutex.withLock {
                    counter++
                }
            }
        }
        println("Counter = $counter")
    }
    
<br>
&emsp;&emsp; actor是由协程、被限制并封装到该协程中的状态以及一个与其他协程通信的通道组合而成的一个实体。

<h2 id="7">7.多平台程序设计</h2>
&emsp;&emsp; kotlin提供了一种预期声明(expect)和实际声明的机制(actual)。利用这种机制公共模块可以定义预期声明，
平台模块可以提供与预期声明相对应的实际声明。公共模块中预期声明与其对应的实际声明始终具有完全相同的完整限定名。
预期声明标有expect关键字，实际声明标有actual关键字。与预期声明的任何部分匹配的所有实际声明都要标记为actual。预期声明不包含任何实现代码。

    expect class Foo(bar: String) {
        fun frob()
    }
    
    actual class Foo actual constructor(val bar: String) {
        actual fun frob() {
            println("Forbbing the $bar")
        }
    }
    
    // 公共
    expect fun formatString(source: String, vararg args: Any): String
    
    expect annotation class Test
    
    // JVM
    actual fun formatString(source: String, vararg args: Any) =
        String.format(source, *args)
        
    actual typealias Test = org.junit.Test

<h2 id="8">8.语言结构</h2>
### 类型检测与转换
&emsp;&emsp; 可以使用is、!is来进行类型检测。使用as进行类型转换，如果转换失败会抛异常，可以使用as?进行安全的类型转换，如果转换失败返回null。

    val x: String? = y as? String
    
    inline fun <reified A, reified B> Pair<*, *>.asPairOf(): Pair<A, B>? {
        if (first !is A || second !is B) return null
        return first as A to second as B
    }
    
    val somePair: Pair<Any?, Any?> = "items" to listOf(1, 2, 3)
    
    val stringToSomething = somePair.asPairOf<String, Any>()
    val stringToInt = somePair.asPairOf<String, Int>()
    val stringToList = somePair.asPairOf<String, List<*>>()
    val stringToStringList = somePair.asPairOf<String, List<String>>() // 破坏类型安全！
    
    inline fun <reified T> List<*>.asListOfType(): List<T>? =
        if (all { it is T })
            @Suppress("UNCHECKED_CAST")
            this as List<T> else
            null
            
### 注解
&emsp;&emsp; 注解使用annotation修饰符放在类的前面。如果对类的主构造函数进行标注，那么需要在构造函数声明中添加constructor关键字，
并将注解添加到其前面。注解也可以标注属性访问器。

    @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
            AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION)
    @Retention(AnnotationRetention.SOURCE)
    @MustBeDocumented
    annotation class Fancy
    
    @Fancy class Foo {
        @Fancy fun baz(@Fancy foo: Int): Int {
            return (@Fancy 1)
        }
    }
    
    // 注解主构造函数
    class Foo @Inject constructor(dependency: MyDependency) { ... }
    
    // 注解属性访问器
    class Foo {
        var x: MyDependency? = null
            @Inject set
    }
    
<br>
&emsp;&emsp; 如果需要将一个类指定为注解的参数，请使用Kotlin类(KClass)，kotlin编译器会自动将其转换为java类，以便java代码能正常访问该注解与参数。

    annotation class Ann(val arg1: KClass<*>, val arg2: KClass<out Any>)
    
    @Ann(String::class, Int::class) class MyClass
    
<br>
&emsp;&emsp; 注解也可以用于lambda表达式，它们会被应用于生成lambda表达式体的invoke()方法上。注解可以被用于file、property、field、get、
set、receiver、param、setparam、delegate。

    annotation class Suspendable
    val f = @Suspendable { Fiber.sleep(10) }
    
    class Example {
        @set:[Inject VisibleForTesting]
        var collaborator: Collaborator
    }
    
### 反射
&emsp;&emsp; 最基本的反射功能是获取Kotlin类的运行时引用。
    
    // 该引用是KClass类型的值，如果要获得java类引用，需要在KClass实例上使用.java属性
    val c = MyClass::class

<br>
&emsp;&emsp; 可以使用::操作符来调用函数。

    // 这里的::isOdd 是函数 (Int) -> Boolean 的一个值
    fun isOdd(x: Int) = x % 2 != 0
    val number = listOf(1, 2, 3)
    println(numbers.filter(::isOdd))
    
    val x = 1
    
    fun main() {
        println(::x.get()) // 1
        println(::x.name)  // x  返回属性名
    }
    
    var y = 1
    
    fun main() {
        ::y.set(2)
        println(y)  // 2 
    }
    
    // 访问类的成员的属性
    class A(val p: Int)
    val prop = A::p
    println(prop.get(A(1)))
    
<br>
&emsp;&emsp; java平台上，标准库包含反射类的扩展，提供了与java反射对象之间的映射。

    class A(val p: Int)
    
    // 查找一个用作Kotlin属性getter的幕后字段或java方法
    fun main() {
        println(A::p.javaGetter) // 输出 "public final int A.getP()"
        println(A::p.javaField)  // 输出 "private final int A.p"
    }
    // 获得对应于java类的kotlin类，使用.kotlin扩展属性
    fun getKClass(o: Any): KClass<Any> = o.javaClass.kotlin
    
    val numberRegex = "\\d+".toRegex()
    val strings = listOf("abc", "124", "a70")
    println(strings.filter(numberRegex::matches))
    
<br>
&emsp;&emsp; apply 及 also的返回值是上下文对象本身。因此特曼可以作为辅助步骤包含在调用链中：可以继续在同一个对象上进行函数调用。
let、run 及 with 返回 lambda 表达式的结果。所以，在需要使用其结果给一个变量赋值，或者在需要对其结果进行链式操作等情况下，可以使用它们。

    val numberList = mutableListOf<Double>()
    numberList.also { println("Populating the list") }
        .apply {
            add(2.71)
            add(3.14)
            add(1.0)
        }
        .also { println("Sorting the list") }
        .sort()
        
    fun getRandomInt(): Int {
        return Random.nextInt(100).also {
            writeToLog("getRandomInt() generated value $it")
        }
    }
    
    val i = getRandomInt()
    
    val numbers = mutableListOf("one", "two", "three")
    val countEndsWithE = numbers.run { 
        add("four")
        add("five")
        count { it.endsWith("e") }
    }
    println("There are $countEndsWithE elements that end with e.")
    
    val numbers = mutableListOf("one", "two", "three")
    with(numbers) {
        val firstItem = first()
        val lastItem = last()        
        println("First item: $firstItem, last item: $lastItem")
    }
    
<br>
&emsp;&emsp; 上下文对象作为 lambda 表达式的参数（it）来访问。返回值是 lambda 表达式的结果。
    
    val numbers = mutableListOf("one", "two", "three", "four", "five")
    numbers.map { it.length }.filter { it > 3 }.let(::println)
    
<br>
&emsp;&emsp; with 一个非扩展函数：上下文对象作为参数传递，但是在 lambda 表达式内部，它可以作为接收者（this）使用。 返回值是lambda 表达式结果。

    val numbers = mutableListOf("one", "two", "three")
    with(numbers) {
        println("'with' is called with argument $this")
        println("It contains $size elements")
    }
   
<br>
&emsp;&emsp; run 上下文对象 作为接收者（this）来访问。 返回值 是 lambda 表达式结果。
    
    val service = MultiportService("https://example.kotlinlang.org", 80)
    
    val result = service.run {
        port = 8080
        query(prepareRequest() + " to port $port")
    }
    
    // 同样的代码如果用 let() 函数来写:
    val letResult = service.let {
        it.port = 8080
        it.query(it.prepareRequest() + " to port ${it.port}")
    }
    
<br>
&emsp;&emsp; apply 上下文对象 作为接收者（this）来访问。 返回值 是上下文对象本身。

    val adam = Person("Adam").apply {
        age = 32
        city = "London"        
    }
    println(adam)
    
<br>
&emsp;&emsp; also 上下文对象作为 lambda 表达式的参数（it）来访问。 返回值是上下文对象本身。
    
    val numbers = mutableListOf("one", "two", "three")
    numbers
        .also { println("The list elements before adding new one: $it") }
        .add("four")
        
<br>
&emsp;&emsp; 对一个非空（non-null）对象执行 lambda 表达式：let。将表达式作为变量引入为局部作用域中：let。对象配置：apply。
对象配置并且计算结果：run。在需要表达式的地方运行语句：非扩展的 run。附加效果：also。一个对象的一组函数调用：with。
        
    函数	    对象引用	    返回值	            是否是扩展函数
    let	    it	        Lambda表达式结果	        是
    run	    this	    Lambda表达式结果	        是
    run	    -	        Lambda表达式结果	        不是：调用无需上下文对象
    with	this	    Lambda表达式结果	        不是：把上下文对象当做参数
    apply	this	    上下文对象	            是
    also	it	        上下文对象	            是
    
<br>
&emsp;&emsp; 除了作用域函数外，标准库还包含函数 takeIf 及 takeUnless。这俩函数使你可以将对象状态检查嵌入到调用链中。
若该对象与谓词匹配，则 takeIf 返回此对象。否则返回 null。

    val number = Random.nextInt(100)
    
    val evenOrNull = number.takeIf { it % 2 == 0 }
    val oddOrNull = number.takeUnless { it % 2 == 0 }
    println("even: $evenOrNull, odd: $oddOrNull")
    