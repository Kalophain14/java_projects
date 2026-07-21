package com.bankcore.api.model.enums;

/*
Enums are like a list of accounts available on the api,
that can be read by the Bank core API

- It helps you to catch typos,
- Won't compile if the item isn't on the list
- Java is able to pick its meaning
- You can only pick a valid option, otherwise won't run
- When ENUM is called Java will creates CLASS with an OBJECT for each item on the list
 */
public enum AccountType {
    SAVINGS,
    CHEQUE,
    MINOR
}