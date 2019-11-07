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
	HashMap<String, Node> header = new HashMap<String, Node>();
	
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
	public HashMap<String, Integer> getFreqCount(ArrayList<ArrayList<String>> ds,Ssrrun obj)
	{
		thrashold=25;
		HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
		
		for(ArrayList<String> transac: ds){
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
		for(ArrayList<String> items : ds) 
		{
			items.removeAll(abandonSet);
		}
		for(String item :abandonSet )
		{
			itemCount.remove(item);
		}
		System.out.println(ds);
		ArrayList<ArrayList<String>> dataset=obj.itemSort(itemCount,ds);
		System.out.println(dataset);
		
		/*for(Map.Entry<String, Node> hmm:header.entrySet())
		{
			System.out.println(hmm.getKey());
		}*/
		System.out.println(header);
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
		LinkedHashMap<String, Node> headerTable = new LinkedHashMap<String,Node>();
		for(Entry<String, Node> entry : listOfEntries){
			headerTable.put(entry.getKey(), entry.getValue());
        }
		System.out.println(headerTable);
		return itemCount;
	}
	
	
	/**Reading the file and parsing the data into proper format**/
	public ArrayList<ArrayList<String>> readFile(String path) throws IOException {
		File f = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String first=reader.readLine();
		String [] split=first.split(" ");
		int n=Integer.parseInt(split[0]);
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
	public static void main(String[] args) throws IOException 
	{
		Ssrrun obj=new Ssrrun();
		ArrayList<ArrayList<String>> ds = obj.readFile("C:\\Users\\Shivam\\eclipse-workspace\\SSR\\src\\abc.txt");
		obj.getFreqCount(ds,obj);
	}

}
