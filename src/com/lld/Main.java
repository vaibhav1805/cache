package com.lld;

import com.lld.lru.LRUCache;
import com.lld.models.Cache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        executeParallel();
    }

    public static void executeSequentially(){
        Cache<String,String> lruCache = new LRUCache<>(3);
        lruCache.set("1","test1");
        lruCache.set("2","test2");
        lruCache.set("3","test3");
        lruCache.set("4","test4");

        assert !lruCache.get("1").isPresent();
        assert lruCache.get("2").get().equals("test2");
        assert lruCache.get("4").get().equals("test4");
    }
    public static void executeParallel(){
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
