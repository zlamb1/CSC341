package com.github.zlamb1.assignment1.view;

import com.github.zlamb1.assignment1.AddressInfo;
import com.github.zlamb1.view.ConsoleView;

import java.util.Collection;
import java.util.OptionalInt;

public class ConsoleAddressView extends ConsoleView implements IAddressView {
    @Override
    public void displayAddressInfo(Collection<AddressInfo> addressInfoColl) {
        OptionalInt widthSpecifier = addressInfoColl
            .stream()
            .mapToInt(a -> Math.max(Math.max(a.getFirstName().length(), a.getLastInitial().length()), a.getAddress().length()))
            .max();

        for (AddressInfo addressInfo : addressInfoColl) {
            System.out.println(addressInfo.toFormattedString(-widthSpecifier.orElse(1)));
        }
    }
}
