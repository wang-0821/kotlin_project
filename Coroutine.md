## 协程学习笔记

* [1.概述](#1)
* [2.协程的结构](#2)

<h2 id="1">1.概述</h2>
&emsp;&emsp; 协程是以状态机的形式运行在线程池中的，通过resumeWith回调的方式来实现非阻塞。以Future为例，异步任务在Future.get()方法执行时，
会使用while(true)自旋来判断异步任务是否已经执行完毕，Future.get()的这种方式本质上是同步阻塞，这种方式会降低CPU的执行效率。Future.get()可以
通过callback回调的方式来实现非阻塞，例如ForkJoinPool。但是callback多层嵌套会容易导致代码结构混乱，Kotlin的协程本质上是提供了一种简洁的异步非阻塞编程
的方式。

    // FutureTask中Future.get()方法的具体实现如下
    public V get() throws InterruptedException, ExecutionException {
            int s = state;
            if (s <= COMPLETING)
                s = awaitDone(false, 0L);
            return report(s);
    }
    
    // 这里实际上是自旋阻塞等待异步任务执行完毕
    private int awaitDone(boolean timed, long nanos)
            throws InterruptedException {
            final long deadline = timed ? System.nanoTime() + nanos : 0L;
            WaitNode q = null;
            boolean queued = false;
            for (;;) {
                if (Thread.interrupted()) {
                    removeWaiter(q);
                    throw new InterruptedException();
                }
    ......
    
<h2 id="1">1.协程的结构</h2>
&emsp;&emsp; 协程分为：CoroutineScope、CoroutineContent、CoroutineDispatcher、Job。

### CoroutineScope
&emsp;&emsp; CoroutineScope是协程作用域，它里面只包含一个CoroutineContent。协程作用域在所有已启动子协程执行完毕之前不会结束。

### CoroutineContext
&emsp;&emsp; CoroutineContext是协程上下文，用来放置协程的具体元素集合，通过Key来区分。

### CoroutineDispatcher
&emsp;&emsp; CoroutineDispatcher是协程调度器，用来确定线程池或者线程。协程调度器有四种：Default、IO、Main、Unconfined。Main主要用于安卓中，
Unconfined会指定协程在当前线程中执行。如果不指定协程调度器，那么默认为Dispatchers.Default。

### Job
&emsp;&emsp; Job是具体的协程任务，Job可以取消并且具有生命周期。Deferred和CompletableJob都是Job的子类。

### CoroutineScope.launch
&emsp;&emsp; launch函数本质上是创建了一个新的协程，并执行了dispatcher.dispatch函数。在ExperimentalCoroutineDispatcher调度器中，
调度器把协程添加到了任务队列中。因此launch函数并不会阻塞当前线程，类似于ExecutorService.submit。

### runBlocking
&emsp;&emsp; runBlocking是在提交一个协程后阻塞当前线程，直到协程结束。

### CoroutineScope.async
&emsp;&emsp; async跟launch执行的内容一致，唯一的区别是async执行的block是带返回值的，async返回的结果不是Job而是Deferred<T>。

