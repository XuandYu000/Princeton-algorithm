## Description

Implement the _Burrows–Wheeler data compression algorithm_. This revolutionary algorithm outcompresses _gzip_ and _PKZIP_, is relatively easy to implement, and is not protected by any patents.

The Burrows–Wheeler data compression algorithm consists of three algorithmic components, which are applied in succession:

1. _Burrows–Wheeler transform._ Given a typical English text file, transform it into a text file in which sequences of the same character occur near each other many times.
    
2. _Move-to-front encoding._ Given a text file in which sequences of the same character occur near each other many times, convert it into a text file in which certain characters appear much more frequently than others.
    
3. _Huffman compression._ Given a text file in which certain characters appear much more frequently than others, compress it by encoding frequently occurring characters with short codewords and infrequently occurring characters with long codewords.

Step 3 is the only one that compresses the message: it is particularly effective because Steps 1 and 2 produce a text file in which certain characters appear much more frequently than others. To expand a message, apply the inverse operations in reverse order: first apply the Huffman expansion, then the move-to-front decoding, and finally the inverse Burrows–Wheeler transform. Your task is to implement the Burrows–Wheeler and move-to-front components.

_Huffman compression and expansion_. [Huffman](https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/Huffman.html) (Program 5.10 in _Algorithms, 4th edition_) implements the classic Huffman compression and expansion algorithms.

```
~/Desktop/burrows> java-algs4 edu.princeton.cs.algs4.Huffman - < abra.txt | java-algs4 edu.princeton.cs.algs4.HexDump 16
50 4a 22 43 43 54 a8 40 00 00 01 8f 96 8f 94
120 bits
```

```
~/Desktop/burrows> java-algs4 edu.princeton.cs.algs4.Huffman - < abra.txt | java-algs4 edu.princeton.cs.algs4.Huffman +
ABRACADABRA!
```
## Step
_Move-to-front encoding and decoding._ The main idea of move-to-front encoding is to maintain an ordered sequence of the characters in the alphabet by repeatedly reading a character from the input message; printing the position in the sequence in which that character appears; and moving that character to the front of the sequence. As a simple example, if the initial ordering over a 6-character alphabet is `A B C D E F`, and we want to encode the input `CAAABCCCACCF`, then we would update the move-to-front sequence as follows:

```
move-to-front    in   out
-------------    ---  ---
 A B C D E F      C    2 
 C A B D E F      A    1
 A C B D E F      A    0
 A C B D E F      A    0
 A C B D E F      B    2
 B A C D E F      C    2
 C B A D E F      C    0
 C B A D E F      C    0
 C B A D E F      A    2
 A C B D E F      C    1
 C A B D E F      C    0
 C A B D E F      F    5
 F C A B D E  
```

If equal characters occur near one another many times in the input, then many of the output values will be small integers (such as 0, 1 and 2). The resulting high frequency of certain characters (0s, 1s, and 2s) provides exactly the kind of input for which Huffman coding achieves favorable compression ratios.

- _Move-to-front encoding._ Your task is to maintain an ordered sequence of the 256 extended ASCII characters. Initialize the sequence by making the _i_ th character in the sequence equal to the _i_ th extended ASCII character. Now, read each 8-bit character `c` from standard input, one at a time; output the 8-bit index in the sequence where `c` appears; and move `c` to the front.
```
~/Desktop/burrows> java-algs4 MoveToFront - < abra.txt | java-algs4 edu.princeton.cs.algs4.HexDump 16
41 42 52 02 44 01 45 01 04 04 02 26
96 bits
```
- _Move-to-front decoding._ Initialize an ordered sequence of 256 characters, where extended ASCII character _i_ appears _i_ th in the sequence. Now, read each 8-bit character  _i_  (but treat it as an integer between 0 and 255) from standard input one at a time; write the _i_ th character in the sequence; and move that character to the front. Check that the decoder recovers any encoded message.
```
~/Desktop/burrows> java-algs4 MoveToFront - < abra.txt | java-algs4 MoveToFront +
ABRACADABRA!
```

