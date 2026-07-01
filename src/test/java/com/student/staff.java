package com.student;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class staff {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    public void closePopupIfPresent() {
        try {
            WebElement closeBtn = driver.findElement(
                    By.xpath("//button[normalize-space(text())='×'] " +
                            "| //button[@aria-label='Close'] " +
                            "| //*[contains(@class,'modal') or contains(@class,'popup') " +
                            "or contains(@class,'dialog')]" +
                            "//*[normalize-space(text())='×' or normalize-space(text())='✕' " +
                            "or normalize-space(text())='Close']"));

            if (closeBtn.isDisplayed()) {
                closeBtn.click();
                Thread.sleep(1500);
            }

        } catch (Exception e) {

        }
    }

    public void selectBT02() throws Exception {

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'IndicatorsContainer')]")))
                .click();

        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'option') and normalize-space(text())='BT-02']")))
                .click();

        Thread.sleep(3000);

        closePopupIfPresent();
    }

    @BeforeTest
    public void setup() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        js = (JavascriptExecutor) driver;
    }

    @Test
    public void studentLookupFlow() throws Exception {

        driver.get("https://beta.campushub.thebettertomorrow.in/login");

        Thread.sleep(5000);

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@type='email']")));

        email.sendKeys("vickyvigneshvickythangam@gmail.com");

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@type='password']")));

        password.sendKeys("Test@123");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit']")))
                .click();

        Thread.sleep(8000);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("staff-data-instruction"),
                ExpectedConditions.urlContains("individual-report")
        ));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'individual-report')]")))
                .click();

        Thread.sleep(4000);

        closePopupIfPresent();

        selectBT02();

        WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@placeholder='Search by name, roll no, or email']")));

        searchBox.click();

        Thread.sleep(1000);

        searchBox.clear();

        searchBox.sendKeys("21cs111");

        Thread.sleep(3000);

        searchBox.sendKeys(Keys.CONTROL + "a");
        searchBox.sendKeys(Keys.DELETE);

        Thread.sleep(1000);

        selectBT02();

        searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@placeholder='Search by name, roll no, or email']")));

        searchBox.click();

        Thread.sleep(1000);

        searchBox.clear();

        searchBox.sendKeys("nandhinimadhu599@gmail.com");

        Thread.sleep(3000);

        searchBox.sendKeys(Keys.CONTROL + "a");
        searchBox.sendKeys(Keys.DELETE);

        Thread.sleep(1000);

        searchBox.sendKeys("nandhini");

        Thread.sleep(4000);

        WebElement firstRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("(//table/tbody/tr)[1]")));

        String firstRowText = firstRow.getText();

        if (firstRowText.toLowerCase().contains("nandhini")) {
            System.out.println("Search Working Properly");
        } else {
            System.out.println("Search Not Working");
        }

        firstRow.click();

        Thread.sleep(5000);

        closePopupIfPresent();

        driver.navigate().back();

        Thread.sleep(4000);

        closePopupIfPresent();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/admin/reports/course')]")))
                .click();

        Thread.sleep(5000);

        closePopupIfPresent();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h3[contains(text(),'BT-02')]")))
                .click();

        Thread.sleep(5000);

        closePopupIfPresent();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'IndicatorsContainer')]")))
                .click();

        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'option') and normalize-space(text())='TILL DATE']")))
                .click();

        Thread.sleep(5000);

        closePopupIfPresent();

        WebElement enableBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space()='Enable' and @aria-pressed='false']")));

        js.executeScript("arguments[0].click();", enableBtn);

        wait.until(ExpectedConditions.attributeToBe(
                By.xpath("//button[normalize-space()='Enable']"),
                "aria-pressed",
                "true"));

        Thread.sleep(4000);

        WebElement leetcodeHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p[contains(text(),'LeetCode')]")));

        if (leetcodeHeading.isDisplayed()) {
            System.out.println("LeetCode section present");
        }

        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//table")));

        System.out.println(table.getText());

        Thread.sleep(3000);

        WebElement disableBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space()='Disable' and @aria-pressed='false']")));

        js.executeScript("arguments[0].click();", disableBtn);

        wait.until(ExpectedConditions.attributeToBe(
                By.xpath("//button[normalize-space()='Disable']"),
                "aria-pressed",
                "true"));

        Thread.sleep(4000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@title='Download' and @aria-label='Download']")))
                .click();

        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'w-full') and normalize-space(text())='CSV']" +
                        "| //button[.//text()='CSV' and contains(@class,'rounded-xl')]")))
                .click();

        Thread.sleep(5000);

        closePopupIfPresent();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@title='Download' and @aria-label='Download']")))
                .click();

        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'w-full') and normalize-space(text())='PDF']" +
                        "| //button[.//text()='PDF' and contains(@class,'rounded-xl')]")))
                .click();

        Thread.sleep(5000);

        closePopupIfPresent();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@title='Share' and @aria-label='Share']")))
                .click();

        Thread.sleep(3000);

        closePopupIfPresent();

        WebElement emailBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//textarea[@placeholder='Emails — comma, space, or one per line']")));

        emailBox.clear();

        emailBox.sendKeys("nandhinimadhu599@gmail.com,praveenmarimuthu29@gmail.com");

        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space(text())='Send Now']")))
                .click();

        Thread.sleep(5000);

        closePopupIfPresent();
    }

    @AfterTest
    public void closeBrowser() throws Exception {

        Thread.sleep(3000);

        driver.quit();
    }
}