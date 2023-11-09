
import org.jsoup.nodes.Document;

/* [DESCRIPTION]
    - This is the standard strategy to apply to selectors when assigning scores.
* */
public interface ScoreStrategy {
    float getIdComplexityScore(String selectorString);
    float getUrlComplexityScore(String selectorString);
    float getXPathComplexityScore(String xpathSelector);
    float getTagNameComplexityScore(String selectorString);
    float getLinkTextComplexityScore(String selectorString, Document document);
    float getCssSelectorComplexityScore(String cssSelector, Document document);

    /* Methods that may be useful to help Judge */
    float getSelectorScoreWeight();
}