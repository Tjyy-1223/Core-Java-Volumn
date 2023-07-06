## Section 9 集合

[TOC]

### 9.1 Java集合框架

#### 9.1.1 将集合的接口与实现分离

Java集合类库中将接口(interface) 与实现 (implementation) 分离。

首先看一下队列(queue) 是如何分离的：队列接口指出可在队列的尾部添加元素， 在队列的头部删除元素， 并且可以査找队列中元素的个数。当需要收集对象， 并按照“ 先进先出” 的规则检索对象时就应该使用队列。

队列接口的最简形式可能类似下面这样:

```java
public interface Queue<E> // a simplified form of the interface in the standard library
{
  void add(E element);
  E remove();
	int size();
}
```

队列通常有两种实现方式:

+ 循环数组
+ 链表

<img src="./assets/image-20230704142145029.png" alt="image-20230704142145029" style="zoom: 67%;" />

每一个实现都可以通过一个实现了 Queue 接口的类表示:

```java
public class CicularArayQueue<E> implements Queue<E>{ // not an actual library class
 	private int head;
  private int tail;
  
  CircularArrayQueue(int capacity) {...} 
  public void add(E element) {...} 
	public E remove() {...} 
	public int size() {...}  
  private E[] elements;
} 

public class LinkedListQueue<E> implements Queue<E>{ // not an actual library class
  private int head;
  private int tail;
  
  LinkedListQueue(){...}
  public void add(E element) {...} 
	public E remove() {...} 
	public int size() {...}  
}
```

**实际上， Java 类库没有名为 CircularArrayQueue 和 LinkedListQueue 的类。** 这里， 只是以这些类作为示例， 解释一下集合接口与实现在概念上的不同。 如果需要一个循环数组队列， 就可以使用 ArrayDeque 类。如果需要一个链表队列， 就直接使用 LinkedList 类， 这个类实现了 Queue 接口。

当在程序中使用队列时， 一旦构建了集合就不需要知道究竟使用了哪种实现（**封装性的体现**），只有在构建集合对象时，使用具体的类才有意义。**可以使用接口类型存放集合的引用**：

```java
Queue<Customer> expressLane = new CircularArrayQueue<>(100); 
expressLane.add(new Customer("Harry"));
```

可以轻松地使用另外一种不同的实现。  如果觉得 LinkedListQueue 是个更好的选择， 就将代码修改为:

```java
Queue<Customer> expressLane = new LinkedListQueue<>(100); 
expressLane.add(new Customer("Harry"));
```

**接口本身并不能说明哪种实现的效率究竟如何：**

>  循环数组要比链表更高效，因此多数人优先选择循环数组，通常这样做也需要付出一定的代价。循环数组是一个有界集合， 即容量有限。如果程序中要收集的对象数量没有上限， 就最好使用链表来实现。

在研究 API 文档时， 会发现另外一组名字以 Abstract 开头的类，例如，AbstractQueue。 这些类是为类库实现者而设计的。 如果想要实现自己的队列类会发现扩展 AbstractQueue 类要比实现 Queue 接口中的所有方法轻松得多。



#### 9.1.2 Collection 接口

在 Java 类库中， **集合类的基本接口是 Collection 接口**。这个接口有两个基本方法:

```java
public interface Collection<E>{
	boolean add(E element);
  Iterator<E> iterator();
  ...
}
```

除了这两个方法之外， 还有几个方法， 将在稍后介绍。

+ add 方法用于向集合中添加元素。如果添加元素确实改变了集合就返回 true, 否则返回 false。 例如，如果这个对象在集中已经存在，这个添加请求就没有实效，因为集合中不允许有重复的对象。

+ iterator 方法用于返回一个实现了 Iterator 接口的对象，可以使用这个迭代器对象依次访问集合中的元素。 



#### 9.1.3 迭代器

Iterator 接口包含 4 个方法:

```java
public interface Iterator<E>{
  E next();
	boolean hasNext();
	void remove();
	default void forEachRemaining(Consumer<? super E> action);
}
```

通过反复调用 next 方法，可以逐个访问集合中的每个元素。但是，如果到达了集合的末尾，next 方法将抛出一个 NoSuchElementException。因此，需要在调用 next 之前调用 hasNext 方法。

如果想要査看集合中的所有元素，就请求一个迭代器，并在 hasNext 返回 true 时反复地调用 next 方法。 例如:

```java
Collection<String> c = ...;
Iterator<String> iter = c.iterator();
while(iter.hasNext()){
  String element = iter.next();
  do something with element
}
```

用“ foreach ” 循环可以更加简练地表示同样的循环操作:

```java
for (String element : c){
  do something with element
}
```

**编译器简单地将“ foreach ” 循环翻译为带有迭代器的循环。**“ for each ” 循环可以与任何实现了 **Iterable 接口**的对象一起工作， 这个接口只包含一个抽象方法:

```java
public interface Iterable<E>{
	Iterator<E> iterator();
  ...
}
```

