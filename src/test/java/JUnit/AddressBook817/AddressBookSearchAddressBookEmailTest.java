package JUnit.AddressBook817;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AddressBookSearchAddressBookEmailTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addressBookSearchAddressBookEmail() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.name("searchstring")).clear();
		driver.findElement(By.name("searchstring")).sendKeys("mail@mail.it");
		assertEquals("Numero di risultati: 1",
				driver.findElement(By.xpath("html/body/div[1]/div[4]/label/strong")).getText());

		// CODICE PER VERSIONE 8.0.0 e 8.1.0 non funziona sulla versione 8.1.6
		/*
		assertEquals("lastname", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[2]")).getText());
		assertEquals("firstname", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[3]")).getText());
		assertEquals("mail@mail.it", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[4]")).getText());
		assertEquals("01056321", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[5]")).getText());
		*/

		// CODICE PER VERSIONE 8.1.6

		assertEquals("lastname", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[3]")).getText());
		assertEquals("firstname", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[4]")).getText());
		assertEquals("mail@mail.it", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[5]")).getText());
		assertEquals("01056321", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[6]")).getText());


	}


	public void tearDown() throws Exception {
		driver.quit();
	}

}
