package com.lld.models;

public class CacheElement<K,V> {
    private K key;
    private V value;
    public CacheElement(K key, V value){
        this.key = key;
        this.value = value;
    }

    public K key(){
        return this.key;
    }

    public V getValue(){
        return this.value;
    }
}
