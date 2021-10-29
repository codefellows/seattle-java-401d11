package codechallenges.hashMap;

import java.util.AbstractMap;

public class HashMapPair<K, V> extends AbstractMap.SimpleEntry<K, V> implements Comparable<HashMapPair<K, V>>
{
    public HashMapPair(K key, V value)
    {
        super(key,value);
    }

    @Override
    public int compareTo(HashMapPair<K, V> o)
    {
        throw new UnsupportedOperationException("HashMapPair doesn't support comparison!");
    }
}
