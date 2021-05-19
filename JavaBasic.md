* [1.对象创建方式](#1)
* [2.线程池](#2)
* [3.类加载](#3)
* [4.锁](#4)
* [5.缓存一致性MESI与volatile](#5)
* [6.ThreadLocal](#6)
* [7.Java的四种引用](#7)

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

<h2 id="4">4.锁</h2>
&emsp;&emsp; ReentrantLock默认使用非公平锁，synchronized也使用的非公平锁。

    ReentrantLock {
        Synd sync; // FairSync、NonfairSync。
    }
    
    abstract class Sync extends AbstractQueuedSynchronizer

### ReentrantLock lock()获取锁的流程
&emsp;&emsp; ReentrantLock使用lock()方法获取锁时，实际是使用ReentrantLock中具体的同步锁(公平锁、非公平锁)来执行锁定。
非公平锁使用了AQS(AbstractQueuedSynchronizer)，AQS提供了一个FIFO的队列，head节点表示当前持有锁的节点，在其他线程竞争锁失败后，
会加入到队尾。
    
    AQS队列节点状态：
        1，CANCELLED(1)，取消状态，如果当前节点为CANCELLED，那么表示节点已经等待超时或被取消，需要从队列中删除。
        2，SIGNAL(-1)，等待触发，如果当前节点的前置节点为SIGNAL，那么当前线程需要阻塞。
        3，CONDITION(-2)，等待条件触发，表示当前节点在等待condition，即在condition队列中。
        4，PROPAGATE(-3)，状态需要向后传播，表示releaseShared需要被传播给后续节点，仅在共享锁模式下使用。
    
    对于非公平锁，lock()流程如下：
        1，先判断当前锁是否已经被锁定(state == 0表示未锁定)，如果没有，那么使用CAS将状态设置为1，并设置互斥锁的持有者为当前线程。
        2，如果当前ReentrantLock已经被锁定，那么再次判断是否当前ReentrantLock是否还是处于锁定状态，如果已经被释放锁了，
            那么当前线程获取锁。如果还是处于锁定状态，那么判断是否ReentrantLock锁是当前线程持有的，如果是，那么重入锁，即state值增加。
        3，如果前两步还是没获取到锁，那么利用当前Thread构建Node节点，加入到当前锁的节点链表中。
        4，然后在for死循环中自旋，1，如果当前Node的前置节点是头节点，那么再次尝试步骤1，2来获取锁，如果获取到了锁，那么将当前节点置为头节点，
            并返回。2，如果当前节点前置节点不是头节点或者尝试获取锁失败，那么判断当前线程的前置节点是否应该为SIGNAL，如果是那么当前线程需要阻塞。
            3，如果当前线程需要阻塞，那么会调用LockSupport.park(blocker)来对当前线程进行阻塞。
            
    对于公平锁，相比于非公平锁，会判断当前AQS队列是否有在等待锁的节点，如果有，则将当前线程添加到队列尾部，如果没有才会尝试获取锁。
    
### ReentrantLock unlock()释放锁的流程
&emsp;&emsp; ReentrantLock使用unlock()方法释放锁。公平锁、非公平锁释放锁的逻辑都是一样的。
    
    释放锁的流程：
        1，先确保当前线程持有当前锁，先设置当前锁的持有者为空，然后更新当前锁的state--。
        2，使用LockSupport.unpark(Thread)，会唤醒下一个同步队列节点的线程。
    
### Condition await()流程
&emsp;&emsp; 对于公平锁(FairSync)、非公平锁(NonfairSync) 使用newCondition()来创建Condition本质是创建了一个新的ConditionObject。
Condition 的await、signal、signalAll都需要在先使用lock()获取锁。

    对于所有的AbstractQueuedSynchronizer，Condition.await()执行的逻辑都是一样的，不区分公平锁还是非公平锁。
    Condition.await()流程：
        1，使用当前线程和CONDITION构造Node，然后添加到ConditionObject队列的尾部。这一步会清理掉队列中被取消的节点。
        2，释放掉当前Condition所属的AQS的锁，在释放过程中，会唤醒同步队列中下一个节点。
        3，阻塞当前线程，等待Condition.signal来唤醒当前线程。
        4，当Condition await阻塞被唤醒后，将当前Node，加入到AQS的同步队列中，再次尝试获取锁，如果没获取到，那么会阻塞当前线程。
        
### Condition signal()、signalAll()释放锁的流程。
&emsp;&emsp; Condition使用signal、signalAll释放锁，相比于signal，signalAll相当于对Condition同步队列中所有的节点都执行了signal。
    
    Condition.signal流程：
        1，判断当前Condition所属的锁，是否被当前线程持有。
        2，将Condition中同步队列的头节点，添加到所属的Lock的同步队列中。
        3，如果当前节点的前置节点设置状态为SIGNAL失败或者前置节点被取消，表明当前节点不需要阻塞，那么此时直接唤醒当前节点的线程。

### Condition await 与 Object wait的区别
&emsp;&emsp; Condition await不会丢失已经获取的锁，而Object wait会丢失已经持有的锁，唤醒其他的阻塞的线程，然后在被其他线程通过notify唤醒时，
需要再次获取锁。
    
    synchronized(obj) {
        obj.wait();   // 此时需要先放弃锁，否则其他的线程没办法获取到锁，无法被唤醒。
    }
    
<h2 id="5">5.缓存一致性MESI与volatile</h2>
&emsp;&emsp; CPU与内存之间的速度差距较大，在处理器时钟周期中，CPU需要等待主存，因此使用：CPU -> Cache -> Memory结构。
Cache由Cache line组成。

### 局部性原理
&emsp;&emsp; 在CPU访问存储设备时，无论是存取数据或者存取指令，都趋于聚集在一片连续的区域中，这被称为局部性原理。
时间局部性：如果一个信息项正在被访问，那么近期它还可能被再次访问。空间局部性：如果一个存储器的位置被引用，那么将来它附近的位置也会被引用。

### 三级缓存
&emsp;&emsp; 在多核CPU的结构中，分为三级缓存，一级、二级缓存为CPU私有的缓存，三级缓存为多核CPU共享的。一级缓存分为数据和指令两部分，
二级缓存指令和数据共存。

### 缓存一致性(MESI)
&emsp;&emsp; 在多核CPU中，内存中的数据会在多个核心中保存数据副本，其中一个CPU修改数据，就产生了数据不一致的问题，一致性协议用于保证多个CPU
Cache之间共享数据的一致。每个Cache line可能存在4种状态，状态值可以用2bit表示。

    缓存行状态                       描述                                                     监听任务
    M(modified):    该缓存行有效，数据被修改了，与内存中的数据不一致，                  缓存行需要监听其他试图读取该缓存行的操作，
                    数据只存在本Cache中。监听所有试图读主存中该缓存行的操作，           监听到后，需要先将本Cache中缓存行写回主存，   
                                                                                 并将状态变为S，然后再让读取操作执行。
                                                                                                                  
    E(Exclusive)：  该缓存行有效，数据与内存中的数据一致，数据只存在本Cache中。          缓存行需要监听其他缓存读取主存中该缓存行的操作，
                                                                                 一旦监听到，该缓存行需要变成S状态。
    
    S(Shared)：     该缓存行有效，数据与内存中的数据一致，数据存在多个Cache中。         缓存行需要监听其他缓存使该缓存行无效或者独享
                                                                                的请求。并将该缓存行变成I状态。
                                                                                
    I(Invalid):     该缓存行无效。                                                无

### Cache操作
&emsp;&emsp; Cache的操作有4种：1，本地读取(Local Read): 本地Cache读取本地Cache数据。
2，本地写入(Local Write): 本地Cache写入本地Cache数据。3，远端读取(Remote Read): 其他Cache读取本Cache数据。
4，远端写入(Remote Write): 其他Cache写入本地Cache数据。

### Store buffer和Invalidate queue
&emsp;&emsp; Store buffer在 CPU 和 Cache之间，对其他Cache不可见。如果要修改本地Cache中的一条缓存行，此时需要将无效状态通知到其他Cache中，
并且要等待确认，这个过程比较耗时，因此使用Store buffer(存储缓冲区)。此时CPU会先发出一条无效(Invalid)消息，
然后将写操作放到Store buffer中，接着CPU会去处理其他任务，Store buffer在收到所有失效确认后，会将缓存行写入到内存中。

<br>
&emsp;&emsp; 为了提高无效请求的处理，CPU使用Invalidate queue(无效队列)，会在接收到无效请求时，将无效请求直接放入到无效队列，然后直接返回。

### 内存屏障
&emsp;&emsp; 写屏障：告诉处理器在执行这条指令前，将所有Store buffer中的缓存行修改刷新到一级缓存中，这样修改就对其他Cache可见。
读屏障：告诉处理器在执行这条指令前，先刷新所有已经在失效队列中的指令到一级缓存中，这样就能知道当前Cache的缓存行是否失效。

### Volatile
&emsp;&emsp; Volatile原理是通过内存屏障来保证变量的可见性。由于Store buffer和Invalidate buffer的存在，可能导致脏读的问题。
volatile通过内存屏障，实时刷新Store buffer和Invalidate buffer，从而实现变量的可见性。

<h2 id="6">6.ThreadLocal</h2>
&emsp;&emsp; ThreadLocal的实现为：每个线程维护一个ThreadLocalMap，ThreadLocalMap Entry的key为WeakReference<ThreadLocal<?>>，
key为弱引用，弱引用的对象在GC时，可能会被回收。这样ThreadLocalMap中就会出现key为null的Entry。如果当前线程迟迟不结束，
那么会存在一条强引用链：Thread.threadLocals -> ThreadLocalMap -> Entry(key == null) -> value，这条强引用链无法回收，
可能导致内存泄漏。ThreadLocal的get()、set()、remove()方法，在调用的时候都会检查key == null的情况，并进行清理。

    ThreadLocal导致内存泄漏的情况：
    1，使用线程池时，线程执行任务完毕，放回线程池中没有销毁，线程一直没被使用，导致内存泄漏。
    2，分配了ThreadLocal，但是没有调用get()、set()、remove()方法，导致线程泄漏。
    
<h2 id="7">7.Java的四种引用</h2>
&emsp;&emsp; Java有四种引用：强引用、软引用(SoftReference)、弱引用(WeakReference)、虚引用(PhantomReference)。
软引用：当JVM触发GC后，内存还是不足，那么会把软引用包裹的对象回收掉。弱引用：只要发生GC，弱引用包裹的对象就会被回收。
虚引用：虚引用必须跟引用队列(ReferenceQueue)一起使用，一个对象是否有虚引用的存在，完全不会对其生存时间构成影响，当对象被回收时，
如果对象有虚引用，那么会将回收的通知放到引用队列中，这样我们就能知道对象已经被回收。

    只要对象存在任一强引用就不会被GC，例如A对象有一个强引用，同时存在一个弱引用ref = WeakReference(A)，弱引用对象每次GC都会被回收，
    但此时由于A对象存在强引用，因此即时GC，ref.get()也不为null。
