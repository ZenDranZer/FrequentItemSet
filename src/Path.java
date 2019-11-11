import java.util.ArrayList;
public class Path  {

    private ArrayList<String> path;
    private int pathCount;

    public Path(int pathCount) {
        this.path = new ArrayList<>();
        this.pathCount = pathCount;
    }

    public ArrayList<String> getPath() {
        return path;
    }

    public void addNode(String path) {
        this.path.add(path);
    }

    public void removeNode(String node){
        this.path.remove(node);
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
        return "\nPath{" +
                "path=" + path +
                ", pathCount=" + pathCount +
                '}';
    }
}