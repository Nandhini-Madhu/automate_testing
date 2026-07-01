package com.student;

import java.io.File;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class allreport {

    static final String LOGIN_URL = "https://campushub.thebettertomorrow.in/login";
    static final String EMAIL     = "placement@acetcbe.edu.in";
    static final String PASSWORD  = "acetcbe#123";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions actions;

    private static final String FULL_CLICK_JS =
        "var el = arguments[0];" +
        "el.scrollIntoView({block:'center'});" +
        "function fire(type){" +
        "  var evt = new MouseEvent(type, {bubbles:true, cancelable:true, view:window});" +
        "  el.dispatchEvent(evt);" +
        "}" +
        "try { el.dispatchEvent(new PointerEvent('pointerdown', {bubbles:true})); } catch(e) {}" +
        "fire('mousedown');" +
        "fire('mouseup');" +
        "fire('click');";

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.setExperimentalOption("prefs", java.util.Map.of(
            "download.default_directory", System.getProperty("user.home") + File.separator + "Downloads",
            "download.prompt_for_download", false,
            "plugins.always_open_pdf_externally", true
        ));

        driver  = new ChromeDriver(options);
        wait    = new WebDriverWait(driver, Duration.ofSeconds(40));
        js      = (JavascriptExecutor) driver;
        actions = new Actions(driver);
    }

    @Test
    public void studentDetailsReportFlow() throws Exception {

        doLogin();
        openStudentDetails();
        dismissTourIfPresent();
        confirmYesOnLoadAllModal();

        selectDateFilter("Last 15 Days");
        waitForDataLoaded();
        clickDownload();
    }

    private void doLogin() throws Exception {
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
        sleep(4000);
    }

    private void openStudentDetails() throws Exception {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[href='/admin/student-details']")));
        jsClick(link);
        sleep(3000);
    }

    private void dismissTourIfPresent() throws Exception {
        for (int i = 0; i < 5; i++) {
            List<WebElement> tourRoot = driver.findElements(By.id("__reactour"));
            if (tourRoot.isEmpty() || !tourRoot.get(0).isDisplayed()) {
                return;
            }

            List<WebElement> closeButtons = driver.findElements(
                By.cssSelector("button[class*='reactour__close'], button[aria-label='Close']"));
            if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
                jsClick(closeButtons.get(0));
                sleep(600);
                continue;
            }

            List<WebElement> nextButtons = driver.findElements(
                By.xpath("//button[contains(.,'Next')]"));
            if (!nextButtons.isEmpty() && nextButtons.get(0).isDisplayed()) {
                jsClick(nextButtons.get(0));
                sleep(600);
                continue;
            }

            actions.sendKeys(Keys.ESCAPE).perform();
            sleep(600);
        }
    }

    private void confirmYesOnLoadAllModal() throws Exception {
        WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//span[normalize-space()='Yes']] | //button[normalize-space()='Yes']")));
        jsClick(yesButton);
        sleep(2000);
    }

    private void selectDateFilter(String optionLabel) throws Exception {
        WebElement dateFilterControl = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//p[normalize-space(text())='Date filter']/following-sibling::div"
                + "//div[contains(@class,'-control')]")));
        jsClick(dateFilterControl);
        sleep(300);

        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("div[id*='-option-']")));
        sleep(300);

        List<WebElement> options = driver.findElements(By.cssSelector("div[id*='-option-']"));
        for (WebElement opt : options) {
            if (opt.getText().trim().equalsIgnoreCase(optionLabel)) {
                jsClick(opt);
                break;
            }
        }
        sleep(1500);
    }

    private void waitForDataLoaded() throws Exception {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("[class*='loading'], [class*='spinner'], [class*='skeleton']")));
        } catch (TimeoutException ignored) {
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("table tbody tr")));

        sleep(500);
    }

    private void clickDownload() throws Exception {
        WebElement downloadButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@data-tour='download-btn']//button")));
        jsClick(downloadButton);
        sleep(2000);

        WebElement excelOption = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//span[normalize-space()='Excel']]")));
        jsClick(excelOption);
        sleep(2000);
    }

    private void jsClick(WebElement el) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        js.executeScript(FULL_CLICK_JS, el);
    }

    private void sleep(int ms) throws Exception {
        Thread.sleep(ms);
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }
}