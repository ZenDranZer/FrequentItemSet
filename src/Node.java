import java.util.HashMap;

public class Node 
{
	int count;
	String itemname;
	HashMap<String,Node> children;
	Node next;
	Node parent;
	public Node(String name) {
		this.itemname = name;
		this.count = 1;
		this.children =  new HashMap<String, Node>();
		this.next =null;
		this.parent = null;
	}

	public int getCounts() 
	{
		return count;
	}
	@Override
	public String toString() {
		return "FPNode [count=" + count + ", itemname=" + itemname + "]";
	}
	
	public void attach(Node t){
		Node node = this;
		while(node.next!=null){
			node = node.next;
		}
		node.next = t;
	}	
}
