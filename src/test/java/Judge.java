
import org.jsoup.nodes.Document;

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

    /* [OLD] THIS WAS THE OLD WAY TO PENALIZE A TEST FAILURE BECAUSE OF A SELECTOR
        public static float getBadElementScore(Selector lastSelector) {
            // If the selector caused a test failure, we subtract 100 points form the return value of getElementScore().
            return lastSelector.getSelectorScore() - 100;
        }
    */

    public static double getTestScore(Test test) {
        double sumInverseScores = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumInverseScores += 1.0 / tes.getSelectorFinalScore();

        numOfTestJudged += 1;
        double harmonicMean   = n / sumInverseScores;             // Media Armonica
        double geometryMean   = getTestScoreGeometryMean(test);   // Media Geometrica
        double arithmeticMean = getTestScoreArithmeticMean(test); // Media Aritmetica
        double quadraticMean  = getTestScoreQuadraticMean(test);  // Media Quadratica

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.00", symbols);

        System.out.println("Test no. " + (int) numOfTestJudged + " scores: \n" +
                "Geometric Calculation: " + df.format(geometryMean) + ", Harmonic Calculation: " + df.format(harmonicMean) +
                ", Arithmetic Calculation: " + df.format(arithmeticMean) + ", Quadratic Calculation: " + df.format(quadraticMean) + "\n");

        return harmonicMean;
    }

    private static double getTestScoreArithmeticMean(Test test) {
        double sumScores = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumScores += tes.getSelectorFinalScore();
        return sumScores / n;
    }

    private static double getTestScoreGeometryMean(Test test) {
        double prodotto = 1;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) prodotto *= tes.getSelectorFinalScore();
        return Math.pow(prodotto, 1.0 / n);
    }

    private static double getTestScoreQuadraticMean(Test test) {
        double sumSquares = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumSquares += Math.pow(tes.getSelectorFinalScore(), 2);
        return Math.sqrt(sumSquares / n);
    }
}