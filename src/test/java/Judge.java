
import org.htmlcleaner.TagNode;
import org.jsoup.nodes.Document;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.CleanerProperties;

import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Judge {
    private static ScoreStrategy strategy = null;
    private static float numOfTestJudged = 0;

    public Judge(ScoreStrategy strategy) {
        Judge.strategy = strategy;
    }

    public ScoreStrategy getStrategy() { return this.strategy; }

    public float getElementScore(Selector selector, Page documentPage) {
        return applyMetric(selector, documentPage);
    }

    private static float applyMetric(Selector selector, Page documentPage) {
        Document document = documentPage.getPage();

        float numberOfElements  = 1, numElementsWeight = 1, complexitySelectorScore;

        String type               = selector.getType();
        String selectorString     = selector.getSelector();
        float complexityPageScore = documentPage.getPageComplexity();

        // Selector complexity score
        switch (type) {
            case "url":
                complexitySelectorScore = strategy.getUrlComplexityScore(selector.getSelector());
                break;
            case "Id":
                complexitySelectorScore = strategy.getIdComplexityScore(selectorString);
                break;
            case "CssSelector":
                complexitySelectorScore = strategy.getCssSelectorComplexityScore(selectorString, document);
                break;
            case "XPath":
                complexitySelectorScore = strategy.getXPathComplexityScore(selectorString);
                break;
            case "TagName":
                complexitySelectorScore = strategy.getTagNameComplexityScore(selectorString);
                break;
            case "LinkText":
                complexitySelectorScore = strategy.getLinkTextComplexityScore(selectorString, document);
                break;
            default:
                complexitySelectorScore = 8.0f;
        }
        selector.setSelectorComplexity(complexitySelectorScore);

        // The score is inversely proportional to the complexity of the selector/page
        float elementScore = Math.abs(complexitySelectorScore - 10);
        float pageScore    = Math.abs(complexityPageScore - 10);

        documentPage.setPageScore(pageScore);
        selector.setSelectorScore(elementScore);

        /* SELECTOR + PAGE */

        // Judge adds one more criteria: if the locator identifies multiple elements on the page, unless it is of the tag type, I have to reduce the final score
        if(!type.equals("url") && !type.equals("LinkText") && !type.equals("id")) numberOfElements = countOccurences(selector, documentPage);
        if(numberOfElements > 1 && !type.equals("tag")) numElementsWeight = (1.8f / numElementsWeight);
        System.out.println("NumberOfElements: " + numberOfElements + " numElementsWeight: " + numElementsWeight);

        // Weighting to combine selector and complexity scores
        final float selectorWeight = strategy.getSelectorScoreWeight();
        final float PageWeight     = strategy.getPageScoreWeight();

        // Judge combines the weighted selector and complexity scores to get the final score
        return ((selectorWeight * elementScore) + (PageWeight * pageScore)) * numElementsWeight; /* Read the flowchart (page 42/100) */
    }

    private static float countOccurences(Selector selector, Page documentPage) {
        if (!selector.getType().equals("XPath")) return documentPage.getPage().select(selector.getSelector()).size();
        else return countElementsByXPath(selector.getSelector(), documentPage.getPage());
    }

    private static float countElementsByXPath(String xPathSelector, Document page) {
        org.w3c.dom.Document doc = null;
        TagNode tagNode = new HtmlCleaner().clean(page.toString());

        try {
            doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            // Create the XPath
            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();

            XPathExpression expr = xpath.compile(xPathSelector);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            return (float) nodes.getLength();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return 1.0f;
    }

    public static float getBadElementScore(Selector lastSelector) {
        // If the selector caused a test failure, we subtract 100 points form the return value of getElementScore().
        return lastSelector.getSelectorScore() - 100;
    }

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