package com.taskmanager.backend.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TaskBoardPage {
    private static final By NEW_TASK_BUTTON = By.cssSelector("[data-testid='new-task-button']");
    private static final By TASK_CARDS = By.cssSelector("[data-testid^='task-card-']");
    private static final By ERROR_BANNER = By.cssSelector("[data-testid='error-banner']");
    private static final By CONFIRM_DELETE_BUTTON = By.cssSelector("[data-testid='confirm-delete-button']");
    private static final By EDIT_BUTTON = By.cssSelector("[data-testid='task-edit-button']");
    private static final By DELETE_BUTTON = By.cssSelector("[data-testid='task-delete-button']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public TaskBoardPage (WebDriver driver ,WebDriverWait wait){
        this.driver=driver;
        this.wait=wait;
    }

    public TaskBoardPage open(String baseUrl) {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(NEW_TASK_BUTTON));
        return this;
    }
}
