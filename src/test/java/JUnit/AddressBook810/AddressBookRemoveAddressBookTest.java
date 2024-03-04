package JUnit.AddressBook810;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AddressBookRemoveAddressBookTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addressBookRemoveAddressBook() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.xpath("html/body/div[1]/div[4]/form[2]/table/tbody/tr[2]/td[1]/input")).click();
		driver.findElement(By.xpath("html/body/div[1]/div[4]/form[2]/div[2]/input")).click();
		driver.switchTo().alert().accept();
		driver.findElement(By.linkText("homepage")).click();
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]/label/strong")).getText()
				.contains("Numero di risultati: 0"));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
