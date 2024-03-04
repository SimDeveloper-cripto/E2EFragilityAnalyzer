package JUnit.AddressBook810;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertTrue;

public class AddressBookEditAddressBookTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addressBookEditAddressBook() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.cssSelector("img[alt=\"Modifica\"]")).click();
		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("newaddress");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("333333");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("newmail@mail.it");
		driver.findElement(By.xpath(".//*[@id='content']/form[1]/input[19]")).click();

		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/div")).getText()
				.contains("Rubrica"));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
