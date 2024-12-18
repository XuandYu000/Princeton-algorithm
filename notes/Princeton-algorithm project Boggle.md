## Description

_The Boggle game._ Boggle is a word game. It involves a board made up of 16 cubic dice, where each die has a letter printed on each of its 6 sides. At the beginning of the game, the 16 dice are shaken and randomly distributed into a 4-by-4 tray, with only the top sides of the dice visible. The players compete to accumulate points by building valid words from the dice, according to these rules:

- A valid word must be composed by following a sequence of adjacent dice--two dice are adjacent if they are horizontal, vertical, or diagonal neighbors.(有效单词必须由相邻的块组成，当两个块是上下，左右，对角的邻居时，两个块相邻)
- A valid word can use each die at most once.  有效单词最多使用块中的字符一次
- A valid word must contain at least 3 letters. 有效单词最少含有3个字符
- A valid word must be in the dictionary (which typically does not contain proper nouns). 有效单词必须是字典内含有的

_Scoring._ Valid words are scored according to their length, using this table:

![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411291343397.png)

_The Qu special case._ In the English language, the letter `Q` is almost always followed by the letter `U`. Consequently, the side of one die is printed with the two-letter sequence `Qu` instead of Q( and this two-letter sequence must be used together when forming words). When scoring, `Qu` counts as two letters; for example, the word `QuEUE` scores as a 5-letter word even though it formed by following a sequence of only 4 dice.
## Step

Official:
- Familiarize yourself with the `BoggleBoard.java` data type.
- Use a standard set data type to represent the dictionary, e.g., a `SET<String>`, a `TreeSet<String>`, or a `HashSet<String>`.
- Create the data type `BoggleSolver`. Write a method based on depth-first search to enumerate all strings that can be composed by following sequences of adjacent dice. That is, enumerate all simple paths in the Boggle graph (but there is no need to explicitly form the graph). For now, ignore the special two-letter sequence `Qu`.
- Now, implement the following critical backtracking optimization: _when the current path corresponds to a string that is not a prefix of any word in the dictionary, there is no need to expand the path further_. To do this, you will need to create a data structure for the dictionary that supports the _prefix query_ operation: given a prefix, is there any word in the dictionary that starts with that prefix?
- Deal with the special two-letter sequence `Qu`.

Mine:
本来打算使用alg4中的`TrieSET`类，写了一个未加优化的dfs，正确但是Time和Memory均超。加入的优化分为两部分，第一是使用`TrieSET.keysWithPrefix`存储所有的含有当前前缀的words，然后检测前缀和words中的匹配，但是不行，因为含有$10^4$多个的words，Memory一定超。
第二是使用`TrieSET.longestPrefixof`来看返回的String是否能和当前前缀完全匹配，但是感觉还是不是很顺利。因为返回的最长公共前缀，仍然需要自己手动比较。是代码更加繁琐了。
同时考虑到`TrieSET`中`R=256`对Memory有很大的负担，于是查是否有更简单的方法
参考完大多数的代码后(主要是飞猪的)。找到一种方法
我们只需要一个阉割版的`TrieSET`:包含`add`操作即可。
然后将前缀从空串`""` 开始从`root`开始`sink`，当下沉到空值时剪枝（因为这样的话说明前缀已经不匹配了），理论上这样dfs产生的树会与字典中的合法节点一一对应大大减少Time和Memory操作。

## Problems
1. StringBuilder使用错误
原来以为StringBuilder构造函数可以使用char字符，但是:
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411291646986.png)
很显然不行，只能接受空值，char序列，初始容量和String初始化
如果直接使用`StringBuilder('x') : x is a character` 则会导致char变为int从而初始化容量，达不到把第一个字符加上的效果。

