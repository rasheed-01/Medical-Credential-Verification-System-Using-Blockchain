package Menus;

import java.util.Scanner;

public abstract class AccessMenu {
    protected Scanner scanner;

    public AccessMenu() {
        this.scanner = new Scanner(System.in);
    }

    public abstract void displayMenu();
    public abstract void handleUserInput();
}

