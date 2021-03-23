package org.prices;

import java.time.Instant;
import java.util.Objects;

public class Price {

    private Long id; //если только у нас нет константы для обозначения новых записей null в id хорошо описывает новую запись еще не добавленную в БД
    private String productCode;
    private int number;
    private int department;
    private Instant validFrom;
    private Instant invalidSince;
    private Long amount; //в случае проблем с этим значением мне кажется иметь null предпочтительнее чем 0, null очевидно ошибочная цена, а 0 похож на реальную

    public Price() {

    }

    public Price(Long id, String productCode, int number, int department, Instant validFrom, Instant invalidSince, Long amount) {
        this.id = id;
        this.productCode = productCode;
        this.number = number;
        this.department = department;
        this.validFrom = validFrom;
        this.invalidSince = invalidSince;
        this.amount = amount;
    }

    public Price(PriceKey key, PriceSlice slice) {
        this.id = slice.getId();
        this.productCode = key.getProductCode();
        this.number = key.getNumber();
        this.department = key.getDepartment();
        this.validFrom = slice.getValidFrom();
        this.invalidSince = slice.getInvalidSince();
        this.amount = slice.getAmount();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getInvalidSince() {
        return invalidSince;
    }

    public void setInvalidSince(Instant invalidSince) {
        this.invalidSince = invalidSince;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public PriceSlice getPriceSlice() {
        return new PriceSlice(id, validFrom, invalidSince, amount);
    }

    public PriceKey getPriceKey() {
        return new PriceKey(productCode, number, department);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return number == price.number && department == price.department && Objects.equals(id, price.id) && productCode.equals(price.productCode) && validFrom.equals(price.validFrom) && invalidSince.equals(price.invalidSince) && amount.equals(price.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productCode, number, department, validFrom, invalidSince, amount);
    }
}
