import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Node{
    Node lChild = null;
    Node rChild = null;
    double value;

    public Node(double value){
        this.value = value;
        this.lChild = null;
        this.rChild = null;
    }

    public Node(double value, Node lChild, Node rChild){
        this.value = value;
        this.lChild = lChild;
        this.rChild = rChild;
    }

    public ArrayList<Integer> getLeafNodes(){
        ArrayList<Integer> leafNodes = new ArrayList<Integer>();
        ArrayList<Node> q = new ArrayList<Node>();
        q.add(this);
        while(!q.isEmpty()){
            Node curr = q.remove(0);
            if(curr.isLeaf()){
                leafNodes.add((int)curr.value);
            }
            else{
                q.add(curr.lChild);
                q.add(curr.rChild);
            }
        }
        return leafNodes;
    }

    public boolean isLeaf(){
        if(this.lChild == null && this.rChild == null)  { return true; }
        return false;
    }

    public JSONObject toJSON(boolean isRoot){
        JSONObject json = new JSONObject();
        try{
            if(this.lChild == null){
                JSONObject leafJSON = new JSONObject();
                leafJSON.put("type", "leaf");
                leafJSON.put("height", 0);
                leafJSON.put("value", this.value);
                return leafJSON;
            } else{
                JSONObject nodeJSON = new JSONObject();
                if(isRoot){
                    nodeJSON.put("type", "root");
                } else{
                    nodeJSON.put("type", "node");
                }
                JSONArray childArr = new JSONArray();
                childArr.put(this.lChild.toJSON(false));
                childArr.put(this.rChild.toJSON(false));
                nodeJSON.put("height", this.value);
                nodeJSON.put("nodes", childArr);
                return nodeJSON;
            }
        } catch(JSONException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}