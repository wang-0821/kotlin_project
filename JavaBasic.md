* [1.对象创建方式](#1)
* [2.线程池](#2)
* [3.类加载](#3)
* [4.锁](#4)
* [5.缓存一致性MESI与volatile](#5)

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
&emsp;&emsp; 