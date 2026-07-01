package com.student;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class StudentCourseReport {

    static final String LOGIN_URL    = "https://campushub.thebettertomorrow.in/login";
    static final String EMAIL        = "placement@acetcbe.edu.in";
    static final String PASSWORD     = "acetcbe#123";
    static final String SHARE_EMAIL  = "nandhinimadhu599@gmail.com";

    static final String DOWNLOAD_DIR =
        System.getProperty("user.home") + File.separator + "Downloads";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        options.setExperimentalOption("prefs", java.util.Map.of(
            "download.default_directory", DOWNLOAD_DIR,
            "download.prompt_for_download", false,
            "plugins.always_open_pdf_externally", true
        ));

        driver  = new ChromeDriver(options);
        wait    = new WebDriverWait(driver, Duration.ofSeconds(40));
        js      = (JavascriptExecutor) driver;
        System.out.println("Chrome browser opened");
    }

    @Test
    public void courseReportFlow() throws Exception {

        driver.get(LOGIN_URL);
        sleep(2000);

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("login-email")));
        emailField.clear();
        emailField.sendKeys(EMAIL);
        sleep(300);

        driver.findElement(By.id("login-password")).sendKeys(PASSWORD);
        sleep(300);

        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Sign in')]"))));
        System.out.println("Signed in as " + EMAIL);
        sleep(4000);

        driver.get("https://campushub.thebettertomorrow.in/admin/reports/course");
        System.out.println("Navigated to 'All Student Course'");
        sleep(3000);

        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[aria-label='Open cluster report']"))));
        System.out.println("Opened ACE cluster report");
        sleep(3000);

      
        org.openqa.selenium.interactions.Actions actions =
            new org.openqa.selenium.interactions.Actions(driver);

        String[] dateOptions = {"TODAY", "PAST 10 DAYS", "THIS MONTH", "TILL DATE", "PAST 7 DAYS"};

        for (String option : dateOptions) {
            WebElement control = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[class*='-control']")));
            actions.moveToElement(control).click().perform();
            System.out.println("Opened date filter dropdown");
            sleep(800);

            List<WebElement> optionEls = driver.findElements(
                By.xpath("//*[normalize-space(text())='" + option + "']"));

            if (!optionEls.isEmpty()) {
                WebElement target = optionEls.get(optionEls.size() - 1);
                actions.moveToElement(target).click().perform();
                System.out.println("Selected date filter: " + option);
                sleep(1000);

                try {
                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("[data-testid='table-row-element']")),
                        ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(),'No data') or contains(text(),'no data')]"))
                    ));
                } catch (TimeoutException ignored) {
                    
                }

                List<WebElement> nameCells = driver.findElements(
                    By.cssSelector("[data-testid='table-row-element'] td"));
                if (nameCells.isEmpty()) {
                    System.out.println("WARNING: No student rows visible after selecting " + option);
                } else {
                    System.out.println("Verified student names visible for filter '" + option
                        + "' (" + nameCells.size() + " cells found)");
                }
            } else {
                System.out.println("Date filter option not found: " + option);
                actions.sendKeys(Keys.ESCAPE).perform();
                sleep(500);
            }
        }

        actions.sendKeys(Keys.ESCAPE).perform();
        js.executeScript("document.activeElement.blur();");
        sleep(1000);

      
        downloadAs("CSV");

        downloadAs("PDF");

        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@aria-label='Share']"))));
        System.out.println("Opened share panel");
        sleep(1000);

        WebElement shareEmailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//textarea | //input[contains(@placeholder,'mail')]")));
        shareEmailField.clear();
        shareEmailField.sendKeys(SHARE_EMAIL);
        sleep(500);

        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Send Now')]"))));
        System.out.println("Sent report to " + SHARE_EMAIL);
        sleep(2000);

     
        openRowMenuAndSelect("Course Details");
        sleep(1500);
        closeModalIfPresent();

        sleep(6000);

        openRowMenuAndSelect("LeetCode");
        sleep(1500);
        closeModalIfPresent();

        System.out.println("Course report flow completed successfully");
    }

    private void downloadAs(String format) throws Exception {
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@title='Download']"))));
        System.out.println("Opened download menu");
        sleep(800);

        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[text()='" + format + "']"))));
        System.out.println("Clicked " + format + " download option");
        sleep(3000);
    }

    private void openRowMenuAndSelect(String menuItem) throws Exception {
      
        List<WebElement> rows = driver.findElements(
            By.cssSelector("[data-testid='table-row-element']"));

        if (rows.isEmpty()) {
            throw new Exception("Could not find any table rows");
        }

        WebElement firstRow = rows.get(0);
        List<WebElement> rowButtons = firstRow.findElements(By.tagName("button"));

        if (rowButtons.isEmpty()) {
            throw new Exception("Could not find row '...' menu trigger button");
        }

        WebElement moreButton = rowButtons.get(rowButtons.size() - 1);
        jsClick(moreButton);
        System.out.println("Opened row '...' menu");
        sleep(800);

        WebElement menuOption = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[text()='" + menuItem + "']")));
        jsClick(menuOption);
        System.out.println("Clicked menu option: " + menuItem);
    }

    private void closeModalIfPresent() throws Exception {
        List<WebElement> closeButtons = driver.findElements(
            By.xpath("//button[@aria-label='close' or @aria-label='Close']"));

        if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
            jsClick(closeButtons.get(0));
            System.out.println("Closed modal");
            sleep(500);
            return;
        }

       
        List<WebElement> xIcons = driver.findElements(By.cssSelector("svg.lucide-x, button > svg"));
        if (!xIcons.isEmpty()) {
            jsClick(xIcons.get(xIcons.size() - 1));
            System.out.println("Closed modal via fallback X icon");
            sleep(500);
        }
    }

    private void jsClick(WebElement el) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        js.executeScript("arguments[0].click();", el);
    }

    private void sleep(int ms) throws Exception {
        Thread.sleep(ms);
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }
}