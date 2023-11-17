
/* [DESCRIPTION]
    - Strategy to apply to pages when assigning scores.
* */
public interface IPageScoreStrategy {
    float evaluatePageComplexity(Page page);
    // float getPageScoreWeight();
}