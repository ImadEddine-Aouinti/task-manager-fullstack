package com.taskmanager.backend.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TaskFormModal {

    private static final By TITLE_INPUT = By.cssSelector("[data-testid='task-title-input']");
    private static final By DESCRIPTION_INPUT = By.cssSelector("[data-testid='task-description-input']");
    private static final By STATUS_SELECT = By.cssSelector("[data-testid='task-status-select']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='task-submit-button']");
    private static final By TITLE_ERROR = By.cssSelector("[data-testid='task-title-error']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public TaskFormModal(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE_INPUT));
    }

    public TaskFormModal fillTitle(String title) {
        WebElement input = driver.findElement(TITLE_INPUT);
        input.clear();
        input.sendKeys(title);
        return this;
    }

    public TaskFormModal fillDescription(String description) {
        WebElement input = driver.findElement(DESCRIPTION_INPUT);
        input.clear();
        input.sendKeys(description);
        return this;
    }

    public TaskFormModal selectStatus(String statusLabel) {
        new Select(driver.findElement(STATUS_SELECT)).selectByVisibleText(statusLabel);
        return this;
    }

    public TaskFormModal submit() {
        driver.findElement(SUBMIT_BUTTON).click();
        return this;
    }

    public TaskBoardPage waitUntilClosed() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(TITLE_INPUT));
        return new TaskBoardPage(driver, wait);
    }

    public boolean hasTitleError() {
        return !driver.findElements(TITLE_ERROR).isEmpty();
    }
}