**Collection 接口扩展了 Iterable 接口。因此， 对于标准类库中的任何集合都可以使用“ for each” 循环。**在JavaSE8中，甚至不用写循环。可以调用forEachRemaining方法并提供lambda 表达式(它会处理一个元素)。 将对迭代器的每一个元素调用这个 lambda 表达式， 直到再没有元素为止。

```java
iterator.forEachRemaining(element -> do something with element);
```

注意：某些集合类型的遍历并不一定能保证顺序。

Java 迭代器查找一个元素的唯一方法是调用next，而在执行查找操作的同时，迭代器的位置随之向前移动。**因此，应该将 Java 迭代器认为是位于两个元素之间。**当调用 next 时，**迭代器就越过下 一个元素，并返回刚刚越过的那个元素的引用** (见图 9-3 ) 。

<img src="./assets/image-20230704151559198.png" alt="image-20230704151559198" style="zoom: 67%;" />

> 这里还有一个有用的推论。 可以将 Iterator.next 与 InputStream.read 看作为等效的。从数据流中读取一个字节， 就会自动地“ 消耗掉” 这个字节，下一次调用 read 将会消耗并返回输入的下一个字节；用同样的方式，反复地调用 next 就可以读取集合中所有元素。

**Iterator 接口的 remove 方法将会删除上次调用 next 方法时返回的元素。**在大多数情况下，在决定删除某个元素之前应该先看一下这个元素是很具有实际意义的。**然而， 如果想要删除指定位置上的元素，仍然需要越过这个元素。** 例如，下面是如何删除字符串集合中第一个元素的方法:

```java
Iterator<String> it = c.iterator();
it.next(); // skip over the first element
it.remove(); // now remove it
```

更重要的是，对 next 方法和 remove 方法的调用具有互相依赖性。 如果调用 remove 之前没有调用 next 将是不合法的，如果这样做， 将会抛出一个 IllegalStateException 异常。

如果想删除两个相邻的元素， 不能直接地这样调用:

```java
it.remove(); 
it.remove();// Error!
```

相反地， 必须先调用 next 越过将要删除的元素。

```java
it.remove();
it.next();
it.remove(); // OK
```



#### 9.1.4 泛型实用方法

**由于 Collection 与 Iterator 都是泛型接口，可以编写操作任何集合类型的实用方法。** 例如，下面是一个检测任意集合是否包含指定元素的泛型方法:

```java
public static <E> boolean contains(Collection<E> c, Object obj){
	for (E element : c)
		if (element.equals(obj)) 
      return true;
  return false; 
}
```

Java 类库的设计者认为：这些实用方法中的某些方法非常有用， 应该将它们提供给用户使用。这样类库的使用者就不必自己重新构建这些方法了，contains 就是这样一个实用方法。

事实上，**Collection 接口声明了很多有用的方法，所有的实现类都必须提供这些方法。**下面列举了其中的一部分:

```java
int size()
boolean isEmpty()
boolean contains(Object obj)
boolean containsAl1(Col1ection<?> c)
boolean equals(Object other)
boolean addAll (Collection<? extends E> from) 
boolean remove(Object obj)
boolean removeAl1(Col1ection<?> c)
void clear()
boolean retainAl1(Col1ection<?> c)
Object口 toArray()
<T> T[] toArray(T[] arrayToFill)
```

**当然，如果实现 Collection 接口的每一个类都要提供如此多的例行方法将是一件很烦人的事情。**为了能够让实现者更容易地实现这个接口， **Java 类库提供了一个类 AbstractCollection，**它将基础方法 size 和 iterator 抽象化了，但是在此提供了例行方法。例如:

```java
public abstract class AbstractCollection<E> implements Collection<E>{
  ...
  public abstract Iterator<E> iterator();
  public boolean contains(Object obj){
    for(E element : this) // calls iterator()
      if(element,equals(obj))
        return = true;
    return false;
  }
  ...
}
```

此时， 一个具体的集合类可以扩展 AbstractCollection 类了。**现在要由具体的集合类提供 iterator 方法，而 contains 方法已由 AbstractCollection 超类提供了**。 如果子类有更加有效的方式实现 contains 方法， 也可以由子类提供。

对于 Java SE 8 , 已经增加了很多默认方法。其中大部分方法都与流的处理有关。还有一个很有用的方法:

```java
default boolean removelf(Predicate<? super E> filter)
```

这个方法用于删除满足某个条件的元素。



#### 9.1.5 集合框架中的接口

Java 集合框架为不同类型的集合定义了大量接口， 如图 9-4 所示:

<img src="./assets/image-20230704160604216.png" alt="image-20230704160604216"  />

集合有两个基本接口: Collection 和 Map。 我们已经看到， 可以用以下方法在集合中插入元素:

```java
boolean add(E element)
```

不过， 由于映射包含键值对， 所以要用 put 方法来插人:

```java
V put(K key, V value)
```

要从集合读取元素， 可以用迭代器访问元素。不过， 从映射中读取值则要使用 get 方法:

