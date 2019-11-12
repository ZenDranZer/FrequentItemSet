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
		this.children =  new HashMap<String, Node>();
		this.myNextNode =null;
		this.PrevNode = null;
	}

	public int getCounts() 
	{
		return count;
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

	public String getItemName() {
		return itemName;
	}

	public void increaseCount() {
		this.count++;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Node getMyNextNode() {
		return myNextNode;
	}

	public void setMyNextNode(Node myNextNode) {
		this.myNextNode = myNextNode;
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

	public void addChild(Node child) {
		children.put(child.itemName,child);
	}

}
