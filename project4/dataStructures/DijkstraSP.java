package dataStructures;
/**
 *  The {@code DijkstraSP} class represents a data type for solving the
 *  single-source shortest paths problem in edge-weighted digraphs
 *  where the edge weights are nonnegative.
 *  <p>
 *  This implementation uses Dijkstra's algorithm with a binary heap.
 *  The constructor takes time proportional to <em>E</em> log <em>V</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the {@code distTo()} and {@code hasPathTo()} methods take
 *  constant time and the {@code pathTo()} method takes time proportional to the
 *  number of edges in the shortest path returned.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 * This modified version of Dijkstra allows for 3 shortes path calculations:
 * by distance, by price, or fewest hops. Fewest hops is just a breadth first
 * search since weights are hard-coded as 1.
 *
 * @author Craig Mazzotta
 */
public class DijkstraSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices
    private int mode; //1 for distances, 2 for price, 3 for hops

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every other
     * vertex in the edge-weighted digraph {@code G}.
     *
     * @param  G the edge-weighted digraph
     * @param  s the source vertex
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DijkstraSP(EdgeWeightedGraph G, int s,int mode) {
        this.mode = mode;

        for (Edge e : G.edges()) {
            if (e.dist() < 0){
              throw new IllegalArgumentException("edge " + e + " has negative distance");
            } else if(e.pri() < 0){
              throw new IllegalArgumentException("edge " + e + " has negative price");
            }
        }

        distTo = new double[G.V()];
        edgeTo = new Edge[G.V()];
        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : G.adj(v))
                relax(e,v);
        }

        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(Edge e,int v) {
        int w = e.other(v);
        switch(this.mode){
          case 1:
            if (distTo[w] > distTo[v] + e.dist()) {
                distTo[w] = distTo[v] + e.dist();
                edgeTo[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
            break;
          case 2:
            if (distTo[w] > distTo[v] + e.pri()) {
                distTo[w] = distTo[v] + e.pri();
                edgeTo[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
            break;
          case 3:
            if (distTo[w] > distTo[v] + 1) {
                distTo[w] = distTo[v] + 1;
                edgeTo[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
            break;
        }

    }

    /**
     * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @param  v the destination vertex
     * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
     *         {@code Double.POSITIVE_INFINITY} if no such path
     */
    public double distTo(int v) {
        return distTo[v];
    }

    /**
     * Returns true if there is a path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     */
    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges, and {@code null} if no such path
     */
    public Iterable<Edge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        Edge e = edgeTo[v];
        int prev=v;
        while (e != null) {
            path.push(e);
            int next = e.other(prev);
            prev = next;
            e = edgeTo[next];
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(EdgeWeightedGraph G, int s) {

        // check that edge weights are nonnegative
        switch(this.mode){
          case 1:
            for (Edge e : G.edges()) {
                if (e.dist() < 0) {
                    System.err.println("negative edge dist detected");
                    return false;
                }
            }
            break;
          case 2:
            for (Edge e : G.edges()) {
                if (e.pri() < 0) {
                    System.err.println("negative edge pri detected");
                    return false;
                }
            }
            break;
          case 3:
            break;
        }


        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (Edge e : G.adj(v)) {
                int w = e.either();
                switch(this.mode){
                  case 1:
                    if (distTo[v] + e.dist() < distTo[w]) {
                        System.err.println("edge " + e + " not relaxed");
                        return false;
                    }
                    break;
                  case 2:
                    if (distTo[v] + e.pri() < distTo[w]) {
                        System.err.println("edge " + e + " not relaxed");
                        return false;
                    }
                    break;
                  case 3:
                    if (distTo[v] + 1 < distTo[w]) {
                        System.err.println("edge " + e + " not relaxed");
                        return false;
                    }
                    break;
                }

            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            int v = e.either();
            if (w != e.other(v)) return false;

            switch(this.mode){
              case 1:
                if (distTo[v] + e.dist() != distTo[w]) {
                    System.err.println("edge " + e + " on shortest path not tight");
                    return false;
                }
                break;
              case 2:
                if (distTo[v] + e.pri() != distTo[w]) {
                    System.err.println("edge " + e + " on shortest path not tight");
                    return false;
                }
                break;
              case 3:
                if (distTo[v] + 1 != distTo[w]) {
                    System.err.println("edge " + e + " on shortest path not tight");
                    return false;
                }
                break;
            }

        }
        return true;
    }


}
