
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.List;

public class SelectorWebDriver implements WebDriverListener {
	private List<Selector> visitedSelectors;
	private List<Page> visitedPages;
	private final Judge judge;

	public SelectorWebDriver(Judge judge) {
		this.judge = judge;
	}

	/* [DESCRIPTION]
		- This method is called when using a selector, and not when performing a GET request. Therefore, in the Selector object, there will be the locator used in the command within the page.
	* */
	@Override
	public void beforeFindElement(WebDriver driver, By locator) {
		String pageSource    = driver.getPageSource();
		Document pageContent = Jsoup.parse(pageSource);

		Selector selector = new Selector(locator);
		Page page        = new Page(pageContent);

		// Scores calculation
		float selectorComplexityScore        = judge.applyMetricToSelector(selector, pageContent);
		float pageComplexityScore            = judge.applyMetricToPage(page);
		float pageAndSelectorComplexityScore = judge.applyMetricToPageAndSelector(selector, page, driver);

		selector.setSelectorScore(selectorComplexityScore);
		page.setPageComplexity(pageComplexityScore);
		selector.setSelectorCombinedScoreWithPageScore(pageAndSelectorComplexityScore);

		/* Weighted Average Calculation */
		float result = (selectorComplexityScore * DefaultSelectorComplexityEvaluator.getSelectorScoreWeight())
				+ (pageComplexityScore * DefaultPageComplexityEvaluator.getPageScoreWeight())
				+ (pageAndSelectorComplexityScore * DefaultPageAndSelectorComplexityEvaluator.getPageAndSelectorScoreWeight()); // [0-1]

		selector.setWeightedAverageResultScore(result);

		System.out.println("(Analyzed) " + selector + "  " + page + "  " + "PageAndSelectorComplexityScore = " + pageAndSelectorComplexityScore + "\n");

		visitedSelectors.add(selector);
		visitedPages.add(page);
	}

	@Override
	public void beforeFindElements(WebDriver driver, By locator) {
		this.beforeFindElement(driver,locator);
		WebDriverListener.super.beforeFindElements(driver, locator);
	}

	@Override
	public void afterFindElement(WebDriver driver, By locator, WebElement result) {
		WebDriverListener.super.afterFindElement(driver, locator, result);
	}

	public void setVisitedSelectors(List<Selector> selectors) {
		this.visitedSelectors = selectors;
	}
	public List<Selector> getVisitedSelectors() {
		return visitedSelectors;
	}

	public List<Page> getVisitedPages() {
		return visitedPages;
	}
	public void setVisitedPages(List<Page> pages) {
		this.visitedPages = pages;
	}
}