* [1.对象创建方式](#1)
* [2.线程池](#2)
* [3.类加载](#3)

<h2 id="1">1.对象创建方式</h2>
### 使用new创建对象
&emsp;&emsp; 先在方法区中查看类是否被加载过，如果没有先进行类加载，加载完后：1，先给实例分配内存。2，调用构造函数，初始化成员字段。
3，对象指向分配的内存空间。

### 使用clone创建对象
&emsp;&emsp; 当调用clone方法时，JVM会创建一个新的对象，将前面对象的内容全部拷贝进去。clone方法不会调用任何构造函数。原理是从内存中以二进制
流的方式进行拷贝，重新分配一个内存块。默认的clone()方法是浅拷贝，对于对象字段，只会拷贝内存地址，而不是产生一个新的对象。

### 使用反序列化
&emsp;&emsp; 序列化是指将对象的状态信息转换为可以存储或传输的形式的过程。序列化过程中涉及到的所有对象，都要实现Serializable接口。
在反序列化过程中实际上是调用了类的构造函数，创建了新的对象。

### 使用反射
&emsp;&emsp; 通过反射来创建类对象的实例：1，先拿到类对象的Class。2，通过反射创建类对象的实例对象。可以通过Class.newInstance()，
调用类的无參构造函数创建对象，也可以通过类对象的Constructor.newInstance()来创建实例对象。

### 使用Unsafe
&emsp;&emsp; 可以通过Unsafe.allocateInstance()创建实例对象，仅需Class对象就可以创建类的实例对象，
并且不需要调用类的构造函数、初始化代码、JVM安全检测等。它抑制修饰符检测，即使是private 修饰的，也可以通过这个方法实例化。Unsafe创建的对象，
并不会进行实例属性赋值。

    // 例如使用Unsafe创建下面实例对象，最终发现并没有调用构造方法，而且val的值为0。
    public class UnsafeInstanceObject {
        public int val =  2;
    
        public UnsafeInstanceObject(int val) {
            this.val = val;
        }
    }
    
<h2 id="2">2.线程池</h2>
### 为什么使用线程池
&emsp;&emsp; 如果单个任务处理时间短，需要处理的任务量很大。那么使用线程池：1，可以减少在创建和销毁线程上所花费的时间及系统资源的开销。
2，避免系统创建大量线程而导致消耗完系统内存及过度切换。

<h2 id="3">3.类加载</h2>
&emsp;&emsp; 通常使用ClassLoader.loadClass(name)，这一步只是将类数据加载到了内存中，生成了Class对象，没有进行连接。
使用Class.forName(name) 会加载类，并执行类对象初始化。Class.forName(String name, boolean initialize, ClassLoader loader) 如果
initialize传true，加载类时会执行初始化。