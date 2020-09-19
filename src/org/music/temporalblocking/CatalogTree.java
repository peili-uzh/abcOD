package org.music.temporalblocking;

import java.util.ArrayList;
import java.util.List;

public class CatalogTree<T> {

    private Node<T> root;

    public CatalogTree(T rootData) {
        root = new Node<T>();
        root.data = rootData;
        root.clidren = new ArrayList<Node<T>>();

    }

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> clidren;
    }

}
