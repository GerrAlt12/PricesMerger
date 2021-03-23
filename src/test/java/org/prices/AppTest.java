package org.prices;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;

public class AppTest {

    @Test
    public void checkAddSomeIndependentPrices() throws WrongPriceException {

        var pricesMerger = new PricesMerger();

        var price1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);
        var price2 = new Price(1l, "Продукт 2", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);

        var result = pricesMerger.merge(Arrays.asList(price1), Arrays.asList(price2));

        assertThat(result).containsOnly(price2, price1);
    }

    @Test
    public void checkNewPriceFullyReplaceOld() throws WrongPriceException {
        var price1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);
        var price2 = new Price(2l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 105l);

        var pricesMerger = new PricesMerger();

        var result = pricesMerger.merge(Arrays.asList(price1), Arrays.asList(price2));

        assertThat(result).containsOnly(price2);
    }

    @Test
    public void checkNewPriceSplitOldBy2() throws WrongPriceException {
        var price1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);
        var price2 = new Price(2l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-04T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-06T00:00:00Z").toInstant(), 105l);

        var testPrice1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-04T00:00:00Z").toInstant(), 100l);
        var testPrice2 = new Price(null, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-06T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);

        var pricesMerger = new PricesMerger();

        var result = pricesMerger.merge(Arrays.asList(price1), Arrays.asList(price2));

        assertThat(result).containsOnly(testPrice1, price2, testPrice2);
    }

    @Test
    public void checkNewPriceCutOlderHalfFromOldPrice() throws WrongPriceException {
        var price1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);
        var price2 = new Price(2l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-05T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-11T00:00:00Z").toInstant(), 105l);
        var price3 = new Price(3l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-01-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-01-10T00:00:00Z").toInstant(), 100l);
        var price4 = new Price(4l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-01-10T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-01-15T00:00:00Z").toInstant(), 100l);


        var testPrice1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-05T00:00:00Z").toInstant(), 100l);

        var pricesMerger = new PricesMerger();

        var result = pricesMerger.merge(Arrays.asList(price1, price3, price4), Arrays.asList(price2));

        assertThat(result).containsOnly(testPrice1, price2, price3, price4);
    }

    @Test(expected = WrongPriceException.class)
    public void checkPriceRangeValidation() throws WrongPriceException {
        var price1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);
        var price2 = new Price(2l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-05T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-01-11T00:00:00Z").toInstant(), 105l);

        var pricesMerger = new PricesMerger();

        var result = pricesMerger.merge(Arrays.asList(price1), Arrays.asList(price2));
    }

    @Test
    public void checkMultiplePriceNumberDepartmentNotOverlapped() throws WrongPriceException {
        var price1 = new Price(1l, "Продукт 1", 1, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);
        var price2 = new Price(2l, "Продукт 1", 2, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 101l);
        var price3 = new Price(3l, "Продукт 1", 3, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 102l);
        var price4 = new Price(4l, "Продукт 1", 4, 1, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 103l);
        var price5 = new Price(5l, "Продукт 1", 1, 2, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 100l);
        var price6 = new Price(6l, "Продукт 1", 2, 2, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 101l);
        var price7 = new Price(7l, "Продукт 1", 3, 2, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 102l);
        var price8 = new Price(8l, "Продукт 1", 4, 2, ZonedDateTime.parse("2021-02-01T00:00:00Z").toInstant(), ZonedDateTime.parse("2021-02-10T00:00:00Z").toInstant(), 103l);

        var pricesMerger = new PricesMerger();

        var result = pricesMerger.merge(Arrays.asList(price1, price3, price4), Arrays.asList(price2, price5, price6, price7, price8));

        assertThat(result).containsOnly(price1, price2, price3, price4, price5, price6, price7, price8);
    }
}
