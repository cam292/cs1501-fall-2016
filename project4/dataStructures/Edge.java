package dataStructures;
/**
 *  The {@code Edge} class represents a weighted edge in an
 *  {@link EdgeWeightedGraph}. Each edge consists of two integers
 *  (naming the two vertices) and 2 weights: price and distance. The data type
 *  provides methods for accessing the two endpoints of the edge and
 *  the 2 weights. The natural order for this data type is by
 *  ascending order of weight.
 *  <p>
 * @author Craig Mazzotta
 */
public class Edge implements Comparable<Edge>{

    private final int v;
    private final int w;
    private final int dist;
    private final double pri;

    /**
     * Initializes an edge between vertices {@code v} and {@code w} of
     * the given {@code weight}.
     *
     * @param  v one of the cities that forms the edge
     * @param  w the other city that forms the edge
     * @param  dist the distance weight of this edge
     * @param  pri the price weight of this edge
     */
    public Edge(int v, int w, int dist,double pri) {
        this.v = v;
        this.w = w;
        this.dist = dist;
        this.pri = pri;
    }

    /**
     * Returns the distance weight of this edge.
     *
     * @return the distance weight of this edge
     */
    public int dist() {
        return dist;
    }
    /**
     * Returns the price weight of this edge.
     *
     * @return the price weight of this edge
     */
    public double pri() {
        return pri;
    }

    /**
     * Returns either endpoint of this edge.
     *
     * @return either endpoint of this edge
     */
    public int either() {
        return v;
    }

    /**
     * Returns the endpoint of this edge that is different from the given vertex.
     *
     * @param  vertex one endpoint of this edge
     * @return the other endpoint of this edge
     * @throws IllegalArgumentException if the vertex is not one of the
     *         endpoints of this edge
     */
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    /**
     * Compares two edges by distance.
     * Note that {@code compareTo()} is not consistent with {@code equals()},
     * which uses the reference equality implementation inherited from {@code Object}.
     *
     * @param  that the other edge
     * @return a negative integer, zero, or positive integer depending on whether
     *         the weight of this is less than, equal to, or greater than the
     *         argument edge
     */
    public int compareTo(Edge that) {
      if(this.dist > that.dist){
        return 1;
      } else if(this.dist > that.dist){
        return -1;
      } else {
        return 0;
      }
    }


    /**
     * Returns a string representation of this edge.
     *
     * @return a string representation of this edge
     */
    public String toString() {
        return String.format("%d-%d %.5f %d", v, w, dist,pri);
    }

}
