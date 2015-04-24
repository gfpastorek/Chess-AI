package OpeningLibrary;

import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by Yuriy on 4/22/2015.
 */
public class OpeningGraph {
        ArrayList<MoveNode> rootNodes;
        public OpeningGraph(){
            rootNodes= new ArrayList<MoveNode>();

        }
        public MoveNode addRootNode(Node curNode){
            MoveNode newMove= new MoveNode(curNode);
            rootNodes.add(newMove);
            return newMove;
        }
        public MoveNode addNewMoveNode(Node curNode, MoveNode parentNode){
            if (curNode == null){
                return null;
            }
            if (parentNode == null){
                return addRootNode(curNode);
            }
            MoveNode newMove= new MoveNode(curNode);
            parentNode.addNextMove(newMove);
            return newMove;
        }
        public ArrayList<MoveNode> getRootNodes(){
            return rootNodes;
        }
        public void Flatten(){
            flattenLevel(rootNodes);
        }
        public void flattenLevel(ArrayList<MoveNode> currentLevel){
            if(currentLevel==null) {
                return;
            }
            for (int i=0; i<currentLevel.size();i++){
                for(int j=currentLevel.size()-1; j>i;j--){
                    if (currentLevel.get(i).getIdentifier().equals(currentLevel.get(j).getIdentifier())){
                        currentLevel.get(i).nextMoves.addAll(currentLevel.get(j).nextMoves);
                        currentLevel.remove(j);
                    }
                }
            }
            for(MoveNode currentNode:currentLevel) {
                flattenLevel(currentNode.nextMoves);
            }
        }
}
