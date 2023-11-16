
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/* [DESCRIPTION]
    - The final score value is set to the interval [0-1].
    - This means that the more the score tends to 1 the more fragile that selector is (and so is the Test).
**/
public class DefaultSelectorComplexityEvaluator implements ISelectorScoreStrategy {
    @Override
    public float evaluateSelectorComplexity(Selector selector, Document document, WebDriver driver) {
        String selectorType   = selector.getType();
        String selectorString = selector.getSelector();

        int level, depth = 0;
        float hierarchyScore; // [0-1]
        float typeScore;      // [0-1]

        // Step 1: Hierarchy Score calculation
        switch (selectorType) {
            case "CssSelector":
                depth = evaluateCssSelectorHierarchyDepth(selectorString);
                level = depth + 1;
                hierarchyScore = (float) (1 - (1 / level)); // [0-1]
                break;
            case "XPath":
                depth = evaluateXPathSelectorHierarchyDepth(selectorString);
                level = depth + 1;
                hierarchyScore = (float) (1 - (1 / level)); // [0-1]
                break;
            default:
                hierarchyScore = 0.0f;
        }
        System.out.println("(Debug) Hierarchy Depth: " + depth);
        System.out.println("(Analyzed) Selector Hierarchy score: " + hierarchyScore);

        // Step 2: Type Score calculation (Rule Based)
        // TODO: "PartialLinkText" and "ClassName" never occur (at least for now).
        switch (selectorType) {
            case "CssSelector":
                typeScore = evaluateCssSelectorTypeScore(selectorString, depth);
                break;
            case "XPath":
                typeScore = evaluateXPathTypeScore(selectorString, depth, driver);
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
        System.out.println("(Analyzed) Selector Type score: " + typeScore);

        // Step 3: Combination
        return ((hierarchyScore + typeScore) / 2); // [0-1]
    }

    @Override
    public float getSelectorScoreWeight() {
        return 0.8f;
    }

    /* METHODS */

    private static int evaluateCssSelectorHierarchyDepth(String selectorString) {
        String[] combinators = selectorString.split("[ >~+]+");
        return combinators.length - 1;
    }

    private static int evaluateXPathSelectorHierarchyDepth(String selectorString) {
        if (selectorString.startsWith("//"))
            selectorString = selectorString.substring(2);

        String[] combinators = selectorString.split("/");
        return combinators.length - 1;
    }

    private static float evaluateCssSelectorTypeScore(String selectorString, int depth) {
        if (depth >= 2) {
            String lastElement = getLastCssSelectorElement(selectorString);
            return evaluateCssSelectorElementType(lastElement);
        }
        return 0.0f;
    }

    private static float evaluateXPathTypeScore(String selectorString, int depth, WebDriver driver) {
        if (depth >= 1) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(selectorString));

                int size = elements.size();
                if (size > 0) return evaluateXPathElementType(elements.get(size - 1));
            } catch(org.jsoup.select.Selector.SelectorParseException e) {
                e.printStackTrace();
            }
        }
        return 0.0f;
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

    private static float evaluateXPathElementType(WebElement lastElement) { // [0-1]
        float score = 0.0f;

        // If the element has associated an id or name
        if ((lastElement.getAttribute("id") != null && !lastElement.getAttribute("id").isEmpty())
            || (lastElement.getAttribute("name") != null && !lastElement.getAttribute("name").isEmpty())) {
            score = 0.0f;
        }

        // If the element has associated a class
        if (lastElement.getAttribute("class") != null && !lastElement.getAttribute("class").isEmpty()) {
            score = 0.5f;
        }

        // If the element has associated a tag name
        if (lastElement.getTagName() != null && !lastElement.getTagName().isEmpty()) {
            score = 0.8f;
        }

        // If the element has associated a link text
        if (lastElement.getTagName().equalsIgnoreCase("a") && lastElement.getText() != null && !lastElement.getText().isEmpty()) {
            score = 0.6f;
        }

        // If the element has associated a partial link text
        if (lastElement.getTagName().equalsIgnoreCase("a") && lastElement.getText() != null && lastElement.getText().contains("partial text")) {
            score = 0.7f;
        }

        return score;
    }
}
