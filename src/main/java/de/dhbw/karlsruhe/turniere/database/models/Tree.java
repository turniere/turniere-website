package de.dhbw.karlsruhe.turniere.database.models;

public class Tree<T> {
    private T content;
    private Tree<T> leftTree;
    private Tree<T> rightTree;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Tree<T> getLeftTree() {
        return leftTree;
    }

    public void setLeftTree(Tree<T> leftTree) {
        this.leftTree = leftTree;
    }

    public Tree<T> getRightTree() {
        return rightTree;
    }

    public void setRightTree(Tree<T> rightTree) {
        this.rightTree = rightTree;
    }
}
