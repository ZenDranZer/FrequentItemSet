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

    public void removeNode(Node node){
        this.path.remove(node);
    }

    public int getPathCount() {
        return pathCount;
    }

    public void setPathCount(int pathCount) {
        this.pathCount = pathCount;
    }

    @Override
    public String toString() {
        return "\nPath{" +
                "path=" + path +
                ", pathCount=" + pathCount +
                '}';
    }
}