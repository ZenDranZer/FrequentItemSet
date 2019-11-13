import java.util.HashMap;

public class Node {
	String itemName;
	int count;
	Node myNextNode;
	Node PrevNode;
	HashMap<String,Node> children;


	public Node(String name) {
		this.itemName = name;
		this.count = 1;
		this.children =  new HashMap<>();
		this.myNextNode =null;
		this.PrevNode = null;
	}

	@Override
	public String toString() {
		return itemName;
	}
	
	public void attach(Node t){
		Node node = this;
		while(node.myNextNode!=null)
		{
			node = node.myNextNode;
		}
		node.myNextNode = t;
	}

	public void increaseCount() {
		this.count++;
	}

	public Node getMyPrevNode() {
		return PrevNode;
	}

	public void setMyPrevNode(Node myPrevNode) {
		this.PrevNode = myPrevNode;
	}

	public HashMap<String,Node> getChildren() {
		return children;
	}

	@Override
	public Node clone(){
		Node p = new Node(itemName);
		return p;
	}
}
