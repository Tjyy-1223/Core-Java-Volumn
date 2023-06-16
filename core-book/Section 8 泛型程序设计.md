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

**泛型类(generic class) 就是具有一个或多个类型变量的类。**本章使用一个简单的 Pair 类作为例子，我们只关注泛型，而不会为数据存储的细节烦恼。

```java
public class Pair<T>{
  private T first; 
  private T second;
  
	public Pair() { first = null ; second = null ; }
	public Pair(T first, T second) { this.first = first; this.second = second; }
  
	public T getFirst() { return first; } 
  public T getSecond() { return second; }
  
	public void setFirst(T newValue) { first = newValue; }
	public void setSecond(T newValue) { second = newValue; }
}
```

**Pair类引入了一个类型变量T**，用尖括号括起来，并放在类名的后面。泛型类可以有多个类型变量例如，可以定义 Pair 类，其中第一个域和第二个域使用不同的类型:

```java
public class Pair<T,U> { . . . }
```

类定义中的类型变量指定方法的返回类型以及域和局部变量的类型。 例如:

```java
private T first; // uses the type variable
```

用具体的类型替换类型变量就可以实例化泛型类型， 例如:

```java
Pair<String>
void setFirst(String)
...
```

> 从表面上看， Java 的泛型类类似于 C++ 的模板类。唯一明显的不同是 Java 没有专用的 template 关键字。 但是， 在本章中将会看到两种机制有着本质的区别。



### 8.3 泛型方法

前面已经介绍了如何定义一个泛型类，实际上， 还可以定义一个带有类型参数的简单方法。

```java
class ArrayAlg{
  public static <T> T getMiddle(T...a){
    return a[a.length / 2];
  }
}
```

泛型方法可以定义在普通类中， 也可以定义在泛型类中。然而这是一个泛型方法，可以从尖括号和类型变量看出这一点。**注意，类型变量放在修饰符(这里是 public static) 的后面，返回类型的前面。**

当调用一个泛型方法时，在方法名前的尖括号中放人具体的类型:

```java
String middle = ArrayAlg.<String>getMiddle("John", "Q.", "Public");
```

在大多数情况下， 方法调用中可以省略` <String> `类型参数。编译器有足够的信息能够推断出所调用的方法，它用 names 的类型与泛型类型 T[ ] 进行匹配并推断出 T 一定是 String。也就是说， 可以调用

```java
String middle = ArrayAlg.getMiddle("John", "Q.", "Public");
```

几乎在大多数情况下，对于泛型方法的类型引用没有问题。 偶尔编译器也会提示错误，此时需要解译错误报告:

```java
double middle = ArrayAlg.getMiddle(3.14, 1729, 0);
```

错误消息会以晦涩的方式指出：解释这句代码有两种方法，而且这两种方法都是合法的。

>  编译器将会自动打包参数为 1 个 Double 和 2 个 Integer 对象， 而后寻找这些类的共同超类型。事实上可以找到 2 个这样的超类型: Number 和 Comparable 接口。在这种情况下，可以采取的补救措施是将所有的参数写为 double 值。

如果想知道编译器对一个泛型方法调用最终推断出哪种类型， 可以有目的地引入一个错误，并研究所产生的错误消息。例如调用 `ArrayAlg.getMiddle(“ Hello”，0，null);` 将会得到一个措误报告:

```
found:
java.1ang.0bject&java.io.Seriallzable&java.lang.Comparable<? extends java.1ang.0bject&java.io.Seriallzable&java.lang.Comparable<?>>
```

大致的意思是: 可以将结果赋值给 Object、Serialiable 或 Comparable。



### 8.4 类型变量的限定