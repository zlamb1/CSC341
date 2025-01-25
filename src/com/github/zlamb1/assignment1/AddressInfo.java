package com.github.zlamb1.assignment1;

public class AddressInfo {
    private final String firstName;
    private final String lastInitial;
    private final String address;

    public AddressInfo(String firstName, String lastInitial, String address) {
        this.firstName = firstName;
        this.lastInitial = lastInitial;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastInitial() {
        return lastInitial;
    }

    public String getFullName() {
        return firstName + " " + lastInitial;
    }

    public String getAddress() {
        return address;
    }

    public static AddressInfo fromCSVRow(String row) {
        String[] parts = row.split(",");
        String firstName = parts.length > 0 ? parts[0] : null;
        String lastInitial = parts.length > 1 ? parts[1] : null;
        String address = parts.length > 2 ? parts[2] : null;
        return new AddressInfo(firstName, lastInitial, address);
    }

    public String toCSVRow() {
        return firstName + "," + lastInitial + "," + address;
    }

    public String toFormattedString(int widthSpecifier) {
        return String.format("%" + widthSpecifier + "s | %" + widthSpecifier + "s | %" + widthSpecifier + "s", firstName, lastInitial, address);
    }
}