```java
V get(K key)
```

**List是一个有序集合：元素会增加到容器的特定位置。**可以采用两种方式访问元素: 

+ 使用迭代器访问
+ 使用一个整数索引来访问。

后一种方法称为随机访问(random access)，因为这样可以按任意顺序访问元素。与之不同，使用迭代器访问时，必须顺序地访问元素。

List 接口定义了多个用于随机访问的方法:

```java
void add(int index, E element) 
void remove(int index)
E get(int index)
E set(int index, E element)
```

Listlterator 接口是 Iterator 的一个子接口。它定义了一个方法用于在迭代器位置前面增加一个元素:

```java
void add(E element)
```

**坦率地讲， 集合框架的这个方面设计得很不好**：实际中有两种有序集合， 其性能开销有很大差异。 

+ 由数组支持的有序集合可以快速地随机访问， 因此适合使用 List 方法并提供一个 整数索引来访问。
+ 链表尽管也是有序的， 但是随机访问很慢， 所以最好使用迭代器来遍历。 如果原先提供两个接口就会容易一些了。

为了避免对链表完成随机访问操作， Java SE 1.4 引入了一个标记接口 RandomAccess。这个接口不包含任何方法，不过可以用它来测试一个特定的集合是否支持高效的随机访问:

```java
if (c instanceof RandomAccess) {
	use random access algorithm
	else {
		use sequential access algorithm
  }
}
```

**Set接口等同于Collection接口，不过其方法的行为有更严谨的定义:**

+ set的add方 法不允许增加重复的元素。 
+ equals 方法: 只要两个集包含同样的元素就认为是相等的， 而不要求这些元素有同样的顺序。
+  hashCode 方法的定义要保证包含相同元素的两个集得到相同的散列码。

SortedSet 和 SortedMap 接口会提供用于排序的比较器对象，这两个接口定义了可以得到集合子集视图的方法，有关内容将在 9.4 节讨论。

最后， Java SE 6 引入了接口 NavigableSet 和 NavigableMap，其中包含一些用于**搜索和遍历有序集和映射的方法**(理想情况下， 这些方法本应当直接包含在 SortedSet 和 SortedMap 接口中)。TreeSet 和 TreeMap 类实现了这些接口。



### 9.2 具体的集合

表 9-1 展示了 Java 类库中的集合， 并简要描述了每个集合类的用途：**除了以 Map 结尾的类之外，其他类都实现了 Collection 接口， 而以 Map 结尾的类实现了 Map 接口。**

![image-20230704162615738](./assets/image-20230704162615738.png)

<img src="./assets/image-20230704162739382.png" alt="image-20230704162739382" style="zoom:67%;" />



#### 9.2.1 链表

> 很多示例已经使用了数组以及动态的 ArrayList 类，然而他们都有一个重大的缺陷，就是**从数组的中间位置删除一个元素要付出很大的代价**， 其原因是数组中处于被删除元素之后的所有元素都要向数组的前端移动；在数组中间的位置上插入一个元素也是如此。

**链表(linked list) 解决了这个问题，链表将每个对象存放在独立的结点中，每个结点还存放着序列中下一个结点的引用；**在 Java 程序设计语言中， 所有链表实际上都是双向链接的(doubly linked)：每个结点还存放着指向前驱结点的引用。

+ 从链表中间删除一个元素是一个很轻松的操作， 即需要更新被删除元素附近的链接。
+ Java提供的类LinkedList简化了原始链表中绕来绕去的指针过程

在下面的代码示例中， 先添加 3 个元素， 然后再将第 2 个元素删除：

```java
List<String> staff = new LinkedList<>(); // LinkedList implements List 
staff.add("Amy") ;
staff.add("Bob");
staff.add("Carl");
Iterator iter = staff.iterator();
String first = iter.next(); // visit first element
String second = iter.next(); // visit second element
iter.remove(); // remove last visited element
```



链表是一个有序集合(ordered collection), 每个对象的位置十分重要。LinkedList.add方法将对象添加到链表的尾部。但是， 常常需要将元素添加到链表的中间，**集合类库提供了子接口ListIterator, 其中包含add方法:**

```java
interface ListIterator<E> extends Iterator<E>{
  void add(E element);
  ...
}
```

与 Collection.add 不同，这个方法不返回 boolean 类型的值，它假定添加操作总会改变链表。

**另外，ListIterator 接口有两个方法， 可以用来反向遍历链表：**与 next 方法一样， previous 方法返回越过的对象。

```java
E previous()
boolean hasPrevious()
```

LinkedList 类的 ListIterator 方法返回一个实现了 ListIterator 接口的迭代器对象。

```java
ListIterator<String> iter = staff.listIterator();
```

**Add 方法在迭代器位置之前添加一个新对象。**例如，下面的代码将越过链表中的第一个元素，并在第二个元素之前添加“ Juliet”:

