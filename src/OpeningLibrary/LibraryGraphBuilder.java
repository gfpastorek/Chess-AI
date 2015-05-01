package OpeningLibrary;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Yuriy on 4/22/2015.
 */
public class LibraryGraphBuilder {
    //singleton pattern for a opening library builder class for XML documents
    // Builds a forest data structure from the XML document for the chess game to use
    private static LibraryGraphBuilder instance = null;
    static File libraryDirectory= null;
    protected LibraryGraphBuilder() {
        // Exists only to defeat instantiation.
    }
    public static LibraryGraphBuilder getInstance() {
        if(instance == null) {
            instance = new LibraryGraphBuilder();
        }
        return instance;
    }
    public OpeningGraph buildGraphFromXML() throws Exception{
        //opens the local XML doc
        File libraryXML = new File(libraryDirectory,"openingLibrary.xml");
        if (libraryXML==null){
            return null;
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(libraryXML);

        if (!doc.hasChildNodes()){
            return null;
        }
        OpeningGraph curGraph= new OpeningGraph();
        Node rootNode= doc.getFirstChild();
        NodeList rootChildren= rootNode.getChildNodes();
        for (int i=0; i< rootChildren.getLength();i++){
            Node moveNode = rootChildren.item(i);
            if(moveNode.getNodeName()=="Move") {
                addNodesToGraph(curGraph, null, rootChildren.item(i));
            }
        }
        curGraph.Flatten();
        return curGraph;
    }
    public void addNodesToGraph(OpeningGraph graph, MoveNode parent, Node child){
        //TODO fix case where opening graph fails to load
        MoveNode newNode= graph.addNewMoveNode(child, parent);
        NodeList nextChildren= child.getChildNodes();
        for (int i=0; i< nextChildren.getLength();i++) {
            addNodesToGraph(graph, newNode, nextChildren.item(i));
        }
    }
    public void setDirectory(File savedDirectory){
        libraryDirectory=savedDirectory;
    }
}
