package OpeningLibrary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.StringTokenizer;


/**
 * Created by Yuriy on 4/22/2015.
 */
public class OpeningLibraryParser {
    private static OpeningLibraryParser instance = null;
    static File libraryDirectory= null;
    String [] openingMoves;
    protected OpeningLibraryParser() {
        // Exists only to defeat instantiation.
    }
    public static OpeningLibraryParser getInstance() {
        if(instance == null) {
            instance = new OpeningLibraryParser();
        }
        return instance;
    }

    public Boolean updateLibrary(String source) throws Exception{
        Document doc = Jsoup.connect(source).get();
        if (source=="http://www.chess.com/openings/"){
            Elements openings= doc.getElementsByTag("tr");
            if (openings.size()!=22){
               return false;
            }
            openingMoves= new String [openings.size()-2];
            for (int i=2; i< openings.size();i++){
                openingMoves[i-2]= openings.get(i).child(1).textNodes().get(0).text();
            }
            return true;
        }
        return false;
    }
    public Boolean parseOpeningGraph() throws Exception{

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("OpeningLibrary");
        doc.appendChild(rootElement);

        for (int i=0; i<openingMoves.length;i++){
            StringTokenizer sequence = new StringTokenizer(openingMoves[i], " .");
            org.w3c.dom.Element previousElement=rootElement;
            previousElement=rootElement;
            while (sequence.hasMoreTokens()){
                String move= sequence.nextToken();
                if (isMoveString(move)){
                    org.w3c.dom.Element moveElement=makeMoveElement(doc,move);
                    previousElement.appendChild(moveElement);
                    previousElement=moveElement;
                }
            }
        }
        saveOpeningXML(doc);
        return false;
    }
    public org.w3c.dom.Element makeMoveElement(org.w3c.dom.Document doc, String moveString){
        org.w3c.dom.Element moveElement;
        moveElement= doc.createElement("Move");
        org.w3c.dom.Attr attr = doc.createAttribute("id");
        attr.setValue(moveString);
        moveElement.setAttributeNode(attr);
        return moveElement;
    }
    public boolean saveOpeningXML(org.w3c.dom.Document doc) throws Exception{
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(libraryDirectory,"openingLibrary.xml"));
            transformer.transform(source, result);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public boolean isMoveString(String moveString){
        if ((moveString.length()==0)||(!Character.isLetter(moveString.charAt(0)))){
            return false;
        }
        return true;
    }
    public void setDirectory(File saveDirectory){
        libraryDirectory=saveDirectory;
    }
}