```java
List<String> staff = new LinkedList(); 
staff.add("Amy");
staff.add("Bob");
staff.add("Carl");
ListIterator<String> iter = staff.listIterator(); 
iter.next();// skip past first element 
iter.add("Juliet") ;
```

**注意：**

> 在用“ 光标” 类比时要格外小心。remove 操作与 BACKSPACE 键的工作方式不 太一样。 在调用 next 之后， remove 方法确实与 BACKSPACE 键一样删除了迭代器左侧的元素。但是，如果调用 previous 就会将右侧的元素删除掉，并且不能连续调用两次 remove。add 方法只依赖于迭代器的位置，而 remove 方法依赖于迭代器的状态。

最后需要说明， set 方法用一个新元素取代调用 next 或 previous 方法返回的上一个元素。 例如， 下面的代码将用一个新值取代链表的第一个元素:

```java
ListIterator<String> iter = list.listIterator();
String oldValue = iter.next(); // returns first element 
iter.set(newValue); // sets first element to newValue
```

**如果在某个迭代器修改集合时，另一个迭代器对其进行遍历，一定会出现混乱的状况**。 例如，一个迭代器指向另一个迭代器刚刚删除的元素前面， 现在这个迭代器就是**无效**的， 并且不应该再使用。链表迭代器的设计使它能够检测到这种修改：如果迭器发现它的集合被另一个迭代器修改了，或是被该集合自身的方法修改了，就会抛出一个 **ConcurrentModificationException 异常**。 例如，看一看下面这段代码:

```java
List<String> list = ...;
ListIterator<String> iterl = list.listlterator(); 
ListIterator<String> iter2 = list.listlterator(); 
iterl.next();
iterl.remove();
iter2.next(); // throws ConcurrentModificationException
```

**由于 iter2 检测出这个链表被从外部修改了， 所以对 iter2.next 的调用抛出了一个异常。**

**关于迭代器修改的注意事项：**

+ 为了避免发生并发修改的异常， 请遵循下述简单规则：可以根据需要给容器附加许多的迭代器，但是这些迭代器只能读取列表。另外，再单独附加一个既能读又能写的迭代器。

+ 有一种简单的方法可以检测到并发修改的问题：集合可以跟踪改写操作(诸如添加或删除元素)的次数，每个迭代器都维护一个独立的计数值，在每个迭代器方法的开始处检查自己改写操作的计数值是否与集合的改写操作计数值一致；如果不一致， 抛出一个 ConcurrentModificationException 异常。

+ 对于并发修改列表的检测有一个奇怪的例外。链表只负责跟踪对列表的结构性修改，例如，添加元素、删除元素。 **set 方法不被视为结构性修改**。 可以将多个迭代器附加给一个链表， 所有的迭代器都调用 set 方法对现有结点的内容进行修改。 在本章后面所介绍的 Collections 类的许多算法都需要使用这个功能。



在上一节已经看到， Collection 接口中声明了许多用于对链表操作的有用方法，其中大部分方法都是在 LinkedList 类的超类 AbstractCollection 中实现的，例如：

+ toString 方法调用了所有元素的toString，并产生了一个很长的格式为[A,B，C]的字符串
+ contains 方法检测某个元素是否出现在链表中。

**在 Java 类库中， 还提供了许多在理论上存在一定争议的方法**：链表不支持快速地随机访问。如果要查看链表中第n个元素，就必须从头开始，越过n-1个元素，没有捷径可走。鉴于这个原因，在程序需要采用整数索引访问元素时，程序员通常不选用链表。**尽管如此，LinkedList 类还是提供了一个用来访问某个特定元素的 get 方法:**

```java
LinkedList<String> list = ...;
String obj = list.get(n);
```

这个方法的效率并不太高，如果发现自己正在使用这个方法， 说明有可能对于所要解决的问题使用了错误的数据结构。绝对不应该使用这种让人误解的随机访问方法来遍历链表。下面这段代码的**效率极低**：

```java
for(int i = 0; i < list.size(); i++)
	do something with list.get(i);
```

每次査找一个元素都要从列表的头部重新开始搜索，LinkedList 对象根本不做任何缓存位置信息的操作。get 方法做了微小的优化：如果索引大于 size() / 2 就从列表尾端开始搜索元素。



**列表迭代器接口还有一个方法， 可以返回当前位置的索引。** 从概念上讲， 由于 Java 迭代器指向两个元素之间的位置， 所以可以同时产生两个索引: 

+ nextlndex 方法返回下一 次调用 next 方法时返回元素的整数索引;
+ previouslndex 方法返回下一次调用 previous 方法时返回元素的整数索引；比 nextlndex 返回的索引值小 1

**这两个方法的效率非常高，这是因为迭代器保持着当前位置的计数值。**需要说一下，如果有一个整数索引n，`list.listlterator(n)` 将返回一个迭代器， 这个迭代器指向索引为 n 的元素前面的位置。也就是说， 调用 next 与调用list.get(n) 会产生同一个元素， 只是**获得这个迭代器的效率比较低**。

