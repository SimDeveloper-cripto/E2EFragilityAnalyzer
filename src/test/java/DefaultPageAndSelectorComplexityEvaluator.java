
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import exception.ElementNotFoundException;

public class DefaultPageAndSelectorComplexityEvaluator implements IPageAndSelectorScoreStrategy {
    @Override
    public float evaluatePageAndSelectorComplexity(Selector selector, Page page, WebDriver driver) {
        String selectorType   = selector.getType();
        String selectorString = selector.getSelector();

        String newSelectorString;
        int nElemFirstMatch, nElemSecondMatch;
        float ratio;

        System.out.println("[Page And Selector Complexity Evaluator] Current URL: " + driver.getCurrentUrl());
        System.out.println("[Page And Selector Complexity Evaluator] Selector: " + selectorString);

        try {
            if (!selectorType.equals("CssSelector") && !selectorType.equals("XPath")) {
                return 0;
            } else {
                /* HERE IS THE ONLY PLACE WHERE THE EXCEPTION MUST BE GENERATED (IF IT HAPPENS) */
                nElemFirstMatch = getNumberOfMatches(selectorString, selectorType, driver);
                System.out.println("[Page And Selector Complexity Evaluator] nElemFirstMatch: " + nElemFirstMatch);

                int depth;
                if (selectorType.equals("CssSelector"))
                    depth = SelectorDepthEvaluator.evaluateCssSelectorHierarchyDepth(selectorString);
                else
                    depth = SelectorDepthEvaluator.evaluateXPathSelectorHierarchyDepth(selectorString);

                System.out.println("[Page And Selector Complexity Evaluator] Depth: " + depth);
                if (depth >= 1) {
                    newSelectorString = removeFirstLevelForSelector(selectorString, selectorType);

                    /* [IMPORTANT]
                        - If the first time we call getNumberOfMatches() no exceptions occur,
                            an Exception on the 2nd time should not happen: at least one element must be caught.
                    **/
                    nElemSecondMatch  = getNumberOfMatches(newSelectorString, selectorType, driver);
                    System.out.println("[Page And Selector Complexity Evaluator] nElemSecondMatch: " + nElemSecondMatch);
                    ratio = (float) nElemFirstMatch / (float) nElemSecondMatch;
                } else {
                    ratio = 1; // So that we return 0 (A selector without hierarchy is good)
                }
            }
        } catch (ElementNotFoundException e) {
            System.err.println("Element not found: " + e.getMessage()); // Should set the Test to "failed"
            ratio = 0.0f;
        }

        System.out.println("[Page And Selector Complexity Evaluator] Ratio: " + ratio);
        return (1 - ratio);
    }

    private String removeFirstLevelForSelector(String selectorString, String selectorType) {
        if (selectorType.equals("CssSelector"))
            return removeFirstLevelCssSelector(selectorString);
        else
            return removeFirstLevelXPath(selectorString);
    }

    private String removeFirstLevelCssSelector(String selectorString) {
        String[] combinators = { ">", "+", "~", " " };

        for (String combinator : combinators) {
            int index = selectorString.indexOf(combinator);
            if (index != -1 && index < selectorString.length() - 1) {
                return selectorString.substring(index + 1).trim();
            }
        }
        return selectorString.trim();
    }

    private String removeFirstLevelXPath(String selectorString) {
        int index;
        if (selectorString.startsWith("//")) {
            index = selectorString.indexOf("/", 2); // The XPath string starts with "//"
            if (index != -1)
                return "//" + selectorString.substring(index + 1);
        } else if (selectorString.startsWith("/")) {
            index = selectorString.indexOf("/", 1);

            /*
                - Input: /app/description/subject[1]
                - Output: //description/subject[1]
            **/
            if (index != -1)
                return "/" + selectorString.substring(index);
        } else {
            /*
                - Input:  html/body/table[3]/tbody/tr[3]/td[2]
                - Output: //body/table[3]/tbody/tr[3]/td[2]     (We can't risk it to not be recognized)
            **/
            index = selectorString.indexOf("/");
            if (index != -1)
                return "//" + selectorString.substring(index + 1);
        }
        return selectorString;
    }

    private static int getNumberOfMatches(String selectorString, String type, WebDriver driver) throws ElementNotFoundException {
        System.out.println("[Page And Selector Complexity Evaluator] getNumberOfMatcher(), received: " + selectorString);
        List<WebElement> list;

        if (type.equals("CssSelector")) {
            list = driver.findElements(By.cssSelector(selectorString));
        } else {
            list = driver.findElements(By.xpath(selectorString));
        }

        if (list.isEmpty()) {
            System.err.println("[Page And Selector Complexity Evaluator] getNumberOfMatcher(), Exception generated!");
            throw new ElementNotFoundException("[Page And Selector Complexity Evaluator] Exception! Match = 0, Could not find: " + selectorString);
        }

        return list.size();
    }

    public static float getPageAndSelectorScoreWeight() {
        return 0.33f;
    }
}