
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Judge {
    private static ISelectorScoreStrategy selectorScoreStrategy = null;
    private static IPageScoreStrategy pageScoreStrategy = null;
    private static IPageAndSelectorScoreStrategy pageAndSelectorScoreStrategy = null;
    private static float numOfTestJudged = 0;

    public Judge(ISelectorScoreStrategy selectorScoreStrategy, IPageScoreStrategy pageScoreStrategy, IPageAndSelectorScoreStrategy pageAndSelectorScoreStrategy) {
        Judge.selectorScoreStrategy        = selectorScoreStrategy;
        Judge.pageScoreStrategy            = pageScoreStrategy;
        Judge.pageAndSelectorScoreStrategy = pageAndSelectorScoreStrategy;
    }

    public ISelectorScoreStrategy getSelectorScoreStrategy() { return selectorScoreStrategy; }

    public IPageScoreStrategy getPageScoreStrategy() { return pageScoreStrategy; }

    public IPageAndSelectorScoreStrategy getPageAndSelectorScoreStrategy() { return pageAndSelectorScoreStrategy; }

    public float applyMetricToSelector(Selector selector, Document document) {
        return getSelectorScoreStrategy().evaluateSelectorComplexity(selector, document);
    }

    public float applyMetricToPage(Page documentPage) {
        return getPageScoreStrategy().evaluatePageComplexity(documentPage);
    }

    public float applyMetricToPageAndSelector(Selector selector, Page page, WebDriver driver) {
        return getPageAndSelectorScoreStrategy().evaluatePageAndSelectorComplexity(selector, page, driver);
    }

    public double getTestScore(Test test) {
        double sumInverseScores = 0;
        int n = test.getSelectors().size();

        for (Selector s : test.getSelectors()) {
            sumInverseScores += 1.0 / s.getSelectorWeightedAverageResultScore();
        }

        numOfTestJudged += 1;
        System.out.println("sumInverseScores: " + sumInverseScores);
        double harmonicMean   = n / sumInverseScores;             // Harmonic average

        double geometryMean   = getTestScoreGeometryMean(test);   // Geometric average
        double arithmeticMean = getTestScoreArithmeticMean(test); // Arithmetic average
        double quadraticMean  = getTestScoreQuadraticMean(test);  // Quadratic average

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.00", symbols);

        System.out.println("Test no. " + (int) numOfTestJudged + " Scores: \n" +
                "Geometric Calculation: " + df.format(geometryMean) + ", Harmonic Calculation: " + df.format(harmonicMean) +
                ", Arithmetic Calculation: " + df.format(arithmeticMean) + ", Quadratic Calculation: " + df.format(quadraticMean) + "\n");

        return harmonicMean;
    }

    private static double getTestScoreArithmeticMean(Test test) {
        double sumScores = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumScores += tes.getSelectorWeightedAverageResultScore();
        return sumScores / n;
    }

    private static double getTestScoreGeometryMean(Test test) {
        double prodotto = 1;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) prodotto *= tes.getSelectorWeightedAverageResultScore();
        return Math.pow(prodotto, 1.0 / n);
    }

    private static double getTestScoreQuadraticMean(Test test) {
        double sumSquares = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumSquares += Math.pow(tes.getSelectorWeightedAverageResultScore(), 2);
        return Math.sqrt(sumSquares / n);
    }
}