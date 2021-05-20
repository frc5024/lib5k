# Path Planning

{% include katex.html %}

Lib5K provides a few types of path generation tools, mainly: [`Path`](/lib5k/javadoc/io/github/frc5024/purepursuit/pathgen/Path.html) and [`SmoothPath`](/lib5k/javadoc/io/github/frc5024/purepursuit/pathgen/SmoothPath.html). These tools are used for converting a small list of control points (aka. waypoints) into a longer list of closely spaced goal poses for the robot to follow.

## Path

The `Path` class creates a linear, point-to-point path from a few control points:

![Visualization of a Path](/lib5k/assets/PurePursuit_UnitTest_FourPointPath.png)

The process for generating the inner points (blue) is as follows. For every pair of control points in the path, we can get their magnitude with (where $$\vec{b}$$ is the first control point, and $$\vec{e}$$ is the second):

$$
\begin{aligned}
    \vec{d} &= \vec{e} - \vec{b} \\
    \lVert m \rVert &= \sqrt{\vec{d}_0^2 + \vec{d}_1^2}
\end{aligned}
$$

and get their unit vector with:

$$
\vec{u} = \frac{\vec{d}}{m}
$$

To determine the number of inner points that are required between the two control points, we simply divide the magnitude by a `spacing` ($$s$$) constant (we almost always use 6 inches for this value). Where $$\vec{P_{n-1}}$$ is the previously calculated inner point, we can place the next inner point ($$\vec{P_n}$$) with:

$$
\vec{P_n} = \vec{P_{n-1}} + \vec{u}s
$$

Continuing this process through all control points, we get a linear path. 

*(Java implementation is [here](https://github.com/frc5024/lib5k/blob/5cf14136ff0c00bdd2d5dc7caadb130f2d60a5ec/lib5k/src/main/java/io/github/frc5024/purepursuit/pathgen/Path.java#L41-L88))*

## SmoothPath

The `SmoothPath` class extends `Path`, as it takes a linear, point-to-point path and modifies it using a set number of erosion steps.

![Visualization of a SmoothPath](/lib5k/assets/PurePursuit_UnitTest_SmoothFourPointPath.png)

One cycle of the erosion step involves running computing the following for every inner point in the path. Cycling multiple times will improve the smoothness of the path. When eroding a path, a copy is made, so there is a list of origional, un-modified points, and a list of modified points (the inner points being modified by the erosion step). In the following equations, $$\vec{P_n}$$ is the current un-modified inner point, $$\vec{A_n}$$ is the current modified inner point. Like above, the subscripts `n-1` and `n+1` refer to the previous and next points. $$w_1$$ and $$w_2$$ are coefficients that affect the aggression of each erosion step.

$$
\vec{A_n} = \vec{A_n} + w_1(\vec{P_n} - \vec{A_n}) + w_2(\vec{A_{n-1}} + \vec{A_{n+1}} - 2\vec{A_n})
$$

The above visualization uses `0.5` as both the $$w_1$$ and $$w_2$$ values. 

*(Java implementation is [here](https://github.com/frc5024/lib5k/blob/5cf14136ff0c00bdd2d5dc7caadb130f2d60a5ec/lib5k/src/main/java/io/github/frc5024/purepursuit/util/Smoothing.java#L24-L57))*

