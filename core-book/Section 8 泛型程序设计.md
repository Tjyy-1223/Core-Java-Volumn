## Section 8 泛型程序设计

[TOC]

### 8.1 为什么要使用泛型程序设计

**泛型程序设计(Generic programming) 意味着编写的代码可以被很多不同类型的对象所重用**，例如：我们并不希望为聚集 String 和 File 对象分别设计不同的类，以及 ArrayList 类可以聚集任何类型的对象。

#### 8.1.1 类型参数的好处

在 Java 中增加范型类之前， 泛型程序设计是用**继承**实现的。ArrayList 类只维护一个 Object 引用的数组:

```java
public class ArrayList // before generic classes
{
  private Object[] elementData;
  ...
  public Object get(int i){...}
  public void add(Object o){...}
}
```

这种方法有两个问题:

+  当获取一个值时必须进行强制类型转换

```java
ArrayList files = new ArrayList();
...
String filename = (String) files.get(0);
```

+ 这里没有错误检査，可以向数组列表中添加任何类的对象。对于下面的调用， 编译和运行都不会出错，然而如果将 get 的结果强制类型 转换为 String 类型， 就会产生一个错误。

```java
files.add(new File(...)); 
```

**泛型提供了一个解决方案: 类型参数 (type parameters )。** ArrayList 类有一个类型参数用来指示元素的类型:

```java
ArrayList<String> files = new ArrayList<String>();
```

这使得代码具有更好的可读性，可以知道数组列表中包含的是 String 对象。

> 在 Java SE 7 及以后的版本中， 构造函数中可以省略泛型类型:  ArrayList<String> files = new ArrayList();

编译器也可以很好地利用这个信息。 当调用 get 的时候，不需要进行强制类型转换，编译器就知道返回值类型为 String， 而不是 Object:

```java
String filename = files.get(0);
```

编译器还知道 ArrayList <String> 中 add 方法有一个类型为 String 的参数，这将比使用 Object 类型的参数安全一些。**编译器可以进行检査，避免插人错误类型的对象。**例如:

```java
files.add(new File("...")); // can only add String objects to an ArrayList<String>
```

**类型参数的魅力在于: 使得程序具有更好的可读性和安全性。**



#### 8.1.2 谁想成为泛型程序员

使用像 ArrayList 的泛型类很容易。大多数 Java 程序员都使用 `ArrayList<String> `这样的类型。但是， 实现一个泛型类并没有那么容易。

> 对于类型参数，使用这段代码的程序员可能想要内置(plugin) 所有的类。他们希望在没有过多的限制以及混乱的错误消息的状态下， 做所有的事情。 因此，一个泛型程序员的任务就是预测出所用类的未来可能有的所有用途。

下面是标准类库的设计者们产生争议的一个典型问题：

AirayList 类有一个方法 addAll 用来添加另一个集合的全部元素。 程序员可能想要将 `ArrayList<Manager>` 中的所有元素添加到 `ArrayList<Employee>` 中去。如果只能允许前一个调用，而不能允许后一个调用呢？Java语言的设计者发明了一个具有独创性的新概念，通配符类型 (wildcard type)，解决了这个问题。通配符类型非常抽象， 然而， 它们能让库的构建者编写出尽可能灵活的方法。

泛型程序设计划分为 3 个能力级别：

+ 基本级别是， 仅仅使用泛型类——典型的是像ArrayList 这样的集合，不必考虑它们的工作方式与原因。
+ 第二个阶段：当把不同的泛型类混合在一起时，或是在与对类型参数代码进行衔接时，可能会看到含混不清的错误消息。 这个阶段需要学习 Java 泛型来系统地解决这些问题， 而不要胡乱地猜测。 
+ 最终级别是想要实现自己的泛型类与泛型方法。

应用程序员很可能不喜欢编写太多的泛型代码。JDK 开发人员已经做出了很大的努力，**为所有的集合类提供了类型参数**。 凭经验来说， 那些原本涉及许多来自通用类型(如 Object 或 Comparable 接口)的强制类型转换的代码一定会因使用类型参数而受益。



### 8.2 定义简单泛型类