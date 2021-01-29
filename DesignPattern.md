## 设计模式

* [1，创建型模式](#1)
* [1.1，单例模式](#1.1)
* [1.2，简单工厂模式](#1.2)
* [1.3，抽象工厂模式](#1.3)
* [1.4，建造者模式](#1.4)
* [1.5，原型模式](#1.5)
* [2，行为型模式](#2)
* [2.1，访问者模式](#2.1)
* [2.2，模版模式](#2.2)
* [2.3，策略模式](#2.3)
* [2.4，状态模式](#2.4)
* [2.5，观察者模式](#2.5)
* [2.6，备忘录模式](#2.6)
* [2.7，中介者模式](#2.7)
* [2.8，迭代器模式](#2.8)
* [2.9，解释器模式](#2.9)
* [2.10，命令模式](#2.10)
* [2.11，责任链模式](#2.11)
* [3，结构型模式](#3)
* [3.1，适配器模式](#3.1)
* [3.2，桥接模式](#3.2)
* [3.3，组合模式](#3.3)
* [3.4，装饰模式](#3.4)
* [3.5，外观模式](#3.5)
* [3.6，享元模式](#3.6)
* [3.7，代理模式](#3.7)

<h2 id="1">1，创建型模式</h2>
&emsp;&emsp; 创建型模式分为：单例模式、工厂模式、抽象工厂模式、建造者模式、原型模式。

<h3 id="1.1">1.1，单例模式</h3>
&emsp;&emsp; singleton pattern，一个类只能有一个实例，提供一个全局的访问点。
    
    // 懒汉模式，不会进行初始化，lazy init。
    public class SingletonLazy {
        private static volatile SingletonLazy instance;
    
        public static SingletonLazy getInstance() {
            if (instance == null) {
                synchronized (SingletonLazy.class) {
                    if (instance == null) {
                        instance = new SingletonLazy();
                    }
                }
            }
            return instance;
        }
    }
    
    // 饿汉模式
    public class SingletonHungry {
        private static SingletonLazy instance = new SingletonLazy();
    
        public SingletonLazy getInstance() {
            return instance;
        }
    }
    
<h3 id="1.2">1.2，简单工厂模式</h3>
&emsp;&emsp; factory pattern，一个工厂类根据传入的参数决定创建出哪一种类的实例。
    
    public class PhoneFactory {
        Phone produce(PhoneType phoneType) {
            switch (phoneType) {
                case IPHONE:
                    return new Iphone();
                case ANDROID:
                    return new AndroidPhone();
                default:
                    return null;
            }
        }
    }
    
<h3 id="1.3">1.3，抽象工厂模式</h3>
&emsp;&emsp; abstract factory pattern，定义一个创建对象的接口，让子类决定实例化哪个类。
    
    public class PhoneFactoryUsage {
        Phone produce(PhoneType phoneType) {
            PhoneFactory phoneFactory;
            switch (phoneType) {
                case ANDROID:
                    phoneFactory = new AndroidPhoneFactory();
                    break;
                case IPHONE:
                    phoneFactory = new IphoneFactory();
                    break;
                default:
                    phoneFactory = null;
            }
            return phoneFactory.produce();
        }
    
        public static void main(String[] args) {
            PhoneFactoryUsage usage = new PhoneFactoryUsage();
            usage.produce(PhoneType.ANDROID).printPhoneInfo();
            usage.produce(PhoneType.IPHONE).printPhoneInfo();
        }
    }
    
<h3 id="1.4">1.4，建造者模式</h3>
&emsp;&emsp; builder pattern，使用多个简单的对象一步一步构建成一个复杂的对象。

    public class CarBuilderUsage {
        public static void main(String[] args) {
            CarABuilder carABuilder = new CarABuilder("red");
            CarBBuilder carBBuilder = new CarBBuilder("blue");
            CarDirector carDirector = new CarDirector();
    
            carDirector.build(carABuilder);
            carDirector.build(carBBuilder);
    
            Car carA = carABuilder.getCar();
            Car carB = carBBuilder.getCar();
    
            carA.printInfo();
            carB.printInfo();
    
            // 另一种builder实现方式
            Car carC = Car.builder().name("Car C").color("green").price(30000).build();
            carC.printInfo();
        }
    }

<h3 id="1.5">1.5，原型模式</h3>
&emsp;&emsp; prototype pattern，通过复制现有的实例来创建新的实例。
    
    public class PrototypeUsage {
        public static void main(String[] args) {
            Prototype prototype1 = new Prototype1();
            prototype1.setName("prototype 1");
            Prototype prototype2 = new Prototype2();
            prototype2.setName("prototype 2");
    
            PrototypeManager.setPrototype("p1", prototype1);
            PrototypeManager.setPrototype("p2", prototype2);
    
            System.out.println(PrototypeManager.getPrototype("p1").clone().getName());
            System.out.println(PrototypeManager.getPrototype("p2").clone().getName());
        }
    }
    
<h2 id="2">2，行为型模式</h2>
&emsp;&emsp; 行为型模式有：访问者模式、模版模式、策略模式、状态模式、观察者模式、备忘录模式、中介者模式、迭代器模式、解释器模式、命令模式、责任链模式。

<h3 id="2.1">2.1，访问者模式</h3>
&emsp;&emsp; visitor pattern，在不改变数据结构的前提下，增加作用于一组对象元素的新功能。

    public class VisitorUsage {
        public static void main(String[] args) {
            Book book = new Book("Story book");
            ObjectStructure objectStructure = new ObjectStructure();
            objectStructure.addElement(book);
    
            CommonVisitor commonVisitor = new CommonVisitor();
            objectStructure.accept(commonVisitor);
        }
    }
    
<h3 id="2.2">2.2，模版模式</h3>
&emsp;&emsp; template pattern，定义一个算法结构，而将一些步骤延迟到子类实现。抽象类公开定义了执行它的方法的模版，子类可以重写方法，
但调用方式是在抽象类中定义。

    public class TemplateUsage {
        public static void main(String[] args) {
            BuyTemplate offlineBuyPhone = new OfflineBuyPhone();
            BuyTemplate onlineBuyComputer = new OnlineBuyComputer();
    
            offlineBuyPhone.buy();
            onlineBuyComputer.buy();
        }
    }
    
<h3 id="2.3">2.3，策略模式</h3>
&emsp;&emsp; strategy pattern，一个类的行为或者算法可以在运行时更改。

    public class StrategyUsage {
        public static void main(String[] args) {
            Context contextAdd = new Context(new OperationAdd());
            Context contextSubtract = new Context(new OperationSubtract());
    
            System.out.println(contextAdd.execute(1, 1));
            System.out.println(contextSubtract.execute(1, 1));
        }
    }
    
<h3 id="2.4">2.4，状态模式</h3>
&emsp;&emsp; state pattern，类的行为是基于它的状态改变的。
    
    public class StateUsage {
        public static void main(String[] args) {
            Action action = new Action();
            StartState startState = new StartState();
            EndState endState = new EndState();
            startState.doAction(action);
            System.out.println(action.getStateVal());
            endState.doAction(action);
            System.out.println(action.getStateVal());
        }
    }
    
<h3 id="2.5">2.5，观察者模式</h3>
&emsp;&emsp; observer pattern，当对象之间存在一对多的关系时，使用观察者模式。例如当一个对象修改时，通知所有依赖它的对象。

    public class ObserverUsage {
        public static void main(String[] args) {
            DataSubject dataSubject = new DataSubject();
            DataObserver dataModifyObserver = new DataModifyObserver(dataSubject);
            DataObserver dataUpdateObserver = new DataUpdateObserver(dataSubject);
    
            dataSubject.setVal(2);
        }
    }
    
<h3 id="2.6">2.6，备忘录模式</h3>
&emsp;&emsp; memento pattern，保存一个对象的某个状态，便于在适当的时候恢复对象。

    public class MementoUsage {
        public static void main(String[] args) {
            MementoHolder mementoHolder = new MementoHolder();
            Originator originator = new Originator();
    
            originator.setState("1");
            mementoHolder.addMemento(originator.buildMemento());
            originator.setState("2");
            mementoHolder.addMemento(originator.buildMemento());
    
            System.out.println(originator.getStateFromMemento(mementoHolder.get(0)));
        }
    }
    
<h3 id="2.7">2.7，中介者模式</h3>
&emsp;&emsp; mediator pattern，用来降低多个对象和类之间的通信复杂性。这种模式提供了一个中介类，该类通常处理不同类之间的通信。

    public class MediatorUsage {
        public static void main(String[] args) {
            User user1 = new User();
            user1.setName("User 1");
    
            User user2 = new User();
            user2.setName("User 2");
    
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.sendMessage(user1, "Hello");
            chatRoom.sendMessage(user2, "world");
        }
    }
    
<h3 id="2.8">2.8，迭代器模式</h3>
&emsp;&emsp; iterator pattern，这种模式用于访问集合对象的元素，不需要知道集合对象的底层表示。
    
    public class IteratorUsage {
        public static void main(String[] args) {
            NameContainer nameContainer = new NameContainer();
            nameContainer.addName("name 1");
            nameContainer.addName("name 2");
            nameContainer.addName("name 3");
    
            ItemIterator<String> itemIterator = nameContainer.iterator();
            while (itemIterator.hasNext()) {
                System.out.println(itemIterator.next());
            }
        }
    }

<h3 id="2.9">2.9，解释器模式</h3>
&emsp;&emsp; interceptor pattern，提供了评估语言的语法或表达式的方式。这种模式实现了一个表达式接口，该接口解释一个特定的上下文。
    
    public class InterceptorUsage {
        public static void main(String[] args) {
            CheckDataInterceptor checkDataInterceptor = new CheckDataInterceptor();
            checkDataInterceptor.addInterceptor(new CheckNullInterceptor());
            checkDataInterceptor.addInterceptor(new CheckBlankInterceptor());
    
            checkDataInterceptor.intercept("aaa");
        }
    }
    
<h3 id="2.10">2.10，命令模式</h3>
&emsp;&emsp; command pattern，命令模式是数据驱动的设计模式，请求以命令的形式包裹在对象中，并传给调用对象。
调用对象寻找可以处理该命令的合适的对象，并把该命令传给相应的对象，该对象执行命令。

    public class OrderUsage {
        public static void main(String[] args) {
            Stock stock = new Stock();
            Broker broker = new Broker();
            broker.addOrder(new BuyOrder(stock));
            broker.addOrder(new SellOrder(stock));
            broker.exec();
        }
    }
    
<h3 id="2.11">2.11，责任链模式</h3>
&emsp;&emsp; chain of responsibility pattern，为请求创建了一个接收者对象的链。

    public class ChainUsage {
        public static void main(String[] args) {
            OuterChain outerChain = new OuterChain(new InnerChain(null));
            outerChain.proceed();
        }
    }

<h2 id="3">3，结构型模式</h2>
&emsp;&emsp; 结构型模式包含：适配器模式，桥接模式、组合模式、装饰模式、外观模式、享元模式、代理模式。

<h3 id="3.1">3.1，适配器模式</h3>
&emsp;&emsp; adapter pattern，作为两个不兼容的接口之间的桥梁。结合了两个独立接口的功能。

    public class AdapterUsage {
        public static void main(String[] args) {
            MediaAdapter mediaAdapter = new MediaAdapter();
            mediaAdapter.play("a.mp3");
            mediaAdapter.play("b.mp4");
        }
    }

<h3 id="3.2">3.2，桥接模式</h3>
&emsp;&emsp; bridge pattern，用于把抽象化与实现化解耦，使得二者可以独立变化。

    public class BridgeUsage {
        public static void main(String[] args) {
            Circle circle = new Circle(new RedCircle());
            circle.draw();
        }
    }
    
<h3 id="3.3">3.3，组合模式</h3>
&emsp;&emsp; composite pattern，组合模式把一组相似的对象当作一个单一的对象。

    public class CompositeUsage {
        public static void main(String[] args) {
            CompositeStreaming compositeStreaming = new CompositeStreaming(new ChunkedStreaming(new PlainStreaming()));
            compositeStreaming.display();
        }
    }
    
<h3 id="3.4">3.4，装饰模式</h3>
&emsp;&emsp; decorator pattern，允许向一个现有的对象添加新的功能，同时又不改变其结构。
    
    public class DecoratorUsage {
        public static void main(String[] args) {
            ReaderApi bufferedReader = new BufferedInputReader(new InputReader());
            bufferedReader.read();
        }
    }

<h3 id="3.5">3.5，外观模式</h3>
&emsp;&emsp; facade pattern，隐藏系统的复杂性，并向客户端提供了一个客户端可以访问系统的接口。为子系统中的一组接口提供一个一致的界面。

    public class FacadeUsage {
        public static void main(String[] args) {
            ShapeFacade shapeFacade = new ShapeFacade();
            shapeFacade.drawCircle();
            shapeFacade.drawRectangle();
        }
    }

<h3 id="3.6">3.6，享元模式</h3>
&emsp;&emsp; flyweight pattern，主要用于减少创建对象的数量，以减少内存占用和提高性能。运用共享技术有效地支持大量细粒度的对象。

    public class FlyweightUsage {
        public static void main(String[] args) {
            ShapeFlyweightFactory.getShape("red").draw();
            ShapeFlyweightFactory.getShape("blue").draw();
        }
    }
    
<h3 id="3.7">3.7，代理模式</h3>
&emsp;&emsp; proxy pattern，一个类代表另一个类的功能。为其他对象提供一种代理以控制对这个对象的访问。

    public class ProxyUsage {
        public static void main(String[] args) {
            DisplayApi displayProxy = new DisplayProxy();
            displayProxy.display();
        }
    }