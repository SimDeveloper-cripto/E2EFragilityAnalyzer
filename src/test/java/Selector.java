
import org.openqa.selenium.By;

public class Selector {
    private String selector, type;
    private float selectorComplexity, selectorScore, selectorFinalScore;

    @Override
    public String toString() {
        return
                "Selector = '" + selector + '\'' +
                ", Type = '" + type + '\'' +
                ", SelectorComplexity = " + selectorComplexity + '\'';
    }

    public Selector(By locator) {
        this.selector = createSelector(locator);
        this.type = locator.getClass().getSimpleName().replaceFirst("By", "");
        this.selectorScore = 0;
    }

    public Selector(String value, String type) {
        this.selector = value;
        this.type = type;
    }

    private String createSelector(By locator) {
        String stringLocator = locator.toString();
        int index = stringLocator.indexOf(":");
        String selectorString = stringLocator.substring(index + 2);

        System.out.println(selectorString);
        return selectorString;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }
    public float getSelectorScore() {
        return selectorScore;
    }
    public void setSelectorScore(float selectorScore) {
        this.selectorScore = selectorScore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public float getSelectorComplexity() {
        return selectorComplexity;
    }

    public void setSelectorComplexity(float selectorComplexity) {
        this.selectorComplexity = selectorComplexity;
    }

    public float getSelectorFinalScore() {
        return selectorFinalScore;
    }

    public void setSelectorFinalScore(float selectorFinalScore) {
        this.selectorFinalScore = selectorFinalScore;
    }
}