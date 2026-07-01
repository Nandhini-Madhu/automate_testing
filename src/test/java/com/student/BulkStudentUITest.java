package com.student;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;

public class BulkStudentUITest {

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        js = (JavascriptExecutor) driver;
    }

    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    @Test
    public void bulkStudentCreation() throws InterruptedException {

  
        driver.get("https://beta.campushub.thebettertomorrow.in/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-email")))
                .sendKeys("beta-admin@bt.in");
        driver.findElement(By.id("login-password")).sendKeys("Test@123");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'Sign in')]")))
                .click();
        wait.until(ExpectedConditions.urlContains("/admin"));

       
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'/admin/students')]")))
                .click();

        wait.until(ExpectedConditions.urlContains("/admin/students"));
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h5[normalize-space()='Better Tomorrow']/ancestor::a[contains(@href,'/admin/students/')]")
        )));

        wait.until(ExpectedConditions.urlMatches(".*/admin/students/[a-z0-9\\-]+$"));
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'create-students')]")
        )));


        wait.until(ExpectedConditions.urlContains("create-students"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Create Student']"))).click();
        Thread.sleep(1500);

        WebElement textarea = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@rows='6']")));
        js.executeScript("arguments[0].removeAttribute('disabled');", textarea);
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", textarea);
        textarea.click();
        textarea.clear();
        String emails = "mahilan072@gmail.com,kamarajdharani17@gmail.com, dharanikec30@gmail.com, dharaniprakash1730@gmail.com";
        textarea.sendKeys(emails);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Next']"))).click();
        Thread.sleep(2000);

        WebElement clusterSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("existingCluster")));
        js.executeScript("arguments[0].removeAttribute('disabled');", clusterSelect);
        new Select(clusterSelect).selectByVisibleText("BT");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Submit']"))).click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Submit']"))).click();

        WebElement summaryTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Bulk Upload Summary')]")
        ));
        Assert.assertTrue(summaryTitle.isDisplayed(), "Bulk Upload Summary modal did not appear");
        System.out.println("Bulk Upload Summary modal appeared");

        try {
            String success   = driver.findElement(By.xpath("//*[contains(text(),'Success')]")).getText();
            String duplicate = driver.findElement(By.xpath("//*[contains(text(),'Duplicate')]")).getText();
            String error     = driver.findElement(By.xpath("//*[contains(text(),'Error')]")).getText();
            System.out.println( success);
            System.out.println(duplicate);
            System.out.println(error);
        } catch (Exception e) {
            System.out.println("Could not read summary counts: " + e.getMessage());
        }

        boolean closed = false;

        try {
            WebElement xBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@aria-label='Close' or @aria-label='close' or contains(@class,'close')]")
            ));
            jsClick(xBtn);
            closed = true;
            System.out.println("Modal closed via X button.");
        } catch (Exception e) {
            System.out.println("X button not found, trying Close button...");
        }
        if (!closed) {
            try {
                WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space()='Close']")
                ));
                jsClick(closeBtn);
                closed = true;
                System.out.println("Modal closed via Close button.");
            } catch (Exception e) {
                System.out.println("Close button not found either: " + e.getMessage());
            }
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Bulk Upload Summary')]")
        ));
        System.out.println("Modal dismissed successfully.");
        System.out.println("Bulk Student Creation Test PASSED");
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}