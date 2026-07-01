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

public class Softdelete {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    List<String> studentEmails = Arrays.asList(
            "nandhini"
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

        for (String email : studentEmails) {

            System.out.println("\n====== Processing student : " + email + " ======");

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href,'/admin/students')]"))).click();
            wait.until(ExpectedConditions.urlContains("/admin/students"));

            WebElement campusCard = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//h5[normalize-space()='Better Tomorrow']" +
                            "/ancestor::a[contains(@href,'/admin/students/')]")));
            jsClick(campusCard);
            System.out.println("Campus opened");

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

            WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//tr[td[contains(text(),'" + email + "')]]//input[@type='checkbox']")));
            jsClick(checkbox);
            System.out.println("Checkbox ticked : " + email);
            Thread.sleep(2000);

            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Delete Selected')]")));
            jsClick(deleteBtn);
            System.out.println("Delete Selected clicked");
            Thread.sleep(3000);

            WebElement clearSubmissionBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(.,'Clear Submissions')]")));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", clearSubmissionBtn);
            Thread.sleep(1000);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(clearSubmissionBtn));
                clearSubmissionBtn.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", clearSubmissionBtn);
            }
            System.out.println("Clear Submissions clicked");
            Thread.sleep(3000);

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

            // ── Approval ───────────────────────────────────────────────────────
            driver.get("https://beta.campushub.thebettertomorrow.in/admin/deletion-requests");
            wait.until(ExpectedConditions.urlContains("/admin/deletion-requests"));
            Thread.sleep(4000);

            WebElement approvalSearch = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//input")));
            approvalSearch.clear();
            Thread.sleep(1000);
            approvalSearch.sendKeys(email);
            Thread.sleep(4000);
            System.out.println("Email typed in search : " + email);

            List<WebElement> tickButtons = driver.findElements(
                    By.xpath("//button[.//*[name()='svg' and " +
                            "contains(@class,'text-green') or " +
                            "contains(@class,'green')]] | " +
                            "//button[contains(@class,'green')] | " +
                            "//button[.//*[name()='path' and contains(@d,'M5 13l4 4L19 7')]] | " +
                            "//td[contains(@class,'action') or contains(@class,'Actions')]//button[1]"));

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

            // ── Report Validation ──────────────────────────────────────────────
            validateSoftDelete(email);

            System.out.println("====== Done : " + email + " ======\n");
            Thread.sleep(2000);
        }

        System.out.println("All students processed successfully");
    }

    // ── Navigation to Assessment Report ───────────────────────────────────────
    //
    // Step 1 : Navigate to /admin/reports
    //          Image 1 → Cards: Assignments | Course | Leetcode
    //
    // Step 2 : Click "Assignments" card
    //          Image 2 → <a href="/admin/reports/assignment">
    //                      <h5 ...>Assignments</h5>
    //                    </a>
    //
    // Step 3 : Click "Better Tomorrow" campus card
    //          Image 3 → <a href="/admin/reports/assignments/Better Tomorrow">
    //                      campus name text = "Better Tomorrow"
    //                    </a>
    //
    // Step 4 : Click first assessment card (data-tour="assignment-card")
    //          Image 4 → <a class="font-poppins" data-tour="assignment-card"
    //                         href="/admin/reports/assignments/Better Tomorrow/<uuid>">
    //                      card title e.g. "testing-29-5"
    //                    </a>
    //
    // Step 5 : Wait for report table
    //          Image 5 → <table class="w-full ..."> with tbody rows
    // ──────────────────────────────────────────────────────────────────────────
    public void openBulkAssessmentCard() throws Exception {

        // ── Step 1 : Reports landing page ─────────────────────────────────────
        driver.get("https://beta.campushub.thebettertomorrow.in/admin/reports");
        wait.until(ExpectedConditions.urlContains("/admin/reports"));
        Thread.sleep(3000);
        System.out.println("Reports page loaded");

        // ── Step 2 : Click "Assignments" card ─────────────────────────────────
        // Image 2: <a href="/admin/reports/assignment">
        //            <h5 class="text-2xl font-bold tracking-tight">Assignments</h5>
        //          </a>
        WebElement assignmentsCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='/admin/reports/assignment']")));
        jsClick(assignmentsCard);
        System.out.println("Assignments card clicked");
        wait.until(ExpectedConditions.urlContains("/admin/reports/assignment"));
        Thread.sleep(3000);

        // ── Step 3 : Click "Better Tomorrow" campus card ──────────────────────
        // Image 3: <a class="block h-full outline-none ..."
        //               href="/admin/reports/assignments/Better Tomorrow">
        //            <div class="relative flex h-full min-h-[120px] ...">
        //              ... "Better Tomorrow" text ...
        //            </div>
        //          </a>
        WebElement btCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'/admin/reports/assignments/Better%20Tomorrow')" +
                                " or contains(@href,'/admin/reports/assignments/Better Tomorrow')]")));
        jsClick(btCard);
        System.out.println("Better Tomorrow campus card clicked");
        wait.until(ExpectedConditions.urlContains("/admin/reports/assignments/Better"));
        Thread.sleep(4000);

        // ── Step 4 : Click the first assessment card ──────────────────────────
        // Image 4: <a class="font-poppins" data-tour="assignment-card"
        //               href="/admin/reports/assignments/Better Tomorrow/<uuid>">
        //
        // Primary locator  → data-tour attribute (most reliable)
        // Fallback locator → font-poppins class link inside the grid
        WebElement assessmentCard;
        try {
            assessmentCard = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[@data-tour='assignment-card'])[1]")));
        } catch (Exception e) {
            System.out.println("data-tour locator failed, trying font-poppins fallback...");
            assessmentCard = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[contains(@class,'font-poppins') " +
                                    "and contains(@href,'/admin/reports/assignments/Better')])[1]")));
        }

        String cardTitle = assessmentCard.getText().trim();
        System.out.println("Assessment card found : " + cardTitle);
        jsClick(assessmentCard);
        System.out.println("Assessment card clicked");

        // ── Step 5 : Wait for the report table to fully load ──────────────────
        // Image 5: <table class="w-full text-left text-sm text-gray-500 ...">
        //            <thead> ... </thead>
        //            <tbody class="group/body divide-y divide-gray-200">
        //              <tr data-testid="table-row-element" ...>
        //                <td ...>Test Student</td>
        //                ...
        //              </tr>
        //            </tbody>
        //          </table>
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table[contains(@class,'w-full')]//tbody")));
        Thread.sleep(3000);
        System.out.println("Report table loaded. Current URL : " + driver.getCurrentUrl());
    }

    // ── Soft Delete Validation ─────────────────────────────────────────────────
    // Expected after soft-delete:
    //   Assignment Report table  →  student NOT present in NAME column  (PASS if absent)
    //   View Not Attended table  →  student IS  present in NAME column  (PASS if present)
    // ──────────────────────────────────────────────────────────────────────────
    public void validateSoftDelete(String email) throws Exception {

        System.out.println("\n---------- SOFT DELETE VALIDATION : " + email + " ----------");

        openBulkAssessmentCard();

        // ── Check 1 : Assignment Report table ─────────────────────────────────
        // Image 5: tbody rows with <td> cells — NAME column contains student name/email
        List<WebElement> nameRowsInReport = driver.findElements(
                By.xpath("//table[contains(@class,'w-full')]" +
                        "//tbody//td[contains(normalize-space(),'" + email + "')]"));

        System.out.println("Rows found in Assignment Report : " + nameRowsInReport.size());

        if (nameRowsInReport.isEmpty()) {
            System.out.println("PASS : Student NOT present in Assignment Report → " + email);
        } else {
            System.out.println("FAIL : Student still present in Assignment Report → " + email);
            for (WebElement row : nameRowsInReport) {
                System.out.println("  Found cell text : " + row.getText().trim());
            }
        }

        // ── Check 2 : Click "View Not Attended" button ────────────────────────
        // Image 5: <button ...>View Not Attended</button>  (top-right of report page)
        WebElement notAttendedBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(normalize-space(),'View Not Attended')]")));
        jsClick(notAttendedBtn);
        System.out.println("View Not Attended clicked");
        Thread.sleep(3000);

        // ── Check 2 : Not Attended table ──────────────────────────────────────
        List<WebElement> nameRowsInNotAttended = driver.findElements(
                By.xpath("//table[contains(@class,'w-full')]" +
                        "//tbody//td[contains(normalize-space(),'" + email + "')]"));

        System.out.println("Rows found in Not Attended : " + nameRowsInNotAttended.size());

        if (nameRowsInNotAttended.isEmpty()) {
            System.out.println("FAIL : Student NOT found in Not Attended list → " + email);
        } else {
            System.out.println("PASS : Student present in Not Attended list → " + email);
            for (WebElement row : nameRowsInNotAttended) {
                System.out.println("  Found cell text : " + row.getText().trim());
            }
        }

        System.out.println("------------------------------------------------------------\n");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}