package org.prices;

import java.time.Instant;

public class PriceSlice implements Comparable<PriceSlice>{
    private Long id;
    private Instant validFrom;
    private Instant invalidSince;
    private Long amount;

    public PriceSlice(Long id, Instant validFrom, Instant invalidSince, Long amount) {
        this.id = id;
        this.validFrom = validFrom;
        this.invalidSince = invalidSince;
        this.amount = amount;
    }

    public PriceSlice(Instant validFrom, Instant invalidSince, Long amount) {
        this.validFrom = validFrom;
        this.invalidSince = invalidSince;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public int compareTo(PriceSlice o) {
        return o.getInvalidSince().compareTo(invalidSince);
        /* упорядочив по времени окончания начиная с самого позднего можно немного сэкономить на сравнении */
    }
}
