
import org.openqa.selenium.By;

public class Selector {
    private String selector; // String value of the Locator
    private String type; // Type of the Locator
    private float selectorScore, selectorCombinedScoreWithPageScore;

    /** [DESCRIPTION]
        - This value is the result of a weighted average combined with
            selectorScore, pageComplexity, selectorCombinedScoreWithPageScore (all of them with their weights)
            Result calculation:
                (selectorComplexityScore * DefaultSelectorComplexityEvaluator.getSelectorScoreWeight())
                + (pageComplexityScore * DefaultPageComplexityEvaluator.getPageScoreWeight())
                + (pageAndSelectorComplexityScore * DefaultPageAndSelectorComplexityEvaluator.getPageAndSelectorScoreWeight()

        - It is used for calculating the Harmonic Mean used to set the Test score (Test in which is the Selector is used)
     */
    private float weightedAverageResult;

    public Selector(String value, String type) {
        this.selector = value;
        this.type     = type;
    }

    public Selector(By locator) {
        this.selector = createSelector(locator);
        this.type     = locator.getClass().getSimpleName().replaceFirst("By", "");
    }

    @Override
    public String toString() {
        return "Selector = '" + selector + '\'' + ", Type = '" + type + '\'' + ", Selector's WeightedAverageResult = " + getSelectorWeightedAverageResultScore() + '\'';
    }

    private String createSelector(By locator) {
        String stringLocator = locator.toString();
        int index = stringLocator.indexOf(":");

        return stringLocator.substring(index + 2);
    }

    public String getSelector() {
        return this.selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSelectorScore(float score) {
        this.selectorScore = score;
    }

    public float getSelectorScore() {
        return selectorScore;
    }

    public void setSelectorCombinedScoreWithPageScore(float score) {
        this.selectorCombinedScoreWithPageScore = score;
    }

    public float getSelectorCombinedScoreWithPageScore() {
        return selectorCombinedScoreWithPageScore;
    }

    public void setWeightedAverageResultScore(float score) {
        this.weightedAverageResult = score;
    }

    public double getSelectorWeightedAverageResultScore() {
        return this.weightedAverageResult;
    }
}