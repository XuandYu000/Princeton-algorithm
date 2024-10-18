/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node<Item> sentinel;
    private int n;

    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
        private Node<Item> prev;

        public Node() {
            prev = null;
            next = null;
            item = null;
        }

        public Node(Item item, Node<Item> pprev, Node<Item> nnext) {
            this.item = item;
            this.next = nnext;
            this.prev = pprev;
        }
    }

    // construct an empty deque
    public Deque() {
        sentinel = new Node<Item>();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item");
        }
        Node<Item> e = new Node<Item>(item, sentinel, sentinel.next);
        sentinel.next.prev = e;
        sentinel.next = e;
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item");
        }
        Node<Item> e = new Node<Item>(item, sentinel.prev, sentinel);
        sentinel.prev.next = e;
        sentinel.prev = e;
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty");
        }
        Node<Item> del = sentinel.next;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        n--;
        return del.item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty");
        }
        Node<Item> del = sentinel.prev;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        n--;
        return del.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node<Item> ptr;
        private int remains;

        public DequeIterator() {
            ptr = sentinel.next;
            remains = size();
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported");
        }

        public boolean hasNext() {
            return remains > 0;
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Deque is empty");
            }
            Item i = ptr.item;
            ptr = ptr.next;
            remains--;
            return i;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> dq = new Deque<>();
        for (int i = 0; i < 5; i++) {
            dq.addFirst("A" + i);
        }
        for (int i = 0; i < 5; i++) {
            dq.addLast("B" + i);
        }
        for (String s : dq) {
            System.out.println(s);
        }
        System.out.println("dq has " + dq.size() + " elements in total");
        for (int i = 0; i < 10; i++) {
            System.out.println(dq.removeFirst());
            System.out.println(dq.removeLast());
            System.out.println(dq.size());
        }
    }

}