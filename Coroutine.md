## 协程学习笔记

* [1.概述](#1)
* [2.协程的结构](#2)
* [3.协程执行过程](#3)

<h2 id="1">1.概述</h2>
&emsp;&emsp; 协程是以状态机的形式运行在线程池中的，通过resumeWith回调的方式来实现非阻塞。以Future为例，异步任务在Future.get()方法执行时，
会判断异步任务是否已经执行完毕，并阻塞等待结果产生，Future.get()的这种方式本质上是同步阻塞，这种方式会降低CPU的执行效率。Future.get()可以
通过callback回调的方式来实现非阻塞，例如ForkJoinPool。但是callback多层嵌套会容易导致代码结构混乱，Kotlin的协程本质上是提供了一种简洁的异步非阻塞编程
的方式。

    // FutureTask中Future.get()方法的具体实现如下
    public V get() throws InterruptedException, ExecutionException {
            int s = state;
            if (s <= COMPLETING)
                s = awaitDone(false, 0L);
            return report(s);
    }
    
    // 这里实际上是阻塞等待异步任务执行完毕
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
    
### 挂起函数
&emsp;&emsp; 协程使用suspend来标记函数为挂起函数，suspend并不是协程，只是用来标记这个函数可能会被挂起。被suspend修饰的函数，
被其他suspend修饰的函数调用，这是因为每一个suspend修饰的函数，最终编译过后，函数的参数列表中会有一个Continuation。suspend函数之间的调用，
会传递Continuation。

### delay
&emsp;&emsp; delay不会导致线程阻塞，因为delay并没有执行Thread.sleep，而是将延迟队列中添加一条DelayedTask，然后从队列中拉取任务，
判断任务是否已经到期，这种延迟方式不会导致线程阻塞。

    private fun scheduleImpl(now: Long, delayedTask: DelayedTask): Int {
            if (isCompleted) return SCHEDULE_COMPLETED
            val delayedQueue = _delayed.value ?: run {
                _delayed.compareAndSet(null, DelayedTaskQueue(now))
                _delayed.value!!
            }
            return delayedTask.scheduleTask(now, delayedQueue, this)
        }
    
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

<h2 id="3">3.协程执行过程</h2>
&emsp;&emsp; 协程的启用包括七种方式：1，CoroutineScope.launch。2，CoroutineScope.async。3，CoroutineScope.broadcast。
4，CoroutineScope.produce。5，CoroutineScope.flowProduce。6，runBlocking。7，CoroutineScope.actor。
对于runBlocking来说，会使用当前线程创建BlockingEventLoop(Thread.currentThread()) CoroutineDispatcher，并且会阻塞当前线程，
直到runBlocking中所有的任务包括子协程都执行完毕。
                        
            CoroutineContext.plus(context)：一般执行此类操作时，originalContext和context都是Element，每个Element都有一个key。
                CoroutineContext接口默认plus(context)方法：
                    1，如果originalContext和context的key是同一个key，那么返回context，用这个新的context替代之前的originalContext。
                    2，originalContext和context不是同一个key，且originalContxet.key不是ContinuationInterceptor，
                        那么返回CombinedContext(originalContext，context)。
                    3，如果originalContext和context不是同一个key，且originalContext.key为ContinuationInterceptor，
                        那么返回CombinedContext(context, originalContext)。
                    4，CombinedContext(left, element)，根据key获取Element时，总是先查询element，再查询left。
                        也就是每次plus操作，将originalContext、context中key为ContinuationInterceptor的放在优先位置。
                
    
                     先构建CoroutineScope，CoroutineScope只有一个CoroutineContext属性
                                                |
                                                V
                    执行CoroutineScope.async(CoroutineContext, CoroutineStart, block)
                                                |
                                                V
               根据CoroutineContext参数，执行newCoroutineContext(context)创建新的CoroutineContext
                                                |
                                                V
               执行CoroutineScope.coroutineContext.plus(context)获取CoroutineContext combined
                                                |
                                                V
               如果combined不是Dispatchers.Default,并且不包含ContinuationInterceptor Key --------------
                                                |                                                   |
                                                V                                                   |
                  获取Dispatchers.Default，即DefaultScheduler(CoroutineDispatcher)                    |
                                                |                                                   |
                                                V                                                   V
               执行combined(CoroutineContext).plus(Dispatchers.Default)获取CoroutineContext   将combined返回
                                                |                                                   |
                                                V <-------------------------------------------------
                 newCoroutineContext(context)执行完毕，获取到新的CoroutineContext newContext
                                                |
                                                V
                         创建DeferredCoroutine(newContext: DefaultScheduler, true)
                                                |
                                                V
            DeferredCoroutine创建完context属性为CombinedContext(DeferredCoroutine, DefaultScheduler)
                                                |
                                                V
                   执行DeferredCoroutine.start(CoroutineStart, DeferredCoroutine, block)                                                    
                                                |
                                                V
                                   执行cotoutine.initParentJob()
                                                |
                                                V
             执行DeferredCoroutine.parentContext.get(Job)，获取parentContext DefaultScheduler Job对象
                                                |
                                                V
         如果parentContext Job为null，设置当前DeferredCoroutine.parentHandle为NonDisposableHandle，return
                                                |
                                                V
                     执行CoroutineStart.invoke(block, receiver, DeferredCoroutine)
                                                |
                                                V
         执行block.startCoroutineCancellable(receriver: DeferredCoroutine, completion: DeferredCoroutine)
                                                |
                                                V
           执行block.createCoroutineUnintercepted(DeferredCoroutine, DeferredCoroutine)创建SuspendLambda
                                                |
                                                V
                                     执行SuspendLambda.intercepted()
                                                |
                                                V
            执行SuspendLambda.context.get(ContinuationInterceptor).interceptContinuation(SuspendLambda)
                                                |
                                                V
             执行DefaultScheduler.interceptContinuation(SuspendLambda)将结果赋值给SuspendLambda.intercepted
                                                |
                                                V
                          创建DispatchedCotinuation(DefaultScheduler, SuspendLambda)
                                                |
                                                V
                      执行DispatchedCotinuation.resumeCancellableWith(Result(Unit), null)
                                                |
                                                V
                     执行DispatchedCotinuation.resumeCancellableWith(Result, onCancellation)
                                                |
                                                V
                        如果DispatchedCotinuation.dispatcher.isDispatchNeeded(context)
                            只有Unconfined CoroutineDispatcher 为false，默认为true
                                                |
                                                V
                   执行DispatchedContinuation.dispatcher(DefaultScheduler).dispatch(context, this)
                                                |
                                                V
                          执行CoroutineScheduler.dispatch(block: DispatchedContinuation)
                                                |
                                                V
                           执行完当前Continuation后，执行Continuation.resumeWith(result)。                      
                           每调用一个suspend方法，都会创建一个Continuation，每个Continuation中，
                           resumeWith(result)方法，包含了协程执行完毕后，恢复现场继续执行的逻辑。
                                                |
                                                V
                            返回coroutine DeferredCoroutine作为Deferred<T>
                                                                                                                                                     
