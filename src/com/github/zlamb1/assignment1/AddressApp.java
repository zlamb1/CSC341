package com.github.zlamb1.assignment1;

import com.github.zlamb1.assignment1.view.IAddressView;
import com.github.zlamb1.io.DeserializeException;
import com.github.zlamb1.io.MapSerializer;

import java.util.Arrays;
import java.util.Map;

public class AddressApp {
    private final IAddressView view;
    private final MapSerializer<String, AddressInfo> serializer;
    private Map<String, AddressInfo> addressInfoBook = null;

    public AddressApp(IAddressView view, MapSerializer<String, AddressInfo> serializer) throws DeserializeException {
        this.view = view;
        this.serializer = serializer;
        addressInfoBook = serializer.deserialize();
    }

    public void run() {
        for (;;) {
            int choice = view.promptChoice(Arrays.asList("Add Address", "Search Name", "View Addresses", "Quit"));
            switch (choice) {
                case 0:
                    addAddress();
                    serializer.serialize(addressInfoBook);
                    break;
                case 1:
                    searchName();
                    break;
                case 2:
                    view.displayAddressInfo(addressInfoBook.values());
                    break;
                case 3:
                    view.disposeView();
                    return;
                default:
                    throw new AssertionError("Invalid Choice");
            }
        }
    }

    public void addAddress() {
        AddressInfo info = new AddressInfo(
            view.promptString("Enter First Name"),
            view.promptString("Enter Last Initial"),
            view.promptString("Enter Address")
        );
        addressInfoBook.put(info.getFullName(), info);
    }

    public void searchName() {
        String name = view.promptString("Enter Name");

        if (!name.contains(" ")) {
            view.displayInfo("Expected First Name and Last Initial");
            return;
        }

        if (!addressInfoBook.containsKey(name)) {
            view.displayInfo("No Address Found");
            return;
        }

        view.displayInfo(addressInfoBook.get(name).getAddress());
    }
}
