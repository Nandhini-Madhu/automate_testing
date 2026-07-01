package com.student;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

class StudentData {

    private String email;
    private String password;
    private String name;

    StudentData(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    String getEmail()    { return email; }
    String getPassword() { return password; }
    String getName()     { return name; }
}

public class AssignmentRevokeTest {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions actions;

    String adminEmail     = "beta-admin@bt.in";
    String adminPassword  = "Test@123";
    String assignmentName = "bulk delete Assignment";

    List<StudentData> allStudents = Arrays.asList(
        new StudentData("beta.bt.better.026@bt.in", "Test@123", "Student1"),
        new StudentData("beta.bt.better.027@bt.in", "Test@123", "Student2")
    );

    List<StudentData> revokeStudents = Arrays.asList(
        new StudentData("beta.bt.better.026@bt.in", "Test@123", "Student1")
    );

    String javaCode =
        "import java.util.Scanner;\n\n" +
        "public class Main {\n" +
        "    public static void main(String[] args) {\n" +
        "        Scanner sc = new Scanner(System.in);\n" +
        "        int n = sc.nextInt();\n" +
        "        int[] arr = new int[n];\n" +
        "        for(int i=0;i<n;i++){\n" +
        "            arr[i]=sc.nextInt();\n" +
        "        }\n" +
        "        int max=arr[0];\n" +
        "        for(int i=1;i<n;i++){\n" +
        "            if(arr[i]>max){\n" +
        "                max=arr[i];\n" +
        "            }\n" +
        "        }\n" +
        "        System.out.println(max);\n" +
        "    }\n" +
        "}";

    private void openBrowser() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver  = new ChromeDriver(options);
        wait    = new WebDriverWait(driver, Duration.ofSeconds(40));
        js      = (JavascriptExecutor) driver;
        actions = new Actions(driver);

        System.out.println("Browser Opened");
    }

