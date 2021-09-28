package datastructures;

public class LinkedList<T>
{
    Node<T> head;

    public void insertBefore(T value, T valueToInsertBefore)
    {
        Node<T> currentNode = head;

        while (currentNode != null)
        {
            if(currentNode.value.equals(valueToInsertBefore))
            {
                // insert before this one
            }
            else
            {
                currentNode = currentNode.next;
            }
        }
    }
}
