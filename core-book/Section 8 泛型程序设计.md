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

如果想知道编译器对一个泛型方法调用最终推断出哪种类型， 可以有目的地引入一个错误，并研究所产生的错误消息。例如调用 `ArrayAlg.getMiddle(“ Hello”，0，null);` 将会得到一个错误报告:

```
found:
java.1ang.0bject&java.io.Seriallzable&java.lang.Comparable<? extends java.1ang.0bject&java.io.Seriallzable&java.lang.Comparable<?>>
```

大致的意思是: 可以将结果赋值给 Object、Serialiable 或 Comparable。



### 8.4 类型变量的限定

类或方法需要对类型变量加以约束，下面是一个典型的例子，我们要计算数组中的最小元素:

```java
class ArrayAlg{
  public static <T> T min(T[] a){ // almost correct
    if (a null || a.length = 0) return null;
    T smallest = a[0];
    for (int i = 1; i < a.length; i++)
      if (smallest.compareTo(a[i]) > 0) smallest = a[i];
    return smallest
  }
}
```

min方法的代码内部中，变量smallest类型为T，这意味着它可以是任何一个类的对象。 **怎么才能保证 T 所属的类有 compareTo 方法呢?** 

解决这个问题的方案是将 T 限制为实现了 Comparable 接口 ，可以通过对类型变量 T 设置限定(bound) 实现这一点:

```
public static <T extends Comparable> T min(T[] a) . . .
```

实际上 Comparable 接口本身就是一个泛型类型。目前， 我们忽略其复杂性以及编译器产生的警告。 第 8.8 节讨论了如何在 Comparable 接口中适当地使用类型参数。现在， 泛型的 min 方法只能被实现了 Comparable 接口的类(如 String、 LocalDate 等)的数组调用。 由于 Rectangle 类没有实现 Comparable 接口， 所以调用 min 将会产生一个编译错误。

> 在 Java 的继承中， 可以根据需要拥有多个**接口**超类型， 但限定中**至多有一个类**。 如果用 一个类作为限定， 它必须是限定列表中的第一个。



### 8.5 泛型代码和虚拟机

虚拟机没有泛型类型对象，所有对象都属于普通类。

#### 8.5.1 类型擦除

无论何时定义一个泛型类型， 都自动提供了一个相应的**原始类型(raw type)**。原始类型的名字就是删去类型参数后的泛型类型名。 **擦除 ( erased ) 类型变量 , 并替换为限定类型 (无限定的变量用 Object)。**

**例如， Pair<T> 的原始类型如下所示:**

```java
public class Pair
{
 	private Object first;
  private Object second;
  
 	public Pair(Object first, Object second){
    this.first = first; 
    this.second = second;
  }
  public Object getFirst() {return first;} 
  public Object getSecond() {return second;}
  
  public void setFirst(Object newValue) { first = newValue; }
	public void setSecond(Object newValue) { second = newValue; }
}
```

因为 T 是一个无限定的变量， 所以直接用 Object 替换 -> 结果是一个普通的类。在程序中可以包含不同类型的 Pair, 例如，`Pair<String>` 或 `Pair<LocalDate>`，而擦除类型后就变成原始的 Pair 类型了。

> C++ 注释: 就这点而言， Java 泛型与 C++ 模板有很大的区别。C++ 中每个模板的实例化产生不同的类型，这一现象称为“ 模板代码膨账”。Java不存在这个问题的困扰。

**原始类型用第一个限定的类型变量来替换， 如果没有给定限定就用 Object 替换。**例如， 类 Pair<T> 中的类型变量没有显式的限定， 因此， 原始类型用 Object 替换 T。

 假定声明了一个不同的类型:

```java
public class Interval <T extends Comparable & Serializable〉 implements Serializable{
  private T lower; 
  private T upper;
	...
  public Interval(T first, T second){
    if (first.compareTo(second) <= 0) { lower = first; upper = second;}
    else {lower = second; upper = first; } 
  }
}
```

原始类型 Interval 如下所示:

```java
public class Interval implements Serializable {
	private Comparable lower; 
  private Comparable upper;
	public Interval (Comparable first , Comparable second) { . . . } 
}
```

> class Interval<T extends Serializable & Comparable> 会发生什么。如果这样做，原始类型用Serializable替换T, 而编译器在必要时要向 Comparable 插入强制类型转换。 为了提高效率， 应该将标签(tagging) 接口 (即没有方法的接口)放在边界列表的末尾。



#### 8.5.2 翻译泛型表达式

**当程序调用泛型方法时，如果擦除返回类型，编译器插入强制类型转换。** 例如，下面这个语句序列:

```java
Pair<Employee> buddies = ...; 
Employee buddy = buddies.getFirst();
```

**擦除 getFirst 的返回类型后将返回 Object 类型。**编译器自动插入 Employee 的强制类型转换，也就是说编译器把这个方法调用翻译为两条虚拟机指令:

