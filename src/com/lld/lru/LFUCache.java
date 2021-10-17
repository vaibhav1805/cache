package com.lld.lru;

import com.lld.models.Cache;
import com.lld.models.CacheElement;
import com.lld.models.DoublyLinkedList;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LFUCache<K,V> implements Cache<K,V> {
    private final int capacity;
    private int size = 0;
    private final Map<K, CacheElement<K,V>> elementMap;
    private final Map<Integer, DoublyLinkedList<CacheElement<K,V>>> frequencyMap;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public LFUCache(int capacity){
        this.capacity = capacity;
        this.elementMap = new ConcurrentHashMap<>();
        this.frequencyMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean set(K key, V value) {
        this.lock.writeLock().lock();
        try{
            if(elementMap.containsKey(key)){
                CacheElement<K,V> element = elementMap.get(key);
                element.setValue(value);
                get(key);
            }else{
                CacheElement<K,V> element = new CacheElement<>(key, value);
                DoublyLinkedList<CacheElement<K,V>> dll = frequencyMap.getOrDefault(1, new DoublyLinkedList<>());
                if(size >= capacity){
                    CacheElement<K,V> removed = dll.removeLast();
                    elementMap.remove(removed.key());
                }else{
                    size++;
                }
                dll.addFront(element);
                elementMap.put(key, element);
                frequencyMap.put(1, dll);
            }
        }catch (Exception e){
            System.out.printf("Cache update failed, error: {}%n", e.getMessage());
            return false;
        }finally {
            this.lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public Optional<V> get(K key) {
        this.lock.readLock().lock();
        try{
            if(elementMap.containsKey(key)){
                CacheElement<K,V> element = elementMap.get(key);
                int prevCount = element.getCount();
                DoublyLinkedList<CacheElement<K,V>> prevList = frequencyMap.get(prevCount);
                prevList.remove(element);
                if(prevList.isEmpty()){
                    frequencyMap.remove(prevCount);
                }

                element.incrementCount();
                DoublyLinkedList<CacheElement<K,V>> currList = frequencyMap.getOrDefault(element.getCount(), new DoublyLinkedList<>());
                currList.addFront(element);
                frequencyMap.put(prevCount, prevList);
                frequencyMap.put(element.getCount(), currList);
                return Optional.of(element.getValue());
            }
        }catch (Exception e){

        }finally {
            lock.readLock().unlock();
        }
        return Optional.empty();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {

    }
}
