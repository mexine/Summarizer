package application;

import java.util.Objects;

public class RougeL {
    private final double rougeScore;

    public RougeL(String referenceText, String generatedText) {
        String[] reference = textCleaner(referenceText);
        String[] generated = textCleaner(generatedText);

        int lcs = getLCSLength(reference, generated, reference.length, generated.length);
        rougeScore = computeRougeScore(lcs, reference.length, generated.length);
    }

    public double getRougeScore() {
        return rougeScore;
    }

    // declarative function that gets the LCS length
    private int getLCSLength(String[] ref, String[] gen, int m, int n) {
        int[][] table = new int[m + 1][n + 1];
        
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) table[i][j] = 0; // for initial cell
                else if (Objects.equals(ref[i - 1], gen[j - 1])) table[i][j] = table[i - 1][j - 1] + 1; // if word is equal
                else table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]); // if word is not equal
            }
        }

        return table[m][n];
    }

    private double computeRougeScore(int lcs, int referenceTotal, int modelTotal) {
        double recall = (double) lcs / (double) referenceTotal;
        double precision = (double) lcs / (double) modelTotal;

        return 2 * (precision * recall) / (precision + recall);
    }

    // split the tokens, remove special characters, and convert to lower case to effectively detect equal statements
    private String[] textCleaner(String text) {
        String[] separatedText = text.split(" ");
        for (int x = 0; x < separatedText.length; x++)
            separatedText[x] = separatedText[x].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        return separatedText;
    }
}