_Circular suffix array._ To efficiently implement the key component in the Burrows-Wheeler transform, you will use a fundamental data structure know as _circular suffix array_, which describes the abstraction of a sorted array of the n circular suffix of a string of length n. As an example, consider the string `ABRACADABRA!` of length 12. The table below shows its 12 circular suffixes and the result of sorting them.
```
 i       Original Suffixes           Sorted Suffixes         index[i]
--    -----------------------     -----------------------    --------
 0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
 1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
 2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
 3    A C A D A B R A ! A B R     A B R A C A D A B R A !    0
 4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
 5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
 6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
 7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
 8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
 9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
```
We define `index[i]` to be the index of the original suffix that appears _ith_ in the sorted array. For example, `index[11]` = `2` means that the 2 _nd_  original suffix appears 11 _th_ in the sorted order (i.e., last alphabetically).

_Explicit_ : 
*How to get `Sorted Suffixes`?*
We have `Original Suffixes`: `A B R A C A D A B R A !`  `B R A C A D A B R A ! A` and so on. Sort them.

_For example_ The index of the _0th_ Sorted Suffixes `! A B R A C A D A B R A` is _11th_ Original Suffixes `A B R A C A D A B R A !`.


_Burrows-Wheeler transform._ The goal of the Burrow-Wheeler transform is not to compress a message, but rather to transform it into a form that is more amenable for compression. The Burrows-Wheeler transform rearranges the characters in the input so that there are lots of cluster with repeated characters, but in such a way that it still possible to recover the original input. It relies on the following intuition: if you see the letters hen in English text, then, most of the time, the letter preceding it is either `t`  to `w`, then you would have a propitious opportunity for data compression.

- _Burrows–Wheeler transform._ The Burrows–Wheeler transform of a string _s_ of length _n_ is defined as follows: Consider the result of sorting the _n_ circular suffixes of _s_. The Burrows–Wheeler transform is the last column in the sorted suffixes array `t[]`, preceded by the row number `first` in which the original string ends up. Continuing with the "`ABRACADABRA!`" example above, we highlight the two components of the Burrows–Wheeler transform in the table below.
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202412012111053.png)

![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202412012111906.png)

- _Burrows–Wheeler inverse transform._ Now, we describe how to invert the Burrows–Wheeler transform and recover the original input string. If the _jth_ original suffix (original string, shifted _j_ characters to the left) is the _ith_ row in the sorted order, we define `next[i]` to be the row in the sorted order where the (_j_ + 1)_st_ original suffix appears. For example, if `first` is the row in which the original input string appears, then `next[first]` is the row in the sorted order where the 1_st_ original suffix (the original string left-shifted by 1) appears; `next[next[first]]` is the row in the sorted order where the 2_nd_ original suffix appears; `next[next[next[first]]]` is the row where the 3_rd_ original suffix appears; and so forth.
	- _Inverting the message given t[], first, and the next[] array._ The input to the Burrows–Wheeler decoder is the last column `t[]` of the sorted suffixes along with `first`. From `t[]`, we can deduce the first column of the sorted suffixes because it consists of precisely the same characters, but in sorted order.![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202412012112779.png)Now, given the `next[]` array and `first`, we can reconstruct the original input string because the first character of the _i_th original suffix is the _i_th character in the input string. In the example above, since `first` = `3`, we know that the original input string appears in row 3; thus, the original input string starts with `'A'` (and ends with `'!'`). Since `next[first]` = `7`, the next original suffix appears in row 7; thus, the next character in the original input string is `'B'`. Since `next[next[first]]` = `11`, the next original suffix appears in row 11; thus, the next character in the original input string is `'R'`.
	- _Constructing the next[] array from t[] and first._ Amazingly, the information contained in the Burrows–Wheeler transform suffices to reconstruct the `next[]` array, and, hence, the original message! Here’s how. It is easy to deduce a `next[]` value for a character that appears exactly once in the input string. For example, consider the suffix that starts with `'C'`. By inspecting the first column, it appears 8_th_ in the sorted order. The next original suffix after this one will have the character `'C'` as its last character. By inspecting the last column, the next original appears 5_th_ in the sorted order. Thus, `next[8]` = `5`. Similarly, `'D'` and `'!'` each occur only once, so we can deduce that `next[9]` = `2` and `next[0]` = `3`.
	 ![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202412012130968.png)

