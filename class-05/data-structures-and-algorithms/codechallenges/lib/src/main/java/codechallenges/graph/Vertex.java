package codechallenges.graph;

import java.util.Objects;

public class Vertex<T extends Comparable<? super T>> implements Comparable<Vertex<T>>
{
    public T value;

    public Vertex(T value)
    {
        this.value = value;
    }

    @Override
    public int compareTo(Vertex<T> otherVertex)
    {
        return value.compareTo(otherVertex.value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return Objects.equals(value, vertex.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}