如果链表中只有很少几个元素，就完全没有必要为 get 方法和 set 方法的开销而烦恼。但是，**为什么要优先使用链表呢？** 

**使用链表的唯一理由是尽可能地减少在列表中间插人或删除元素所付出的代价**，如果列表只有少数几个元素， 就完全可以使用 ArrayList。我们建议**避免**使用以整数索引表示链表中位置的所有方法。 如果需要对集合进行随机访 问，就使用数组或 ArrayList，而不要使用链表。（ArrayList 的底层是数组队列，相当于动态数组。）

程序清单 9-1 中的程序使用的就是链表：它简单地创建了两个链表， 将它们合并在一起， 然后从第二个链表中每间隔一个元素删除一个元素， 最后测试 removeAIl 方法。 建议跟踪一 下程序流程， 并要特别注意迭代器。从这里会发现绘制一个下面这样的迭代器位置示意图是非常有用的:

```
|ACE 丨BDFC 
A|CE 丨BDFC 
AB|CE B|DFC
```



#### 9.2.2 数组列表

9.2.1中重点关注了LinkedList类，其内部构成原理主要是链表的数据结构。集合类库提供了一种大家熟悉的 ArrayList 类， 这个类也实现了 List 接口。 **ArrayList 封装了一个动态再分配的对象数组**。

对于一个经验丰富的 Java 程序员来说， 在需要动态数组时，可能会使用 Vector 类。**为什么要用 ArrayList 取代 Vector 呢:**

+ Vector 类的所有方法都是同步的，可以由两个线程安全地访问一个 Vector 对象。 但是，如果由一个线程访问 Vector，代码要在同步操作上耗费大量的时间。 
+  ArrayList 方法不是同步的。
+  建议在不需要同步时使用 ArrayList, 而不要使用 Vector。



#### 9.2.3 散列集

如果不在意元素的顺序，Java提供了几种能够快速査找元素的数据结构。 其缺点是无法控制元素出现的次序，它们将按照有利于其操作目的的原则组织数据，**散列表(hash table)就是其中一种**。

+ 散列表为每个对象计算一个整数，称为散列码(hashcode) 。
+ 散列码是由对象的实例域产生的一个整数，具有不同数据域的对象将产生不同的散列码。
+ 如果自定义类， 就要负责实现这个类的 hashCode 方法。 **注意，自己实现的 hashCode 方法应该与 equals 方法兼容，即如果a_equals(b)为true，a与b必须具有相同的散列码。**

**在 Java 中散列表用链表数组实现，每个列表被称为桶：**

+ 査找表中对象位置， 先计算它的散列码， 然后与桶的总数取余， 得到桶的索引。
+ 有时候会遇到桶被占满的情况， 这也是不可避免的。这种现象被称为**散列冲突(hash collision)**；这时， 需要用新对象与桶中的所有对象进行比较， 査看这个对象是否已经存在。
+ **在 JavaSE 8 中， 桶满时会从链表变为平衡二叉树。**如果选择的散列函数不当，会产生很多冲突， 或者如果有恶意代码试图在散列表中填充多个有相同散列码的值，使用平衡二叉树可以提高性能。

**散列表可以用于实现几个重要的数据结构。**  其中最简单的是 set 类型：set 是没有重复元素的元素集合。set 的 add 方法首先在集中查找要添加的对象，如果不存在就将元素进行插入。

Java 集合类库提供了一个 HashSet 类， 它实现了基于散列表的集。可以用 add 方法添加元素；contains 方法已经被重新定义，用来快速地查看是否某个元素已经出现在集中。**底层原理是只在某个桶中査找元素，而不必查看集合中的所有元素。**



#### 9.2.4 数集

TreeSet 类对9.2.3中提及的散列集有所改进：

+ **树集是一个有序集合 ( sorted collection ) ，可以以任意顺序将元素插入到集合中。**
+ 在对集合进行遍历时， 每个值将自动地按照排序后的顺序呈现。 

例如， 假设插入 3 个字符串， 然后访问添加的所有元素。

```java
SortedSet<String> sorter = new TreeSet<>(); // TreeSet implements SortedSet
sorter.add("Bob");
sorter.add("Amy");
sorter.add("Carl");
for(String s : sorter): System.println(s);
// Amy Bob Carl
```

