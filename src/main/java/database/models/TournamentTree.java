package database.models;

public class TournamentTree{

    private Game content;
    private Tree<Game> leftTree;
    private Tree<Game> rightTree;

    public Game getContent() {
        return content;
    }

    public void setContent(Game content) {
        this.content = content;
    }

    public Tree<Game> getLeftTree() {
        return leftTree;
    }

    public void setLeftTree(Tree<Game> leftTree) {
        this.leftTree = leftTree;
    }

    public Tree<Game> getRightTree() {
        return rightTree;
    }

    public void setRightTree(Tree<Game> rightTree) {
        this.rightTree = rightTree;
    }

}