+ 对原始方法 Pair.getFirst 的调用。
+ 将返回的 Object 类型强制转换为 Employee 类型。



#### 8.5.3 翻译泛型方法

类型擦除也会出现在泛型方法中。 程序员通常认为下述的泛型方法：

```java
public static <T extends Comparable> T min(T[] a)
```

是一个完整的方法族， 而擦除类型之后， 只剩下一个方法:

```java
public static Comparable min(Comparable[] a)
```

类型参数 T 被擦除，只留下了限定类型 Comparable。方法的擦除带来了两个复杂问题，如下：

```java
class Datelnterval extends Pair<LocalDate>{
  public void setSecond(LocalDate second){
    if(second.compareTo(getFirst() >= 0))
      super.setSecond(second);
  }
}
```

**一个日期区间是一对 LocalDate 对象， 并且需要覆盖这个方法来确保第二个值永远不小于第一个值。 这个类擦除后变成:**

```java
class Datelnterval extends Pair{// after erasure
  public void setSecond(LocalDate second){...}
}
```

令人感到奇怪的是， 存在另一个从 Pair 继承的 setSecond 方法，即

```java
public void setSecond(Object second)
```

这显然是一个不同的方法， 因为它有一个不同类型的参数--Object，而不是 LocalDate。然而， 不应该不一样。 考虑下面的语句序列:

```java
Datelnterval interval = new Datelnterval(...);
Pair<Loca1Date> pair = interval; // OK assignment to superclass 
pair.setSecond(aDate) ;
```

这里， 希望对 setSecond 的调用具有多态性， 并调用最合适的那个方法。 由于 pair 引用 Datelnterval 对象， 所以应该调用 Datelnterval.setSecond。**问题在于类型擦除与多态发生了冲突。**要解决这个问题， 就需要**编译器在 Datelnterval 类中生成一个桥方法(bridge method):**

```java
public void setSecond(Object second) { 
  setSecond((Date) second);
} 
```

要想了解它的工作过程， 请仔细地跟踪下列语句的执行:

```
pair.setSecond(aDate)
```

变量 pair 已经声明为类型 `Pair<LocalDate> `, 并且这个类型只有一个简单的方法叫setSecond， 即setSecond(Object)。 虚拟机用 pair 引用的对象调用这个方法。 这个对象是 Datelnterval 类型的， 因而将会调用 Datelnterval.setSecond(Object) 方法，这个方法是合成的桥方法。

**桥方法可能会变得十分奇怪，假设 Datelnterval 方法也覆盖了 getSecond 方法:**

```java
class Datelnterval extends Pair<LocalDate>{
	public LocalDate getSecond() { return (Date) super.getSecond().clone(); }
	...
}
```

在 Datelnterval 类中， 有两个 getSecond 方法:

```java
LocalDate getSecond() // defined in Datelnterval
Object getSecond() // overrides the method defined in Pair to call the first method
```

不能这样编写 Java 代码，它们都没有参数。 **但是在虚拟机中， 用参数类型和返回类型确定一个方法。 因此，编译器可能产生两个仅返回类型不同的方法字节码， 虚拟机能够正确地处理这一情况。**

总之， 需要记住有关 Java 泛型转换的事实：

+ 虚拟机中没有泛型， 只有普通的类和方法。 
+ 所有的类型参数都用它们的限定类型替换。 
+ 桥方法被合成来保持多态。 
+ 为保持类型安全性， 必要时插人强制类型转换。



#### 8.5.4 调用遗留代码

设计 Java 泛型类型时，主要目标是允许泛型代码和遗留代码之间能够互操作。 下面看一个具体的示例。 要想设置一个 JSlider 标签， 可以使用方法:

```java
void setLabelTable(Dictionary table)
```

在这里，Dictionary 是一个原始类型， 因为实现 JSlider 类时 Java 中还不存在泛型。不过，填充字典时， 要使用泛型类型。

```java
Dictionary<Integer, Component> labelTable = new Hashtable<>();
labelTable.put(0, new JLabel(new Imagelcon("nine.gif")));
labelTable.put(20, new JLabel(new Imagelcon("ten.gif")));
```

将Dictionary<Integer, Component>对象传递给setLabelTable时，编译器会发出一个警告：

```java
slider.setLabelTable(labelTable); // warning
```

毕竟， 编译器无法确定 setLabelTable 可能会对 Dictionary 对象做什么操作。 这个方法可 能会用字符串替换所有的key。这就打破了key类型为整型(Integer) 的承诺，未来的操作有可能会产生强制类型转换的异常。可以忽略这个警告。

现在，看一个相反的情形，由一个遗留的类得到一个原始类型的对象。 可以将它赋给一个参数化的类型变量，这样做会看到一个警告。 例如:

```java
Dictionary<Integer, Components> labelTable = slider.getLabelTableO; // Warning
```

再看一看警告， 确保标签表已经包含了 Integer 和 Component 对象。最差的情况就是程序抛出一个异常。



### 8.6 约束与局限性