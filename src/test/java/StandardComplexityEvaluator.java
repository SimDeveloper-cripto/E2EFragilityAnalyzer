
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* [INTRODUCTION]
    * All the scores mentioned have a range from 0 to 10, so a low complexity score means that that selector/page is not very complex (robust), vice versa if the score
    * is high the selector/page is very complex, same thing for the fragility calculation.
* */
public class StandardComplexityEvaluator implements ScoreStrategy {
        /* VARIABLES AND CONSTANTS DECLARATION */
        private final float selectorWeight = 0.8f; // Selector score weight (you can adjust the value)
        private final float pageWeight = 0.2f; // Page score weight (you can adjust the value)
        static float CLASS_COEFF = 1.4f, ATTR_COEFF = 1.4f, ID_COEFF = 0.3f, CHILD_COEFF = 1.4f, NODE_COEFF = 0.7f, FUNC_COEFF = 1.2f;

        public StandardComplexityEvaluator() {}

        /*********************** SELECTOR SECTION ***********************/
        @Override
        public float getIdComplexityScore(String selectorString) { // For convention, selectorString stays as parameter
            return 0.0f;
        }

        @Override
        public float getUrlComplexityScore(String selectorString) {
            if(isPartialUrl(selectorString)) return 0.0f;
            else return 4.0f;
        }

        @Override
        public float getTagNameComplexityScore(String selectorString) {
            return 5.0f; // TODO: This function must be modified
        }

        /* [DESCRIPTION]
            - Returns normalized value of the score in the interval [0-10] relative to selector type: LinkText
        */
        @Override
        public float getLinkTextComplexityScore(String selectorString, Document document) {
            int len        = selectorString.length();
            int depth      = evaluatePagePosition(selectorString, document);
            int n_add_attr = evaluateNumberOfAdditionalAttributes(selectorString, document);
            int n_el       = evaluateNumberOfElementsWithSameText(selectorString, document);

            int len_score         = len < 30 ? 2 : 4;
            int n_add_attr_score  = n_add_attr == 0 ? 0 : 3; // If there are no additional attributes (robust) score 0, otherwise 3
            int n_el_score        = n_el == 0 ? 1 : 7;       // If there are no elements with the same (robust) text, score 0, otherwise 7

            // Complexity
            int complexity = len_score + depth + n_add_attr_score + n_el_score;

            // Normalization of score in [0-10]
            return normalizeScore((float) complexity, 100.0f);
        }

        /* [DESCRIPTION]
            - Returns normalized value of the score in the interval [0-10] relative to selector type: cssSelector
        */
        @Override
        public float getCssSelectorComplexityScore(String cssSelector, Document document) {
            return countCssSelectorComplexity(cssSelector, document);
        }

        /* [DESCRIPTION]
            - Returns normalized value of the score in the interval [0-10] relative to selector type: XPath
        */
        @Override
        public float getXPathComplexityScore(String xpathSelector) {
            return countXPathSelectorComplexity(xpathSelector);
        }

        /* HELPER METHODS */
        private static float countCssSelectorComplexity(String cssSelector, Document document) {
            Elements elements = document.select(cssSelector);

            int classCount     = countSelectorSpecificity(cssSelector, "\\.");
            int attributeCount = countSelectorSpecificity(cssSelector, "\\[");
            int idCount        = countSelectorSpecificity(cssSelector, "\\#");
            int childCount     = countSelectorSpecificity(cssSelector, "child\\(\\d+| >");
            int functionCount  = countSelectorSpecificity(cssSelector, ":|::");
            int nodeCount      = elements.size();

            System.out.println("CSS Selector Complexity: attributeCount: " + attributeCount + " node count: " + nodeCount + " classCount: " + classCount +
                    " idCount: " + idCount + " childCount: " + childCount + " functionCount: " + functionCount);

            float selectorScore = (classCount * CLASS_COEFF) + (attributeCount * ATTR_COEFF) + (idCount * ID_COEFF) + (childCount * CHILD_COEFF)
                    + (nodeCount * NODE_COEFF) + (functionCount * FUNC_COEFF);

            return normalizeScore(selectorScore, 100.0f);
        }

        private static float countXPathSelectorComplexity(String xpathSelector) {
            int classCount     = countSelectorSpecificity(xpathSelector, "\\[@class");
            int attributeCount = countSelectorSpecificity(xpathSelector, "\\[@");
            int idCount        = countSelectorSpecificity(xpathSelector, "\\[@id");
            int childCount     = countSelectorSpecificity(xpathSelector, "\\[\\d+");
            int nodeCount      = countSelectorSpecificity(xpathSelector, "\\/");
            int functionCount  = countSelectorSpecificity(xpathSelector, "\\(\\)");

            System.out.println("XPATH: attributeCount: " + attributeCount + " node count: " + nodeCount + " classCount: " + classCount +
                    " idCount: " + idCount + " childCount: " + childCount + " functionCount: " + functionCount + "\n");

            float selectorScore = (classCount * CLASS_COEFF) + (attributeCount * ATTR_COEFF) + (idCount * ID_COEFF) + (childCount * CHILD_COEFF)
                    + (nodeCount * NODE_COEFF) + (functionCount * FUNC_COEFF);

            // In the case of a page change, an absolute XPath may be less effective than a relative one.
            // According to this criterion I slightly modify the score.
            if(!isXPathAbsolute(xpathSelector)) selectorScore -= 0.2f;

            return normalizeScore(selectorScore, 100.0f);
        }

