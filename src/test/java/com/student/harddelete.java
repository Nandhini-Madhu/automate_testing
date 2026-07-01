package com.student;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class harddelete {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    // ── Student email to hard-delete ───────────────────────────────────────────
    List<String> studentEmails = Arrays.asList(
            "kamarajdharani17@gmail.com"
    );

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

    public void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    @Test
    public void bulkDeleteAndApprove() throws Exception {

        // ── Login ──────────────────────────────────────────────────────────────
        driver.get("https://beta.campushub.thebettertomorrow.in/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("login-email"))).sendKeys("beta-admin@bt.in");

        driver.findElement(By.id("login-password")).sendKeys("Test@123");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Sign in')]"))).click();

        wait.until(ExpectedConditions.urlContains("/admin"));
        System.out.println("Login successful");

        // ── Process each email one by one ─────────────────────────────────────
        for (String email : studentEmails) {

            System.out.println("\n====== Processing student : " + email + " ======");

            // ── Navigate to Students page ──────────────────────────────────────
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href,'/admin/students')]"))).click();

            wait.until(ExpectedConditions.urlContains("/admin/students"));

            WebElement campusCard = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//h5[normalize-space()='Better Tomorrow']" +
                            "/ancestor::a[contains(@href,'/admin/students/')]")));
            jsClick(campusCard);
            System.out.println("Campus opened");

            // ── Select BT Cluster ──────────────────────────────────────────────
            WebElement clusterDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("cluster-filter")));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", clusterDropdown);
            Thread.sleep(1000);

            try {
                wait.until(ExpectedConditions.elementToBeClickable(clusterDropdown));
                clusterDropdown.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", clusterDropdown);
            }
            Thread.sleep(2000);

            WebElement btOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//option[normalize-space()='BT'] | //*[normalize-space()='BT']")));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", btOption);
            Thread.sleep(1000);

            try {
                btOption.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", btOption);
            }
            Thread.sleep(3000);
            System.out.println("BT cluster selected");

            // ── Search for student ─────────────────────────────────────────────
            System.out.println("Searching : " + email);

            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@placeholder='Search by name, email or roll no...']")));
            searchBox.clear();
            searchBox.sendKeys(email);
            Thread.sleep(4000);

            List<WebElement> rows = driver.findElements(
                    By.xpath("//tr[td[contains(text(),'" + email + "')]]"));

            if (rows.isEmpty()) {
                System.out.println("Student not found : " + email);
                continue;
            }

            // ── Tick checkbox ──────────────────────────────────────────────────
            WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//tr[td[contains(text(),'" + email + "')]]//input[@type='checkbox']")));
            jsClick(checkbox);
            System.out.println("Checkbox ticked : " + email);
            Thread.sleep(2000);

            // ── Click Delete Selected ──────────────────────────────────────────
            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Delete Selected')]")));
            jsClick(deleteBtn);
            System.out.println("Delete Selected clicked");
            Thread.sleep(3000);

            // ── Click Clear Student Profile ────────────────────────────────────
            WebElement clearProfileBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Clear Student Profile')]")));
            jsClick(clearProfileBtn);
            System.out.println("Clear Student Profile clicked");
            Thread.sleep(3000);

            // ── Enter Reason and Submit ────────────────────────────────────────
            WebElement reasonBox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@role='dialog']//input")));
            reasonBox.click();
            Thread.sleep(1000);
            reasonBox.sendKeys("Automation testing");
            System.out.println("Reason entered");

            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Submit']")));
            jsClick(submitBtn);
            System.out.println("Delete request submitted for : " + email);
            Thread.sleep(6000);

            // ══════════════════════════════════════════════════════════════════
            // APPROVAL — go to deletion-requests, type email, click GREEN TICK
            // ══════════════════════════════════════════════════════════════════
            driver.get("https://beta.campushub.thebettertomorrow.in/admin/deletion-requests");
            wait.until(ExpectedConditions.urlContains("/admin/deletion-requests"));
            Thread.sleep(4000);
            System.out.println("On deletion-requests page for : " + email);

            // ── Type email in search box ───────────────────────────────────────
            WebElement approvalSearch = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//input")));
            approvalSearch.clear();
            Thread.sleep(1000);
            approvalSearch.sendKeys(email);
            Thread.sleep(4000);
            System.out.println("Email typed in search : " + email);

            // ── Click the GREEN TICK button ────────────────────────────────────
            List<WebElement> tickButtons = driver.findElements(
                    By.xpath("//button[.//*[name()='svg' and " +
                            "contains(@class,'text-green') or " +
                            "contains(@class,'green')]] | " +
                            "//button[contains(@class,'green')] | " +
                            "//button[.//*[name()='path' and contains(@d,'M5 13l4 4L19 7')]] | " +
                            "//td[contains(@class,'action') or contains(@class,'Actions')]" +
                            "//button[1]"));

            if (tickButtons.isEmpty()) {
                tickButtons = driver.findElements(
                        By.xpath("//tr[td[contains(.,'" + email + "')]]//button[1]"));
            }

            if (tickButtons.isEmpty()) {
                System.out.println("No tick button found for : " + email);
                continue;
            }

            try {
                WebElement tickBtn = tickButtons.get(0);
                js.executeScript("arguments[0].scrollIntoView(true);", tickBtn);
                Thread.sleep(1000);
                jsClick(tickBtn);
                System.out.println("Green tick clicked for : " + email);
                Thread.sleep(2000);

                try {
                    WebElement confirmBtn = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.xpath("//button[normalize-space()='Confirm' or " +
                                            "normalize-space()='Yes' or " +
                                            "normalize-space()='Approve' or " +
                                            "normalize-space()='OK']")));
                    jsClick(confirmBtn);
                    System.out.println("Confirmation accepted for : " + email);
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    System.out.println("No confirmation dialog for : " + email);
                }

            } catch (Exception e) {
                System.out.println("Tick click failed for : " + email);
                continue;
            }

            // ── Hard Delete Validation ─────────────────────────────────────────
            validateHardDelete(email);

            System.out.println("====== Done : " + email + " ======\n");
            Thread.sleep(2000);
        }

        System.out.println("All students processed successfully");
    }

    // ── Hard Delete Validation ─────────────────────────────────────────────────
    //
    // Check 1 : Assignment Report table
    //           Reports → Assignments → Better Tomorrow → assessment card
    //           matching the student's email → student NOT in table  (PASS if absent)
    //
    // Check 2 : View Not Attended table
    //           Same report page → "View Not Attended" button
    //           student NOT in table  (PASS if absent)
    //           Hard delete removes the student entirely — absent from both views
    //
    // Check 3 : Manage Cluster / Students page
    //           /admin/students → Better Tomorrow → BT cluster → search email
    //           student NOT in table  (PASS if absent)
    // ──────────────────────────────────────────────────────────────────────────
    public void validateHardDelete(String email) throws Exception {

        System.out.println("\n---------- HARD DELETE VALIDATION : " + email + " ----------");

        // ══════════════════════════════════════════════════════════════════════
        // CHECK 1 & 2 : Assignment Report + Not Attended
        // ══════════════════════════════════════════════════════════════════════

        // Open the assessment card whose title matches the student's email
        openAssessmentReport(email);

        // ── Check 1 : Assignment Report table ─────────────────────────────────
        List<WebElement> nameRowsInReport = driver.findElements(
                By.xpath("//table[contains(@class,'w-full')]" +
                        "//tbody//td[contains(normalize-space(),'" + email + "')]"));

        System.out.println("Rows found in Assignment Report : " + nameRowsInReport.size());

        if (nameRowsInReport.isEmpty()) {
            System.out.println("PASS : Student NOT present in Assignment Report → " + email);
        } else {
            System.out.println("FAIL : Student still present in Assignment Report → " + email);
            for (WebElement cell : nameRowsInReport) {
                System.out.println("  Found cell : " + cell.getText().trim());
            }
        }

        // ── Check 2 : View Not Attended table ─────────────────────────────────
        WebElement notAttendedBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(normalize-space(),'View Not Attended')]")));
        jsClick(notAttendedBtn);
        System.out.println("View Not Attended clicked");
        Thread.sleep(3000);

        List<WebElement> nameRowsInNotAttended = driver.findElements(
                By.xpath("//table[contains(@class,'w-full')]" +
                        "//tbody//td[contains(normalize-space(),'" + email + "')]"));

        System.out.println("Rows found in Not Attended : " + nameRowsInNotAttended.size());

        if (nameRowsInNotAttended.isEmpty()) {
            System.out.println("PASS : Student NOT present in Not Attended → " + email);
        } else {
            System.out.println("FAIL : Student still present in Not Attended → " + email);
            for (WebElement cell : nameRowsInNotAttended) {
                System.out.println("  Found cell : " + cell.getText().trim());
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // CHECK 3 : Manage Cluster — student must not appear in BT cluster
        // ══════════════════════════════════════════════════════════════════════
        driver.get("https://beta.campushub.thebettertomorrow.in/admin/students");
        wait.until(ExpectedConditions.urlContains("/admin/students"));
        Thread.sleep(3000);
        System.out.println("Students page loaded");

        WebElement campusCard = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h5[normalize-space()='Better Tomorrow']" +
                        "/ancestor::a[contains(@href,'/admin/students/')]")));
        jsClick(campusCard);
        System.out.println("Better Tomorrow campus opened");
        Thread.sleep(3000);

        WebElement clusterDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("cluster-filter")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", clusterDropdown);
        Thread.sleep(1000);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(clusterDropdown));
            clusterDropdown.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", clusterDropdown);
        }
        Thread.sleep(2000);

        WebElement btOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//option[normalize-space()='BT']")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btOption);
        Thread.sleep(500);
        try {
            btOption.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", btOption);
        }
        Thread.sleep(3000);
        System.out.println("BT cluster selected");

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@placeholder,'Search')]")));
        searchBox.clear();
        searchBox.sendKeys(email);
        Thread.sleep(3000);

        List<WebElement> studentRowsInCluster = driver.findElements(
                By.xpath("//tr[td[contains(normalize-space(),'" + email + "')]]"));

        System.out.println("Rows found in BT cluster : " + studentRowsInCluster.size());

        if (studentRowsInCluster.isEmpty()) {
            System.out.println("PASS : Student NOT present in BT cluster → " + email);
        } else {
            System.out.println("FAIL : Student still exists in BT cluster → " + email);
            for (WebElement row : studentRowsInCluster) {
                System.out.println("  Found row : " + row.getText().trim());
            }
        }

        System.out.println("------------------------------------------------------------\n");
    }

    // ── Navigate to the assessment card whose title contains the student email ──
    //
    // Step 1 : /admin/reports
    //          Reports landing page — Assignments | Course | Leetcode cards
    //
    // Step 2 : Click "Assignments" card
    //          <a href="/admin/reports/assignment">
    //
    // Step 3 : Click "Better Tomorrow" campus card
    //          <a href="/admin/reports/assignments/Better Tomorrow">
    //
    // Step 4 : Click the assessment card with title "testing-29-5"
    //          Two cards visible in screenshot: "testing-29-5" and "test 001"
    //          We always target "testing-29-5" by matching its title text
    //          <a class="font-poppins" data-tour="assignment-card" href=".../<uuid>">
    //
    // Step 5 : Wait for the report table tbody to be present
    //          <table class="w-full ..."><tbody>...</tbody></table>
    // ──────────────────────────────────────────────────────────────────────────
    public void openAssessmentReport(String studentEmail) throws Exception {

        // Step 1 — Reports landing page
        driver.get("https://beta.campushub.thebettertomorrow.in/admin/reports");
        wait.until(ExpectedConditions.urlContains("/admin/reports"));
        Thread.sleep(3000);
        System.out.println("Reports page loaded");

        // Step 2 — Click "Assignments" card
        // <a href="/admin/reports/assignment">
        WebElement assignmentsCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='/admin/reports/assignment']")));
        jsClick(assignmentsCard);
        System.out.println("Assignments card clicked");
        wait.until(ExpectedConditions.urlContains("/admin/reports/assignment"));
        Thread.sleep(3000);

        // Step 3 — Click "Better Tomorrow" campus card
        // <a href="/admin/reports/assignments/Better Tomorrow">  (space or %20)
        WebElement btCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'/admin/reports/assignments/Better%20Tomorrow')" +
                                " or contains(@href,'/admin/reports/assignments/Better Tomorrow')]")));
        jsClick(btCard);
        System.out.println("Better Tomorrow campus card clicked");
        wait.until(ExpectedConditions.urlContains("/admin/reports/assignments/Better"));
        Thread.sleep(4000);

        // Step 4 — Click the assessment card with title "testing-29-5"
        //
        // From the screenshot there are two cards visible:
        //   Card 1 : "testing-29-5"  (ARRAY tag)   ← we want this one
        //   Card 2 : "test 001"      (JAVA tag)
        //
        // The card HTML (Image 4):
        //   <a class="font-poppins" data-tour="assignment-card" href=".../<uuid>">
        //     <div ...>
        //       <p ...>testing-29-5</p>   ← exact title text
        //     </div>
        //   </a>
        //
        // Target card title — change this constant if the card name ever changes
        final String TARGET_CARD_TITLE = "testing-29-5";

        WebElement assessmentCard = null;

        // Primary — find card by exact title text inside data-tour="assignment-card"
        List<WebElement> matchByTitle = driver.findElements(
                By.xpath("//a[@data-tour='assignment-card' " +
                        "and contains(normalize-space(.),'" + TARGET_CARD_TITLE + "')]"));

        if (!matchByTitle.isEmpty()) {
            assessmentCard = matchByTitle.get(0);
            System.out.println("Assessment card matched by title '" + TARGET_CARD_TITLE
                    + "' : " + assessmentCard.getText().trim());
        }

        // Fallback — title match using font-poppins class (no data-tour)
        if (assessmentCard == null) {
            System.out.println("data-tour match failed — trying font-poppins fallback for '"
                    + TARGET_CARD_TITLE + "'");
            List<WebElement> matchFallback = driver.findElements(
                    By.xpath("//a[contains(@class,'font-poppins') " +
                            "and contains(normalize-space(.),'" + TARGET_CARD_TITLE + "')]"));
            if (!matchFallback.isEmpty()) {
                assessmentCard = matchFallback.get(0);
                System.out.println("Fallback matched : " + assessmentCard.getText().trim());
            }
        }

        // Last resort — first card on page (should not normally reach here)
        if (assessmentCard == null) {
            System.out.println("WARNING : '" + TARGET_CARD_TITLE
                    + "' card not found — clicking first available card");
            assessmentCard = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[@data-tour='assignment-card'])[1]")));
            System.out.println("Last-resort card : " + assessmentCard.getText().trim());
        }

        jsClick(assessmentCard);
        System.out.println("Assessment card clicked");

        // Step 5 — Wait for report table tbody
        // <table class="w-full ..."><tbody class="group/body ...">
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table[contains(@class,'w-full')]//tbody")));
        Thread.sleep(3000);
        System.out.println("Report table loaded. URL : " + driver.getCurrentUrl());
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}