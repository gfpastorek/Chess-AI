package OpeningLibrary;

import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by Yuriy on 4/22/2015.
 */
public class OpeningGraph {
        // all root nodes for this opening graph forest
        ArrayList<MoveNode> rootNodes;
        public OpeningGraph(){
            rootNodes= new ArrayList<MoveNode>();

        }
        public MoveNode addRootNode(Node curNode){
            //adds a rootnode
            MoveNode newMove= new MoveNode(curNode);
            rootNodes.add(newMove);
            return newMove;
        }
        public MoveNode addNewMoveNode(Node curNode, MoveNode parentNode){
            // adds any node to this forest
            // pass in the node as a raw XML node, but pass in the parent as a MoveNode
            if (curNode == null){
                return null;
            }
            if (parentNode == null){
                return addRootNode(curNode);
            }
            MoveNode newMove= new MoveNode(curNode);
            parentNode.addNextMove(newMove);
            // returns the new MoveNode so that it can be passed as a parameter for its children
            return newMove;
        }
        public ArrayList<MoveNode> getRootNodes(){
            return rootNodes;
        }
        public void Flatten(){
            flattenLevel(rootNodes);
        }
        public void flattenLevel(ArrayList<MoveNode> currentLevel){
            //super cool function
            // takes a series of lists that may have divergent paths as completely separate lists (one directional)
            // and flattens it into a forest with multiple follow nodes per movenode (multi directional)
            // optimal for quick traversal
            if(currentLevel==null) {
                return;
            }
            for (int i=0; i<currentLevel.size();i++){
                for(int j=currentLevel.size()-1; j>i;j--){
                    //flattens the list in backwards iteration
                    if (currentLevel.get(i).getIdentifier().equals(currentLevel.get(j).getIdentifier())){
                        currentLevel.get(i).nextMoves.addAll(currentLevel.get(j).nextMoves);
                        currentLevel.remove(j);
                    }
                }
            }
            for(MoveNode currentNode:currentLevel) {
                // flattens the next level
                flattenLevel(currentNode.nextMoves);
            }
        }
}
