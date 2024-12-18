Seam-carving is a content-aware image resizing technique where the image is reduced in size by one pixel of height (or width) at a time.

简而言之，就是在保证图片缩放中保证主要像素不变，每次移除只移除最不相关像素。如下图所示。

注意：最左侧的那个人时Josh Hug，UCB CS61b Fall 20主讲人。

![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411221739432.png) 
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411221739179.png)


Finding and removing a seam involves three parts a tiny bit of notation:
0. _Notation._ In image processing, pixel (_x_, _y_) refers to the pixel in column _x_ and row _y_, with pixel (0, 0) at the upper left corner and pixel (_W_ − 1, _H_ − 1) at the bottom right corner. This is consistent with the [Picture](https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/Picture.html) data type in `algs4.jar`. _Warning_: this is the opposite of the standard mathematical notation used in linear algebra where (_i_, _j_) refers to row _i_ and column _j_ and with Cartesian coordinates where (0, 0) is at the lower left corner.  We also assume that the color of a pixel is represented in RGB space, using three integers between 0 and 255. This is consistent with the [java.awt.Color](http://docs.oracle.com/javase/7/docs/api/java/awt/Color.html) data type.
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411221742785.png)
1. _Energy calculation._ The first step is to calculate the _energy_ of each pixel, which is a measure of the importance of each pixel—the higher the energy, the less likely that the pixel will be included as part of a seam (as we'll see in the next step). In this assignment, you will implement the _dual-gradient energy function_, which is described below. Here is the dual-gradient energy function of the surfing image above:
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411221744531.png)
2.  _Seam identification._ The next step is to find a vertical seam of minimum total energy. This is similar to the classic shortest path problem in an edge-weighted digraph except for the following:
- The weights are on the vertices instead of the edges.
- We want to find the shortest path from any of the _W_ pixels in the top row to any of the _W_ pixels in the bottom row.
- The digraph is acyclic, where there is a downward edge from pixel (_x_, _y_) to pixels (_x_ − 1, _y_ + 1), (_x_, _y_ + 1), and (_x_ + 1, _y_ + 1), assuming that the coordinates are in the prescribed range.
3.   _Seam removal._ The final step is to remove from the image all of the pixels along the seam.

_Computing the energy of a pixel._ You will use the _dual-gradient energy function_: The energy of pixel (x,y)(x,y) is $\sqrt{\Delta^{2}_{x}(x,y) + \Delta^{2}_{y}(x, y)}$, where the square of the _x-gradient_ $\Delta^{2}_{x}(x,y)={R_x(x, y)}^2 + {G_x(x, y)}^2 + {B_x(x, y)}^2$,  and where the central differences $R_x(x, y)$, $G_x(x, y)$, $B_x(x, y)$  are the differences in the red, green, and blue components between pixel (_x_ + 1, _y_) and pixel (_x_ − 1, _y_), respectively. The square of the _y_-gradient $\Delta^{2}_{y}(x, y)$  is defined in an analogous manner. We define the energy of a pixel at the border of the image to be 1000, so that it is strictly larger than the energy of any interior pixel.

As an example, consider the 3-by-4 image (supplied as [3x4.png](https://coursera.cs.princeton.edu/algs4/assignments/seam/files/3x4.png)) with RGB values—each component is an integer between 0 and 255—as shown in the table below:

As an example, consider the 3-by-4 image (supplied as [3x4.png](https://coursera.cs.princeton.edu/algs4/assignments/seam/files/3x4.png)) with RGB values—each component is an integer between 0 and 255—as shown in the table below:
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411221754910.png)

The ten border pixels have energy 1000. Only the pixel (1, 1) and (1, 2) are not nontrivial. We calculate the energy of pixel (1, 2) in detail:
![image.png](https://raw.githubusercontent.com/XuandYu000/picture/main/202411221808083.png)


