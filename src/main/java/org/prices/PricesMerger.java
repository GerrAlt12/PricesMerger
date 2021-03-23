package org.prices;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

public class PricesMerger {

    private HashMap<PriceKey, TreeSet<PriceSlice>> temporaryStorage;

    public PricesMerger() {
        this.temporaryStorage = new HashMap<>();
    }

    public Collection<Price> merge(Collection<Price> existedPrices, Collection<Price> newPrices) throws WrongPriceException {

        for (var existedPrice: existedPrices) {
            var currentKey = existedPrice.getPriceKey();
            var existedValue = this.temporaryStorage.get(currentKey);
            if (existedValue == null) {
                addNewPriceSlice(currentKey, existedPrice);
                continue;
            }

            var priceSlice = existedPrice.getPriceSlice();

            if (!validatePriceSlice(priceSlice)) {
                throw new WrongPriceException(getPriceAsString(existedPrice));
            }
            existedValue.add(priceSlice);
        }

        for (var newPrice: newPrices) {
            var currentKey = newPrice.getPriceKey();
            var checkedPrice = this.temporaryStorage.get(currentKey);
            if (checkedPrice == null) {
                addNewPriceSlice(currentKey, newPrice);
                continue;
            }
            var priceSlice = newPrice.getPriceSlice();

            if (!validatePriceSlice(priceSlice)) {
                throw new WrongPriceException(getPriceAsString(newPrice));
            }
            mergePriceSlices(checkedPrice, priceSlice);
        }

        return createResult();
    }

    private String getPriceAsString(Price price) {
        return "{id :" + price.getId() + ", productCode :" + price.getProductCode() + ", number :" + price.getNumber() + ", validFrom :" + price.getValidFrom().atZone(ZoneOffset.UTC) + ", invalidSince :" + price.getInvalidSince().atZone(ZoneOffset.UTC) + ", amount :" + price.getAmount() + "}";
    }

    private boolean validatePriceSlice(PriceSlice priceSlice) {
        if (priceSlice.getInvalidSince().compareTo(priceSlice.getValidFrom()) < 1) { //Это важное требование, на нем строится внутренняя логика
            return false;
        }
        return true;
    }

    private void addNewPriceSlice(PriceKey currentKey, Price newPrice) throws WrongPriceException {
        var priceSlice = newPrice.getPriceSlice();

        if (!validatePriceSlice(priceSlice)) {
            throw new WrongPriceException(getPriceAsString(newPrice));
        }
        var newElement = new TreeSet<PriceSlice>();
        newElement.add(newPrice.getPriceSlice());
        this.temporaryStorage.put(currentKey, newElement);
    }

    private void mergePriceSlices(TreeSet<PriceSlice> existedPrices, PriceSlice newPrice) {

        var correctedElements = new LinkedList<PriceSlice>();

        Iterator<PriceSlice> iterator = existedPrices.iterator();

        while (iterator.hasNext()) {
            var existedPrice = iterator.next();
            if (existedPrice.getInvalidSince().compareTo(newPrice.getValidFrom()) < 1 ) { // т.к. упорядочено по убыванию invalidSince даже если что-то там еще есть уже можно спокойно класть и дальше не смотреть
                break;
            }
            if (existedPrice.getValidFrom().compareTo(newPrice.getInvalidSince()) > -1) { //окончание новой цены не позднее начала имеющейся
               continue; //с этой ценой не пересекаемся, смотрим дальше
            }
            iterator.remove(); //нужно убрать и передобавить значения, иначе может поломаться сортировка
            if (existedPrice.getValidFrom().compareTo(newPrice.getValidFrom()) < 1) { //Новая цена начинается не раньше старой - т.е. остальные элементы можно будет не смотреть (начало больше конца, упорядочено по убыванию конца, два одновременно не могут действовать)
                Long idForOlderSlice = null; //id оставляем за самым ранним слайсом, если разрезаем на несколько
                Instant oldInvalidSince = existedPrice.getInvalidSince();
                if (existedPrice.getValidFrom().compareTo(newPrice.getValidFrom()) < 0) { //часть времени действует старая цена, ДО того как начинает действовать новая
                    oldInvalidSince = existedPrice.getInvalidSince();
                    if (existedPrice.getAmount().equals(newPrice.getAmount())) { //цена одинаковая, соединяем
                        newPrice.setValidFrom(existedPrice.getValidFrom());
                    } else {
                        existedPrice.setInvalidSince(newPrice.getValidFrom()); //цена разная - вычитаем
                        if (existedPrice.getValidFrom().compareTo(existedPrice.getInvalidSince()) < 0) { //добавляем обратно только если начало все еще раньше конца
                            correctedElements.add(existedPrice);
                        }
                    }
                } else {
                    idForOlderSlice = existedPrice.getId();
                }
                if (oldInvalidSince.compareTo(newPrice.getInvalidSince()) > 0) {//часть времени действует старая цена, ПОСЛЕ того как начинает действовать новая
                    if (existedPrice.getAmount().equals(newPrice.getAmount())) { //цена одинаковая, соединяем
                        newPrice.setInvalidSince(oldInvalidSince);
                    } else {
                        correctedElements.add(new PriceSlice(idForOlderSlice, newPrice.getInvalidSince(), oldInvalidSince, existedPrice.getAmount())); //цена разная - вычитаем
                    }
                }
                //т.к. начало цены больше конца, упорядочено по убыванию окончания, и одновременно два слайса не могут быть действующими значит дальше если что-то и есть то оно точно не пересекается
                break;
            }
            //часть времени действует старая цена после новой - отрезаем или соединяем
            if (existedPrice.getAmount().equals(newPrice.getAmount())) {
                newPrice.setInvalidSince(existedPrice.getInvalidSince());
            } else {
                existedPrice.setValidFrom(newPrice.getInvalidSince());
                if (existedPrice.getValidFrom().compareTo(existedPrice.getInvalidSince()) < 0) { //добавляем обратно только если начало все еще раньше конца
                    correctedElements.add(existedPrice);
                }
            }
        }

        correctedElements.add(newPrice);
        existedPrices.addAll(correctedElements);
    }

    private Collection<Price> createResult() {
        final var result = new LinkedList<Price>();

        this.temporaryStorage.forEach((priceKey, priceSlices) -> {
            for(var priceSlice: priceSlices) {
                result.add(new Price(priceKey, priceSlice));
            }
        });

        return result;
    }
}
