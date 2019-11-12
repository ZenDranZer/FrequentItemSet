import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;


public class FreqentItems {

	private int thrashold;
	private HashMap<String, Integer> itemCount = new HashMap<>();
	private ArrayList<ArrayList<String>> dataSet = new ArrayList<>();
	private LinkedHashMap<String, Node> headerTable = new LinkedHashMap<>();
	private HashMap<String, Node> header = new HashMap<>();
	private Node root;
	private ArrayList<Path> frequentPatterns = new ArrayList<>();

	/**Reading the file and parsing the data into proper format**/
	public ArrayList<ArrayList<String>> readFile(String path) throws IOException {
		File f = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String first=reader.readLine();
		String [] split=first.split(" ");
		int numberOfTransations = Integer.parseInt(split[0]);
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

	/** Counting the frequency of the item**/
	public void getFreqCount() {
		//Get count for each item.
		for (ArrayList<String> transac : dataSet) {
			for (String item : transac) {
				if (itemCount.containsKey(item)) {
					int count = itemCount.get(item);
					itemCount.put(item, ++count);
				} else {
					itemCount.put(item, 1);
				}
			}
		}

		//Remove if less than Min_Supp
		itemCount.entrySet().removeIf(e -> e.getValue() < thrashold);
		itemCount = itemCount.entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
						LinkedHashMap::new));
	}

	public void generateHeaderTable() {
		String key;

		//Generation of Header Table.
		for (Map.Entry<String, Integer> hm : itemCount.entrySet()) {
			key = hm.getKey();
			Node node = new Node(key);
			node.count = hm.getValue();
			header.put(key, node);
		}

		//Remove from dataset if not in header table.
		for (ArrayList<String> row : dataSet) {
			row.removeIf(e -> !header.keySet().contains(e));
		}

		//Sorting header Table
		Comparator<Entry<String, Node>> comp = (key1, key2) -> key2.getValue().getCounts() - key1.getValue().getCounts();
		Set<Entry<String, Node>> entries = header.entrySet();
		ArrayList<Entry<String, Node>> listOfEntries = new ArrayList<>(entries);
		Collections.sort(listOfEntries, comp);
		for (Entry<String, Node> entry : listOfEntries) {
			headerTable.put(entry.getKey(), entry.getValue());
		}
	}

	public void sortDataSet(){
		//sorting dataset
		ArrayList<String> pattern = new ArrayList<>(headerTable.keySet());
		ArrayList<ArrayList<String>> sortedDS = new ArrayList<>();
		for (ArrayList<String> row:dataSet) {
			ArrayList<String> tempRow = new ArrayList<>();
			for (String s:pattern) {
				if(row.contains(s))
					tempRow.add(s);
			}
			sortedDS.add(tempRow);
		}
		dataSet = sortedDS;
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
		//printTree(root);
		return root;
	}

	public void printTree(Node root){
		ArrayList<Node> printedNodes = new ArrayList<>();
		printNode(root,printedNodes);
	}

	public void printNode(Node root,ArrayList<Node> printedNodes){
		if(printedNodes.contains(root))
			return;
		System.out.println(root + "\t\t"+root.getMyPrevNode());
		printedNodes.add(root);
		for (Node n:root.children.values()) {
			printNode(n,printedNodes);
		}
	}


	public void frequentItemsGen(){
		Node n ;
		for (String itemName: headerTable.keySet()) {
			ArrayList<Path> prefixPatternBase = new ArrayList<>();
			HashMap<Node,Integer> frequentItemList = new HashMap<>();
			n = headerTable.get(itemName);
			while (n!=null){
				Path p = new Path(n.count);
				Node prevNode = n.PrevNode;
				while(prevNode!=root && prevNode!=null){
					p.addNode(prevNode);
					if(frequentItemList.containsKey(prevNode)){
						frequentItemList.put(prevNode,frequentItemList.get(prevNode)+n.count);
					}
					else
						frequentItemList.put(prevNode,n.count);
					prevNode = prevNode.PrevNode;
				}
				if(!p.getPath().isEmpty()){
					prefixPatternBase.add(p);
				}
				n = n.myNextNode;
			}
			frequentItemList.entrySet().removeIf(e -> e.getValue()<thrashold);
//			System.out.println("Item: " + itemName);
//			System.out.println("Base: "+ prefixPatternBase);
//			System.out.println("FIL: " + frequentItemList);
			int minimumCount = 65536555;
			Node itemNode = new Node(itemName);
			Path allP = new Path(minimumCount);
			allP.addNode(itemNode);
			for (Node node:frequentItemList.keySet()) {
				int count = frequentItemList.get(node);
				Path p = new Path(count);
				p.addNode(itemNode);
				p.addNode(node);
				if(!allP.contains(node)){
					allP.addNode(node);
					frequentPatterns.add(p);
				}
				if(count<minimumCount)
					minimumCount=count;
			}
			allP.setPathCount(minimumCount);
			frequentPatterns.removeIf(e -> e.equals(allP));
			frequentPatterns.add(allP);
		}
		frequentPatterns.removeIf(e->e.getPathCount()<thrashold || e.getPathCount()==65536555);
	}

	public static void main(String[] args) throws IOException
	{
		long start=System.nanoTime();
		FreqentItems obj=new FreqentItems();
		obj.dataSet = obj.readFile("src/InputFiles/input1.txt");
		obj.getFreqCount();
		obj.generateHeaderTable();
		obj.sortDataSet();
		obj.root = obj.buildFPTree("root");
		obj.frequentItemsGen();
		long end=System.nanoTime();
		double sec=(end-start)/1000000000.0;
		System.out.println("\n Frequent patterns: \n");
		for (Path p:obj.frequentPatterns) {
			System.out.println(p);
		}
		System.out.println(obj.frequentPatterns.size());
		System.out.println(sec);
	}
}