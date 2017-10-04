package nl.programon.ias.microservice.domain;

import java.time.LocalDateTime;

public class Transaction {

    private final String id;
    private final String accountNumber;
    private final String description;
    private final String field1;
    private final LocalDateTime field2;
    private final String field3;
    private final String field4;
    private final String field5;
    private final String field6;
    private final String field7;
    private final String field8;
    private final String field9;
    private final String field10;
    private final String field11;
    private final String field12;


    public Transaction(String id, String accountNumber, String description, String field1, LocalDateTime field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.description = description;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
        this.field7 = field7;
        this.field8 = field8;
        this.field9 = field9;
        this.field10 = field10;
        this.field11 = field11;
        this.field12 = field12;
    }


    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getField1() {
        return field1;
    }

    public LocalDateTime getField2() {
        return field2;
    }

    public String getField3() {
        return field3;
    }

    public String getField4() {
        return field4;
    }

    public String getField5() {
        return field5;
    }

    public String getField6() {
        return field6;
    }

    public String getField7() {
        return field7;
    }

    public String getField8() {
        return field8;
    }

    public String getField9() {
        return field9;
    }

    public String getField10() {
        return field10;
    }

    public String getField11() {
        return field11;
    }

    public String getField12() {
        return field12;
    }
}
