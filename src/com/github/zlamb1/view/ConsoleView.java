package com.github.zlamb1.view;

import java.util.List;
import java.util.Scanner;

public class ConsoleView extends AbstractView {
    protected static final Scanner scanner = new Scanner(System.in);

    @Override
    public void displayInfo(String info) {
        System.out.println(info);
        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    @Override
    public String promptString(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    @Override
    public int promptChoice(List<String> choices) {
        int choice;

        do {
            for (int i = 0; i < choices.size(); i++) {
                System.out.print((i + 1) + ". ");
                System.out.print(choices.get(i));
                System.out.println();
            }
            choice = scanner.nextInt();
            scanner.nextLine();
        } while (choice < 1 || choice > choices.size());

        return choice - 1;
    }
}
