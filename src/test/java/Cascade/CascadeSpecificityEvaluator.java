package Cascade;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/* [CASCADE: SPECIFICITY ALGORITHM]
    Source: https://cssbootcamp.com/css/specificity-calculator
    (A, B, C)
        A = # of IDs
        B = # of class, attribute, and pseudo-class selectors
        C = # of element type and pseudo-elements
*/

/* [SCORE CALCULATION]
    - Given k1 = 0.5f, k2 = 0.3f, k3 = 0.2f
        - The score is ( (A*k1) + (B*k2) + (C*k3) )
**/

public class CascadeSpecificityEvaluator {
    private int A, B, C; // That is (A, B, C)

    public CascadeSpecificityEvaluator () {
        this.A = 0;
        this.B = 0;
        this.C = 0;
    }

    /* [CSS SELECTOR] */

    public float applyAlgorithmForCssSelectors(String selector) {
        System.out.println("[CascadeSpecificityEvaluator] Selector received: " + selector);

        Pattern idPattern            = Pattern.compile("#\\w+");
        Pattern classPattern         = Pattern.compile("\\.[-\\w]+");
        Pattern attrPattern          = Pattern.compile("\\[.+?\\]");
        Pattern pseudoClassPattern   = Pattern.compile(":[^:]+(?![^\\[]*\\])(?:::)?");
        Pattern elementTypePattern   = Pattern.compile("(?<=^|\\s|>|\\+|~)([a-z]+)(?![^\\[]*\\]|[^\\(]*\\))");
        Pattern pseudoElementPattern = Pattern.compile("::[-\\w]+");

        // Count IDs
        Matcher m = idPattern.matcher(selector);
        while (m.find()) {
            A++;
        }

        // Count Classes
        m = classPattern.matcher(selector);
        while (m.find()) {
            B++;
        }

        // Count Attributes
        m = attrPattern.matcher(selector);
        while (m.find()) {
            B++;
        }

        // Count Pseudo-Classes
        m = pseudoClassPattern.matcher(selector);
        while (m.find()) {
            B++;
        }

        // Count Element-Types
        m = elementTypePattern.matcher(selector);
        while (m.find()) {
            C++;
        }

        // Count Pseudo-Elements
        m = pseudoElementPattern.matcher(selector);
        while (m.find()) {
            C++;
        }

        System.out.printf("[CascadeSpecificityEvaluator] (A, B, C) = (%d, %d, %d)%n", A, B, C);
        System.out.println();

        Cascade cascade = new Cascade(A, B, C);
        return evaluateScore(cascade); // [0 - 1]
    }

    /* [XPATH SELECTOR] */
    /*public float applyAlgorithmForXPathSelectors(String selector, Document document) {
        Elements elements = document.select(selector);
        int A = countOccurrencesOfPatternForXPathSelector(elements, "id"); // Count the number of elements that have ID

        int B = countOccurrencesOfPatternForXPathSelector(elements, "class") +
                countOccurrencesOfRegexForXPathSelector(selector, "\\[@[^\\]]+\\]") + countOccurrencesOfRegexForXPathSelector(selector, ":.*\\(.*\\)");

        int C = countOccurrencesOfRegexForXPathSelector(selector, "^[^\\[]+") +
                countOccurrencesOfRegexForXPathSelector(selector, "::[^:]+");
        // return evaluateScore(); // [0 - 1]
    } */

    /*private int countOccurrencesOfPatternForXPathSelector(Elements elements, String pattern) {
        int count = 0;
        for(Element e : elements) {
            if (e.hasAttr(pattern)) count++;
        }
        return count;
    } */

    /*private int countOccurrencesOfRegexForXPathSelector(String selector, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(selector);

        int count = 0;
        while (matcher.find()) count++;
        return count;
    } */

    /* [EVALUATE SCORE] */
    private float evaluateScore(Cascade c) {
        final float k1 = 0.5f, k2 = 0.3f, k3 = 0.2f;
        return ((c.getA() * k1) + (c.getB() * k2) + (c.getC() * k3));
    }
}