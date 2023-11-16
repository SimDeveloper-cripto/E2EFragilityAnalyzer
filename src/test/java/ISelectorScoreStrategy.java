
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

/* [DESCRIPTION]
    - Strategy to apply to selectors when assigning scores.
* */
public interface ISelectorScoreStrategy {
    float evaluateSelectorComplexity(Selector selector, Document document, WebDriver driver);
    float getSelectorScoreWeight();
}