import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;


public class FreqentItems {

	private int threshold;
	private HashMap<String, Integer> itemCount = new HashMap<>();
	private ArrayList<ArrayList<String>> dataSet = new ArrayList<>();
	private LinkedHashMap<String, Node> headerTable = new LinkedHashMap<>();
	private Node root;
	private ArrayList<Path> frequentPatterns = new ArrayList<>();

	/**Reading the file and parsing the data into proper format**/
	public ArrayList<ArrayList<String>> readFile(String path) throws IOException {
		File f = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String first=reader.readLine();
		String [] split=first.split(" ");
		threshold =Integer.parseInt(split[1]);
		String str;
		ArrayList<ArrayList<String>> dataSet = new ArrayList<ArrayList<String>>();
		while((str = reader.readLine()) != null) {
			if(!str.isEmpty()) {
				ArrayList<String> tmpList = new ArrayList<>();
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
		System.out.println("Data Read.");
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
		itemCount.entrySet().removeIf(e -> e.getValue() < threshold);
		itemCount = itemCount.entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
						LinkedHashMap::new));
		System.out.println("Count added.");
	}

	public void generateHeaderTable() {
		String key;

		//Generation of Header Table.
		for (Map.Entry<String, Integer> hm : itemCount.entrySet()) {
			key = hm.getKey();
			Node node = new Node(key);
			node.count = hm.getValue();
			headerTable.put(key, node);
		}

		//Remove from dataset if not in header table.
		for (ArrayList<String> row : dataSet) {
			row.removeIf(e -> !headerTable.keySet().contains(e));
		}
		System.out.println("Header table generated.");
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
		System.out.println("Dataset Sorted.");
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
		System.out.println("Tree generated.");
		return root;
	}

	public void frequentItemsGen(){
		Node n ;
		for (String itemName: headerTable.keySet()) {
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
				n = n.myNextNode;
			}
			frequentItemList.entrySet().removeIf(e -> e.getValue()< threshold);
			int minimumCount = 65536555;
			Node itemNode = new Node(itemName);
			Path allP = new Path(minimumCount);
			allP.addNode(itemNode);
			Path cp = new Path(minimumCount);
			cp.addNode(itemNode);
			for (Node node:frequentItemList.keySet()) {
				int count = frequentItemList.get(node);
				Path p = new Path(count);
				p.addNode(itemNode);
				p.addNode(node);
				cp.addNode(node);
				if(!allP.contains(node)){
					allP.addNode(node);
					frequentPatterns.add(p);
				}
				if(count<minimumCount)
					minimumCount=count;

				if(cp.getPath().size()>1){
					cp.setPathCount(minimumCount);
					Path newCP = cp.clone();
					frequentPatterns.add(newCP);
				}
			}
			allP.setPathCount(minimumCount);
			frequentPatterns.removeIf(e -> e.equals(allP));
			frequentPatterns.add(allP);
			frequentPatterns.removeIf(e->e.getPathCount()< threshold || e.getPathCount()==65536555);
			writeToFile();
		}
	}

	private void writeToFile(){
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File("src/output.txt"), true)));
			for (Path p:frequentPatterns) {
				pw.println(p);
			}
			pw.flush();
			frequentPatterns = new ArrayList<>();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException
	{
		long start=System.nanoTime();
		FreqentItems obj=new FreqentItems();
		obj.dataSet = obj.readFile("src/InputFiles/Sparse 25000 500.txt");
		System.out.println("Threshold: "+obj.threshold);
		obj.getFreqCount();
		obj.generateHeaderTable();
		obj.sortDataSet();
		obj.root = obj.buildFPTree("root");
		obj.frequentItemsGen();
		long end=System.nanoTime();
		double sec=(end-start)/1000000000.0;
		System.out.println(sec);
	}
}