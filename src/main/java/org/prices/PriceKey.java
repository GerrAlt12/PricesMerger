package org.prices;

import java.util.Objects;

/*
* "В один момент времени может действовать только одна цена из цен, с одинаковым номером и отделом"
* Кажется немного странным что один и тот же товар (с одним кодом товара) может иметь разную цену в зависимости от отдела,
* но сделаю как сказано
*/
public class PriceKey {
    private String productCode;
    private int number;
    private int department;

    public String getProductCode() {
        return this.productCode;
    }

    public int getNumber() {
        return this.number;
    }

    public int getDepartment() {
        return this.department;
    }

    public PriceKey(String productCode, int number, int department) {
        this.productCode = productCode;
        this.number = number;
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PriceKey priceKey = (PriceKey) o;
        return number == priceKey.number && department == priceKey.department && productCode.equals(priceKey.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, number, department);
    }
}
