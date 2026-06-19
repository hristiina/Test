package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class coBinarySearchTreeTest {

    private BinarySearchTree<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = new BinarySearchTree<>();
    }

    @Test
    void newTreeIsEmpty() {
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertEquals(-1, tree.height());
        assertEquals(List.of(), tree.bfs());
        assertEquals(List.of(), tree.toSortedList());
    }

    @Test
    void insertReturnsTrueForNewValues() {
        assertTrue(tree.insert(5));
        assertEquals(1, tree.size());
        assertTrue(tree.contains(5));
    }

    @Test
    void insertReturnsFalseForDuplicates() {
        assertTrue(tree.insert(5));
        assertFalse(tree.insert(5));
        assertEquals(1, tree.size());
    }

    @Test
    void insertNullThrows() {
        assertThrows(NullPointerException.class, () -> tree.insert(null));
    }

    @Test
    void containsIsFalseForMissingValue() {
        tree.insert(5);
        assertFalse(tree.contains(99));
    }

    @Test
    void containsIsTrueForExistingValue() {
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);

        assertTrue(tree.contains(30));
    }

    @Test
    void containsNullThrows() {
        assertThrows(NullPointerException.class, () -> tree.contains(null));
    }

    @Test
    void isEmptyTracksInsertsAndDeletes() {
        assertTrue(tree.isEmpty());

        tree.insert(1);
        assertFalse(tree.isEmpty());

        tree.delete(1);
        assertTrue(tree.isEmpty());
    }

    @Test
    void sizeTracksInsertsAndDeletes() {
        assertEquals(0, tree.size());

        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        assertEquals(3, tree.size());

        tree.delete(2);
        assertEquals(2, tree.size());
    }

    @Test
    void dfsOnEmptyTreeReturnsEmptyListForAllOrders() {
        assertEquals(List.of(), tree.dfs(BinarySearchTree.DfsOrder.PRE_ORDER));
        assertEquals(List.of(), tree.dfs(BinarySearchTree.DfsOrder.IN_ORDER));
        assertEquals(List.of(), tree.dfs(BinarySearchTree.DfsOrder.POST_ORDER));
    }

    @Test
    void inOrderTraversalIsAlwaysSorted() {
        int[] values = {50, 30, 70, 20, 40, 60, 80, 10, 25};
        for (int v : values) {
            tree.insert(v);
        }
        assertEquals(List.of(10, 20, 25, 30, 40, 50, 60, 70, 80), tree.toSortedList());
    }

    @Test
    void preOrderTraversalMatchesInsertionShape() {
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);

        assertEquals(List.of(50, 30, 20, 40, 70), tree.dfs(BinarySearchTree.DfsOrder.PRE_ORDER));
    }

    @Test
    void postOrderTraversalVisitsChildrenBeforeParent() {
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);

        assertEquals(List.of(30, 70, 50), tree.dfs(BinarySearchTree.DfsOrder.POST_ORDER));
    }

    @Test
    void bfsVisitsLevelByLevel() {
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);

        assertEquals(List.of(50, 30, 70, 20, 40, 60, 80), tree.bfs());
    }

    @Test
    void deleteLeafNode() {
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);

        assertTrue(tree.delete(30));
        assertEquals(List.of(50, 70), tree.toSortedList());
        assertEquals(2, tree.size());
    }

    @Test
    void deleteNodeWithSingleChild() {
        tree.insert(50);
        tree.insert(30);
        tree.insert(20);

        assertTrue(tree.delete(30));
        assertEquals(List.of(20, 50), tree.toSortedList());
        assertTrue(tree.contains(20));
        assertFalse(tree.contains(30));
    }

    @Test
    void deleteNodeWithTwoChildrenUsesInOrderSuccessor() {
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        for (int v : values) {
            tree.insert(v);
        }

        assertTrue(tree.delete(50));
        assertFalse(tree.contains(50));
        assertEquals(List.of(20, 30, 40, 60, 70, 80), tree.toSortedList());
        assertEquals(6, tree.size());
    }

    @Test
    void deleteRootOfSingleNodeTreeEmptiesTree() {
        tree.insert(42);
        assertTrue(tree.delete(42));
        assertTrue(tree.isEmpty());
        assertEquals(List.of(), tree.bfs());
    }

    @Test
    void deleteMissingValueReturnsFalseAndLeavesTreeUnchanged() {
        tree.insert(50);
        tree.insert(30);

        assertFalse(tree.delete(99));
        assertEquals(2, tree.size());
        assertEquals(List.of(30, 50), tree.toSortedList());
    }

    @Test
    void deleteNullThrows() {
        assertThrows(NullPointerException.class, () -> tree.delete(null));
    }

    @Test
    void heightReflectsTreeShape() {
        assertEquals(-1, tree.height());

        tree.insert(50);
        assertEquals(0, tree.height());

        tree.insert(30);
        tree.insert(70);
        assertEquals(1, tree.height());

        tree.insert(20);
        assertEquals(2, tree.height());
    }

    @Test
    void repeatedInsertAndDeleteKeepsBstInvariantAndSizeConsistent() {
        int[] values = {15, 6, 18, 3, 7, 17, 20, 2, 4, 13, 9};
        for (int v : values) {
            tree.insert(v);
        }
        assertEquals(values.length, tree.size());

        tree.delete(6);
        tree.delete(15);
        tree.delete(2);

        assertEquals(values.length - 3, tree.size());
        List<Integer> sorted = tree.toSortedList();
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1) < sorted.get(i));
        }
    }
}
