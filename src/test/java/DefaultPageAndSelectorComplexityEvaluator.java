
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DefaultPageAndSelectorComplexityEvaluator implements IPageAndSelectorScoreStrategy {
    @Override
    public float evaluatePageAndSelectorComplexity(Selector selector, Page page, WebDriver driver) {
        String selectorType   = selector.getType();
        String selectorString = selector.getSelector();

        String newSelectorString;
        int nElemFirstMatch, nElemSecondMatch;
        float ratio;

        if (!selectorType.equals("CssSelector") && !selectorType.equals("XPath")) {
            return 0;
        } else {
            nElemFirstMatch = getNumberOfMatches(selectorString, selectorType, driver);

            if (nElemFirstMatch == 0) {
                ratio = 0.0f;
            } else {
                newSelectorString = removeFirstLevelForSelector(selectorString, selectorType);
                nElemSecondMatch  = getNumberOfMatches(newSelectorString, selectorType, driver);
                ratio = (float) nElemFirstMatch / (float) nElemSecondMatch;
            }
        }
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
            if (index != -1)
                return selectorString.substring(index);
        } else {
            index = selectorString.indexOf("/");
            if (index != -1)
                return selectorString.substring(index + 1);
        }
        return selectorString;
    }

    private static int getNumberOfMatches(String selectorString, String type, WebDriver driver) {
        System.err.println("getNumberOfMatches(), selector received: " + selectorString);

        List<WebElement> list;
        if (type.equals("CssSelector")) {
            list = driver.findElements(By.cssSelector(selectorString));
        } else {
            list = driver.findElements(By.xpath(selectorString));
        }

        return list.size();
    }

    public static float getPageAndSelectorScoreWeight() {
        return 0.33f;
    }
}