
import org.jsoup.nodes.Document;

/* [DESCRIPTION]
    - The final score value is set to the interval [0-1].
    - This means that the more the score tends to 1 the more fragile that selector is (and so is the Test).
**/
public class DefaultSelectorComplexityEvaluator implements ISelectorScoreStrategy {
    @Override
    public float evaluateSelectorComplexity(Selector selector, Document document) {
        String selectorType   = selector.getType();
        String selectorString = selector.getSelector();

        int level, depth = 0;
        float hierarchyScore; // [0-1]
        float typeScore;      // [0-1]

        // Step 1: Hierarchy Score calculation
        switch (selectorType) {
            case "CssSelector":
                depth = SelectorDepthEvaluator.evaluateCssSelectorHierarchyDepth(selectorString);
                level = depth + 1;
                hierarchyScore = (1 - ((float) 1 / level)); // [0-1]
                break;
            case "XPath":
                depth = SelectorDepthEvaluator.evaluateXPathSelectorHierarchyDepth(selectorString);
                level = depth + 1;
                hierarchyScore = (1 - ((float) 1 / level)); // [0-1]
                break;
            default:
                hierarchyScore = 0.0f;
        }

        System.out.println("[Selector Complexity Evaluator] Selector: " + selectorString);
        System.out.println("[Selector Complexity Evaluator] (Debug) Hierarchy Depth: " + depth);
        System.out.println("[Selector Complexity Evaluator] (Analyzed) Selector Hierarchy Score: " + hierarchyScore);

        // Step 2: Type Score calculation (Rule Based)
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        switch (selectorType) {
            case "CssSelector":
                typeScore = evaluator.applyAlgorithmForCssSelectors(selectorString);
                /* [OLD] */
                // typeScore = evaluateCssSelectorTypeScore(selectorString, depth);
                break;
            case "XPath":
                typeScore = evaluator.applyAlgorithmForXPathSelectors(selectorString);
                /* [OLD] */
                // typeScore = evaluateXPathTypeScore(selectorString, depth);
                break;
            case "TagName":
            case "Name":
                typeScore = 0.8f;
                break;
            case "LinkText":
            case "ClassName":
                typeScore = 0.5f;
                break;
            case "PartialLinkText":
                typeScore = 0.6f;
                break;
            case "Id":
            default:
                typeScore = 0.0f; // If the selector is of type "id" we set score to 0.0f too.
        }
        System.out.println("[Selector Complexity Evaluator] (Analyzed) Selector Type Score: " + typeScore);

        // Step 3: Combination
        return ((hierarchyScore + typeScore) / 2); // [0-1]
    }

    public static float getSelectorScoreWeight() {
        return 0.33f;
    }

    /* METHODS */

    private static float evaluateCssSelectorTypeScore(String selectorString, int depth) {
        if (depth >= 1) {
            String lastElement = getLastCssSelectorElement(selectorString);
            return evaluateCssSelectorElementType(lastElement);
        }
        return evaluateCssSelectorElementType(selectorString);
    }

    private static float evaluateXPathTypeScore(String selectorString, int depth) {
        if (depth >= 1) {
            String lastLevel = selectorString.substring(selectorString.lastIndexOf("/") + 1);
            String evaluate = "//" + lastLevel;
            return evaluateXPathElementType(evaluate);
        }
        return evaluateXPathElementType(selectorString);
    }

    private static String getLastCssSelectorElement(String selectorString) {
        String[] elements = selectorString.split("[ >~+]+");
        return elements[elements.length - 1];
    }

    private static float evaluateCssSelectorElementType(String lastElement) {
        if (lastElement.startsWith("#")) {
            return 0.0f; // id selector
        } else if (lastElement.startsWith(".")) {
            return 0.5f; // class name selector
        } else {
            return 0.8f; // tag name selector or other types
        }
    }

    private static float evaluateXPathElementType(String xpathLastElement) {
        // In order Best to Worst
        if (xpathLastElement.contains("@id") || xpathLastElement.contains("@name")) {
            return 0.0f;
        } else if (xpathLastElement.contains("@class")) {
            return 0.5f;
        } else if (xpathLastElement.contains("[text()")) { // LinkText
            return 0.6f;
        } else if (xpathLastElement.contains("[contains(text()")) { // Partial LinkText
            return 0.7f;
        }

        return 0.8f;
    }
}