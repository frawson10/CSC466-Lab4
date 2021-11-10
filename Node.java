import java.util.ArrayList;

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
}