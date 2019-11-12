import java.util.ArrayList;
public class Path  {

    private ArrayList<Node> path;
    private int pathCount;

    public Path(int pathCount) {
        this.path = new ArrayList<>();
        this.pathCount = pathCount;
    }

    public ArrayList<Node> getPath() {
        return path;
    }

    public void addNode(Node path) {
        this.path.add(path);
    }

    public boolean contains(Node n){
        for (Node node:path) {
            if(node.itemName.equals(n.itemName))
                return true;
        }
        return false;
    }

    public void resetPath(){
        this.path = new ArrayList<>();
    }

    public int getPathCount() {
        return pathCount;
    }

    public void setPathCount(int pathCount) {
        this.pathCount = pathCount;
    }

    @Override
    public String toString() {
        return path + ":" + pathCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Path) {
            String[] str1 = toString().split(":");
            String[] str2 = obj.toString().split(":");
            return str1[0].equals(str2[0]);
        }
        return false;
    }

    }