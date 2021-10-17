package com.lld.lru;

import com.lld.models.Cache;
import com.lld.models.CacheElement;
import com.lld.models.DoublyLinkedList;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K,V> implements Cache<K,V> {
    private final int size;
    private final Map<K, CacheElement<K, V>> llNodeMap;
    private final DoublyLinkedList<CacheElement<K,V>> dll;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int size){
        this.size = size;
        this.llNodeMap = new ConcurrentHashMap<>();
        this.dll = new DoublyLinkedList<>();
    }

    @Override
    public boolean set(K key, V value) {
        this.lock.writeLock().lock();
        try{
            CacheElement<K,V> newElement = new CacheElement<>(key, value);
            if(llNodeMap.containsKey(key)){
                CacheElement<K,V> oldElement = llNodeMap.get(key);
                this.dll.updateAndMoveToFront(oldElement, newElement);
            }else{
                if(this.size() >= this.size){
                    CacheElement<K, V> removed = this.dll.removeLast();
                    this.llNodeMap.remove(removed.key());
                }
                this.dll.addFront(newElement);
            }
            llNodeMap.put(key, newElement);
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
            CacheElement<K,V> element = this.llNodeMap.get(key);
            if(element != null){
                this.llNodeMap.put(key, this.dll.moveToFront(element));
                return Optional.of(element.getValue());
            }
        }catch (Exception e){
            System.out.printf("Cache read failed, error: {}%n", e.getMessage());
        }finally {
            this.lock.readLock().unlock();
        }
        return Optional.empty();
    }

    @Override
    public int size() {
        return this.llNodeMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.llNodeMap.size() == 0;
    }

    @Override
    public void clear() {

    }
}
