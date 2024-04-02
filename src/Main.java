import java.util.Scanner;

import Menus.MainMenu;

public class Main {
    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        Scanner scanner = new Scanner(System.in);
        mainMenu.displaymenu(scanner);
        scanner.close();
    }
}

