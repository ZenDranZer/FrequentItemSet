import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

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
	HashMap<String,Path> frequentPatterns = new HashMap<>();



	/** Counting the frequency of the item**/
	public void getFreqCount() {
		//Get count for each item.
		for(ArrayList<String> transac: dataSet){
			for(String item: transac) {
				if(itemCount.containsKey(item)){
					int count = itemCount.get(item);
					itemCount.put(item, ++count);
				}
				else {
					itemCount.put(item, 1);
				}
			}
		}

		//Remove if less than Min_Supp
		itemCount.entrySet().removeIf(e -> e.getValue()<thrashold);

		String key;

		//Generation of Header Table.
		for(Map.Entry<String, Integer> hm:itemCount.entrySet())
		{
			key = hm.getKey();
			Node node = new Node(key);
			node.count = hm.getValue();
			header.put(key, node);
		}

		//Remove from dataset if not in header table.
		for (ArrayList<String> row:dataSet) {
			row.removeIf(e -> !header.keySet().contains(e));
		}

		//Sorting header Table
		Comparator<Entry<String, Node>> comp= (key1, key2) -> key2.getValue().getCounts() - key1.getValue().getCounts();
		Set<Entry<String, Node>> entries = header.entrySet();
		ArrayList<Entry<String, Node>> listOfEntries = new ArrayList<>(entries);
		Collections.sort(listOfEntries, comp);
		for(Entry<String, Node> entry : listOfEntries){
			headerTable.put(entry.getKey(), entry.getValue());
        }

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
		System.gc();
		//System.out.println(dataSet);
		//System.out.println(headerTable);
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


	public void SSRAlgorithm(){
		Node n ;
		for (String itemName: headerTable.keySet()) {
			ArrayList<Path> prefixPatternBase = new ArrayList<>();
			HashMap<String,Integer> freqnetItemList = new HashMap<>();
			n = headerTable.get(itemName);
			int count = 1232132;
			while (n!=null){
				Path p = new Path(n.count);
				Node prevNode = n.PrevNode;
				while(prevNode!=root && prevNode!=null){
					p.addNode(prevNode.itemName);
					if(freqnetItemList.containsKey(prevNode.itemName)){
						freqnetItemList.put(prevNode.itemName,freqnetItemList.get(prevNode.itemName)+n.count);
					}
					else
						freqnetItemList.put(prevNode.itemName,n.count);
					prevNode = prevNode.PrevNode;
				}


				if(!p.getPath().isEmpty()){
					prefixPatternBase.add(p);
					p.addNode(itemName);
					String key = p.toString().split(":")[0];
					if(frequentPatterns.containsKey(key)){
						Path path = frequentPatterns.get(key);
						if(path.getPathCount()<p.getPathCount())
							frequentPatterns.put(key,p);
					}else
						frequentPatterns.put(key,p);
				}
				n = n.myNextNode;
			}


			freqnetItemList.entrySet().removeIf(e -> e.getValue()<thrashold);
			if(freqnetItemList.isEmpty())
				continue;
			Path p;

			Path allP = new Path(count);

			for (String s:freqnetItemList.keySet()) {
				allP.addNode(s);
				p = new Path(freqnetItemList.get(s));
				if(p.getPathCount()<count)
					count = p.getPathCount();
				p.addNode(s);
				p.addNode(itemName);
				String key = p.toString().split(":")[0];
				if(frequentPatterns.containsKey(key)){
					Path path = frequentPatterns.get(key);
					if(path.getPathCount()<p.getPathCount())
						frequentPatterns.put(key,p);
				}else
					frequentPatterns.put(key,p);
			}
			allP.setPathCount(count);
			allP.addNode(itemName);
			System.out.println(allP);
			String key = allP.toString().split(":")[0];
			frequentPatterns.put(key,allP);

			frequentPatterns.entrySet().removeIf(e -> e.getValue().getPathCount()<thrashold);
			//System.out.println("FP:"+frequentPatterns.values());

			//Node itemPrefixTree = itemPrefixTree(prefixPatternBase,freqnetItemList,itemName);
//			patternGeneration(itemPrefixTree);
//			itemPrefixHeader = new HashMap<>();
//			System.gc();
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

			printTree(root);
			System.out.println("\n\n\nItem name :"+itemName + "\n FIS: \n" + freqentItem);
			System.out.println("Item name :"+itemName + "\n Base: \n" + base);

		return root;
	}

//	private void patternGeneration(Node root){
//		Node n;
//		ArrayList<Path> combinations;
//		for(String s:itemPrefixHeader.keySet()){
//			n = itemPrefixHeader.get(s);
//			String itemName = n.itemName;
//			while (n!=null){
//				Path p = new Path(n.count);
//				Node prevNode = n.PrevNode;
//				while (prevNode!=root && prevNode!=null){
//					p.addNode(prevNode.itemName);
//					prevNode = prevNode.PrevNode;
//				}
//				n = n.myNextNode;
//				if(p.getPath().isEmpty())
//					continue;
//				combinations = getAllPaths(p,itemName);
//				combinations.removeIf(e -> e.getPathCount() < thrashold);
//				frequentPatterns.addAll(combinations);
//				combinations = new ArrayList<>();
//				System.gc();
//			}
//		}
//	}

	private ArrayList<Path> getAllPaths(Path p,String itemName){
		ArrayList<Path> combinations = new ArrayList<>();
		int length = p.getPath().size();
		double max = Math.pow(2, length);
		for(int i = 1;i < max;i++){
			String bitmap = Integer.toBinaryString(i);
			Path path = new Path(p.getPathCount());
			path.addNode(itemName);
			for(int j = 0;j<bitmap.length();j++){
				if(bitmap.charAt(j)=='1'){
					path.addNode(p.getPath().get(length-bitmap.length()+j));
				}
			}
			combinations.add(path);
		}
		System.gc();
		return combinations;
	}

	public static void main(String[] args) throws IOException 
	{
		long start=System.nanoTime();
		Ssrrun obj=new Ssrrun();
		obj.dataSet = obj.readFile("src/InputFiles/testfile.txt");
		obj.getFreqCount();
		obj.root = obj.buildFPTree("root");
		obj.SSRAlgorithm();
		long end=System.nanoTime();
		double sec=(end-start)/1000000000.0;
		System.out.println("\n Frequent patterns: \n" + obj.frequentPatterns.values());
		//System.out.println(obj.frequentPatterns.size());
		System.out.println(sec);
	}
}