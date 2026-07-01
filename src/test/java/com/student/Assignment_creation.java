package com.student;

import io.github.bonigarcia.wdm.WebDriverManager;
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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Assignment_creation {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions actions;

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        js = (JavascriptExecutor) driver;
        actions = new Actions(driver);
    }

    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    private void clickThreeDots() throws Exception {
        System.out.println("Attempting to click 3-dot menu...");

        WebElement btn = null;
        String[] btnXpaths = {
            "//div[contains(@class,'absolute')]//button[@aria-haspopup='menu']",
            "//button[@aria-haspopup='menu']",
            "//button[contains(@class,'flex') and @aria-haspopup='menu']",
            "//button[@type='button' and @aria-haspopup='menu']"
        };

        for (String xp : btnXpaths) {
            try {
                List<WebElement> btns = driver.findElements(By.xpath(xp));
                if (!btns.isEmpty()) {
                    btn = btns.get(0);
                    System.out.println("3-dot button found via: " + xp);
                    break;
                }
            } catch (Exception e) {
                System.out.println("XPath failed: " + xp);
            }
        }

        if (btn == null) throw new Exception("3-dot button not found!");

        js.executeScript(
            "arguments[0].scrollIntoView({block:'center', inline:'center'});", btn);
        Thread.sleep(800);

        js.executeScript(
            "arguments[0].style.zIndex = '99999';" +
            "arguments[0].style.position = 'relative';", btn);
        Thread.sleep(300);

        // Strategy 1: Full mouse event dispatch
        js.executeScript(
            "var el = arguments[0];" +
            "['mouseenter','mouseover','mousedown','mouseup','click'].forEach(function(evt){" +
            "  el.dispatchEvent(new MouseEvent(evt,{bubbles:true,cancelable:true}));" +
            "});", btn);
        Thread.sleep(1500);

        List<WebElement> dd = driver.findElements(
            By.xpath("//div[@data-testid='flowbite-dropdown']"));
        if (!dd.isEmpty() && dd.get(0).isDisplayed()) {
            System.out.println("Dropdown opened via dispatchEvent!");
            return;
        }


        actions.moveToElement(btn, 0, 0)
               .pause(Duration.ofMillis(300))
               .click()
               .pause(Duration.ofMillis(500))
               .perform();
        Thread.sleep(1500);

        dd = driver.findElements(
            By.xpath("//div[@data-testid='flowbite-dropdown']"));
        if (!dd.isEmpty() && dd.get(0).isDisplayed()) {
            System.out.println("Dropdown opened via Actions!");
            return;
        }

        js.executeScript("arguments[0].click();", btn);
        Thread.sleep(1500);
        System.out.println("3-dot clicked via direct JS");
    }

    @Test
    public void createAssignment() throws Exception {

        driver.get("https://beta.campushub.thebettertomorrow.in/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-email")))
                .sendKeys("beta-admin@bt.in");
        driver.findElement(By.id("login-password")).sendKeys("Test@123");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Sign in')]"))).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
        System.out.println("Logged in successfully");

        WebElement assignmentMenu = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'assignment-folders')]")));
        jsClick(assignmentMenu);

        WebElement createAssignmentBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'create-assignment')]")));
        jsClick(createAssignmentBtn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")))
                .sendKeys("bulk delete Assignment");
        driver.findElement(By.id("short_description"))
                .sendKeys("bulk delete Assignmenttesting");
        driver.findElement(By.id("description"))
                .sendKeys("Automation assignment creation testing");

        WebElement hours = driver.findElement(By.id("duration_hours"));
        hours.clear();
        hours.sendKeys("1");

        WebElement languageDropdown = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='languages']")));
        jsClick(languageDropdown);
        Thread.sleep(2000);

        WebElement languageInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[contains(@id,'react-select') and @type='text']")));
        languageInput.sendKeys("Java");
        Thread.sleep(1000);
        languageInput.sendKeys(Keys.ENTER);
        Thread.sleep(500);

        WebElement startDate = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[contains(@placeholder,'Select start')]")));
        jsClick(startDate);
        Thread.sleep(2000);

        WebElement todayDate = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class,'react-datepicker__day--today') " +
                                "and not(contains(@class,'outside'))]")));
        jsClick(todayDate);
        Thread.sleep(1500);

        List<WebElement> startTimeList = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//li[contains(@class,'react-datepicker__time-list-item')]")));
        for (WebElement time : startTimeList) {
            if (time.getText().trim().equals("12:00 AM")) {
                jsClick(time);
                break;
            }
        }
        Thread.sleep(1000);

        WebElement dueDateInput = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[contains(@placeholder,'Select due date')]")));
        jsClick(dueDateInput);
        Thread.sleep(2000);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String tomorrowDayStr = String.valueOf(tomorrow.getDayOfMonth());

        List<WebElement> allDayCells = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//div[contains(@class,'react-datepicker__day') " +
                                "and not(contains(@class,'react-datepicker__day--outside-month')) " +
                                "and not(contains(@class,'disabled'))]")));

        WebElement tomorrowCell = null;
        for (WebElement cell : allDayCells) {
            if (cell.getText().trim().equals(tomorrowDayStr)) {
                tomorrowCell = cell;
            }
        }
        if (tomorrowCell != null) jsClick(tomorrowCell);
        else throw new Exception("Tomorrow's date cell not found!");
        Thread.sleep(1500);

        List<WebElement> dueTimeList = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//li[contains(@class,'react-datepicker__time-list-item')]")));
        for (WebElement time : dueTimeList) {
            if (time.getText().trim().equals("11:45 PM")) {
                jsClick(time);
                break;
            }
        }
        Thread.sleep(1000);

        // Domain
        WebElement domain = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("domain")));
        domain.sendKeys("array");
        Thread.sleep(500);

        WebElement categoryContainer = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='assignment_category']")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", categoryContainer);
        Thread.sleep(1000);
        actions.moveToElement(categoryContainer).click().perform();
        Thread.sleep(1500);

        try {
            WebElement catInput = driver.findElement(
                    By.xpath("//input[@id='assignment_category']"));
            catInput.sendKeys(Keys.SPACE);
            Thread.sleep(500);
            catInput.sendKeys(Keys.ARROW_DOWN);
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("catInput: " + e.getMessage());
        }

        List<WebElement> categoryOptions = new ArrayList<>();
        categoryOptions = driver.findElements(By.xpath("//div[@role='option']"));
        if (categoryOptions.isEmpty())
            categoryOptions = driver.findElements(
                    By.xpath("//div[contains(@id,'-option-')]"));
        if (categoryOptions.isEmpty())
            categoryOptions = driver.findElements(
                    By.xpath("//div[contains(@class,'MenuList')]/div"));
        if (categoryOptions.isEmpty())
            categoryOptions = driver.findElements(
                    By.xpath("//div[contains(@class,'menu')]//div[contains(@class,'option')]"));

        boolean found = false;
        for (WebElement option : categoryOptions) {
            if (option.getText().trim().equals("Automation Testing")) {
                js.executeScript(
                        "arguments[0].scrollIntoView({block:'nearest'});", option);
                Thread.sleep(300);
                js.executeScript("arguments[0].click();", option);
                found = true;
                System.out.println("Automation Testing selected!");
                break;
            }
        }
        if (!found) throw new Exception("'Automation Testing' category not found!");
        Thread.sleep(1500);

        WebElement createBtn = null;
        String[] createBtnXpaths = {
            "//button[@type='submit' and contains(.,'Create')]",
            "//button[contains(.,'Create Assignment')]",
            "//button[contains(.,'Create') and not(contains(.,'Cancel'))]",
            "//button[normalize-space()='Create']"
        };
        for (String xp : createBtnXpaths) {
            try {
                createBtn = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath(xp)));
                System.out.println("Create button found: " + xp);
                break;
            } catch (Exception e) {
                System.out.println("Not found: " + xp);
            }
        }
        if (createBtn == null) throw new Exception("Create button not found!");
        jsClick(createBtn);
        Thread.sleep(4000);
        System.out.println("Assignment created successfully");

        driver.get(
            "https://beta.campushub.thebettertomorrow.in/admin/assignment-folders");
        wait.until(ExpectedConditions.urlContains("assignment-folders"));
        Thread.sleep(3000);

      
        WebElement automationTestingCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class,'cursor-pointer') " +
                                "and .//*[contains(text(),'Automation Testing')]]")));
        jsClick(automationTestingCard);
        Thread.sleep(2000);

   
        WebElement bulkAssignmentCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h5[contains(text(),'bulk delete Assignment')] | " +
                                "//a[.//*[contains(text(),'bulk delete Assignment')]]")));
        jsClick(bulkAssignmentCard);
        Thread.sleep(2000);

   
        clickThreeDots();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-testid='flowbite-dropdown']")));
        Thread.sleep(500);
        System.out.println("3-dot dropdown is open");

        WebElement importQuestion = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@data-testid='flowbite-dropdown']" +
                                "//*[contains(text(),'Import Question')]")));
        actions.moveToElement(importQuestion).click().perform();
        Thread.sleep(3000);

        wait.until(ExpectedConditions.urlContains("import-question"));
        System.out.println("Import page loaded: " + driver.getCurrentUrl());
        Thread.sleep(1500);

  
        WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@placeholder='Search...']")));
        searchBox.clear();
        Thread.sleep(300);
        searchBox.sendKeys("Largest Element in an Array");
        Thread.sleep(500);

        WebElement searchBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(.,'Search')]")));
        jsClick(searchBtn);
        Thread.sleep(4000);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'font-bold')]")));
        Thread.sleep(1000);

        
        List<WebElement> allCards = driver.findElements(
                By.xpath("//div[contains(@class,'rounded-md') " +
                        "and .//input[@type='checkbox']]"));
        System.out.println("Total question cards: " + allCards.size());

        WebElement targetCheckbox = null;
        for (WebElement card : allCards) {
            try {
                String cardTitle = card.findElement(
                        By.xpath(".//div[contains(@class,'font-bold')]"))
                        .getText().trim();
                System.out.println("Card: " + cardTitle);
                if (cardTitle.equalsIgnoreCase("Largest Element in an Array")) {
                    targetCheckbox = card.findElement(
                            By.xpath(".//input[@type='checkbox']"));
                    System.out.println("MATCH: " + cardTitle);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Card read error: " + e.getMessage());
            }
        }

        if (targetCheckbox == null)
            throw new Exception(
                    "Checkbox for 'Largest Element in an Array' not found!");

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});", targetCheckbox);
        Thread.sleep(2000);

        boolean alreadyChecked = (Boolean) js.executeScript(
                "return arguments[0].checked;", targetCheckbox);
        System.out.println("Already checked: " + alreadyChecked);

        if (!alreadyChecked) {
            try {
                actions.moveToElement(targetCheckbox)
                       .pause(Duration.ofMillis(500))
                       .click()
                       .pause(Duration.ofMillis(500))
                       .perform();
                System.out.println("Checkbox clicked via Actions");
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", targetCheckbox);
                System.out.println("Checkbox clicked via JS");
            }
            Thread.sleep(2000);

            boolean nowChecked = (Boolean) js.executeScript(
                    "return arguments[0].checked;", targetCheckbox);
            System.out.println("Checked after click: " + nowChecked);

            if (!nowChecked) {
                js.executeScript(
                    "var cb = arguments[0];" +
                    "cb.checked = true;" +
                    "cb.dispatchEvent(new Event('change', {bubbles:true}));" +
                    "cb.dispatchEvent(new MouseEvent('click', {bubbles:true}));",
                    targetCheckbox);
                Thread.sleep(2000);
                System.out.println("Final checked state: " +
                    js.executeScript("return arguments[0].checked;", targetCheckbox));
            }
        }

        Thread.sleep(2000);

        WebElement saveBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(.,'Save')]")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", saveBtn);
        Thread.sleep(1000);
        jsClick(saveBtn);
        Thread.sleep(4000);
        System.out.println("Question imported and saved!");

        WebElement manageClusterMenu = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'clusters/campus-list')]")));
        jsClick(manageClusterMenu);
        wait.until(ExpectedConditions.urlContains("clusters/campus-list"));
        Thread.sleep(2000);
        System.out.println("Manage Cluster page loaded");

        WebElement betterTomorrowCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class,'cursor-pointer') " +
                                "and .//h5[normalize-space()='Better Tomorrow']]")));
        jsClick(betterTomorrowCard);
        wait.until(ExpectedConditions.urlContains("Better%20Tomorrow"));
        Thread.sleep(2000);
        System.out.println("Better Tomorrow page loaded");

       
        WebElement btClusterCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class,'shadow-md') and " +
                                ".//h3[normalize-space()='BT']]")));
        jsClick(btClusterCard);
        Thread.sleep(2000);
        System.out.println("BT cluster page loaded: " + driver.getCurrentUrl());

        WebElement manageAssignmentTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[contains(text(),'Manage Assignment')] " +
                                "or contains(text(),'Manage Assignment')]")));
        jsClick(manageAssignmentTab);
        Thread.sleep(2000);
        System.out.println("Manage Assignment tab clicked");

       
        WebElement addAssignmentBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[contains(text(),'Add Assignment')] " +
                                "or contains(text(),'Add Assignment')]")));
        jsClick(addAssignmentBtn);
        Thread.sleep(2000);
        System.out.println("Add Assignment modal opened");

    
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Add Assignment')] | " +
                        "//h3[contains(text(),'Add Assignment')]")));
        Thread.sleep(1000);
        System.out.println("Add Assignment modal visible");

     
        WebElement selectAssignmentDropdown = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class,'css-') and " +
                                ".//div[contains(text(),'Select an assignment')]]")));
        jsClick(selectAssignmentDropdown);
        Thread.sleep(1500);

   
        WebElement assignmentSearchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'css-b62m3t-container')]" +
                                "//input[@type='text'] | " +
                                "//input[contains(@id,'react-select')]")));
        assignmentSearchInput.sendKeys("bulk delete Assignment");
        Thread.sleep(2000);
        System.out.println("Typed assignment name");

        
        WebElement assignmentOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@role='option' and " +
                                "contains(text(),'bulk delete Assignment')] | " +
                                "//div[contains(@class,'option') and " +
                                "contains(text(),'bulk delete Assignment')]")));
        jsClick(assignmentOption);
        Thread.sleep(1500);
        System.out.println("Assignment selected from dropdown");

       
        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@type='submit' and " +
                                "(.//span[normalize-space()='Add'] " +
                                "or normalize-space()='Add')]")));
        jsClick(addBtn);
        Thread.sleep(3000);
        System.out.println("Add button clicked!");

        System.out.println("ALL STEPS COMPLETED SUCCESSFULLY!");
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }
}