
import org.openqa.selenium.By;

public class Selector {
    private String selector, type;
    private float selectorComplexity, selectorScore, selectorFinalScore;

    public Selector(String value, String type) {
        this.selector = value;
        this.type     = type;
    }

    public Selector(By locator) {
        this.selector = createSelector(locator);
        this.type     = locator.getClass().getSimpleName().replaceFirst("By", "");
        this.selectorScore = 0;
    }

    @Override
    public String toString() {
        return "Selector = '" + selector + '\'' + ", Type = '" + type + '\'' + ", SelectorComplexity = " + selectorComplexity + '\'';
    }

    private String createSelector(By locator) {
        String stringLocator = locator.toString();
        int index = stringLocator.indexOf(":");

        // String selectorString = stringLocator.substring(index + 2);
        // System.out.println(selectorString); // Not necessary to print
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

    public float getSelectorScore() {
        return this.selectorScore;
    }

    public void setSelectorScore(float selectorScore) {
        this.selectorScore = selectorScore;
    }

    public float getSelectorComplexity() {
        return this.selectorComplexity;
    }

    public void setSelectorComplexity(float selectorComplexity) {
        this.selectorComplexity = selectorComplexity;
    }

    public float getSelectorFinalScore() {
        return this.selectorFinalScore;
    }

    public void setSelectorFinalScore(float selectorFinalScore) {
        this.selectorFinalScore = selectorFinalScore;
    }
}