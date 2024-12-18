## Description

Given the standings in a sports division at some point during the season, determine which teams have been mathematically eliminated from winning their division.

_The baseball elimination problem_   In the [baseball elimination problem](https://en.wikipedia.org/wiki/Maximum_flow_problem#Baseball_elimination), there is a division consisting of _n_ teams. At some point during the season, team _i_ has `w[i]` wins, `l[i]` losses, `r[i]` remaining games, and `g[i][j]` games left to play against team _j_. A team is mathematically eliminated if it cannot possibly finish the season in (or tied for) first place. The goal is to determine exactly which teams are mathematically eliminated. For simplicity, we assume that no games end in a tie (as is the case in Major League Baseball) and that there are no rainouts (i.e., every scheduled game is played).

The problem is not as easy as many sports writers would have you believe, in part because the answer depends not only on the number of games won and left to play, but also on the schedule of remaining games. To see the complication, consider the following scenario:
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411251523978.png)

Montreal is mathematically eliminated since it can finish with at most 80 wins and Atlanta already has 83 wins. This is the simplest reason for elimination. However, there can be more complicated reasons. For example, Philadelphia is also mathematically eliminated. It can finish the season with as many as 83 wins, which appears to be enough to tie Atlanta. But this would require Atlanta to lose all of its remaining games, including the 6 against New York, in which case New York would finish with 84 wins. We note that New York is not yet mathematically eliminated despite the fact that it has fewer wins than Philadelphia.
## Step

- Write code to read in the input file and store the data.

- Draw by hand the `FlowNetwork` graph that you want to construct for a few small examples. Write the code to construct the `FlowNetwork`, print it out using the `toString()` method, and and make sure that it matches your intent. Do not continue until you have thoroughly tested this stage.
 
- Compute the maxflow and mincut using the `FordFulkerson` data type. You can access the value of the flow with the `value()` method; you can identify which vertices are on the source side of the mincut with the `inCut()` method.

- The `BaseballElimination` API contains the public methods that you will implement. For modularity, you will want to add some private helper methods of your own.

## Problems
1. "Atlanta       83 71  8  0 1 6 1"字符串使用`split(" ")`分割后会有空字符存在

正则表达式匹配 `split("\\s+")` 

2. 建图时网络流上节点team vertices节点与参赛队伍的对应关系比较困难
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411261704403.png)
之前是想使用函数映射将team vertices与参赛队伍（0，……，n - 1）对应，但是那样采用的空间消耗就是$V^2$，因为建立网络流时要去掉询问的参赛队伍（上图询问的队伍team 4）

所以空间上采用了设置偏置量（s节点，game vertices节点的总数作为偏置bias），但是team vertices中的节点对应的参赛队伍一直在变化

- 询问team 4，那么网络流中team vertices中就是`0, 1, 2, 3`
- 询问team 2，那么网络流中team vertices中就是`0, 1, 3, 4`

参考飞猪的代码后得到解决方法：不同网络流图中s, t, game vertices节点的数量和映射关系是不变的，只是在team中会有变化，那么直接将team vertices规模扩为参赛队伍总数即可。那样询问的队伍在流网络中是一个孤立的节点，不影响后续步骤。

```java
int involveTeams = numberofTeams - 1;  
int numberofGames = ((involveTeams - 1) * involveTeams) >> 1;  
int bias = numberofGames + 1;  
int s = 0;  
int t = numberofGames + involveTeams + 2;
```

3. 流网络中最大流意义不清楚
首先game vertices和team vertices之间的flow不会对结果产生影响，因为容量无穷不管队伍是不是nontrivial Elimination都无所谓。

那么只剩下s->game vertices 和 team vertices -> t的关系

- s->game vertices: 流量是剩余比赛。
- team vertices -> t: 流量为当前team还差多少win可以和最理想状态下（后续比赛全win）被询问节点打平。

那么意义就明确了，首先s->game vertices的容量总数最少等于最大流（maxflow）
- 相等：将所有场次打完，所有节点最多和最理想状态下（后续比赛全win）被询问节点打平，那么肯定不用被删去。此时$mincut{s}={s}$ 
- 大于：场次没打完就有队伍肯定能胜过最理想状态下（后续比赛全win）被询问节点，那么删去。