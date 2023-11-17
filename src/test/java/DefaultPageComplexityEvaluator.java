
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/* [DESCRIPTION]
    - The final score value is set to the interval [0-1].
    - This means that the more the score tends to 1 the more fragile that selector is (and so is the Test).
**/
public class DefaultPageComplexityEvaluator implements IPageScoreStrategy {
    private static int numberOfElements;

    @Override
    public float evaluatePageComplexity(Page page) {
        Document document = page.getPage();
        setNumberOfElements(document.select("*").size());

        float firstRatio  = evaluateLinkRatio(document);            // [0-1]
        float secondRatio = evaluateDomRatio(document);             // [0-1]
        float thirdRatio  = evaluateBranchingFactorRatio(document); // [0-1]

        return ((firstRatio + secondRatio + thirdRatio) / 3); // [0-1]
    }

    public static float getPageScoreWeight() {
        return 0.2f;
    }

    private static void setNumberOfElements(int numberOfElements) {
        DefaultPageComplexityEvaluator.numberOfElements = numberOfElements;
    }

    private static int getNumberOfElements() {
        return numberOfElements;
    }

    /* METHODS */

    private static float evaluateLinkRatio(Document document) {
        Elements links = document.select("a");
        float nLinks = links.size();
        return (nLinks / getNumberOfElements()); // [0-1]
    }

    private static float evaluateDomRatio(Document document) {
        float depth  = (float) evaluatePageDepth(document.child(0)); // root element
        return (depth / getNumberOfElements()); // [0-1]
    }

    private static float evaluateBranchingFactorRatio(Document document) {
        int sumBranchingFactor = 0;
        for (Element elem : document.select("*")) {
            sumBranchingFactor += evaluateBranchingFactorForElement(elem);
        }
        return ((float) sumBranchingFactor / getNumberOfElements()); // [0-1]
    }

    /* HELPER FUNCTIONS */

    private static int evaluatePageDepth(Element element) {
        if (element.children().isEmpty()) {
            return 1;
        } else {
            int maxChildrenDepth = 0;
            for (Element child : element.children()) {
                int childDepth = evaluatePageDepth(child);
                maxChildrenDepth = Math.max(maxChildrenDepth, childDepth);
            }

            return 1 + maxChildrenDepth;
        }
    }

    private static int evaluateBranchingFactorForElement(Element element) {
        return element.children().size();
    }
}