    private void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private void sleep(long ms) throws Exception {
        Thread.sleep(ms);
    }

    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    private void loginAsStudent(StudentData student) throws Exception {
        driver.get("https://beta.campushub.thebettertomorrow.in/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-email")))
            .sendKeys(student.getEmail());

        driver.findElement(By.id("login-password"))
            .sendKeys(student.getPassword());

        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Sign in')]"))).click();

        wait.until(ExpectedConditions.urlContains("/student"));
        sleep(4000);

        System.out.println("Student Logged In : " + student.getName());
    }

    private void loginAsAdmin() throws Exception {
        driver.get("https://beta.campushub.thebettertomorrow.in/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-email")))
            .sendKeys(adminEmail);

        driver.findElement(By.id("login-password"))
            .sendKeys(adminPassword);

        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Sign in')]"))).click();

        wait.until(ExpectedConditions.urlContains("/admin"));
        sleep(5000);

        System.out.println("Admin Logged In");
    }

    private void navigateToAssignment() throws Exception {
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(@href,'/assignments')]")));
        jsClick(menu);

        wait.until(ExpectedConditions.urlContains("/assignments"));
        sleep(4000);

        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[contains(text(),'" + assignmentName + "')]")));
        jsClick(card);

        sleep(4000);
    }

    private void startAssignment() throws Exception {
        WebElement startNow = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Start Now')]")));
        jsClick(startNow);
        sleep(3000);

        try {
            WebElement next = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Next')]")));
            jsClick(next);
            sleep(2000);

            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='I TURNED OFF NOTIFICATION']")));
            input.sendKeys("I TURNED OFF NOTIFICATION");
            sleep(2000);

            WebElement startTest = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Start Test')]")));
            jsClick(startTest);
            sleep(5000);

            handleFullscreenIfPresent();
        } catch (Exception ignored) {}
    }

    private void injectCode() throws Exception {
        WebElement question = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//h3[contains(text(),'Largest Element in an Array')]")));
        jsClick(question);
        sleep(3000);

        WebElement editor = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(@class,'cm-content')]")));
        jsClick(editor);
        sleep(1000);

        js.executeScript(
            "var editor=document.querySelector('.cm-content');" +
            "editor.focus();" +
            "document.execCommand('selectAll',false,null);" +
            "document.execCommand('insertText',false,arguments[0]);",
            javaCode);
        sleep(3000);

        System.out.println("Code injected");
    }

    private void goBackToQuestionList() throws Exception {
        try {
            WebElement goBack = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(
                    "//a[contains(normalize-space(),'Go Back')]" +
                    " | //button[contains(normalize-space(),'Go Back')]" +
                    " | //*[contains(@class,'go-back')]")));
            jsClick(goBack);
            sleep(3000);
            System.out.println("Go Back clicked");
        } catch (Exception e) {
            System.out.println("Go Back not found — using browser back");
            driver.navigate().back();
            sleep(3000);
        }

        handleFullscreenIfPresent();
        sleep(1500);
    }

    private void clickEndTest() throws Exception {
        WebElement endTestBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//button[normalize-space()='End Test']" +
                " | //button[contains(.,'End Test')]")));
        jsClick(endTestBtn);
        sleep(3000);

        System.out.println("End Test clicked");
    }

    private void confirmEndTestSubmission() throws Exception {
        WebElement submitTestBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//button[normalize-space()='Submit test']" +
                " | //button[normalize-space()='Submit Test']")));
        jsClick(submitTestBtn);
        System.out.println("Confirmation modal Submit test clicked");
        sleep(3000);

        handleFullscreenIfPresent();

        try {
            WebElement botPopup = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(
                        "//*[contains(text(),'Only the bot can complete')" +
                        " or contains(text(),'activity is considered malpractice')]")));
            System.out.println("Bot modal detected");

            WebElement botEndTestBtn = botPopup.findElement(
                By.xpath(
                    "./ancestor::div[contains(@class,'fixed')" +
                    " or contains(@role,'dialog')]" +
                    "//button[normalize-space()='End Test']"));
            jsClick(botEndTestBtn);
            System.out.println("Bot modal End Test clicked");
            sleep(4000);
        } catch (Exception e) {
            System.out.println("Bot modal not shown");
        }

        WebElement okBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//button[normalize-space()='OK']" +
                " | //button[normalize-space()='Ok']")));
        jsClick(okBtn);
        System.out.println("Success OK clicked");
        sleep(3000);
    }

    private void exitFullscreen() throws Exception {
        try {
            js.executeScript(
                "if(document.fullscreenElement){" +
                "document.exitFullscreen();" +
                "}");
            System.out.println("Fullscreen exited");
        } catch (Exception e) {
            System.out.println("Fullscreen exit skipped");
        }
        sleep(2000);
    }


     private void handleFullscreenIfPresent() throws Exception {
        try {
            WebElement fullscreenText = driver.findElement(
                By.xpath("//*[contains(text(),'Please switch to Fullscreen')" +
                         " or contains(text(),'Fullscreen to continue')" +
                         " or contains(text(),'Full Screen to continue')]"));
            if (fullscreenText.isDisplayed()) {
                System.out.println("    [POPUP] Fullscreen prompt detected");
                try {
                    WebElement closeX = driver.findElement(
                        By.xpath("//button[normalize-space()='×' or normalize-space()='✕']"));
                    jsClick(closeX);
                    sleep(500);
                } catch (Exception ex) { /* no close button */ }
                js.executeScript("try{document.documentElement.requestFullscreen();}catch(e){}");
                sleep(1000);
                WebElement fullBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[normalize-space()='Full Screen']" +
                                 " or normalize-space()='Full Screen']")));
                jsClick(fullBtn);
                sleep(2000);
                System.out.println("    [POPUP] Fullscreen handled");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No fullscreen prompt");
        }
    }


    private void adminRevokeFlow() throws Exception {
        WebElement manageClusterMenu = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(@href,'clusters/campus-list')]")));
        jsClick(manageClusterMenu);
        sleep(3000);
        System.out.println("Manage Cluster opened");

        WebElement betterTomorrowCard = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//div[contains(@class,'cursor-pointer')" +
                " and .//h5[normalize-space()='Better Tomorrow']]")));
        jsClick(betterTomorrowCard);
        sleep(4000);
        System.out.println("Better Tomorrow clicked");

        WebElement btClusterCard = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//div[contains(@class,'shadow-md')" +
                " and .//h3[normalize-space()='BT']]")));
        jsClick(btClusterCard);
        sleep(4000);
        System.out.println("BT Cluster clicked");

        WebElement manageAssignmentTab = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//button[.//span[contains(text(),'Manage Assignment')]" +
                " or contains(text(),'Manage Assignment')]")));
        jsClick(manageAssignmentTab);
        sleep(4000);
        System.out.println("Manage Assignment clicked");

        WebElement bulkRevoke = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Bulk Revoke')]")));
        jsClick(bulkRevoke);
        sleep(4000);
        System.out.println("Bulk Revoke opened");

        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'css-b62m3t-container')]")));
        jsClick(dropdown);
        sleep(2000);

        WebElement assignmentInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@role='combobox']")));
        assignmentInput.sendKeys(Keys.CONTROL + "a");
        assignmentInput.sendKeys(Keys.DELETE);
        sleep(500);
        assignmentInput.sendKeys(assignmentName);
        System.out.println("Assignment typed");
        sleep(4000);

        WebElement assignmentOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@id,'option') and contains(text(),'" + assignmentName + "')]")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", assignmentOption);
        sleep(1000);
        jsClick(assignmentOption);
        System.out.println("Assignment option clicked");
        sleep(3000);

        WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//textarea")));
        textarea.click();
        sleep(1000);
        textarea.sendKeys(Keys.CONTROL + "a");
        textarea.sendKeys(Keys.DELETE);
        sleep(1000);

        StringBuilder emails = new StringBuilder();
        for (int i = 0; i < revokeStudents.size(); i++) {
            emails.append(revokeStudents.get(i).getEmail());
            if (i < revokeStudents.size() - 1) {
                emails.append(", ");
            }
        }
        textarea.sendKeys(emails.toString());
        System.out.println("Emails entered : " + emails);
        sleep(3000);

        WebElement revokeBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[normalize-space()='Revoke']")));
        jsClick(revokeBtn);
        System.out.println("Revoke clicked");
        sleep(5000);

        try {
            WebElement okBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(
                    "//button[normalize-space()='OK']" +
                    " | //button[normalize-space()='Ok']")));
            jsClick(okBtn);
            System.out.println("Success popup OK clicked");
            sleep(2000);
        } catch (Exception ignored) {}
    }

    private void verifyStudent(StudentData student) throws Exception {
        navigateToAssignment();

        List<WebElement> cards = driver.findElements(
            By.xpath("//*[contains(text(),'" + assignmentName + "')]"));

        if (cards.isEmpty()) {
            System.out.println("Assignment not visible : " + student.getName());
            return;
        }

        System.out.println("Assignment visible : " + student.getName());
        jsClick(cards.get(0));
        sleep(3000);

        List<WebElement> startNowButtons = driver.findElements(
            By.xpath("//button[contains(.,'Start Now') and not(@disabled)]"));

        if (!startNowButtons.isEmpty()) {
            System.out.println("Start Now available");
        } else {
            System.out.println("Start Now not available");
        }
    }
         private void completeStudentTest() throws Exception {
        navigateToAssignment();
        startAssignment();
        injectCode();

        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'SUBMIT')]")));
        jsClick(submitBtn);
        sleep(4000);

        goBackToQuestionList();
        clickEndTest();
        confirmEndTestSubmission();
        exitFullscreen();
    }
    

    @Test
    public void runFullFlow() throws Exception {

        // STUDENT FLOW

        for (StudentData student : allStudents) {

            openBrowser();

            try {

                loginAsStudent(student);

                completeStudentTest();

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                closeBrowser();
            }
        }

        // ADMIN FLOW

        openBrowser();

        try {

            loginAsAdmin();

            adminRevokeFlow();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            closeBrowser();
        }

        // VERIFY FLOW

        for (StudentData student : allStudents) {

            openBrowser();

            try {

                loginAsStudent(student);

                verifyStudent(student);

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                closeBrowser();
            }
        }
    }
}