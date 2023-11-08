
import org.htmlcleaner.TagNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.CleanerProperties;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.math.RoundingMode;

import java.net.URL;
import java.util.Locale;
import java.text.DecimalFormat;
import java.net.MalformedURLException;
import java.text.DecimalFormatSymbols;

public class Judge {
    static double testScore = 0;
    static float sumOfSelectorScore = 0, numOfTestValutated = 0;

    public static float getElementScore(Selector selector, Page documentPage) {
        // return doMetricV2(selector, documentPage);
        return doMetricV3(selector, documentPage);
    }

    private static float doMetricV3(Selector selector, Page documentPage) {
        Document document = documentPage.getPage();

        float numberOfElements = 1;
        float numElementsWeight = 1;
        float complexitySelectorScore = -1;

        String type = selector.getType();
        String selectorString = selector.getSelector();
        float complexityPageScore = documentPage.getPageComplexity();

        // Punteggio di complessità del selettore
        switch (type) {
            case "url":
                if(isPartialUrl(selector)) complexitySelectorScore = 0;
                else complexitySelectorScore = 4;
                break;
            case "Id":
                complexitySelectorScore = SelectorComplexityEvaluator.evaluateIdSelectorComplexity(selectorString);
                break;
            case "CssSelector":
                complexitySelectorScore = SelectorComplexityEvaluator.evaluateCssSelectorComplexity(selectorString, document);
                break;
            case "XPath":
                complexitySelectorScore = SelectorComplexityEvaluator.evaluateXPathSelectorComplexity(selectorString);

                // Nel caso di una modifica alla pagina, un XPath assoluto potrebbe essere meno efficace di uno relativo.
                // Secondo questo criterio modifico leggermente il punteggio.
                if(!isXPathAbsolute(selector.getSelector())) complexitySelectorScore -= 0.2f;
                break;
            case "TagName":
                complexitySelectorScore = SelectorComplexityEvaluator.evaluateTagNameSelectorComplexity(selectorString);
                break;
            case "LinkText":
                complexitySelectorScore = SelectorComplexityEvaluator.evaluateLinkTextSelectorComplexity(selectorString, document);
                break;
            default:
                complexitySelectorScore = 8;
        }
        selector.setSelectorComplexity(complexitySelectorScore);

        // Il punteggio è inversamente proporzionato alla complessità del selettore/pagina
        float elementScore = Math.abs(complexitySelectorScore - 10);
        float pageScore = Math.abs(complexityPageScore - 10);

        documentPage.setPageScore(pageScore);
        selector.setSelectorScore(elementScore);

        // SELETTORE + PAGINA
        // Se nella pagina il locatore individua più elementi, a meno che non sia di tipo tag, devo ridurre il punteggio finale
        if(!type.equals("url") && !type.equals("LinkText") && !type.equals("id")) numberOfElements = countOccurence(selector, documentPage);
        if(numberOfElements > 1 && !type.equals("tag")) numElementsWeight = (1.8f / numElementsWeight);
        System.out.println("NumberOfElements: " + numberOfElements + " numElementsWeight: " + numElementsWeight);

        // Ponderazione per combinare i punteggi del selettore e di complessità
        float selectorWeight = 0.8f; // Peso del punteggio del selettore (puoi regolare il valore)
        float PageWeight = 0.2f;     // Peso del punteggio di complessità (puoi regolare il valore)

        // Combina i punteggi ponderati del selettore e di complessità per ottenere il punteggio finale
        return ((selectorWeight * elementScore) + (PageWeight * pageScore)) * numElementsWeight; // Leggi il diagramma di flusso (pagina 42/100)
    }

    private static float countOccurence(Selector selector, Page documentPage) {
        if (!selector.getType().equals("XPath")) return documentPage.getPage().select(selector.getSelector()).size();
        else return countElementsByXPath(selector.getSelector(), documentPage.getPage());
    }

