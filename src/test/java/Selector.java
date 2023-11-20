
import org.openqa.selenium.By;

public class Selector {
    private String selector, type;
    private float selectorScore;
    private float pageScore;
    private float pageAndSelectorScore;
    private float selectorFinalScore;

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
        return "Selector = '" + selector + '\'' + ", Type = '" + type + '\'' + ", SelectorFinalScore = " + getSelectorFinalScore() + '\'';
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

    public void setPageScore(float pageScore) {
        this.pageScore = pageScore;
    }

    public float getPageScore() {
        return pageScore;
    }

    public void setPageAndSelectorScore(float score) {
        this.pageAndSelectorScore = score;
    }

    public float getPageAndSelectorScore() {
        return pageAndSelectorScore;
    }

    public double getSelectorFinalScore() {
        return this.selectorFinalScore;
    }

    public void setSelectorFinalScore(float selectorFinalScore) {
        this.selectorFinalScore = selectorFinalScore;
    }
}