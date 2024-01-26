
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener;

public class SelectorWebDriver implements WebDriverListener {
	private List<Selector> visitedSelectors;
	private List<Page> visitedPages;
	private final Judge judge;

	public SelectorWebDriver(Judge judge) {
		this.judge = judge;
	}

	/* [DESCRIPTION]
		- This method is called when using a selector, and not when performing a GET request. Therefore, in the Selector object, there will be the locator used in the command within the page.
	**/
	@Override
	public void beforeFindElement(WebDriver driver, By locator) {
		String pageSource    = driver.getPageSource();
		Document pageContent = Jsoup.parse(pageSource); // Page Document

		Selector selector = new Selector(locator);
		Page page         = new Page(pageContent);

		System.out.println("[Selector WebDriver] (Starting to analyze) Start URL: " + driver.getCurrentUrl());
		System.out.println("[Selector WebDriver] (Starting to analyze) Selector: " + selector.getSelector());

		// Scores calculation
		float selectorComplexityScore        = judge.applyMetricToSelector(selector, pageContent);
		float pageComplexityScore            = judge.applyMetricToPage(page);
		float pageAndSelectorComplexityScore = judge.applyMetricToPageAndSelector(selector, page, driver);

		System.out.println("+ ---------- Metric Related Values ---------- +");
		System.out.println("	Selector Complexity Score: [" + selectorComplexityScore + "]");
		System.out.println("	Page Complexity Score: [" + pageComplexityScore + "]");
		System.out.println("	Page And Selector Complexity Score: [" + pageAndSelectorComplexityScore + "]");

		selector.setSelectorScore(selectorComplexityScore);
		page.setPageComplexity(pageComplexityScore);
		selector.setSelectorCombinedScoreWithPageScore(pageAndSelectorComplexityScore);

		/* Weighted Average Calculation */
		float result = (selectorComplexityScore * DefaultSelectorComplexityEvaluator.getSelectorScoreWeight())
				+ (pageComplexityScore * DefaultPageComplexityEvaluator.getPageScoreWeight())
				+ (pageAndSelectorComplexityScore * DefaultPageAndSelectorComplexityEvaluator.getPageAndSelectorScoreWeight()); // [0-1]

		System.out.println("	Selector Weighted Average Result Score: [" + result + "]");
		System.out.println("+ ------------------------------------------- +");

		selector.setWeightedAverageResultScore(result);

		System.out.println("[Selector WebDriver] (Terminated to analyze) End URL: " + driver.getCurrentUrl());
		System.out.println("[Selector WebDriver] (Terminated to analyze) Selector: " + selector.getSelector() + "\n");

		/* [OLD DEBUG PRINTING] */
		// System.out.println("(Terminated to analyze) Data: " + selector + "  " + page + "  " + "PageAndSelectorComplexityScore = " + pageAndSelectorComplexityScore + "\n");

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