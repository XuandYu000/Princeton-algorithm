# Problem
**The problem.** Given a set of _n_ distinct points in the plane, find every (maximal) line segment that connects a subset of 4 or more of the points.

![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410192231916.png)

# Solutions
**Point data type.** Create an immutable data type `Point` that represents a point in the plane by implementing the following API:
```java
public class Point implements Comparable<Point> {
   public Point(int x, int y)                         // constructs the point (x, y)

   public   void draw()                               // draws this point
   public   void drawTo(Point that)                   // draws the line segment from this point to that point
   public String toString()                           // string representation

   public               int compareTo(Point that)     // compare two points by y-coordinates, breaking ties by x-coordinates
   public            double slopeTo(Point that)       // the slope between this point and that point
   public Comparator<Point> slopeOrder()              // compare two points by slopes they make with this point
}
```

值得提一点的是最后一个接口$SlopeOrder$的实现
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410192235944.png)
接口中定义了一个$compare$方法需要实现，这个比较器是需要单独实现的，我之前把这个和比较函数$compareTo$实现混了。

**Line segment data type.** **Brute force.** 不用重复了

**A faster, sorting-based solution.** Remarkably, it is possible to solve the problem much faster than the brute-force solution described above. Given a point _p_, the following method determines whether _p_ participates in a set of 4 or more collinear points.

- Think of _p_ as the origin.
    
- For each other point _q_, determine the slope it makes with _p_.
    
- Sort the points according to the slopes they makes with _p_.
    
- Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to _p_. If so, these points, together with _p_, are collinear.

Applying this method for each of the _n_ points in turn yields an efficient algorithm to the problem. The algorithm solves the problem because points that have equal slopes with respect to _p_ are collinear, and sorting brings such points together. The algorithm is fast because the bottleneck operation is sorting.
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410192242486.png)

*API* :
```java
public class FastCollinearPoints {
   public FastCollinearPoints(Point[] points)     // finds all line segments containing 4 or more points
   public           int numberOfSegments()        // the number of line segments
   public LineSegment[] segments()                // the line segments
}
```

The method `segments()` should include each _maximal_ line segment containing 4 (or more) points exactly once. For example, if 5 points appear on a line segment in the order _p_→_q_→_r_→_s_→_t_, then do not include the subsegments _p_→_s_ or _q_→_t_.

_Corner cases._ Throw an `IllegalArgumentException` if the argument to the constructor is `null`, if any point in the array is `null`, or if the argument to the constructor contains a repeated point.

_Performance requirement._ The order of growth of the running time of your program should be _n_2 log _n_ in the worst case and it should use space proportional to _n_ plus the number of line segments returned. `FastCollinearPoints` should work properly even if the input has 5 or more collinear points.

## Iteration 1
在找number时顺便把答案找出来。即将找number和找路径统一实现在$numberOfSegments()$中，避免找number时和找路径时分别计算。

当搜寻到points中第i个元素时
1. 建立backup数组，将backup按照其余点与该点的斜率大小排序
2. 使用指针i, j分别指向区间左右两边，当$backup[j] == backup[j+1]$时j指针右移，同时维护该区间y坐标最高的point，和斜率重复次数repeat
3. $back[j]!=backup[j+1]$时，repeat 大于等于3说明有三段线段斜率相等符合条件，加入答案数组。
4. $j>len$时左指针右移

*Disadvantage* 
1. 左指针移动步长始终为1，当一个线段上含有5个点时，i指针右移后会把该线段子区间含有3，4个点的线段包含进去
2. 当左指针并不是4个及以上点组成的线段的y坐标最低点时，会造成重复计算。

## Iteration 2
直接在构造函数中完成$segements()$的工作，使用$ArrayList$暂存答案。
在`Iteration 1`的第三步维护斜率相同的点的区间，同时加入额外判断条件
1. 当前的左指针必须是重复点中y坐标最小的点。
2. 在加入答案中要选上述区间中y坐标最大的点。

```java
points = Arrays.copyOf(points, len);
        segments = new ArrayList<>();
        Point[] backup = Arrays.copyOf(points, len);

        for (Point p : points) {
            Arrays.sort(backup, p.slopeOrder());
            for (int i = 1; i < len; ) {
                int j = i + 1;
                while (j < len && p.slopeTo(backup[i]) == p.slopeTo(backup[j])) {
                    j++;
                }
                if (j - i >= BOTTOMBOUND && p.compareTo(min(backup, i, j - 1)) < 0) {
                    segments.add(new LineSegment(p, max(backup, i, j - 1)));
                }
                if (j == len) {
                    break;
                }
                i = j;
            }
        }
```