# Task 1 Deque
在cs61b中已经做过，现在大致回忆一下
实现使用双端队列，新建哨兵节点（sentinel），使其头尾均指向自己。
```java
public Deque() {  
    sentinel = new Node<Item>();  
    sentinel.next = sentinel;  
    sentinel.prev = sentinel;  
    n = 0;  
}
```

此时first元素即`sentinel.next`，last元素即`sentinel.prev`

`addFirst`建立一个新节点prev指向`sentinel`，next指向$sentinel.next$，之后再把两个节点之间的指向改变。`addLast`与之相似
```java
public void addFirst(Item item) {  
    if (item == null) {  
        throw new IllegalArgumentException("Cannot add null item");  
    }  
    Node<Item> e = new Node<Item>(item, sentinel, sentinel.next);  
    sentinel.next.prev = e;  
    sentinel.next = e;  
    n++;  
}
```

`removeFirst`即先把`first`之后的节点的`prev`指向`sentinel`，再改变`sentinel.next`。`removeLast`与之类似
```java
public Item removeFirst() {  
    if (isEmpty()) {  
        throw new NoSuchElementException("Deque is empty");  
    }  
    Node<Item> del = sentinel.next;  
    sentinel.next.next.prev = sentinel;  
    sentinel.next = sentinel.next.next;  
    n--;  
    return del.item;  
}
```

`Iterator`从`sentinel`出发指针一旦再指到`sentienl`后结束。但从PKUFlyingpig中学到了另一种简单方法，直接复制一份`deque`的大小。
```java
// return an iterator over items in order from front to back  
public Iterator<Item> iterator() {  
    return new DequeIterator();  
}  
  
private class DequeIterator implements Iterator<Item> {  
    private Node<Item> ptr;  
    private int remains;  
  
    public DequeIterator() {  
        ptr = sentinel.next;  
        remains = size();  
    }  
  
    public void remove() {  
        throw new UnsupportedOperationException("Remove is not supported");  
    }  
  
    public boolean hasNext() {  
        return remains > 0;  
    }  
  
    public Item next() {  
        if (!hasNext()) {  
            throw new NoSuchElementException("Deque is empty");  
        }  
        Item i = ptr.item;  
        ptr = ptr.next;  
        remains--;  
        return i;  
    }  
}
```

# Task2 Randomized queue
## 要求
**Randomized queue.** A _randomized queue_ is similar to a stack or queue, except that the item removed is chosen uniformly at random among items in the data structure. Create a generic data type `RandomizedQueue` that implements the following API:
```java
public class RandomizedQueue<Item> implements Iterable<Item> {

    // construct an empty randomized queue
    public RandomizedQueue()

    // is the randomized queue empty?
    public boolean isEmpty()

    // return the number of items on the randomized queue
    public int size()

    // add the item
    public void enqueue(Item item)

    // remove and return a random item
    public Item dequeue()

    // return a random item (but do not remove it)
    public Item sample()

    // return an independent iterator over items in random order
    public Iterator<Item> iterator()

    // unit testing (required)
    public static void main(String[] args)

}
```
## Solution
主要难点有两个：
1. 随机删除怎么把被删掉的空位填上（即下次再删到统一位置时该位置的元素不为null）
可以直接将队尾元素填入，既符合随机的要求，有免去了将被删掉空位后全部元素前移的麻烦
2. 随机遍历。
复制一遍数组打乱后即可（from PKUFlyingpig）
