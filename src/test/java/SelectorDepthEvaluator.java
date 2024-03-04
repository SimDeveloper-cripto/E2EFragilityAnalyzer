
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
            - Input: #header .menu
            - Output: ["#header", ".menu"]
    **/
    public static String[] getElementsFromCssSelector(String selectorString) {
        if (selectorString == null || selectorString.isEmpty()) {
            System.out.println("[Selector Complexity Evaluator] (ERROR) Function: getElementsFromCssSelector(), " + selectorString + " is Null or empty string!");
            return new String[0];
        }

        return selectorString.split("[ >~+]+");
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
        else if (selectorString.startsWith("(//") || selectorString.startsWith(".//"))
            selectorString = selectorString.substring(3);
        else if(selectorString.startsWith("(/"))
            selectorString = selectorString.substring(2);

        String[] combinators = selectorString.split("/");
        return combinators.length - 1;
    }

    public static String[] getElementsFromXPathSelector(String selectorString) {
        if (selectorString == null || selectorString.isEmpty()) {
            System.out.println("[Selector Complexity Evaluator] (ERROR) Function: getElementsFromXPathSelector(), " + selectorString + " is Null or empty string!");
            return new String[0];
        }

        if (selectorString.startsWith("//"))
            selectorString = selectorString.substring(2);
        else if (selectorString.startsWith("/"))
            selectorString = selectorString.substring(1);
        else if (selectorString.startsWith("(//") || selectorString.startsWith(".//"))
            selectorString = selectorString.substring(3);
        else if(selectorString.startsWith("(/"))
            selectorString = selectorString.substring(2);

        String[] parts = selectorString.split("/");
        String[] hierarchy = new String[parts.length];

        for(int i = 0; i < parts.length; i++) {
            hierarchy[i] = "//" + parts[i];
        }

        return hierarchy;
    }
}