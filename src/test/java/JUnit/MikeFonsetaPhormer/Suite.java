
package JUnit.MikeFonsetaPhormer;

import org.junit.jupiter.api.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.time.Duration;
import java.util.ArrayList;

import static JUnit.MikeFonsetaPhormer.PhormerDictionary.Url.*;
import static JUnit.MikeFonsetaPhormer.PhormerDictionary.Value.*;
import static JUnit.MikeFonsetaPhormer.PhormerDictionary.Locator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Suite {

    private static WebDriver driver;
    @BeforeAll
    public static void init(){
        System.setProperty("webdriver.chrome.driver","/home/mike/Tirocinio/Phormer/SeleniumJava/chromedriver");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(MAIN);

        try{
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(CONFIGURATION_PASSWORD_FIELD_))).sendKeys(PASSWORD);
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(CONFIGURATION_CONFIRM_PASSWORD_FIELD))).sendKeys(PASSWORD);
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className(CONFIGURATION_SUBMIT))).click();
            System.out.println("Configurazione effettuata");
        }
        catch(NoSuchElementException | TimeoutException e)
        {
            System.out.println("Configurazione già effettuata");
        }
    }
    @Test
    @Order(1)
    public void Login() {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(LOGIN_PASSWORD))).sendKeys(PASSWORD);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className(LOGIN_SUBMIT))).click();

        WebElement title = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className(LOGIN_TITLE)));
        String titleTxt = title.findElement(By.tagName("a")).getText();

        assertEquals("The Administration Region",titleTxt);
    }

    //Category
    @Test
    @Order(2)
    public void CreateCategory() {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("categories"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(CATEGORY_NAME_FIELD))).sendKeys(CATEGORY_NAME);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(CATEGORY_DESCRIPTION_FIELD))).sendKeys(CATEGORY_DESCRIPTION);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(CATEGORY_VISIBILITY))).get(1).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(CATEGORY_PRIVACY))).get(1).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(CATEGORY_PASSWORD_FIELD))).sendKeys(PASSWORD);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className(CATEGORY_SAVE_BUTTON))).click();

        assertEquals("Category \""+CATEGORY_NAME+"\" added succesfully!",driver.findElement(By.className(CATEGORY_SUCCESS_NOTE)).getText());
    }

    @Test
    @Order(3)
    public void ModifyCategory()  {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("categories"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        boolean first = true;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("edit"))
            {
                if(first)
                {
                    first = false;
                }
                else
                {
                    items.get(i).click();
                    i = -2;
                }
            }
            i++;
        }

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(CATEGORY_NAME_FIELD))).sendKeys(" edit");
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(CATEGORY_DESCRIPTION_FIELD))).sendKeys(" edit");
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(CATEGORY_VISIBILITY))).get(0).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(CATEGORY_PRIVACY))).get(0).click();

        WebElement selectElem = driver.findElement(By.className(CATEGORY_CHILD_SELECT));
        Select select = new Select(selectElem);
        select.selectByValue(CATEGORY_CHILD);

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className(CATEGORY_SAVE_BUTTON))).click();
        assertEquals("Category \""+CATEGORY_NAME+" edit\" edited succesfully!",driver.findElement(By.className(CATEGORY_SUCCESS_NOTE)).getText());
    }

    @Test
    @Order(4)
    public void DeleteCategory()  {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("categories"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        int cid = 0;
        boolean first = true;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("delete"))
            {
                if(first)
                {
                    first = false;
                }
                else
                {
                    String href = items.get(i).getAttribute("href");
                    cid = Integer.parseInt(href.substring(href.indexOf("cid=")+4));
                    items.get(i).click();
                    driver.switchTo().alert().accept();
                    driver.switchTo().parentFrame();
                    i = -2;
                }
            }
            i++;
        }


        assertEquals("Category \""+CATEGORY_NAME+" edit\" (CategoryID: "+cid+") deleted successfully!",driver.findElement(By.className(CATEGORY_SUCCESS_NOTE)).getText());
    }



    //Story
    @Test
    @Order(5)
    public void CreateStory()  {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("stories"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(STORY_NAME_FIELD))).sendKeys(STORY_NAME);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(STORY_DESCRIPTION_FIELD))).sendKeys(STORY_DESCRIPTION);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(STORY_VISIBILITY))).get(1).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(STORY_PRIVACY))).get(1).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(STORY_PASSWORD_FIELD))).sendKeys(PASSWORD);

        WebElement selectElem = driver.findElement(By.className(STORY_CHILD_SELECT));
        Select select = new Select(selectElem);
        select.selectByValue(STORY_CHILD);

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className(STORY_SAVE_BUTTON))).click();
        assertEquals("Story \""+STORY_NAME+"\" added succesfully!",driver.findElement(By.className(STORY_SUCCESS_NOTE)).getText());
    }

    @Test
    @Order(6)
    public void ModifyStory()  {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("stories"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        boolean first = true;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("edit"))
            {
                if(first)
                {
                    first = false;
                }
                else
                {
                    items.get(i).click();
                    i = -2;
                }
            }
            i++;
        }

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(STORY_NAME_FIELD))).sendKeys(" edit");
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(STORY_DESCRIPTION_FIELD))).sendKeys(" edit");
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(STORY_GET_COMMENTS))).get(0).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(STORY_VISIBILITY))).get(0).click();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(STORY_PRIVACY))).get(0).click();

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className(STORY_SAVE_BUTTON))).click();
        assertEquals("Story \""+STORY_NAME+" edit\" edited succesfully!",driver.findElement(By.className(STORY_SUCCESS_NOTE)).getText());
    }

    @Test
    @Order(7)
    public void DeleteStory()  {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("stories"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        int sid = 0;
        boolean first = true;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("delete"))
            {
                if(first)
                {
                    first = false;
                }
                else
                {
                    String href = items.get(i).getAttribute("href");
                    sid = Integer.parseInt(href.substring(href.indexOf("sid=")+4));
                    items.get(i).click();
                    driver.switchTo().alert().accept();
                    driver.switchTo().parentFrame();
                    i = -2;
                }
            }
            i++;
        }

        assertEquals("Story \""+STORY_NAME+" edit\" (StoryID: "+sid+") deleted successfully!",driver.findElement(By.className(STORY_SUCCESS_NOTE)).getText());
    }


    @Test
    @Order(8)
    public void AddNewPhoto() throws InterruptedException {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("photos"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("add"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        driver.switchTo().frame("upload_iframe");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_ADD_FILE))).sendKeys(System.getProperty("user.dir") + "/"+ PHOTO_NAME);
        driver.switchTo().parentFrame();

        Thread.sleep(10000);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_TITLE_FIELD))).sendKeys(PHOTO_TITLE);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_DESCRIPTION_FIELD))).sendKeys(PHOTO_DESCRIPTION);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(PHOTO_GET_COMMENTS))).get(1).click();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_INFO_FIELD))).sendKeys(PHOTO_INFO);


        WebElement selectElem = driver.findElement(By.name(PHOTO_CATEGORY_SELECT));
        Select select = new Select(selectElem);
        select.selectByValue(PHOTO_CATEGORY_CHILD);

        selectElem = driver.findElement(By.name(PHOTO_STORY_SELECT));
        select = new Select(selectElem);
        select.selectByValue(PHOTO_STORY_CHILD);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.className(PHOTO_SAVE_BUTTON))).click();

        WebElement lastPhoto = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(PHOTO_LAST_ADDED)));
        String href = lastPhoto.getAttribute("href");
        int pid = Integer.parseInt(href.substring(href.indexOf("?p=")+3));

        assertEquals("Photo \""+PHOTO_TITLE+"\" (pid: "+pid+") added succesfully!", new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.className(PHOTO_SUCCESS_NOTE))).getText());
    }

    @Test
    @Order(9)
    public void ModifyPhoto() throws InterruptedException {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("photos"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("edit"))
            {
                items.get(i).click();
                i = -2;
            }
            i++;
        }

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_UPLOAD_NEW_FILE))).click();
        driver.switchTo().frame("upload_iframe");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_ADD_FILE))).sendKeys(System.getProperty("user.dir") + "/"+ PHOTO_NAME);
        driver.switchTo().parentFrame();

        Thread.sleep(10000);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(PHOTO_GET_COMMENTS))).get(0).click();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.className(PHOTO_SAVE_BUTTON))).click();


        WebElement lastPhoto = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(PHOTO_LAST_ADDED)));
        String href = lastPhoto.getAttribute("href");
        int pid = Integer.parseInt(href.substring(href.indexOf("?p=")+3));

        assertEquals("Photo \""+PHOTO_TITLE+"\" (pid: "+pid+") edited succesfully!", new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.className(PHOTO_SUCCESS_NOTE))).getText());

    }

    @Test
    @Order(10)
    public void Comment() {

        driver.get(MAIN);

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(PHOTO_COMMENT_LAST_ADDED))).click();
        List<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_COMMENT_NAME))).sendKeys(COMMENT_NAME);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_COMMENT_EMAIL))).sendKeys(COMMENT_EMAIL);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.name(PHOTO_COMMENT_DESCRIPTION))).sendKeys(COMMENT_TXT);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(PHOTO_COMMENT_SUBMIT))).click();

        assertEquals("Comment added successfully",driver.findElement(By.className(PHOTO_COMMENT_OK_MSG)).getText());

        driver.close();
        driver.switchTo().window(tabs.get(0));
    }

    @Test
    @Order(11)
    public void SelectRatePhoto()  {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("photos"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }


        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getAttribute("title").toLowerCase().contains("new photo"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        WebElement selectElem = driver.findElement(By.name(RATE_SELECT));
        Select select = new Select(selectElem);
        select.selectByValue(RATE_CHILD);

        assertEquals("Your rating saved!",driver.findElement(By.id(RATE_STATUS)).getText());
    }


//    @Test
//    @Order(12)
//    public void HideInfoPhoto()  {
//
//        driver.get(MAIN);
//
//        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(PHOTO_COMMENT_LAST_ADDED))).click();
//        List<String> tabs = new ArrayList<String>(driver.getWindowHandles());
//        driver.switchTo().window(tabs.get(1));
//
//        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.id(PHOTO_SHOW_INFO))).click();
//        assertEquals("Show",driver.findElement(By.id(PHOTO_SHOW_INFO)).getText());
//        driver.close();
//        driver.switchTo().window(tabs.get(0));
//    }


    @Test
    @Order(12)
    public void DeletePhoto()  {

        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("photos"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        WebElement delete = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(PHOTO_DELETE)));
        String href = delete.getAttribute("href");
        int pid = Integer.parseInt(href.substring(href.indexOf("pid=")+4));
        delete.click();
        driver.switchTo().alert().accept();
        driver.switchTo().parentFrame();

        assertEquals("Photo \""+PHOTO_TITLE+"\" (PhotoID: "+pid+") deleted successfully!",driver.findElement(By.className(PHOTO_SUCCESS_NOTE)).getText());
    }

    @Test
    @Order(13)
    public void SelectLast20()
    {
        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        int i = 0;
        while(i > -1 && i < items.size())
        {

            if(items.get(i).getText().toLowerCase().contains("20"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        assertEquals(-1, i);
    }

    @Test
    @Order(14)
    public void PhotoGallery()
    {
        driver.get(MAIN);
        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("photogallery"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        assertEquals(-1, i);
    }

    @Test
    @Order(15)
    public void RSS()
    {
        driver.get(MAIN);
        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("rss"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        assertEquals(-1, i);
    }

    @Test
    @Order(16)
    public void Logout()
    {
        driver.get(MAIN);

        List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));

        int i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("admin"))
            {
                driver.get(items.get(i).findElement(By.tagName("a")).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("logout"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        items = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
        i = 0;
        while(i > -1 && i < items.size())
        {
            if(items.get(i).getText().toLowerCase().contains("login"))
            {
                driver.get(items.get(i).getAttribute("href"));
                i = -2;
            }
            i++;
        }

        assertEquals(-1, i);
    }

    @AfterAll
    public static void finish() {
        if (driver != null) {
            driver.close();
        }
    }
}