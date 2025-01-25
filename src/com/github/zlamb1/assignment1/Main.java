package com.github.zlamb1.assignment1;

import com.github.zlamb1.assignment1.view.ConsoleAddressView;
import com.github.zlamb1.assignment1.view.IAddressView;
import com.github.zlamb1.assignment1.view.SwingAddressView;
import com.github.zlamb1.io.DeserializeException;
import com.github.zlamb1.view.IView;

import java.util.List;

public class Main {
    public final static String OUTPUT_FILE = "address.csv";

    public static void main(String[] args) {
        IView view = new SwingAddressView();
        int choice = view.promptChoice(List.of("GUI", "Console"));
        view.disposeView();

        IAddressView addressView = switch (choice) {
            case 0 -> new SwingAddressView();
            case 1 -> new ConsoleAddressView();
            default -> throw new AssertionError("Invalid Choice");
        };

        try {
            AddressApp app = new AddressApp(addressView, new AddressInfoSerializer(OUTPUT_FILE));
            app.run();
        } catch (DeserializeException exc) {
            System.out.println("Failed to start application.");
            System.out.println(exc.getMessage());
        }
    }
}
