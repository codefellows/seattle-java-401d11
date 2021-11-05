package codechallenges.graph;

import org.junit.jupiter.api.Test;

public class GraphTest
{
    @Test
    void Test_a_small_graph()
    {
        Graph<String> sut = new Graph<>();

        Vertex<String> aNode = sut.addNode("A");
        Vertex<String> bNode = sut.addNode("B");
        Vertex<String> cNode = sut.addNode("C");

        sut.addEdge(aNode, bNode, 1);
        sut.addEdge(aNode, cNode, 2);
        sut.addEdge(bNode, aNode, 3);
        sut.addEdge(cNode, aNode, 4);

        System.out.println(sut);
        System.out.println("Size: " + sut.size());
    }
}