    public static int countElementsByXPath(String xPathSelector, Document page) {
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
            return nodes.getLength();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private static float doMetricV2(Selector selector, Document document) {
        float elementScore = -1;
        String type = selector.getType();

        switch (type) {
            case "url":
                if(isPartialUrl(selector)) elementScore = 10;
                else elementScore = 6;
                break;
            case "Id":
                elementScore = 10;
                break;
            case "CssSelector":
                elementScore = 6;
                // if(canBeIdWithCss(selector, document)) elementScore -= 2;
                break;
            case "XPath":
                elementScore = 4;
                // if(canBeIdWithXPath(selector, document)) elementScore -= 3;
                if(!isXPathAbsolute(selector.getSelector())) elementScore -= 1;
                break;
            case "TagName":
                elementScore = 6;
                if(!isTagMultiple(selector, document)) elementScore -= 1;
                break;
            case "LinkText":
                elementScore = 4;
                break;
            default:
                elementScore = 3;
                break;
        }
        return elementScore;
    }

    private static boolean isPartialUrl(Selector selector) {
        String urlString = selector.getSelector();
        try {
            URL url = new URL(urlString);
            String scheme = url.getProtocol();
            String host = url.getHost();

            return (scheme == null || host == null);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static boolean isTagMultiple(Selector selector, Document document) {
        return document.select(selector.getSelector()).size() > 1;
    }

    private static boolean canBeIdWithCss(Selector selector, Document document) {
        Elements elements = document.select(selector.getSelector());
        for (Element element : elements) {
            if(!element.hasAttr("id")) return true;
        }
        return false;
    }
    private static boolean canBeIdWithXPath(Selector selector, Document document) {
        org.w3c.dom.Document doc = null;
        TagNode tagNode = new HtmlCleaner().clean(document.toString());

        try {
            doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        Node result = null;

        try {
            result= (Node) xpath.evaluate(selector.getSelector(),doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        // Verifica se l'attributo "id" è presente
        return result.getAttributes().getNamedItem("id") != null;
    }

    private static boolean canBeCssSelectorWithXPath(Selector selector, Document document) {
        // TODO: To Be Implemented
        return false;
    }

    public static boolean isXPathAbsolute(String xpath) {
        return xpath.startsWith("/");
    }

    public static float getBadElementScore(Selector lastSelector) {
        // Se il selettore è responsabile del fallimento, togliamo 100 punti dal calcolato di getElementScore().
        return lastSelector.getSelectorScore() - 100;
    }

    public static double getTestScoreGeometry(Test test) {
        double prodotto = 1;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) prodotto *= tes.getSelectorFinalScore();
        return Math.pow(prodotto, 1.0 / n);
    }

    public static double getTestScore(Test test) {
        double sumInverseScores = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumInverseScores += 1.0 / tes.getSelectorFinalScore();

        numOfTestValutated += 1;
        double harmonicMean = n / sumInverseScores;             // Media Armonica
        double geometryMean = getTestScoreGeometry(test);       // Media Geometrica
        double arithmeticMean = getTestScoreAritmetich(test);   // Media Aritmetica
        double quadraticMean = getTestScoreQuadraticMean(test); // Media Quadratica

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.00", symbols);

        System.out.println("Test no. " + (int) numOfTestValutated + " scores: \n" +
                "Geometric Calculation: " + df.format(geometryMean) + ", Harmonic Calculation: " + df.format(harmonicMean) +
                ", Arithmetic Calculation: " + df.format(arithmeticMean) + ", Quadratic Calculation: " + df.format(quadraticMean) + "\n");

        return harmonicMean;
    }

    private static double getTestScoreAritmetich(Test test) {
        double sumScores = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumScores += tes.getSelectorFinalScore();
        return sumScores / n;
    }

    public static double getTestScoreQuadraticMean(Test test) {
        double sumSquares = 0;
        int n = test.getSelectors().size();

        for (Selector tes : test.getSelectors()) sumSquares += Math.pow(tes.getSelectorFinalScore(), 2);
        return Math.sqrt(sumSquares / n);
    }

    private static float wasteAverage(Test test, float arithmeticAverage) {
        float testWaste;
        float sumOfSelectorWaste = 0;
        int numberOfSelector = test.getSelectors().size();

        for (Selector selector : test.getSelectors()) {
            sumOfSelectorWaste += Math.abs(selector.getSelectorScore() - arithmeticAverage);
        }

        testWaste = (sumOfSelectorWaste / numberOfSelector);
        return testWaste;
    }

    private static double arithmeticAverage(Test test) {
        testScore = 0;
        sumOfSelectorScore = 0;
        int numberOfSelector = test.getSelectors().size();

        for (Selector selector:test.getSelectors()) {
            sumOfSelectorScore += selector.getSelectorScore();
        }

        testScore = (sumOfSelectorScore / numberOfSelector);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        String textScore= df.format(testScore);
        testScore = Double.parseDouble(textScore);
        return testScore;
    }
}