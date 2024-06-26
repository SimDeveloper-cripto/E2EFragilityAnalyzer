
import org.openqa.selenium.WebDriver;

/* [DESCRIPTION]
    - Strategy to apply to pages and selectors when assigning combined scores.
**/
public interface IPageAndSelectorScoreStrategy {
    float evaluatePageAndSelectorComplexity(Selector selector, Page page, WebDriver driver);
}