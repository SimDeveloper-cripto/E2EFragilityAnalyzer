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

    public CascadeSpecificityEvaluator () {}

    /* [CSS SELECTOR] */

    public float applyAlgorithmForCssSelectors(String selector) {
        String[] levels = SelectorDepthEvaluator.getElementsFromCssSelector(selector);

        /*
            - depth = 1 --> An input selector like "input"
            - That's because evaluateCssSelectorHierarchyDepth(selector) return 0 if there is only one level
        **/
        int depth = SelectorDepthEvaluator.evaluateCssSelectorHierarchyDepth(selector) + 1;

        // Evaluate Score for each level
        float[] tempScores = new float[levels.length];
        for (int i = 0; i < levels.length; i++) {
            Cascade currCascade = evaluateCascadeFromCssSelector(levels[i]);
            int sumABC = currCascade.getA() + currCascade.getB() + currCascade.getC();
            tempScores[i] = evaluateScore(currCascade) / sumABC;
        }

        float result = 0.0f;
        for (int i = 0; i < tempScores.length; i++) {
            int div = (i + 1) / depth;
            result += tempScores[i] + div; // float + int is casts to float
        }

        return (1 - result);
    }

    public Cascade evaluateCascadeFromCssSelector(String selector) {
        A = 0;
        B = 0;
        C = 0;

        // System.out.println("[CascadeSpecificityEvaluator] Selector received: " + selector);

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

        return new Cascade(A, B, C);
    }

    /* THIS METHOD IS CREATED FOR TESTING */
    public float apply_ABC_For_Css_Selectors(String selector) {
        Cascade cascade = evaluateCascadeFromCssSelector(selector);
        return evaluateScore(cascade); // [0 - 1]
    }

    /* [XPATH SELECTOR] */

    public float applyAlgorithmForXPathSelectors(String selector) {
        String[] levels = SelectorDepthEvaluator.getElementsFromXPathSelector(selector);

        /*
            - depth = 1 --> An input selector like "input"
            - That's because evaluateXPathSelectorHierarchyDepth(selector) return 0 if there is only one level
        **/
        int depth = SelectorDepthEvaluator.evaluateXPathSelectorHierarchyDepth(selector) + 1;

        // Evaluate Score for each level
        float[] tempScores = new float[levels.length];
        for (int i = 0; i < levels.length; i++) {
            Cascade currCascade = evaluateCascadeFromXPathSelector(levels[i]);
            int sumABC = currCascade.getA() + currCascade.getB() + currCascade.getC();
            tempScores[i] = evaluateScore(currCascade) / sumABC;
        }

        float result = 0.0f;
        for (int i = 0; i < tempScores.length; i++) {
            int div = (i + 1) / depth;
            result += tempScores[i] + div; // float + int is casts to float
        }

        return (1 - result);
    }

    public Cascade evaluateCascadeFromXPathSelector(String selector) {
        A = 0;
        B = 0;
        C = 0;

        // System.out.println("[CascadeSpecificityEvaluator] XPath Selector received: " + selector);

        // Count IDs
        Pattern idPattern = Pattern.compile("@id\\b");
        Matcher idMatcher = idPattern.matcher(selector);
        while (idMatcher.find()) A++;

        // Count Classes
        Pattern classPattern = Pattern.compile("@class\\b");
        Matcher classMatcher = classPattern.matcher(selector);
        while (classMatcher.find()) B++;

        // Count Attributes - Including those within predicates and functions, but excluding @id and @class
        Pattern attrPattern = Pattern.compile("@(?!id\\b|class\\b)\\w+");
        Matcher attrMatcher = attrPattern.matcher(selector);
        while (attrMatcher.find()) B++;

        // Count Element-types and Pseudo-elements
        Pattern elementTypePattern = Pattern.compile("/(\\w+)");
        Matcher elementTypeMatcher = elementTypePattern.matcher(selector);
        while (elementTypeMatcher.find()) C++;

        Pattern pseudoElementPattern = Pattern.compile("\\b(text\\(\\)|node\\(\\)|comment\\(\\))");
        Matcher pseudoElementMatcher = pseudoElementPattern.matcher(selector);
        while (pseudoElementMatcher.find()) C++;

        // System.out.printf("[CascadeSpecificityEvaluator] (A, B, C) = (%d, %d, %d)%n", A, B, C);
        // System.out.println();

        return new Cascade(A, B, C);
    }

    /* THIS METHOD IS CREATED FOR TESTING */
    public float apply_ABC_For_XPath_Selectors(String selector) {
        Cascade cascade = evaluateCascadeFromXPathSelector(selector);
        return evaluateScore(cascade); // [0 - 1]
    }

    /* [EVALUATE SCORE] */
    private float evaluateScore(Cascade c) {
        final float k1 = 0.5f, k2 = 0.3f, k3 = 0.2f;
        return ((c.getA() * k1) + (c.getB() * k2) + (c.getC() * k3));
    }
}