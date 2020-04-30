## kotlin 基础

* [1.惯用法](#1)
* [2.基础](#2)
* [3.类与对象](#3)
* [4.函数与Lambda表达式](#4)
* [5.集合](#5)

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
        void demo(Source<String> strs) {
            Source<? extends Object> objects = strs; // 在java中需要这么声明
        }
        
        
        // kotlin 通过对source的类型参数T添加注解，确保Source<T> 成员函数只返回T类型，而不会消费T类型。
        // out 修饰符为协变注解，这个注解出现在类型参数的声明处，因此称为声明处的类型变异，与java中的使用处类型变异相反。
        // in 这个注解导致参数类型反向变异，这个类型只能被消费，不能被生产。
        
        interface Source<out T> {
            abstract fun nextT(): T
        }
        
        fun demo(strs: Source<String>) {
            val objects: Source<Any> = strs   
        }
        
        interface Comparable<in T> {
            operator fun compareTo(other: T): Int
        }
        
        fun demo(x: Comparable<Number>) {
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
&emsp;&emsp; 枚举类可以实现接口，但是不能继承其他类。

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
 crossinline修饰符标记。
 
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
    
