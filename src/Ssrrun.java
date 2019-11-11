import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

public class Ssrrun {

	int thrashold;
	HashMap<String, Integer> itemCount = new HashMap<>();
	ArrayList<ArrayList<String>> dataSet = new ArrayList<>();
	LinkedHashMap<String, Node> headerTable = new LinkedHashMap<>();
	HashMap<String, Node> header = new HashMap<>();
	int numberOfTransations;
	HashMap<String, Node> itemPrefixHeader = new HashMap<>();
	Node root ;
	ArrayList<Path> frequentPatterns = new ArrayList<>();
	
	/**Sorting dataset**/
	public ArrayList<ArrayList<String>> itemSort(HashMap<String, Integer> itemMap, ArrayList<ArrayList<String>> ds) {
		for(ArrayList<String> items : ds) {
			Collections.sort(items, new Comparator<String>() {
				@Override
				public int compare(String key1, String key2) {
					return itemMap.get(key2) - itemMap.get(key1);
				}
			});
		}
		return ds;
	}

	/** Counting the frequency of the item**/
	public void getFreqCount()
	{
		//thrashold=25;
		
		for(ArrayList<String> transac: dataSet){
			for(String item: transac)
			{
				if(itemCount.containsKey(item)){
					int count = itemCount.get(item);
					itemCount.put(item, ++count);
				}
				else
				{
					itemCount.put(item, 1);
				}
			}
		}
		String key="";
		int value;
		ArrayList<String> abandonSet = new ArrayList<String>();
		for(Map.Entry<String, Integer> hm:itemCount.entrySet())
		{
			key=hm.getKey();
			value=hm.getValue();
			if(value<thrashold)
			{
			abandonSet.add(key);
			//itemCount.remove(key,value);	
			}
			else
			{
				Node node = new Node(key);
				node.count = value;
				header.put(key, node);
			}
		}
		for(ArrayList<String> items : dataSet)
		{
			items.removeAll(abandonSet);
		}
		for(String item :abandonSet )
		{
			itemCount.remove(item);
		}
		//System.out.println(dataSet);
		ArrayList<ArrayList<String>> dataset=this.itemSort(itemCount,dataSet);
		//System.out.println(dataset);
		//System.out.println(header);
		//Sorting header Table
		ArrayList<Node> ee = new ArrayList<>(header.values());
		Comparator<Entry<String, Node>> comp= new Comparator<Entry<String, Node>>() {
			@Override
			public int compare(Entry<String,Node> key1, Entry<String,Node> key2) 
			{
				Node obj1=key1.getValue();
				Node obj2=key2.getValue();
				int count1=obj2.getCounts();
				int count2=obj1.getCounts();
				return count1-count2;
			}
		};
		Set<Entry<String, Node>> entries = header.entrySet();
		ArrayList<Entry<String, Node>> listOfEntries = new ArrayList<Entry<String,Node>>(entries);
		Collections.sort(listOfEntries, comp);
		for(Entry<String, Node> entry : listOfEntries){
			headerTable.put(entry.getKey(), entry.getValue());
        }
	}
	
	
	/**Reading the file and parsing the data into proper format**/
	public ArrayList<ArrayList<String>> readFile(String path) throws IOException {
		File f = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String first=reader.readLine();
		String [] split=first.split(" ");
		numberOfTransations=Integer.parseInt(split[0]);
		thrashold=Integer.parseInt(split[1]);
		String str;
		ArrayList<ArrayList<String>> dataSet = new ArrayList<ArrayList<String>>();
		while((str = reader.readLine()) != null) {
			if(!"".equals(str)) {
				ArrayList<String> tmpList = new ArrayList<String>();
				String[] s = str.split(",");
				for(int i = 0; i <s.length; i++) 
				{
					if(i==0)
					{
						s[i]=s[i].replace("{","");
						tmpList.add(s[i]);
						continue;
					}
					if(i==s.length-1)
					{
						s[i]=s[i].replace("}","");
						tmpList.add(s[i]);
						continue;
					}
					tmpList.add(s[i]);
				}
				dataSet.add(tmpList);
			}
		}
		reader.close();
		return dataSet;
	}

