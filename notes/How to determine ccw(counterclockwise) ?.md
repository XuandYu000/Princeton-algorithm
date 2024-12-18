# CCW.
CCW. Given three points a, b and c, is $a \rightarrow b \rightarrow c$ a counterclockwise turn?

![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410182158617.png)

# Solution
- Determinant (or cross product) gives $2\times$ signed area of planar triangle.
$$
2 \times Area(a, b, c) = \left| \begin{array}{ccc} a_x & a_y & 1 \\ b_x & b_y & 1 \\ c_x & c_y & 1  \end{array} \right| =(b_x - a_x)(c_y - a_y) - (b_y - a_y)(c_x - a_x)
$$
- If signed area > 0, then $a\rightarrow b\rightarrow c$ is ccw.
- If signed area < 0, then $a\rightarrow b\rightarrow c$ is cw(clockwise).
- If signed area = 0, then $a\rightarrow b\rightarrow c$ are collinear
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202410182207459.png)

```java
public class Point2D
{
	private final double x;
	private final double y;

	public Point2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	...
	public static int ccw(Point2D a, Point2D b, Point2D c)
	{
		double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
		if(area2 < 0) return -1; // clockwise
		else if(area2 > 0) return 1; // ccw
		else return 0; // collinear
	}
}
```
