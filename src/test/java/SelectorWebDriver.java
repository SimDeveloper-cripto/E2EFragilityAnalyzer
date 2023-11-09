
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
		- When this method is called, the driver is about to visit a new page.
		- The source of the page is obtained and then, a new Selector object is created with the locator used and an identifying string "url".
		- Also, a new Page object (current visited page) is created using the parsed HTML document.
		- After that, page's complexity and the selector score are calculated.
	* */
	@Override
	public void beforeGet(WebDriver driver, String url) {
		String pageSource     = driver.getPageSource();
		Document pageContent  = Jsoup.parse(pageSource);

		Selector selector = new Selector(url,"url");
		Page page         = new Page(pageContent);

		// Scores calculation
		page.setPageComplexity(PageComplexityEvaluator.calculatePageComplexity(page));
		selector.setSelectorFinalScore(judge.getElementScore(selector, page));
		System.out.println(selector + "  " + page);

		visitedSelectors.add(selector);
		visitedPages.add(page);
		WebDriverListener.super.beforeGet(driver, url);
	}

	/* [DESCRIPTION]
		- This method is called when using a selector, and not when performing a GET request. Therefore, in the Selector object, there will be the locator used in the command within the page.
	* */
	@Override
	public void beforeFindElement(WebDriver driver, By locator) {
		String pageSource    = driver.getPageSource();
		Document pageContent = Jsoup.parse(pageSource);

		Selector selector = new Selector(locator);
		Page page         = new Page(pageContent);

		// Scores calculation
		page.setPageComplexity(PageComplexityEvaluator.calculatePageComplexity(page));
		selector.setSelectorFinalScore(judge.getElementScore(selector, page));
		System.out.println("(Analyzed selector) " + selector + "  " + page);

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