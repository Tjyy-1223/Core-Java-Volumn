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