正如 TreeSet 类名所示，TreeSet中排序是用树结构完成的（当前实现使用的是红黑树(red-black tree）。有关红黑树的详细介绍请参看 《 Introduction to Algorithms 》。

**将一个元素添加到树中要比添加到散列表中慢，但是与检查数组或链表中的重复元素相比还是快很多。**如果树中包含 n 个元素， 査找新元素的正确位置平均需要 log2(n) 次比较。例如， 如果一棵树包含了 1000 个元素， 添加一个新元素大约需要比较 10 次。

![image-20230706160002362](./assets/image-20230706160002362.png)

> 要使用树集， 必须能够比较元素。 这些元素必须实现 Comparable 接口 (参见 6.1.1 节，) 或者构造集时必须提供一个 Comparator ( 参见 6.2.2 节和 6.3.8 节)。

**是否总是应该用树集取代散列集：**

+ 取决于所要收 集的数据。 
+ 不需要对数据进行排序， 就没有必要付出排序的开销。
+ 更重要的是，对于某些数据来说，对其排序要比散列函数更加困难。散列函数只是将对象适当地打乱存放， 而比较却要精确地判别每个对象。

> 要想具体地了解它们之间的差异， 还需要研究一个收集矩形集的任务。 如果使用 TreeSet , 就 需 要 提 供 Comparator< Rectangle>。 如何比较两个矩形呢? 比较面积吗? 这行不通。可能会有两个不同的矩形，它们的坐标不同， 但面积却相同；有一种矩形的排序(按照坐标的词典顺序排列)方式，但它的计算很牵强且很繁琐。相反地， Rectangle 类已经定义了散列函数， 它直接对坐标进行散列。

在程序清单 9-3 的程序中创建了两个 Item 对象的树集。第一个按照部件编号排序，这是 Item 对象的默认顺序。第二个通过使用一个定制的比较器来按照描述信息排序。



#### 8.2.5 队列与双端队列

+ 队列可以让人们有效地在尾部添加一个元素， 在头部删除一个元素。
+ 有两个端头的队列， 即双端队列， 可以让人们有效地在头部和尾部同时添加或删除元素，但是不支持在队列中间添加元素。 
+  **Java SE 6 中引人了 Deque 接口， 并由 ArrayDeque 和 LinkedList 类实现。这两个类都提供了双端队列，而且在必要时可以增加队列的长度。在第 14 章将会看到有限队列和有限双端队列。**



#### 8.2.6 优先级队列

**优先级队列(priorityqueue)** 中的元素可以按照任意的顺序插人，却总是按照排序的顺序进行检索：

+ 无论何时调用 remove 方法， **总会获得当前优先级队列中最小的元素。** 
+ 然而，优先级队列并没有对所有的元素进行排序。优先级队列使用了一个优雅且高效的数据结构， 称为堆(heap)。堆是一个可以自我调整的二叉树，对树执行添加(add) 和删除(remore) 操作，可以让最小的元素移动到根，而**不必花费时间对元素进行排序**。

**与 TreeSet 一样，一个优先级队列既可以保存实现了 Comparable 接口的类对象，也可以保存在构造器中提供的 Comparator 对象。**

使用优先级队列的典型示例是任务调度。每一个任务有一个优先级，任务以随机顺序添加到队列中。每当启动一个新的任务时，都将优先级最高的任务从队列中删除 (由于习惯上将1设为“ 最高”优先级，所以会将最小的元素删除)。

**程序清单 9-5 显示了一个正在运行的优先级队列。** 与 TreeSet 中的迭代不同，这里的**迭代并不是按照元素的排列顺序访问的。** 而删除却总是删掉剩余元素中优先级数最小的那个元素。



### 9.3 映射 Mapping

通常，我们知道某些键的信息，并想要查找与之对应的元素。映射(map) 数据结构就是为此设计的：

+ 映射用来存放键值对。如果提供了键，就能够查找到值。 
+ 例如，有一张关于员工信息的记录表，键为员工 ID，值为 Employee 对象。

#### 9.3.1 基本映射操作

**Java 类库为映射提供了两个通用的实现: HashMap 和 TreeMap，这两个类都实现了 Map 接口。**

+ HashMap对键进行散列。
+ TreeMap映射用键的整体顺序对元素进行排序，并将其组织成搜索树。散列或比较函数只能作用于键；与键关联的值不能进行散列或比较。
+ HashMap稍微快一些，如果不需要按照排列顺序访问键，就最好选择散列。

下列代码将为存储的员工信息建立一个散列映射:

```java
Map<String, Employee> staff = new HashMap<>(); // HashMap implements Map
Employee harry = new Employee("Harry Hacker");
staff.put("987-98-9996",harry);
```

要想检索一个对象， 必须使用(因而， 必须记住)一个键:

```java
String id = "987-98-9996";
e = staff.get(id); // gets harry
```

如果在映射中没有与给定键对应的信息，get 将返回 null；null 返回值可能并不方便。有时可以设置默认值，用作为映射中不存在的键，然后使用 getOrDefault 方法：

```java
Map<String, Integer> scores = ..
int score = scores.getOrDefault(id,0); // Gets 0 if the id is not present
```

键必须是唯一的，不能对同一个键存放两个值。如果对同一个键两次调用 put 方法，第二个值就会取代第一个值。实际上，put 将返回用这个键参数存储的上一个值。

+ **remove 方法**用于从映射中删除给定键对应的元素。
+ size 方法用于返回映射中的元素数。 
+ **要迭代处理映射的键和值，**最容易的方法是使用 forEach 方法，可以提供一个接收键和值的 lambda 表达式，映射中的每一项会依序调用这个表达式。

```java
scores.forEach((k,v) -> 
               System.out.println("key=" + k + ",value="  v));
```



#### 9.3.2 更新映射项

**处理映射时的一个难点就是更新映射项：**

正常情况下，可以得到与一个键关联的原值，完成更新，再放回更新后的值。 不过必须考虑一个特殊情况，即键第一次出现。下面使用一个映射统计一个单词在文件中出现的频度，看到一个单词(word) 时， 我们将计数器增 1，如下所示:

```java
counts.put(word,counts.get(word) + 1);
```

**这是可以的，不过有一种情况除外：**就是第一次看到 word 时。在这种情况下，get 会返回null, 因此会出现一个NullPointerException异常。**作为一个简单的补救，可以使用 getOrDefault 方法：**

```java
counts.put(word, counts.getOrDefault(word, 0) + 1);
```

另一种方法是首先调用 putlfAbsent 方法，只有当键原先存在时才会放入一个值：

```java
counts.putlfAbsent(word, 0);
counts.put(word, counts.get(word) + 1); // Now we know that get will succeed
```

**不过还可以做得更好，merge方法可以简化这个常见的操作。如果键原先不存在，下面的调用:**

```java
counts.merge(word, 1, Integer::sum);
```

将把 word 与 1 关联， 否则使用 Integer::sum 函数组合原值和 1 (也就是将原值与1求和) 



#### 9.3.3 映射视图

集合框架不认为映射本身是一个集合；其他数据结构框架认为映射是一个键 / 值对集合，或者是由键索引的值集合。**不过， 可以得到映射的视图(View) ：这是实现了 Collection 接口或某个子接口的对象。**

有 3 种视图：键集、值集合以及键 / 值对集。下面的方法分别返回这三种视图：

```java
Set<K> keySet()
Collection<V> values() 
Set<Map.Entry<K, V>> entrySet()
```

需要说明的是，keySet 不是 HashSet 或 TreeSet，而是实现了 Set 接口的另外某个类的对象。 Set 接口扩展了 Collection 接口，因此可以像使用集合一样使用 keySet。

例如，可以枚举一个映射的所有键：

```java
Set<String> keys = map.keySet(); 
for (String key : keys){
	do something with key 
}
```

如果想同时查看键和值，可以通过枚举条目来避免查找值，使用以下代码：

```java
for (Map.Entry<String, Employee> entry : staff.entrySet()){
  String k = entry.getKey();
  Employee v = entry.getValue();
  do something with k, v
}
```

**关于上面的代码，原先这是访问所有映射条目的最高效的方法。如今，只需要使用 forEach 方法:**

```java
counts.forEach((k，v) -> {
  do something with k,v
});
```

**特别注意：**

+ 在键集视图上调用迭代器的 remove 方法， 实际上会从映射中删除这个键和与它关联的值。 
+ **不能向键集视图增加元素；如果增加一个键而没有同时增加值也是没有意义的** ；如果试图调用 add 方法， 它会抛出一个 UnsupportedOperationException。 
+ 条目集视图有同样的限制， 尽管理论上增加一个新的键 / 值对好像是有意义的。



#### **9.3.4 弱散列映射**

设计 WeakHashMap 类是为了解决一个有趣的问题： **如果有一个值，对应的键已经不再使用了，将会出现什么情况呢？**

> 假定对某个键的最后一次引用已经消亡，不再有任何途径引用这个值的对象了。但是，由于在程序中的任何部分没有再出现这个键，所以，这个键 / 值对无法从映射中删除。为什么垃圾回收器不能够删除它呢？难道删除无用的对象不是垃圾回收器的工作吗？

遗憾的是， 事情没有这样简单，垃圾回收器跟踪活动的对象。只要映射对象是活动的，其中的所有桶也是活动的，它们不能被回收。 **因此，需要由程序负责从长期存活的映射表中删除那些无用的值，或者使用 WeakHashMap 完成这件事情**。当对键的唯一引用来自散列条目时，这一数据结构将与垃圾回收器协同工作一起删除键 / 值对。

下面是这种机制的内部运行情况。WeakHashMap使用弱引用(weak references) 保存键：

+ WeakReference 对象将引用保存到另外一个对象中，在这里，就是散列键。 对于这种类型的对象， 垃圾回收器用一种特有的方式进行处理。通常， 如果垃圾回收器发现散列值已经没有他人引用了， 就将其回收。
+ 如果某个对象只能由 WeakReference 引用，垃圾回收器仍然回收它，但要将引用这个对象的弱引用放入队列中。WeakHashMap 将周期性地检查队列，以便找出新添加的弱引用。
+ 弱引用进入队列意味着这个键不再被他人使用，并且已经被收集起来，WeakHashMap 将删除对应的条目。



#### 9.3.5 链接散列集与映射

**LinkedHashSet 和 LinkedHashMap类用来记住插入元素项的顺序。**这样就可以避免在散列表中的项从表面上看是随机排列的。当条目插入到表中时，就会并入到双向链表中：

![image-20230706201315381](./assets/image-20230706201315381.png)

例如， 在程序清单 9-6 中包含下列映射表插入的处理:

```java
Map<String, Employee> staff = new LinkedHashMap<>(); 
staff.put("144-25-5464", new Employee("Amy Lee")); 
staff.put("567-24-2546", new Employee("Harry Hacker")); 
staff.put("157-62-7935", new Employee("Gary Cooper")); 
staff.put("456-62-5527", new Employee("Francesca Cruz"));
```

然后， staff.keySet().iterator() 以下面的次序枚举键:

```
144-25-5464 
567-24-2546 
157-62-7935 
456-62-5527
```

**如果想让链接散列映射使用访问顺序，而不是插入顺序，对映射条目进行迭代。**

+ 每次调用 get 或 put，受到影响的条目将从当前的位置删除，并放到条目链表的尾部（只有条目在链表中的位 置会受影响，而散列表中的桶不会受影响，一个条目总位于与键散列码对应的桶中)。
+ 要项构造这样一个的散列映射表， 请调用：

