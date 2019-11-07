import java.util.ArrayList;

public class Node {
    String itemName;
    int count;
    Node myNextNode;
    Node myPrevNode;
    ArrayList<Node> children;

    public Node(String itemName, int count, Node nextNode) {
        this.itemName = itemName;
        this.count = count;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        this.count++;
    }

    public Node getMyNextNode() {
        return myNextNode;
    }

    public void setMyNextNode(Node myNextNode) {
        this.myNextNode = myNextNode;
    }

    public Node getMyPrevNode() {
        return myPrevNode;
    }

    public void setMyPrevNode(Node myPrevNode) {
        this.myPrevNode = myPrevNode;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }
}
