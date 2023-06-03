## Section 6 接口、lambda 表达式与内部类

### 6.1 接口

#### **6.1.1 接口概念**

- 接口不是类，而是对类的一组需求描述
- 接口中的所有方法自动地属于 `public` 因此，不必提供关键字 `public`；但是在自己的类实现相关接口时，必须声明为`public`，不然编译器会认为该方法为包可见性。
- 接口绝不能含有实例域

假设希望使用 Arrays 类的 sort 方法对 Employee 对象数组进行排序，Employee 类就必须实现 Comparable 接口：

```java
// 1. 将类声明为实现给定的接口 - 使用到了泛型
class Employee implements Comparable<Employee>

// 2. 对接口中的所有方法进行定义
public int compareTo(Employee other){
	return Double.compare(salary, other,salary);
}

// 3. Arrays.sort
Employee[] staff = new Employee[3];
Arrays.sort(staff);
```

**知识点1：**

> 要让一个类使用排序服务必须让它实现 compareTo 方法。 这是理所当然的， 因为要向 sort 方法提供对象的比较方式。 但是为什么不能在 Employee 类直接提供一个 compareTo 方法， 而必须实现 Comparable 接口呢?

原因：Java 是一种**强类型 ( strongly typed ) 语言**，在调用方法的时候， 编译器将会检查这个方法是否存在。如果继承了Comparable接口，则可以确保此对象拥有compareTo 方法。

**知识点2:**

对于任意的 x 和 y, 实现必须能够保证 `sgn(x.compareTo(y)) = -sgn (y.compareTo(x)`；简单地讲，如果调换 compareTo 的参数， 结果的符号也应该调换(而不是实际值)。

假设Manager扩展了Employee，而Employee中实现了`Compareble<Employee>`,如果Manager覆盖了compareTo如果将函数写成下面这样将会带来报错：

```java
class Manager extends Employee{
  public int compareTo(Employee other){
    Manager otherManager = (Manager) other; // NO
    ...
  }
  ...
}
```

所以如果子类比较含义不一样，则需要首先进行检测：`if (getClassO != other.getClassO) throw new ClassCastExceptionO;`;否则如果可以对两个不同类型对象进行比较，则应该在超类中提供了一个compareTo方法，并将其声明为final类型。



####  6.1.2 接口的特性

+ 接口变量必须引用实现了接口的类对象: `Comparable x = new Employee(. . .); `

+ 与可以建立类的继承关系一样， 接口也可以被扩展。这里允许存在多条从具有较高通用 性的接口到较高专用性的接口的链：

```java
// 假设有一个称为 Moveable 的接口:
public interface Moveable{
	void move(double x, double y); 
}

// 以它为基础扩展一个叫做 Powered 的接口:
// 虽然在接口中不能包含实例域或静态方法， 但却可以包含常量
public interface Powered extends Moveable {
	double milesPerCallon();
  double SPEED_LIMIT = 95; // a public static final constant
}

```

+ 尽管每个类只能够拥有一个超类， 但却可以实现多个接口。这就为定义类的行为提供了极大的灵活性。



#### 6.1.3 接口与抽象类

为什么不将 Comparable 直接设计成如下所示的 抽象类:

```java
abstract class Comparable{ // why not?
		public abstract int compareTo(Object other); 
}

```

然后， Employee 类再直接扩展这个抽象类， 并提供 compareTo 方法的实现:

```java
class Employee extends Comparable{ // why not?
	public int compareTo(Object other) {...}
}  
```

**原因：每个类只能扩展于一个类。** Java 的设计者选择了不支持多继承， 其主要原因是多继承会让语言本身变得非常复杂 (如同 C++) ，效率也会降低 。



#### 6.1.4 静态方法

在 Java SE 8 中， 允许在接口中增加静态方法。目前为止， 通常的做法都是将静态方法放在伴随类中；不过在Java SE 8之后的版本中，不再需要为实用工具方法另外提供一个伴随类。

```java
public interface Path{
  public static Path get(String first, String... more){
    return Fi1eSystems.getDefault().getPath(first, more)
  }
  ...
}

```



#### 6.1.5 默认方法

可以为接口方法提供一个默认实现。 必须用 default 修饰符标记这样一个方法。

```java
public interface Comparable<T>{
	default int compareTo(T other){
    return 0;
  } // By default, all elements are the same
}
```

当然， 这并没有太大用处， 因为 Comparable 的每一个实际实现都要覆盖这个方法。

**使用场景1:**

比如希望发生鼠标点击时候得到通知，现在有一个鼠标监听的接口，但是在大多数情况下，仅需要关注其中的一两个事件类型，可以把所有方法声明为默认方法，默认方法什么也不做。

```java
public interface MouseListener {
	default void mousedieked(MouseEvent event){}; 
  default void mousePressed(MouseEvent event){}; 
  default void mouseReleased(MouseEvent event){}; 
  default void mouseEntered(MouseEvent event){}; 
  default void mouseExited(MouseEvent event){};
}
```

这样一来， 实现这个接口的程序员只需要为他们真正关心的事件覆盖相应的监听器。

**使用场景2:**

默认方法可以调用任何其他方法。 例如， Collection 接口可以定义一个便利方法: 这样实现 Collection 的程序员就不用操心实现 isEmpty 方法了。

```java
public interface Collection {
	int size(); // An abstract method
  default boolean isEmpty(){
    return size() == 0;
  }
  ...
}
```



#### 6.1.6 解决默认方法冲突

