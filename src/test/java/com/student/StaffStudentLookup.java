package com.student;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class StaffStudentLookup {

    String LOGIN_URL  = "https://campushub.thebettertomorrow.in/login";
    String EMAIL      = "placement@acetcbe.edu.in";
    String PASSWORD   = "acetcbe#123";
    String SEARCH_TERM = "lokesh v";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(40));
        js     = (JavascriptExecutor) driver;
        System.out.println("Chrome browser opened");
    }

    @Test
    public void studentLookupFlow() throws Exception {

        driver.get(LOGIN_URL);
        sleep(2000);

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("login-email")));
        emailField.clear();
        emailField.sendKeys(EMAIL);
        sleep(300);

        WebElement passwordField = driver.findElement(By.id("login-password"));
        passwordField.clear();
        passwordField.sendKeys(PASSWORD);
        sleep(300);

        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Sign in')]"))));
        System.out.println("Signed in as " + EMAIL);
        sleep(4000);

        driver.get("https://campushub.thebettertomorrow.in/admin/individual-report");
        sleep(3000);

        dismissTourIfPresent();

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@placeholder='Search by name, roll no, or email']")));
        searchInput.clear();
        searchInput.sendKeys(SEARCH_TERM);
        System.out.println("Typed search term: " + SEARCH_TERM);
        sleep(1500);

        List<WebElement> matchingRows = driver.findElements(
            By.xpath("//tr[.//text()[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
                + "'abcdefghijklmnopqrstuvwxyz'), '" + SEARCH_TERM.toLowerCase() + "')]]"));

        if (matchingRows.isEmpty()) {
            System.out.println("Student '" + SEARCH_TERM + "' NOT found in search results");
            throw new AssertionError("Expected student '" + SEARCH_TERM + "' to appear in search results");
        }

        System.out.println("Student '" + SEARCH_TERM + "' found in search results ("
            + matchingRows.size() + " matching row(s))");

        WebElement studentRow = matchingRows.get(0);
        jsClick(studentRow);
        System.out.println("Clicked on student row");
        sleep(3000);

        wait.until(ExpectedConditions.urlContains("/admin/student/"));
        System.out.println("Navigated to student dashboard: " + driver.getCurrentUrl());
        sleep(2000);

        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.text-gray-500")));
        jsClick(backButton);
        System.out.println("Clicked back button");
        sleep(2000);

        wait.until(ExpectedConditions.urlContains("/admin/individual-report"));
        System.out.println("Back on student list page: " + driver.getCurrentUrl());

        System.out.println("Student lookup flow completed successfully");
    }

    private void dismissTourIfPresent() throws Exception {
        int maxClicks = 10;

        for (int i = 0; i < maxClicks; i++) {
            List<WebElement> nextButtons = driver.findElements(
                By.xpath("//button[contains(.,'Next')]"));

            if (nextButtons.isEmpty() || !nextButtons.get(0).isDisplayed()) {
                break;
            }

            jsClick(nextButtons.get(0));
            System.out.println("Clicked tour 'Next' (" + (i + 1) + ")");
            sleep(800);
        }

     
        List<WebElement> closeButtons = driver.findElements(
            By.cssSelector("button.reactour__close"));
        if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
            jsClick(closeButtons.get(0));
            System.out.println("Closed tour via close button");
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