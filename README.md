# Pit
Pit project

This project contains an Android application written in Java

- Pit is a ViewGroup that renders an interactive horizontal 2D graph of points, between every point there is a connected edge (linear edge).
    every point of Pit is draggable.
- Pit also draws the Origin axis lines
- when a point is dragged, the view is rendered again in order to show the edges, that means Pit is responsive :)
- if a user drags a point before or after another point, then Pit will change that point order and therefore the edges will still render beautifully.
    Pit should have an interface to add point/s. every point is added at the origin axis (0, 0)
