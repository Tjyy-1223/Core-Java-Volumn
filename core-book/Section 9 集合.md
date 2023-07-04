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

**当然，如果实现 Collection 接口的每一个类都要提供如此多的例行方法将是一件很烦人的事情。**为了能够让实现者更容易地实现这个接口， Java 类库提供了一个类 AbstractCollection，它将基础方法 size 和 iterator 抽象化了，但是在此提供了例行方法。例如:

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

