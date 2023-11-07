
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* Tutti i punteggi citati hanno un range da 0 a 10, quindi un punteggio di complessità basso significa che quel selettore/pagina è poco complesso (robusto),
* viceversa se il punteggio è alto il selettore/pagina è molto complesso, stessa cosa per il calcolo della fragilità.
* */

public class SelectorComplexityEvaluator {
    private static final float CLASS_COEFF = 1.4f, ATTR_COEFF = 1.4f, ID_COEFF = 0.3f, CHILD_COEFF = 1.4f, NODE_COEFF = 0.7f, FUNC_COEFF = 1.2f;

    public static int evaluateIdSelectorComplexity(String selectorString) { // Per convenzione lascio il parametro (non si sa mai devo modificare)
        return 0;
    }

    // TODO: Da modificare
    public static int  evaluateTagNameSelectorComplexity(String selectorString) {
        return 5;
    }

    /* [DESCRIZIONE]
        - Ritorna il valore normalizzato nell'intervallo [0-10] del punteggio relativo al selettore di tipo "LinkText"
    */
    public static float evaluateLinkTextSelectorComplexity(String selectorString, Document document) {
        // return 6;

        int len        = selectorString.length();
        int depth      = evaluatePagePosition(selectorString, document);
        int n_add_attr = evaluateNumberOfAdditionalAttributes(selectorString, document);
        int n_el       = evaluateNumberOfElementsWithSameText(selectorString, document);

        int len_score         = len < 30 ? 2 : 4;
        int n_add_attr_score  = n_add_attr == 0 ? 0 : 3; // Se non ci sono attributi aggiuntivi (robusto) punteggio 0, altrimenti 3
        int n_el_score        = n_el == 0 ? 1 : 7;       // Se non ci sono elementi con lo stesso testo (robusto), punteggio 0, altrimenti 7

        // Punteggio complessivo
        int complexity = len_score + depth + n_add_attr_score + n_el_score;

        // Normalizzazione del punteggio in [0-10]
        return normalizeScore((float) complexity);
    }

    /* [DESCRIZIONE]
        - Ritorna il valore normalizzato nell'intervallo [0-10] del punteggio relativo al selettore di tipo "cssSelector"
    */
    public static float evaluateCssSelectorComplexity(String cssSelector, Document document) {
        return countCssSelectorComplexity(cssSelector, document);
    }

    /* [DESCRIZIONE]
        - Ritorna il valore normalizzato nell'intervallo [0-10] del punteggio relativo al selettore di tipo "xPath"
    */
    public static float evaluateXPathSelectorComplexity(String xpathSelector) {
        return countXPathSelectorComplexity(xpathSelector);
    }

    private static float countCssSelectorComplexity(String cssSelector, Document document) {
        // Calcolo della complessità del selettore CSS in base alla sua struttura nella pagina
        Elements elements = document.select(cssSelector);

        int classCount     = countSelectorSpecificity(cssSelector, "\\.");
        int attributeCount = countSelectorSpecificity(cssSelector, "\\[");
        int idCount        = countSelectorSpecificity(cssSelector, "\\#");
        int childCount     = countSelectorSpecificity(cssSelector, "child\\(\\d+| >");
        int functionCount  = countSelectorSpecificity(cssSelector, ":|::");
        int nodeCount      = elements.size();

        System.out.println("CSS Selector Complexity: attributeCount: " + attributeCount + " node count: " + nodeCount + " classCount: " + classCount +
                " idCount: " + idCount + " childCount: " + childCount + " functionCount: " + functionCount);

        // Punteggio calcolato sulla base della lunghezza e specificità del selettore
        float selectorScore = (classCount * CLASS_COEFF) + (attributeCount * ATTR_COEFF) + (idCount * ID_COEFF) + (childCount * CHILD_COEFF)
                + (nodeCount * NODE_COEFF) + (functionCount * FUNC_COEFF);

        return normalizeScore(selectorScore);
    }
    private static float countXPathSelectorComplexity(String xpathSelector) {
        // Calcolo della complessità del selettore XPath in base alla sua struttura nella pagina

        int classCount     = countSelectorSpecificity(xpathSelector, "\\[@class");
        int attributeCount = countSelectorSpecificity(xpathSelector, "\\[@");
        int idCount        = countSelectorSpecificity(xpathSelector, "\\[@id");
        int childCount     = countSelectorSpecificity(xpathSelector, "\\[\\d+");
        int nodeCount      = countSelectorSpecificity(xpathSelector, "\\/");
        int functionCount  = countSelectorSpecificity(xpathSelector, "\\(\\)");

        System.out.println("XPATH: attributeCount: " + attributeCount + " node count: " + nodeCount + " classCount: " + classCount +
                " idCount: " + idCount + " childCount: " + childCount + " functionCount: " + functionCount + "\n");

        // Punteggio calcolato sulla base della lunghezza e specificità del selettore
        float selectorScore = (classCount * CLASS_COEFF) + (attributeCount * ATTR_COEFF) + (idCount * ID_COEFF) + (childCount * CHILD_COEFF)
                + (nodeCount * NODE_COEFF) + (functionCount * FUNC_COEFF);

        return normalizeScore(selectorScore);
    }

