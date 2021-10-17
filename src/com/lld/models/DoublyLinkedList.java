package com.lld.models;

import java.util.LinkedList;

public class DoublyLinkedList<T> {
    private final LinkedList<T> dll;

    public DoublyLinkedList(){
        this.dll = new LinkedList<>();
    }

    public T removeLast(){
        return this.dll.removeLast();
    }

    public void addFront(T n){
        this.dll.addFirst(n);
    }
    public void updateAndMoveToFront(T n1, T n2){
        this.dll.remove(n1);
        this.dll.addFirst(n2);
    }

    public T moveToFront(T n){
        this.dll.remove(n);
        this.dll.addFirst(n);
        return n;
    }

    public void remove(T n){
        this.dll.remove(n);
    }

    public T head(){
        return this.dll.getFirst();
    }

    public T last(){
        return this.dll.getLast();
    }

    public boolean isEmpty(){
        return this.dll.isEmpty();
    }
}
