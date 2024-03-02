package Cascade;

import org.junit.Test;
import org.junit.Assert;

public class CascadeXPATHTest {
    @Test
    public void cascade1() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 1.7f;
        float delta = 0.001f;

        // (1, 2, 3)
        Assert.assertEquals(evaluator.applyAlgorithmForXPathSelectors("//div[@id='uniqueId' and contains(@class, 'exampleClass')]//span[@data-attribute='value']::text()"), expectedScore, delta);
    }

    @Test
    public void cascade2() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.2f;
        float delta = 0.001f;

        // (0, 0, 1)
        Assert.assertEquals(evaluator.applyAlgorithmForXPathSelectors("//p"), expectedScore, delta);
    }

    @Test
    public void cascade3() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 1.0f;
        float delta = 0.001f;

        // (1, 1, 1)
        Assert.assertEquals(evaluator.applyAlgorithmForXPathSelectors("//*[@id='uniqueId']/a[contains(@class, 'linkClass')]"), expectedScore, delta);
    }

    @Test
    public void cascade4() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 1.5f;
        float delta = 0.001f;

        // (0, 3, 3)
        Assert.assertEquals(evaluator.applyAlgorithmForXPathSelectors("//ul[@role='navigation']//li[not(@class='active')]//a[@href]"), expectedScore, delta);
    }
}