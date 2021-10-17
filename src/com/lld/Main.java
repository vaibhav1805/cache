package com.lld;

import com.lld.lru.LFUCache;
import com.lld.lru.LRUCache;
import com.lld.models.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        Cache<String, String> cache = factory("LFU", 3);
        cache.set("1", "test1");
        cache.set("2", "test2");
        cache.set("3", "test3");
        System.out.println(cache.get("2"));
        System.out.println(cache.get("2"));
        cache.set("4", "test4");
        System.out.println(cache.get("1"));
        System.out.println(cache.get("4"));
        cache.set("5", "test5");
        System.out.println(cache.get("2"));
    }

    public static<K,V> Cache<K, V> factory(String cacheType, int capacity){
        Map<String, Cache<K, V>> factory = new HashMap<String, Cache<K, V>>(){{
            put("LFU", new LFUCache<>(capacity));
            put("LRU", new LRUCache<>(capacity));
        }};
        return factory.get(cacheType);
    }

    public static void executeParallelly(){
        final int size = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Cache<Integer, String> cache = new LRUCache<>(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        try {
            IntStream.range(0, size).<Runnable>mapToObj(key -> () -> {
                cache.set(key, "value" + key);
                countDownLatch.countDown();
            }).forEach(executorService::submit);
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        IntStream.range(0, size).forEach(i -> System.out.println("key: " + i + " value: "+ cache.get(i).get()));
    }
}
