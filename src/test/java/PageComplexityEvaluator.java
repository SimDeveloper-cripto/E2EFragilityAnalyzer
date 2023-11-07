
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashSet;

public class PageComplexityEvaluator {
    public static int calculatePageComplexity(Page documentPage) {
        // TODO: This method can surely be modified
        Document document = documentPage.getPage();
        int totalElements = document.getAllElements().size();
        int uniqueElements = countUniqueElements(document.getAllElements());
        int imageCount = countImages(document);
        int linkCount = countLinks(document);

        // Combina i valori in un punteggio di complessità complessivo
        int complexityScore = totalElements + uniqueElements + imageCount + linkCount;

        complexityScore = normalizeScore(complexityScore);
        return complexityScore;
    }

    private static int countUniqueElements(Elements elements) {
        // Utilizza un HashSet per tenere traccia degli elementi unici
        HashSet<String> uniqueElementsSet = new HashSet<>();

        for (Element element : elements) {
            uniqueElementsSet.add(element.tagName());
        }
        return uniqueElementsSet.size();
    }

    private static int countImages(Document document) {
        Elements images = document.select("img");
        return images.size();
    }

    private static int countLinks(Document document) {
        Elements links = document.select("a");
        return links.size();
    }

    private static int normalizeScore(int score) {
        // Normalizza il punteggio nell'intervallo 1-10
        int maxScore = 3000;    // Valore massimo ottenibile (valore massimo attuale dei punteggi)
        int minScore = 0;       // Valore minimo ottenibile (valore minimo attuale dei punteggi)
        int normalizedMax = 10; // Valore massimo normalizzato
        int normalizedMin = 1;  // Valore minimo normalizzato

        // Utilizza la formula di normalizzazione min-max
        int normalizedScore = normalizedMin + (score - minScore) * (normalizedMax - normalizedMin) / (maxScore - minScore);

        if(normalizedScore > 10) normalizedScore = 10;
        return normalizedScore;
    }
}