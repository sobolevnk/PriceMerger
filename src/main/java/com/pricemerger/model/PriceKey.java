package com.pricemerger.model;

import java.util.Objects;

public class PriceKey {
    private String productCode;
    private int number;
    private int depart;

    public PriceKey(String productCode, int number, int depart) {
        this.productCode = productCode;
        this.number = number;
        this.depart = depart;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getNumber() {
        return number;
    }

    public int getDepart() {
        return depart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }

        PriceKey priceKey = (PriceKey) o;
        return Objects.equals(productCode, priceKey.productCode) &&
                number == priceKey.number &&
                depart == priceKey.depart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, number, depart);
    }
}
