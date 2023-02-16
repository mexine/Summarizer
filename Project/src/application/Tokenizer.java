package application;
import java.util.ArrayList;
import java.util.List;

//tokenizer ng buhay mo
public class Tokenizer {
    
    // can be loaded from a file
	String[] delimiters = {".", "!", "?",""};
    // can be loaded from a file
    String[] abbreviations = { "Mr.","Ms.", "Mrs.", "Prof.", "Capt.", "Sgt.", "Lt.", 
    		"Gen.", "Maj.", "Col.", "Gov.", "Sen.", "Rep.", "Pres.", "Sec.", "Asst.", 
    		"Dr.", "PhD.", "MD.", "ESQ.","DDS.", "DMD.", "CPA.", "ATT.", "R.N.", "B.S.",
    		"M.S.", "P.S.", "Sr.", "Jr.", "Sta.", "Pvt.", "Ave.", "Blvd.", "Rd.", "St.",
    		"Pl.", "Cir.", "Pkwy.", "Ln.", "Way.", "Ter.", "Etc.", "Inc.", "Ltd.", "Co.",
    		"Corp.", "LLC.", "GmbH.", "AG.", "SA.", "SAS.", "kg.", "lb.", "km.", "mi.",
    		"m.", "ft.", "in.", "mm.", "cm.", "yd.", "GHz.", "MHz.", "Kbps.", "Mbps.",
    		"GB.", "TB.", "KB.", "MB.", "Hz.", "kHz.", "°C.", "°F.", "°K.", "mol.", "rad.", "sr.", "/." };//pag may kulang lagyan na lang
    

    public Tokenizer() {
        
    }
    public List<Sentence> tokenizeParagraph1(String inputText) {
        List<Sentence> sentences = null;
		// Your code here to tokenize the input text into sentences
        return sentences;
    }

    public ArrayList<Sentence> tokenizeParagraph(String paragraph) {
        var result = new ArrayList<Sentence>();
        var words = paragraph.split("\\s+");
        var artifacts = new ArrayList<String>();

        for (String w : words) {
            if (isDelimeter(w) && !isAbbreviation(w)) {
                artifacts.add(w);
                var value = String.join(" ", artifacts);
                result.add(new Sentence(value, artifacts));
                artifacts = new ArrayList<>();
            }
            else {
                artifacts.add(w);    
            }
        }
        	
        return result;
    }

    private boolean isDelimeter(String value) {
        for(String delimiter : delimiters) {
            if (value.lastIndexOf(delimiter) == value.length() - 1) {
                return true;
            }
        }

        return false;
    }

    private boolean isAbbreviation(String word) {
        for (String a : abbreviations) {
            if (a.toLowerCase().equals(word.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}