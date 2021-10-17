package com.lld.models;

public class CacheElement<K,V> {
    private K key;
    private V value;
    private int count;
    public CacheElement(K key, V value){
        this.key = key;
        this.value = value;
        this.count = 1;
    }

    public K key(){
        return this.key;
    }

    public V getValue(){
        return this.value;
    }

    public void setValue(V value){
        this.value = value;
    }

    public int getCount(){
        return this.count;
    }

    public void incrementCount(){
        this.count++;
    }
}