	public Node buildFPTree(String rootName){
		Node root = new Node(rootName);
		root.setMyPrevNode(null);
		for (ArrayList<String> row: dataSet) {
			Node previous = root;
			HashMap<String , Node> children = previous.getChildren();
			for (String item:row) {
				if(!headerTable.containsKey(item))
					continue;
				Node temp;
				if(children.containsKey(item)){
					temp = children.get(item);
					temp.increaseCount();
				}else{
					temp = new Node(item);
					temp.setMyPrevNode(previous);
					children.put(item,temp);
					Node itemHead = headerTable.get(item);
					if(itemHead!=null) {
						itemHead.attach(temp);
					}
				}
				previous = temp;
				children = temp.getChildren();
			}
		}
		return root;
	}

	public void printTree(Node root){
		ArrayList<Node> printedNodes = new ArrayList<>();
		printNode(root,printedNodes);
	}

	public void printNode(Node root,ArrayList<Node> printedNodes){
		if(printedNodes.contains(root))
			return;
		System.out.println(root);
		printedNodes.add(root);
		for (Node n:root.children.values()) {
			printNode(n,printedNodes);
		}
	}


	public void SSRAlgorithm(){
		Node n ;
		for (String itemName: header.keySet()) {
			ArrayList<Path> prefixPatternBase = new ArrayList<>();
			HashMap<String,Integer> freqnetItemList = new HashMap<>();
			n = header.get(itemName);
			while (n!=null){
				Path p = new Path(n.count);
				Node prevNode = n.PrevNode;
				while(prevNode!=root && prevNode!=null){
					p.addNode(prevNode.itemName);
					if(freqnetItemList.containsKey(prevNode.itemName))
						freqnetItemList.put(prevNode.itemName,freqnetItemList.get(prevNode.itemName)+n.count);
					else
						freqnetItemList.put(prevNode.itemName,n.count);
					//System.out.println(itemName+"      "+prevNode);
					prevNode = prevNode.PrevNode;
				}
				if(!p.getPath().isEmpty())
					prefixPatternBase.add(p);
				n = n.myNextNode;
			}
			freqnetItemList.entrySet().removeIf(e -> e.getValue()<thrashold);
			if(freqnetItemList.isEmpty())
				continue;
			Node itemPrefixTree = itemPrefixTree(prefixPatternBase,freqnetItemList,itemName);
		}
	}

	private Node itemPrefixTree(ArrayList<Path> base,HashMap<String,Integer> freqentItem, String itemName){
		Node root = new Node(itemName);
		freqentItem = freqentItem.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(
				toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
						LinkedHashMap::new));
		itemPrefixHeader = new HashMap<>();

		for (Map.Entry<String,Integer> entry: freqentItem.entrySet()) {
			Node node = new Node(entry.getKey());
			node.count = entry.getValue();
			//System.out.println(node);
			itemPrefixHeader.put(entry.getKey(), node);
		}

		for (Path p:base) {
			p.getPath().removeIf(e -> !itemPrefixHeader.keySet().contains(e));
			ArrayList<String> pattern = new ArrayList<>(freqentItem.keySet());
			ArrayList<String> path = p.getPath();
			p.resetPath();
			for (String s:pattern) {
				if(path.contains(s))
					p.addNode(s);
			}
		}
		for (Path p: base) {
			Node previous = root;
			HashMap<String , Node> children = previous.getChildren();
			for (String s: p.getPath()) {
				Node temp;
				if(children.containsKey(s)){
					temp = children.get(s);
					temp.setCount(temp.count+p.getPathCount());
				}else{
					temp = new Node(s);
					temp.setCount(p.getPathCount());
					temp.setMyPrevNode(previous);
					children.put(s,temp);
					Node itemHead = itemPrefixHeader.get(s);
					if(itemHead!=null) {
						itemHead.attach(temp);
					}
				}
				previous = temp;
				children = temp.getChildren();
			}
		}
		if(itemName.equals("D")){
			printTree(root);
			System.out.println("\n\n\nItem name :"+itemName + "\n FIS: \n" + freqentItem);
			System.out.println("Item name :"+itemName + "\n Base: \n" + base);
		}
		return root;
	}

	private void patternGeneration(Node root){
		Node n;
		for(String s:itemPrefixHeader.keySet()){
			n = itemPrefixHeader.get(s);
			while (n!=null){
				Path p = new Path(n.count);
				Node prevNode = n.PrevNode;
				while (prevNode!=root && prevNode!=null){
					p.addNode(prevNode.itemName);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException 
	{
		Ssrrun obj=new Ssrrun();
		obj.dataSet = obj.readFile("src/Test2.txt");
		obj.getFreqCount();
		obj.root = obj.buildFPTree("root");
		obj.SSRAlgorithm();
		//System.out.println("\n\n\n\n\n\n\n\n\nHeader table: \n" + obj.header);
	}
}