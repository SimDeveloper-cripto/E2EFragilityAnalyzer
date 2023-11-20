import org.jsoup.nodes.Document;

/* [DESCRIPTION]
    - Strategy to apply to selectors when assigning scores.
* */
public interface ISelectorScoreStrategy {
    float evaluateSelectorComplexity(Selector selector, Document document);
    // float getSelectorScoreWeight();
}