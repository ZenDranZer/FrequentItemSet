import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


public class FreqentItems {

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


	public void frequentItemsGen(){

		Node n ;
		for (String itemName: headerTable.keySet()) {
			System.out.println("----------"+itemName);
			ArrayList<Path> prefixPatternBase = new ArrayList<>();
			HashMap<String,Integer> freqnetItemList = new HashMap<>();


			n = headerTable.get(itemName);
			int count = 0;
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
				System.out.println(p);
			}


			freqnetItemList.entrySet().removeIf(e -> e.getValue()<thrashold);
			if(freqnetItemList.isEmpty())
				continue;
			Path p;

			Path allP = new Path(count);

			for (String s:freqnetItemList.keySet()) {
				allP.addNode(s);
				p = new Path(freqnetItemList.get(s));
				count++;
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
			if(frequentPatterns.containsKey(key)) {
				Path path = frequentPatterns.get(key);
				if (path.getPathCount() < allP.getPathCount())
					frequentPatterns.put(key, allP);
			}
			System.out.println(frequentPatterns.values());
		}
		frequentPatterns.entrySet().removeIf(e -> e.getValue().getPathCount()<thrashold);
	}

	public static void main(String[] args) throws IOException 
	{
		long start=System.nanoTime();
		FreqentItems obj=new FreqentItems();
		obj.dataSet = obj.readFile("src/InputFiles/abc.txt");
		obj.getFreqCount();
		obj.root = obj.buildFPTree("root");
		obj.frequentItemsGen();
		long end=System.nanoTime();
		double sec=(end-start)/1000000000.0;
		System.out.println("\n Frequent patterns: \n" + obj.frequentPatterns.values());
		System.out.println(obj.frequentPatterns.size());
		System.out.println(sec);
		System.out.println(obj.thrashold);
	}
}