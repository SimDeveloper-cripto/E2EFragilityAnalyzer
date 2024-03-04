package JUnit.AddressBook816;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class AddressBookPrintMultipleAddressBookTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addressBookPrintMultipleAddressBook() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.linkText("stampa tutto")).click();
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*firstname1[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*lastname1[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*address1[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*01056321[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*mail1@mail\\.it[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*11[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*Giugno[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[1]")).getText()
				.matches("^[\\s\\S]*1981[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*firstname2[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*lastname2[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*address2[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*01056322[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*mail2@mail\\.it[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*12[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*Giugno[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[2]")).getText()
				.matches("^[\\s\\S]*1982[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*firstname3[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*lastname3[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*address3[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*01056323[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*mail3@mail\\.it[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*13[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*Giugno[\\s\\S]*$"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='view']/tbody/tr/td[3]")).getText()
				.matches("^[\\s\\S]*1983[\\s\\S]*$"));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
