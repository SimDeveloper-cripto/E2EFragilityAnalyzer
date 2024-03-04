package JUnit.AddressBook816;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class AddressBookAddAddressBookTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addressBookAddAddressBook() throws Exception {

		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.linkText("nuovo")).click();

		// CODICE FUNZIONANTE PER 8.0.0 8.1.0 8.1.6
		driver.findElement(By.name("quickadd")).click();

		// CODICE FUNZIONANTE VERSIONE 8.1.7
		/*
		List<WebElement> next_buttons = driver.findElements(By.name("quickadd"));
		next_buttons.get(1).click();
		*/

		driver.findElement(By.name("firstname")).clear();
		driver.findElement(By.name("firstname")).sendKeys("firstname");
		driver.findElement(By.name("lastname")).clear();
		driver.findElement(By.name("lastname")).sendKeys("lastname");
		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("address");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("01056321");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("mail@mail.it");
		new Select(driver.findElement(By.name("bday"))).selectByVisibleText("19");
		new Select(driver.findElement(By.name("bmonth"))).selectByVisibleText("Giugno");
		driver.findElement(By.name("byear")).clear();
		driver.findElement(By.name("byear")).sendKeys("1985");
		driver.findElement(By.name("submit")).click();
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/div")).getText()
				.contains("Information entered into address book"));
		driver.findElement(By.linkText("home page")).click();
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]/label/strong")).getText()
				.contains("Numero di risultati: 1"));
	}

	public void tearDown() throws Exception {
		driver.quit();

	}
}
