public class Node {
    String itemName;
    int count;
    Node nextNode;

    public Node(String itemName, int count, Node nextNode) {
        this.itemName = itemName;
        this.count = count;
        this.nextNode = nextNode;
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

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