        private static int countSelectorSpecificity(String input, String regex) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            return (int) matcher.results().count();
        }

        /* [DESCRIPTION]
            - Returns depth value of the link in the specified document
         */
        private static int evaluatePagePosition(String selectorString, Document document) {
            Elements elements = document.select(selectorString);

            if (elements.isEmpty()) return 0; // TODO: what value do we assign in this case?

            int depth = 0;
            Element curr = elements.first();
            try {
                if (curr != null) {
                    while (curr.parent() != null) {
                        curr = curr.parent();
                        depth++;
                    }
                }
            } catch (NullPointerException e) {
                return depth;
            }

            return depth;
        }

        private static int evaluateNumberOfAdditionalAttributes(String selectorString, Document document) {
            Elements elements = document.select(selectorString);

            if (elements.isEmpty()) {
                return 0; // TODO: what value do we assign in this case?
            } else {
                int count = 0;

                if (elements.size() > 1) {
                    for (Element elem : elements) count += elem.attributes().size();

                    return count;
                }

                return Objects.requireNonNull(elements.first()).attributes().size();
            }
        }

        /* [DESCRIPTION]
           - Returns the value relating to the number of additional attributes of the LinkText "selectorString".
           - In case "selectorString" is not unique, we add the number of attributes per selector.
       */
        private static int evaluateNumberOfElementsWithSameText(String selectorString, Document document) {
            Elements elements = document.select("*");
            int count = 0;

            for (Element elem : elements) {
                if (!elem.text().isEmpty() && elem.text().equals(selectorString)) {
                    count++;
                }
            }

            return count;
        }

        /* [DESCRIPTION]
            - Returns true if selectorString is a PartialUrl.
        */
        private static boolean isPartialUrl(String urlString) {
            try {
                URL url       = new URL(urlString);
                String scheme = url.getProtocol();
                String host   = url.getHost();

                return (scheme == null || host == null);
            } catch (MalformedURLException e) {
                return false;
            }
        }

        /* [DESCRIPTION]
            - Returns true if xPath is absolute.
        */
        private static boolean isXPathAbsolute(String xpath) {
            return xpath.startsWith("/");
        }

        @Override
        public final float getSelectorScoreWeight() {
            return this.selectorWeight;
        }

        /*********************** PAGE SECTION ***********************/

        @Override
        public final float getPageScoreWeight() {
            return this.pageWeight;
        }

        /* [DESCRIPTION]
            - Returns as float value of the score of the page based on Its complexity
        * */
        @Override
        public float getPageComplexityScore(Page documentPage) {
            return calculatePageComplexity(documentPage);
        }

        private static float calculatePageComplexity(Page page) {
            Document document  = page.getPage();

            int totalElements  = document.getAllElements().size();
            int uniqueElements = countUniqueElements(document.getAllElements());
            int imageCount     = countImages(document);
            int linkCount      = countLinks(document);

            float complexityScore = (float) (totalElements + uniqueElements + imageCount + linkCount);
            return normalizeScore(complexityScore, 3000.0f);
        }

        private static int countUniqueElements(Elements elements) {
            HashSet<String> hashSet = new HashSet<>();

            for (Element element : elements) {
                hashSet.add(element.tagName());
            }
            return hashSet.size();
        }

        private static int countImages(Document document) {
            return document.select("img").size();
        }

        private static int countLinks(Document document) {
            return document.select("a").size();
        }

        /*********************** STANDARD COMPLEXITY EVALUATOR MAIN FEATURE ***********************/

        /* [DESCRIPTION]
            - Returns normalized value of the selector score in the interval [0-10] given a maxScore
        */
        private static float normalizeScore(float selectorScore, float maxScore) {
            float minSelectorScore = 0.0f, maxSelectorScore = maxScore;
            float normalizedMin = 1.0f, normalizedMax = 10.0f;

            float diff = normalizedMax - normalizedMin; // 9.0f
            float normalizedScore = normalizedMin + (selectorScore - minSelectorScore) * (diff) / (maxSelectorScore - minSelectorScore);

            if(normalizedScore < 0.0f) normalizedScore = normalizedMin;
            if(normalizedScore > 10.0f) normalizedScore = normalizedMax;
            return normalizedScore;
        }
}