import org.junit.Test;
import org.junit.Assert;

public class CascadeCSSTest {
    @Test
    public void cascade1() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.5f;
        float delta = 0.001f;

        // (1, 0, 0)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("#elementoId"), expectedScore, delta);
    }

    @Test
    public void cascade2() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.8f;
        float delta = 0.001f;

        // (1, 1, 0)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("#elementoId .nomeClasse"), expectedScore, delta);
    }

    @Test
    public void cascade3() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.2f;
        float delta = 0.001f;

        // (0, 0, 1)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("div"), expectedScore, delta);
    }

    @Test
    public void cascade4() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.3f;
        float delta = 0.001f;

        // (0, 1, 0)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("[attributo]"), expectedScore, delta);
    }

    @Test
    public void cascade5() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.3f;
        float delta = 0.001f;

        // (0, 1, 0)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("[attributo=\"valore\"]"), expectedScore, delta);
    }

    @Test
    public void cascade6() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.9f;
        float delta = 0.001f;

        // (0, 1, 3)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("div h1 input.button"), expectedScore, delta);
    }

    @Test
    public void cascade7() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.4f;
        float delta = 0.001f;

        // (0, 0, 2)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("div > h1"), expectedScore, delta);
    }

    @Test
    public void cascade8() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 1.0f;
        float delta = 0.001f;

        // (0, 2, 2)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("td.left > td:first-child"), expectedScore, delta);
    }

    @Test
    public void cascade9() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.9f;
        float delta = 0.001f;

        // (1, 0, 2)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("div + form > #elementoID"), expectedScore, delta);
    }

    @Test
    public void cascade10() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 1.2f;
        float delta = 0.001f;

        // (0, 2, 3)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("td.left > form > input.button"), expectedScore, delta);
    }

    @Test
    public void cascade11() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 1.6f;
        float delta = 0.001f;

        // (1, 3, 1)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("#myId .myClass[attr='value']:hover::after"), expectedScore, delta);
    }

    @Test
    public void cascade12() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.9f;
        float delta = 0.001f;

        // (0, 3, 0)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors(".my-buglist-bug:nth-child(1) .status-50-color"), expectedScore, delta);
    }

    @Test
    public void cascade13() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.3f;
        float delta = 0.001f;

        // (0, 1, 0)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors(":is()"), expectedScore, delta);
    }

    @Test
    public void cascade14() {
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 2.4f;
        float delta = 0.001f;

        // (1, 5, 2)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors("#app .user-list div.user.is-admin a.remove:hover"), expectedScore, delta);
    }

    // TODO: FOR FUTURE DEVELOPMENT THIS TEST HAS TO WORK
    @Test
    public void cascade15() { // We expect this test to fail. Encountered regex limit.
        CascadeSpecificityEvaluator evaluator = new CascadeSpecificityEvaluator();
        float expectedScore = 0.6f;
        float delta = 0.001f;

        // (0, 2, 0)
        Assert.assertEquals(evaluator.apply_ABC_For_Css_Selectors(":where(section.where-styling, aside.where-styling, footer.where-styling) a"), expectedScore, delta);
    }
}