
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.QueryParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultPageAndSelectorEvaluator implements IPageAndSelectorScoreStrategy {
    @Override
    public float evaluatePageAndSelectorComplexity(Selector selector, Page page) {
        String selectorType   = selector.getType();
        String selectorString = selector.getSelector();

        Document document = page.getPage(); // That is fine

        int nElemFirstMatch = getNumberOfMatches(document, selectorString);
        int nElemSecondMatch = 0;

        String newSelectorString;
        switch (selectorType) {
            case "CssSelector":
                newSelectorString = removeFirstLevelCssSelector(selectorString);
                nElemSecondMatch  = getNumberOfMatches(document, newSelectorString);
                break;
            case "XPath":
                newSelectorString = removeFirstLevelXPath(selectorString);
                nElemSecondMatch  = getNumberOfMatches(document, newSelectorString);
                break;
            default:
                // TODO: I don't know what to do in this case
        }

        float ratio;
        if (nElemSecondMatch == 0) { // Fragile selector, need to penalize
            ratio = 0.2f; // Could write 0.1f. So that (1 - ratio) is as close as possible to 1
        } else {
            ratio = (float) (nElemFirstMatch / nElemSecondMatch); // Could get a division by zero exception
        }

        return (1 - ratio);
    }

    private String removeFirstLevelCssSelector(String selectorString) {
        Pattern pattern = Pattern.compile("^\\s*([^>]+>)\\s*(.*)$");
        Matcher matcher = pattern.matcher(selectorString);

        if (matcher.matches()) {
            return matcher.group(2).trim();
        } else {
            return selectorString;
        }
    }

    private String removeFirstLevelXPath(String selectorString) {
        if (selectorString.startsWith("//")) {
            int posFirstBar = selectorString.indexOf("/", 2); // The XPath string starts with "//"

            if (posFirstBar != -1)
                return selectorString.substring(posFirstBar);
        }
        return selectorString;
    }

    private static int getNumberOfMatches(Document document, String selectorString) {
        int nMatch = 0;

        try {
            String selector = QueryParser.parse(selectorString).toString();
            for (Element e : document.select(selector)) {
                nMatch += 1;
            }
        } catch (org.jsoup.select.Selector.SelectorParseException e) {
            System.err.println("Errore nel selettore: " + selectorString);
            System.err.println("SelectorParseException: " + e.getMessage());
        }

        return nMatch;
    }
}