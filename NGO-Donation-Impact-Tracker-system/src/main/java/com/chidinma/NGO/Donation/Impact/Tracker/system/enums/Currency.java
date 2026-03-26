package com.chidinma.NGO.Donation.Impact.Tracker.system.enums;

public enum Currency {
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    GBP("British Pound", "£"),
    CAD("Canadian Dollar", "C$"),
    AUD("Australian Dollar", "A$"),
    CHF("Swiss Franc", "Fr"),
    JPY("Japanese Yen", "¥"),
    CNY("Chinese Yuan", "¥"),
    INR("Indian Rupee", "₹"),
    BRL("Brazilian Real", "R$"),
    ZAR("South African Rand", "R"),
    NGN("Nigerian Naira", "₦"),
    KES("Kenyan Shilling", "KSh"),
    MXN("Mexican Peso", "$"),
    BTC("Bitcoin", "₿"),
    ETH("Ethereum", "Ξ");

    private final String name;
    private final String symbol;

    Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
