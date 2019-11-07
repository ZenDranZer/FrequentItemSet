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

public class Ssrrun {

	int thrashold;
	HashMap<String, Integer> itemCount = new HashMap<>();
	ArrayList<ArrayList<String>> dataSet = new ArrayList<>();
	LinkedHashMap<String, Node> headerTable = new LinkedHashMap<>();
	HashMap<String, Node> header = new HashMap<>();
	Node root = new Node("root");
	int numberOfTransations;
	
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

	public void buildFPTree(){
		root.setMyNextNode(null);
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
		printTree();
	}


	public void printTree(){
		ArrayList<Node> printedNodes = new ArrayList<>();
		printNode(root,printedNodes);
	}

	public void printNode(Node root,ArrayList<Node> printedNodes){
		if(printedNodes.contains(root))
			return;
		if(root != this.root)
			System.out.println(root.myPrevNode.itemName + "--Parent-->" + root.itemName);
		else
			System.out.println(root);
		printedNodes.add(root);
		for (Node n:root.children.values()) {
			printNode(n,printedNodes);
		}
	}

	public static void main(String[] args) throws IOException 
	{
		Ssrrun obj=new Ssrrun();
		obj.dataSet = obj.readFile("src/test.txt");
		obj.getFreqCount();
		obj.buildFPTree();
	}

}
