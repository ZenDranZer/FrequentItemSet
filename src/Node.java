import java.util.HashMap;

public class Node {
	String itemName;
	int count;
	Node myNextNode;
	Node myPrevNode;
	HashMap<String,Node> children;


	public Node(String name) {
		this.itemName = name;
		this.count = 1;
		this.children =  new HashMap<String, Node>();
		this.myNextNode =null;
		this.myPrevNode = null;
	}

	public int getCounts() 
	{
		return count;
	}
	@Override
	public String toString() {
		return "FPNode [count=" + count + ", itemName=" + itemName + "]";
	}
	
	public void attach(Node t){
		Node node = this;
		while(node.myNextNode!=null){
			node = node.myNextNode;
		}
		node.myNextNode = t;
	}

	public String getItemName() {
		return itemName;
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

	public HashMap<String,Node> getChildren() {
		return children;
	}

	public void addChild(Node child) {
		children.put(child.itemName,child);
	}

}
