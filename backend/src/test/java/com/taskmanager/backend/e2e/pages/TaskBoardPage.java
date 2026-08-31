package com.taskmanager.backend.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.NoSuchElementException;

public class TaskBoardPage {

    private static final By NEW_TASK_BUTTON = By.cssSelector("[data-testid='new-task-button']");
    private static final By TASK_CARDS = By.cssSelector("[data-testid^='task-card-']");
    private static final By ERROR_BANNER = By.cssSelector("[data-testid='error-banner']");
    private static final By CONFIRM_DELETE_BUTTON = By.cssSelector("[data-testid='confirm-delete-button']");
    private static final By EDIT_BUTTON = By.cssSelector("[data-testid='task-edit-button']");
    private static final By DELETE_BUTTON = By.cssSelector("[data-testid='task-delete-button']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public TaskBoardPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public TaskBoardPage open(String baseUrl) {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(NEW_TASK_BUTTON));
        return this;
    }

    public TaskFormModal clickNewTask() {
        wait.until(ExpectedConditions.elementToBeClickable(NEW_TASK_BUTTON)).click();
        return new TaskFormModal(driver, wait);
    }

    public TaskBoardPage filterByStatus(String statusKeyOrAll) {
        By tab = By.cssSelector("[data-testid='status-filter-" + statusKeyOrAll + "']");
        wait.until(ExpectedConditions.elementToBeClickable(tab)).click();
        return this;
    }

    public List<WebElement> taskCards() {
        return driver.findElements(TASK_CARDS);
    }

    public boolean hasTaskWithTitle(String title) {
        return taskCards().stream().anyMatch(card -> card.getText().contains(title));
    }

    private WebElement findTaskCardByTitle(String title) {
        return taskCards().stream()
                .filter(card -> card.getText().contains(title))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("Aucune carte de tâche trouvée avec le titre : " + title));
    }

    public TaskFormModal editTask(String title) {
        findTaskCardByTitle(title).findElement(EDIT_BUTTON).click();
        return new TaskFormModal(driver, wait);
    }

    public TaskBoardPage deleteTask(String title) {
        findTaskCardByTitle(title).findElement(DELETE_BUTTON).click();
        wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_DELETE_BUTTON)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(CONFIRM_DELETE_BUTTON));
        return this;
    }

    public boolean isErrorBannerVisible() {
        return !driver.findElements(ERROR_BANNER).isEmpty();
    }
}