```java
LinkedHashMap<K, V>(initialCapacity, loadFactor, true)
```

**访问顺序对于实现高速缓存的“ 最近最少使用”原则十分重要。**例如，可能希望将访问频率高的元素放在内存中，而访问频率低的元素则从数据库中读取。当在表中找不到元素项且表又已经满时，可以将枚举的前几个元素删除掉，这些是近期最少使用的几个元素。

**甚至可以让上述过程自动化，即构造一个 LinkedHashMap 的子类，然后覆盖下面这个方法:**

```java
protected boolean removeEldestEntry(Map.Entry<K，V> eldest)
```

每当方法返回 true 时，就表示添加一个新条目，从而导致删除 eldest 条目。例如，下面的高速缓存可以存放 100 个元素:

```java
Map<K, V> cache = new LinkedHashMap<>(128,0.75F,true){
  protected boolean removeEldestEntry(Map.Entry<K，V> eldest){
    return size() > 0;
  }
}();
```

另外还可以对 eldest 条目进行评估，以此决定是否应该将它删除。例如，可以检査与这个条目一起存在的时间戳。



#### 9.3.6 枚举集与映射

**EmimSet 是一个枚举类型元素集的高效实现。** 由于枚举类型只有有限个实例，所以 EnumSet 内部用**位序列**实现，如果对应的值在集中，则相应的位被置为 1。

