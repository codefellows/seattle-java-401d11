package codechallenges.graph;

// NOTE: This is a directed edge
public class Edge<T extends Comparable<? super T>> implements Comparable<Edge<T>>
{
    public Vertex<T> destination;
    public int weight;

    public Edge(Vertex<T> destination, int weight)
    {
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge<T> otherEdge)
    {
        return destination.compareTo(otherEdge.destination);
    }
}
