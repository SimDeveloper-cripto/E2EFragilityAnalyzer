
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/* [CASCADE: SPECIFICITY ALGORITHM]
    Source: https://cssbootcamp.com/css/specificity-calculator
    (A, B, C)
        A = # of IDs
        B = # of class, attribute, and pseudo-class selectors
        C = # of element type and pseudo-elements
*/

/* [SCORE CALCULATION]
    - 1. A higher score at lower specificities and vice versa.
            We could do this kind of normalization: 1 - [1 / (1 + A + B + C)]
            Note: I tried to find "values of A for which the selector is recognized as "too specific" but found none.
**/

public class CascadeSpecificityEvaluator {
    private final String selector;

    public CascadeSpecificityEvaluator(String selector) {
        this.selector = selector;
    }

    /* [CSS SELECTOR] */
    public float applyAlgorithmForCssSelectors() {
        int A = countOccurrencesForCssSelector("#");

        int B = countOccurrencesForCssSelector("\\.\\w+") +
                countOccurrencesForCssSelector("\\[\\w+=[^\\]]+\\]") + countOccurrencesForCssSelector(":\\w+");

        int C = countOccurrencesForCssSelector("(\\w+|::\\w+)");
        return evaluateScore(A, B, C); // [0 - 1]
    }

    private int countOccurrencesForCssSelector(String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(this.selector);
        int count = 0;
        while (matcher.find()) count ++;
        return count;
    }

    /* [XPATH SELECTOR] */
    public float applyAlgorithmForXPathSelectors(Document document) {
        Elements elements = document.select(selector);
        int A = countOccurrencesOfPatternForXPathSelector(elements, "id"); // Count the number of elements that have ID

        int B = countOccurrencesOfPatternForXPathSelector(elements, "class") +
                countOccurrencesOfRegexForXPathSelector("\\[@[^\\]]+\\]") + countOccurrencesOfRegexForXPathSelector(":.*\\(.*\\)");

        int C = /* Element types */ countOccurrencesOfRegexForXPathSelector("^[^\\[]+") +
                /* Pseudo-Elements */ countOccurrencesOfRegexForXPathSelector("::[^:]+");
        return evaluateScore(A, B, C); // [0 - 1]
    }

    private int countOccurrencesOfPatternForXPathSelector(Elements elements, String pattern) {
        int count = 0;
        for(Element e : elements) {
            if (e.hasAttr(pattern)) count++;
        }
        return count;
    }

    private int countOccurrencesOfRegexForXPathSelector(String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.selector);

        int count = 0;
        while (matcher.find()) count++;
        return count;
    }

    /* [EVALUATE SCORE] */
    private float evaluateScore(int A, int B, int C) {
        float total = 1.0f + A + B + C;
        return 1.0f / total;
    }
}