    public static int countSelectorSpecificity(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return (int) matcher.results().count();
    }

    /* [DESCRIZIONE]
        - Ritorna il valore relativo alla profondità del link nel documento
     */
    private static int evaluatePagePosition(String selectorString, Document document) {
        Elements elements = document.select(selectorString);

        if (elements.isEmpty()) return 0; // TODO: what value do we assign in this case?

        // A partire dal primo elemento calcolo la profondità risalendo nella gerarchia
        int depth = 0;
        Element curr = elements.first();
        try {
            if (curr != null) {
                while (curr.parent() != null) {
                    curr = curr.parent();
                    depth++;
                }
            }
        } catch (NullPointerException e) {
            return depth;
        }

        return depth;
    }

    /* [DESCRIZIONE]
        - Ritorna il valore relativo al numero di attributi aggiuntivi del LinkText "selectorString"
        - Nel caso in cui "selectorString" non sia unica, sommiamo il numero di attributi per selettore
    */
    private static int evaluateNumberOfAdditionalAttributes(String selectorString, Document document) {
        Elements elements = document.select(selectorString);

        if (elements.isEmpty()) {
            return 0; // TODO: what value do we assign in this case?
        } else {
            int count = 0;

            if (elements.size() > 1) {
                for (Element elem : elements) count += elem.attributes().size();

                return count;
            }

            return Objects.requireNonNull(elements.first()).attributes().size();
        }
    }

    /* [DESCRIZIONE]
       - Ritorna il valore relativo al numero di attributi aggiuntivi del LinkText "selectorString"
       - Nel caso in cui "selectorString" non sia unica, sommiamo il numero di attributi per selettore
   */
    private static int evaluateNumberOfElementsWithSameText(String selectorString, Document document) {
        Elements elements = document.select("*");
        int count = 0;

        for (Element elem : elements) {
            if (!elem.text().isEmpty() && elem.text().equals(selectorString)) {
                count++;
            }
        }

        return count;
    }

    /* [DESCRIZIONE]
        - Ritorna il valore normalizzato nell'intervallo [0-10] di "selectorScore"
    */
    private static float normalizeScore(float selectorScore) {
        float maxSelectorScore = 100.0f; // TODO: Is this value correct?
        float minSelectorScore = 0.0f;
        float normalizedMax = 10.0f;
        float normalizedMin = 1.0f;

        float diff = normalizedMax - normalizedMin; // 9.0f
        float normalizedScore = normalizedMin + (selectorScore - minSelectorScore) * (diff) / (maxSelectorScore - minSelectorScore);

        if(normalizedScore < 0.0f) normalizedScore = normalizedMin;
        if(normalizedScore > 10.0f) normalizedScore = normalizedMax;
        return normalizedScore;
    }
}