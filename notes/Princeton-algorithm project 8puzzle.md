# Problem
The [8-puzzle](http://en.wikipedia.org/wiki/Fifteen_puzzle) is a sliding puzzle that is played on a 3-by-3 grid with 8 square tiles labeled 1 through 8, plus a blank square. The goal is to rearrange the tiles so that they are in row-major order, using as few moves as possible. You are permitted to slide tiles either horizontally or vertically into the blank square. The following diagram shows a sequence of moves from an _initial board_ (left) to the _goal board_ (right).
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410212212623.png)

# Task1 Board Type
Create a data type that models an _n_-by-_n_ board with sliding tiles. Implement an immutable data type `Board` with the following API:
```java
public class Board {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles)
                                           
    // string representation of this board
    public String toString()

    // board dimension n
    public int dimension()

    // number of tiles out of place
    public int hamming()

    // sum of Manhattan distances between tiles and goal
    public int manhattan()

    // is this board the goal board?
    public boolean isGoal()

    // does this board equal y?
    public boolean equals(Object y)

    // all neighboring boards
    public Iterable<Board> neighbors()

    // a board that is obtained by exchanging any pair of tiles
    public Board twin()

    // unit testing (not graded)
    public static void main(String[] args)

}
```

_Constructor._  You may assume that the constructor receives an _n_-by-_n_ array containing the _n_2 integers between 0 and _n_2 − 1, where 0 represents the blank square. You may also assume that 2 ≤ _n_ < 128.

`Board`为不可变数据类型要在变量前使用`final`修饰，声明一旦创建不可改变。

_String representation._  The `toString()` method returns a string composed of _n_ + 1 lines. The first line contains the board size _n_; the remaining _n_ lines contains the _n_-by-_n_ grid of tiles in row-major order, using 0 to designate the blank square.
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410212216841.png)

`String representation`重载了`toString()`函数。值得注意的是在`toString()`中常常使用`StringBuilder`在最后返回时再转为`String`

在 Java 中重载 `toString()` 函数时，通常使用 `StringBuilder` 而不是直接使用 `String` 来构建字符串，主要是因为 **性能问题**。以下是具体原因：
1. **字符串的不可变性**
`String` 对象在 Java 中是不可变的（immutable），这意味着每当你对 `String` 对象进行修改（如拼接、追加）时，实际上会创建一个新的 `String` 对象，而不是修改原来的对象。这会导致大量的临时 `String` 对象被创建和销毁，特别是在涉及多个字符串拼接的场景下，效率较低。例如：

```java
String result = "";
for (int i = 0; i < 100; i++) {
    result += i;  // 每次都会创建新的 String 对象
}
```

在这个例子中，每次循环都会创建一个新的字符串对象，并将之前的内容复制到新对象中，这会显著增加内存开销和 CPU 时间。
2. **`StringBuilder` 更高效**
`StringBuilder` 是一个可变的字符串类，它允许在不创建新的对象的情况下对字符串进行修改。它使用一个内部的字符数组来存储数据，并在需要时动态扩展容量。这样就避免了频繁创建新的 `String` 对象。对于大量的字符串拼接操作，`StringBuilder` 的性能要优于 `String`。

例如：

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 100; i++) {
    sb.append(i);  // 操作在同一个对象上，效率更高
}
String result = sb.toString();  // 最终生成一个 String 对象
```

在这个例子中，`StringBuilder` 使用一个可变的缓冲区，直接在缓冲区上拼接字符串，直到最后调用 `toString()` 方法才生成最终的 `String` 对象。这避免了多次创建和销毁临时字符串对象，节省了内存和处理时间。
3. **适合频繁的字符串拼接**
在 `toString()` 方法中，通常会进行多次字符串拼接操作，比如将多个字段的值拼接成一个完整的字符串表示。`StringBuilder` 特别适合这种场景，因为它可以高效地处理多次拼接。

### 性能对比
以下是一个简单的性能对比示例：

```java
// 使用 String 拼接
String str = "";
for (int i = 0; i < 10000; i++) {
    str += i;  // 频繁创建新 String 对象
}

// 使用 StringBuilder 拼接
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);  // 在同一个对象上拼接
}
String result = sb.toString();  // 最终生成 String
```

对于大量拼接操作，`StringBuilder` 的性能优势会非常明显。

# Solver
剩下的$A^{*}$倒没什么好说的，已经做过好多次了。