## Problems
1. CircularSuffixArray实现

```
 i       Original Suffixes           Sorted Suffixes         index[i]
--    -----------------------     -----------------------    --------
 0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
 1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
 2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
 3    A C A D A B R A ! A B R     A B R A C A D A B R A !    0
 4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
 5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
 6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
 7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
 8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
 9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
```
后缀数组其实是
```
 i       Original Suffixes         
--    -----------------------    
 0    A B R A C A D A B R A !   
 1    B R A C A D A B R A !
 2    R A C A D A B R A !
 3    A C A D A B R A !
 4    C A D A B R A ! 
 5    A D A B R A ! 
 6    D A B R A !
 7    A B R A ! 
 8    B R A ! 
 9    R A ! 
10    A ! 
11    ! 
```
循环其实是将后面再接上一个原数组直至长度与原数组一致
sorted就是将original排序，`index[i]`是第`i`个sorted数组在original中的索引

_Performance requirements._   On typical English text, your data type must use space proportional to _n_ + _R_ (or better) and the constructor must take time proportional to _n_ log _n_ (or better). The methods `length()` and `index()` must take constant time in the worst case.

要求时间复杂度为$O(n+R)$，所以不能新建n个String来表示所有的循环后缀排序，因为新建String使用的是复制原数组复杂度$O(n^2)$。

`FAQ`中提示使用一个索引来指向输入数组，然后维护一个后缀首字母的指针。
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202412021406277.png)
原来一头雾水，参考飞猪和他人代码逐渐明白了
首先是一个基础问题，`Java`中所有函数中的函数传递都是`值传递`
- 建立一个传递进去的参数的副本
- `int`, `double`这些基本数据类型，会将值赋值到一个新的地址，在函数内改变并不会改变调用函数的方法中的原参数值。
- `String`,`int[]`值传递传入的其实和C++一样是一个头指针，把指针赋值到一个新的指针，其实是两个指向统一地址的不同指针，在函数内改变会影响到调用函数的方法中的原参数。

所以可以建立一个可以表示所有循环后缀的`String`，然后维护一个相应的循环后缀字符串的首字母指针。
- 以上面的String为例不同的循环后缀其实是拼接两个原字符串`A B R A C A D A B R A ! A B R A C A D A B R A !`
- 第`i`个循环后缀字符串就是从`substring(i, i + length of 原数组)`  

排序的话使用字典序即可，先比长度，相等后比每个位置字符的大小。

2. BurrowsWheeler中`inverseTransform`的实现
比较难以理解但是慢慢看后还是挺简单的
我们所有的仅仅是一个sorted中原字符串的位置，以及每个sorted中最后一个字符
以上面字符串为例
```
 i           first                      last          
--    -----------------------  -----------------------
 0                                       A
 1                                       R
 2                                       D
 3*                                      !
 4                                       R
 5                                       C
 6                                       A
 7                                       A
 8                                       A
 9                                       A
 10                                      B
 11                                      B
```
由于字典序和循环的性质，我们有`last`中其实就是`first`字符的不同次序，而`first`有序的
所以可以重构`first`（`last` 排序即可）

`next[]`就是`first`中字符到`last`对应的`i`
- 对于只出现一次的字符很好搞定`! C D`。直接找就行
- 对于重复出现的`A B R`，我们有
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202412021436352.png)

重建原数组很简单，我们有起始数组的下标`i`和首字符，根据定义`nest[i]`就是将`first[i]`中首字符移到所形成的字符串，其首字符就是原数组的第二个字符，以此类推就能重构出原数组。