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

public class reinvite {

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
                By.xpath("//button[normalize-space()='Resend Invite']"))).click();
        Thread.sleep(1500);

        WebElement resendTextarea = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//textarea[@placeholder[contains(.,'Enter emails to send invite')]]")
        ));
        js.executeScript("arguments[0].removeAttribute('disabled');", resendTextarea);
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", resendTextarea);

        js.executeScript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLTextAreaElement.prototype, 'value').set;" +
            "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
            resendTextarea,
            "kamarajdharani17@gmail.com, dharanikec30@gmail.com"
        );
        Thread.sleep(1000);

        WebElement sendInvitesBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[normalize-space()='Send Invites'] or normalize-space()='Send Invites']")
        ));
        jsClick(sendInvitesBtn);
        Thread.sleep(2000);

        System.out.println("Resend Invite sent successfully!");
    }


    @AfterTest
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}