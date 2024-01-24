
public class SelectorDepthEvaluator {
    public SelectorDepthEvaluator() {}

    /* [DESCRIPTION]
        - Lets explain by example
            - Selector: #header .menu
            - Depth: (1 level of hierarchy, so that if there are no levels the depth is 0)
    **/
    public static int evaluateCssSelectorHierarchyDepth(String selectorString) {
        if (selectorString == null || selectorString.isEmpty()) {
            System.out.println("[Selector Complexity Evaluator] (ERROR) " + selectorString + " is Null or empty string!");
            return 0;
        }

        String[] combinators = selectorString.split("[ >~+]+");
        return combinators.length - 1;
    }

    /* [DESCRIPTION]
        - Lets explain by example
            - Selector: /html/body/div
            - Depth: 2 (2 levels of hierarchy)
    **/
    public static int evaluateXPathSelectorHierarchyDepth(String selectorString) {
        if (selectorString == null || selectorString.isEmpty()) {
            System.out.println("[Selector Complexity Evaluator] (ERROR) " + selectorString + " is Null or empty string!");
            return 0;
        }

        if (selectorString.startsWith("//"))
            selectorString = selectorString.substring(2);
        else if (selectorString.startsWith("/"))
            selectorString = selectorString.substring(1);

        String[] combinators = selectorString.split("/");
        return combinators.length - 1;
    }
}