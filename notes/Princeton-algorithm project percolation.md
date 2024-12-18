# Percolation
## 大意
*渗流（Percolation）* 给定一个由随机分布的绝缘材料和金属材料组成的复合系统：其中金属材料需要占多大比例才能使该复合系统成为电导体？给定一个表面有水（或下面有油）的多孔地形，在什么条件下水能够流到底部（或油能够涌到表面）？科学家们已经定义了一个抽象的过程，称为渗滤，用来模拟这种情况。

*模型（model）*  我们用 n×n 个点的网格来模拟渗滤系统。每个点要么是开放的，要么是阻塞的。一个完整网点是一个开放网点，它可以通过相邻（左、右、上、下）开放网点链连接到顶排的开放网点。如果底行有一个完整站点，我们就说这个系统发生了渗透。换句话说，如果我们填满了与顶排相连的所有空位，而这一过程又填满了底排的某个空位，那么系统就发生了渗透。(在绝缘/金属材料的例子中，开放位点与金属材料相对应，因此渗滤系统从上到下有一条金属路径，全位点导电。在多孔物质的例子中，空位对应的是水可能流经的空隙，因此渗流系统会让水充满空位，从上到下流动）。

*问题（problem）* 在一个著名的科学问题中，研究人员对以下问题很感兴趣：如果站点被独立设置为开放概率为 p（因此阻塞概率为 1-p），那么系统渗透的概率是多少？当 p 等于 0 时，系统不会渗透；当 p 等于 1 时，系统会渗透。下图显示了 20×20 随机网格（左）和 100×100 随机网格（右）的场地空置概率 p 与渗流概率的关系。

![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410192230087.png)
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410192230749.png)


当 n 足够大时，存在一个临界值 p*，当 p < p* 时，一个 n-by-n 的随机网格几乎不会渗流，而当 p > p* 时，一个 n-by-n 的随机网格几乎总是会渗流。目前还没有确定渗流临界值 p* 的数学解决方案。你的任务是编写一个计算机程序来估算 p*。

## Percolation data type
### API 实现
```java
public class Percolation {

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n)

    // opens the site (row, col) if it is not open already
    public void open(int row, int col)

    // is the site (row, col) open?
    public boolean isOpen(int row, int col)

    // is the site (row, col) full?
    public boolean isFull(int row, int col)

    // returns the number of open sites
    public int numberOfOpenSites()

    // does the system percolate?
    public boolean percolates()

    // test client (optional)
    public static void main(String[] args)
}
```

## Iteration1
- 建立虚拟top和bottom节点，建立一个二维grid数组维护site是否open，建立一个并查集来维护top和bottom的联通和每个节点是否full。预先将top与第一行合并，bottom与最后一行合并
- 在open(row, col)时，$grid[row][col] = true$，uf将该点的合法邻居（范围内，open）合并
- $isOpen(row, col)$ 使用grid判断
- $isFull(row, col)$ 使用$uf.find(top) == uf.find(bottom)$判断

### advantages
能跑，但没保存源代码

### drawback
1. 只使用一个并查集合时，考虑到将top与bottom合并会引起“倒灌”现象，但实验要求水只能从上到下流入。
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410152212025.png)
2. 虚拟节点与一层相合并时会导致该层所有节点在为设置为open的前提下先灌满水，相当于没给你容器让你取水，不符合逻辑

## Iteration2
- 建立虚拟top节点，建立一个二维grid数组维护site是否open，建立一个并查集来维护top和其他节点的联通从而判断节点是否full。
- 在open(row, col)时，$grid[row][col] = true$， 如果是第一层节点则将其设为open并于top节点联通，uf将该点的合法邻居（范围内，open）合并
- $isOpen(row, col)$ 使用grid判断
- $isFull(row, col)$ 使用$uf.find(top) == uf.find((row - 1) * siz + col)$判断
- $percolation()$遍历最底层节点判断其是否与top节点联通
### advantages
1. 答案正确 
2. 解决了Iteration1中的“倒灌”问题，同时虚拟节点不直接与一层连接符合逻辑

### drawback
最后的$percolation()$每次遍历一遍底层节点在多次判断时以时间换取正确性牺牲效率导致在TIMING测试中失败
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410152227554.png)

## Iteration3
- 建立虚拟top与bottom节点，建立一个一维grid数组维护site是否open，建立两个并查集，一个$ufForfull$来维护top和其他节点的联通从而判断节点是否full， 另一个$uf$维护top与bottom的联通。
- 在open(row, col)时，$grid[row][col] = true$， 如果是第一层节点则将其设为open并于top节点联通（两个并查集都设置）；如果是最底层节点则只将$uf$中该节点与bottom联通。$uf$, $ufForFull$将该点的合法邻居（范围内，open）合并
- $isOpen(row, col)$ 使用grid判断
- $isFull(row, col)$ 使用$ufForfull.find(top) == ufForfull.find((row - 1) * siz + col)$判断
- $percolation()$使用$uf.find(topVir) == uf.find(bottomVir);$
```java
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int siz;
    private int topVir;
    private int bottomVir;
    private int openSite;
    private boolean[] grid;
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF ufForFull;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        siz = n;
        topVir = 0;
        bottomVir = siz * siz + 1;
        openSite = 0;
        grid = new boolean[siz * siz + 1];
        uf = new WeightedQuickUnionUF(siz * siz + 2);
        ufForFull = new WeightedQuickUnionUF(siz * siz + 1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || row > siz || col < 1 || col > siz) {
            throw new IllegalArgumentException("Invalid index");
        }
        int location = siz * (row - 1) + col;
        if (!grid[location]) {
            // Open the site.
            grid[location] = true;
            openSite++;

            // Fill the site if we open the site which is in row 1.
            if (location <= siz) {
                uf.union(topVir, location);
                ufForFull.union(topVir, location);
            }

            if (location > siz * (siz - 1)) {
                uf.union(bottomVir, location);
            }
            // Fill the neighbors which are also open
            int[] dx = { 1, 0, -1, 0 };
            int[] dy = { 0, 1, 0, -1 };
            for (int i = 0; i < 4; i++) {
                int nx = row + dx[i];
                int ny = col + dy[i];
                int nextloc = siz * (nx - 1) + ny;

                // out of index
                if (nx < 1 || nx > siz || ny < 1 || ny > siz) continue;
                // the site is not open
                if (!grid[nextloc]) continue;

                // union the site and the original site
                uf.union(location, nextloc);
                ufForFull.union(location, nextloc);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 1 || row > siz || col < 1 || col > siz) {
            throw new IllegalArgumentException("Invalid index");
        }

        return grid[siz * (row - 1) + col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row < 1 || row > siz || col < 1 || col > siz) {
            throw new IllegalArgumentException("Invalid index");
        }
        return ufForFull.find(siz * (row - 1) + col) == ufForFull.find(topVir);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSite;
    }

    // does the system percolate?
    public boolean percolates() {
        return uf.find(topVir) == uf.find(bottomVir);
    }

    // test client (optional)
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        Percolation p = new Percolation(n);
        while (!in.isEmpty()) {
            int row = in.readInt();
            int col = in.readInt();
            p.open(row, col);
        }
    }
}
```
### advantage
1. 答案正确
2. 通过专门设置一个并查集来维护两个虚拟节点的联通将$percolates()$的复杂度从$O(n)$降到了$O(1)$