EnumSet 类没有公共的构造器，可以使用静态工厂方法构造这个集:

```java
enum Weekday { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY };
EnumSet<Weekday> always = EnumSet.allOf(Weekday.class);
EnumSet<Weekday> never = EnumSet.noneOf(Weekday.class);
EnumSet<Weekday> workday = EnumSet.range(Weekday.MONDAY,Weekday.FRIDAY);
EnumSet<Weekday> mwf = EnumSet.of(Weekday.MONDAY, Weekday.WEDNESDAY ,Weekday.FRIDAY);
```

可以使用 Set 接口的常用方法来修改 EnumSet。

EnumMap 是一个键类型为枚举类型的映射，它可以直接且高效地用一个值数组实现。在使用时，需要在构造器中指定键类型:

```java
EnumMap<Weekday, Employee> personInCharge = new EnumMap<>(Weekday.class);
```

> 在 EnumSet 的 API 文档中，将会看到 Eextends Enum<E> 这样奇怪的类型参数。简单地说，它的意思是  E 是一个枚举类型，所有的枚举类型都扩展于泛型 Enum 类。 例如 Weekday 扩展 Enum<Weekday>。



#### 9.3.7 标识散列映射

**IdentityHashMap 有特殊的作用：** 

+ 键的散列值不是用 hashCode 函数计算的，而是用 System.identityHashCode 方法计算的，这是 Object.hashCode 方法根据对象的内存地址来计算散列码时所使用的方式。
+ 对两个对象进行比较时，IdentityHashMap 类使用 == ，而不使用 equals，也就是说不同的键对象，即使内容相同， 也被视为是不同的对象。 
+ 在实现对象遍历算法 （如对象串形化）时这个类非常有用，可以用来跟踪每个对象的遍历状况。



### 9.4 视图与包装器

看一下图 9-4 和图 9-5 可能会感觉: 用如此多的接口和抽象类来实现数量并不多的具 体集合类似乎没有太大必要。 然而， 这两张图并没有展示出全部的情况。 通过使用视图 ( views ) 可以获得其他的实现了 Collection 接口和 Map 接口的对象。 映射类的 keySet 方法就 是一个这样的示例。 初看起来， 好像这个方法创建了一个新集， 并将映射中的所有键都填进 去， 然后返回这个集。但是， 情况并非如此。 取而代之的是: keySet 方法返回一个实现 Set 接口的类对象， 这个类的方法对原映射进行操作。这种集合称为视图。

视图技术在集框架中有许多非常有用的应用。 下面将讨论这些应用。

#### 9.4.1 轻量级集合包装器