**如果先在一个接口中将一个方法定义为默认方法， 然后又在超类或另一个接口中定义了同样的方法， 会发生什么情况?** 

+ 超类优先：优先使用超类提供的具体方法（保证默认方法不会影响到之前可以正常工作的代码）
+ 接口冲突：在类中必须覆盖这个方法来解决冲突

> 千万不要让一个默认方法重新定义 Object 类中的某个方法。 例如，不能为 toString 或 equals 定义默认方法， 尽管对于 List 之类的接口这可能很有吸引力， 由于“ 类优先” 规则， 这样的方法绝对无法超越 Object.toString 或 Objects.equals。



### 6.2 接口示例

我们将给出接口的另外一些示例， 可以从中了解接口的实际使用。

#### 6.2.1 接口与回调

回调（callback）指出某个特定事件发生时应该采取的动作。例如我们现在希望需要每隔10s钟打印一条信息，这就需要一个实现ActionListener 接口的类来完成任务，样例如下：

```java
public interface ActionListener{
  void actionPerfonned(ActionEvent event);
}

class TinePrinter implements ActionListener{
  public void actionPerformed(ActionEvent event){
    System.out.println("At the tone, the time is " + new Date());
    Toolkit.getDefaultToolkit().beep();
  }
}

//  构造这个类的一个对象， 并将它传递给 Timer 构造器。
// Timer 是一个与本例无关的类， 它主要用于调度后台任务。
ActionListener listener = new TimePrinter();
Timer t = new Timer(10000,listener);
t.start(); // 启动定时器
```



#### 6.2.2 Comparator 接口

现在假设我们希望按长度递增的顺序对字符串进行排序， 而不是按字典顺序进行排序。

要处理这种情况，Arrays.sort方法还有第二个版本，传入数组和一个比较器 (comparator)作为参数，比较器是实现了Comparator接口的类的实例。

```java
public interface Comparator<T>{ 
  int compare(T first, T second);
}
 
// 要按长度比较字符串， 可以如下定义一个实现 Comparator<String> 的类:
class LengthComparator implements Comparator<String>{
  public int compare(String first, String second){
    return first.length() - second.length();
  }
}

// 具体完成比较时，需要建立一个实例:
Comparator<String> comp = new LengthComparator();
if(comp.compare(words[i],words[j]) > 0) ...
```

要对一个数组排序， 需要为 Arrays.sort 方法传人一个 LengthComparator 对象:

```java
String[] friends = { "Peter", "Paul", "Mary" }; 
Array.sort(friends, new LengthComparator()):
```

在 6.3 节中我们会了解， 利用 lambda 表达式可以更容易地使用 Comparator。



#### 6.2.3 对象克隆

Cloneable 接口指示一个类提供了一个安全的 clone 方法。

+ 为对象引用的变量建立副本时 - 原变量和副本都是同一个对象的引用
+ 如果希望 copy 是一个新对象， 它的初始状态与 original 相同， 但是之后它们各自会有自 己不同的状态， 这种情况下就可以使用 clone 方法。

 clone 方法是 Object 的一个 protected 方法， 这说明你的代码不能直接调用这个方法。 换句话说，只有 Employee 类可以克隆 Employee 对象。如果想要自己的对象可以被clone，类必须：

+ 实现 Cloneable 接口;
+ 重新定义 clone 方法， 并指定 public 访问修饰符。

> Object 类中 clone 方法声明为 protected, 所以你的代码不能直接调用 anObject. clone() 。但是， 不是所有子类都能访问受保护方法吗? 不是所有类都是 Object 的子类吗? 幸运的是， 受保护访问的规则比较微妙(见第 5 章)。子类只能调用受保护的 clone 方法来克隆它自己的对象。 必须重新定义 clone 为 public 才能允许所有方法克隆对象。

Cloneable 接口的出现与接口的正常使用并没有关系。这个接口只是作为一个标记， 指示类设计者了解克隆过程。对象对于克隆很“ 偏执”， 果一个对象请求克隆， 但没有实现这个接口， 就会生成一个受査异常。

>Cloneable接口是Java提供的一组标记接口 (tagginginterface)之一,有些程序员称之为记号接口。它唯一的作用就是允许 在类型查询中使用 instanceof: if (obj instanceof Cloneable) . . .

**即使 clone 的默认(浅拷贝)实现能够满足要求， 还是需要实现 Cloneable 接口，将 clone 重新定义为public，再调用super.clone() 。下面给出一个例子:**

如果在一个对象上调用clone, 但这个对象的类并没有实现Cloneable接口，Object类 的clone方法就会拋出一个CloneNotSupportedException()

```java
class Employee implements Cloneable{
	// raise visibility level to public, change return type
	public Employee clone() throws CloneNotSupportedException{
    return (Employee) super.clone();
  }
}
```

下面来看创建深拷贝的 done 方法的一个例子:

```java
class Employee implements Cloneable{
	...
	public Employee clone() throws CloneNotSupportedException{
    // call Object,clone()
    Employee cloned = (Employee) super.clone();
    
    // clone mutable fields
    cloned.hireDay = (Date) hireDay.clone();
    return cloned;
  }
}
```

**知识点： 所有数组类型都有一个 public 的 clone 方法， 而不是 protected: 可以用这个方法建立一个新数组， 包含原数组所有元素的副本。例如:**

```java
int[] luckyNumbers = { 2, 3, 5, 7, 11, 13 }; 
int[] cloned = luckyNumbers.done();
cloned[5] = 12; // doesn't change luckyNumbers[5]
```



### 6.3 lambda表达式

