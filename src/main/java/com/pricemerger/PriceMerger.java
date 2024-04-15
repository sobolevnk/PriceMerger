package com.pricemerger;

import com.pricemerger.model.Period;
import com.pricemerger.model.Price;
import com.pricemerger.model.PriceKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceMerger {

    /*
     * Основной метод
     */
    public List<Price> mergePrices(List<Price> currentPrices, List<Price> newPrices){
        //Преобразовывание исходных коллекций
        Map<PriceKey, Map<Period, Long>> currentPricesMap = splitByPriceKey(currentPrices);
        Map<PriceKey, Map<Period, Long>> newPricesMap = splitByPriceKey(newPrices);

        for(PriceKey priceKey : newPricesMap.keySet()){
            if(!currentPricesMap.containsKey(priceKey)){
                //добавление новых записей с уникальым ключом
                currentPricesMap.put(priceKey, newPricesMap.get(priceKey));
            }
            else{
                //проверка на наличие пересечений в датах между записями
                currentPricesMap.put(priceKey, resolveConflictPrices(currentPricesMap.get(priceKey), newPricesMap.get(priceKey)));
            }
        }

        //преобразование коллекции в исходный вид
        List<Price> mergedPrices = new ArrayList<>();
        for(Map.Entry<PriceKey, Map<Period, Long>> priceEntry : currentPricesMap.entrySet()){
            for(Map.Entry<Period, Long> periodEntry : priceEntry.getValue().entrySet()){
                mergedPrices.add(new Price(priceEntry.getKey(), periodEntry.getKey(), periodEntry.getValue()));
            }
        }

        return mergedPrices;
    }

    /*
     * Формирование колекции уникальных ключей и вложенного списка записей для каждого ключа
     */
    private Map<PriceKey, Map<Period, Long>> splitByPriceKey(List<Price> prices){
        Map<PriceKey, Map<Period, Long>> pricesMap = new HashMap<>();

        for (Price price : prices) {
            PriceKey key = new PriceKey(price.getProductCode(), price.getNumber(), price.getDepart());
            Period period = new Period(price.getBegin(), price.getEnd());

            if (!pricesMap.containsKey(key)) {
                pricesMap.put(key, new HashMap<Period, Long>());
            }

            pricesMap.get(key).put(period, price.getValue());
        }

        return pricesMap;
    }

    /*
     * Проверка и устранение пересечений в датах для существующей и новой записей с одинковым ключом
     */
    private Map<Period, Long> resolveConflictPrices(Map<Period, Long> currentPrices, Map<Period, Long> newPrices){
        Map<Period, Long> mergedPrices = new HashMap<>();
        for(Map.Entry<Period, Long> newPrice : newPrices.entrySet()){
            for(Map.Entry<Period, Long> currentPrice : currentPrices.entrySet()){
                if(newPrice.getKey().containsPeriod(currentPrice.getKey())){
                    mergedPrices.putIfAbsent(newPrice.getKey(), newPrice.getValue());
                    continue;
                }
                switch (currentPrice.getKey().hasConflictWith(newPrice.getKey())){
                    case 0:
                        mergedPrices.put(currentPrice.getKey(), currentPrice.getValue());
                        break;
                    case 1:
                        if(currentPrice.getValue().equals(newPrice.getValue())) {
                            mergedPrices.put(new Period(currentPrice.getKey().getBegin(), newPrice.getKey().getEnd()), currentPrice.getValue());
                        }
                        else{
                            mergedPrices.put(new Period(currentPrice.getKey().getBegin(), newPrice.getKey().getBegin()), currentPrice.getValue());
                        }
                        break;
                    case 2:
                        if(currentPrice.getValue().equals(newPrice.getValue())) {
                            mergedPrices.put(new Period(newPrice.getKey().getBegin(), currentPrice.getKey().getEnd()), currentPrice.getValue());
                        }
                        else {
                            mergedPrices.put(new Period(newPrice.getKey().getEnd(), currentPrice.getKey().getEnd()), currentPrice.getValue());
                        }
                        break;
                    case 3:
                        if(currentPrice.getValue().equals(newPrice.getValue())) {
                            mergedPrices.put(currentPrice.getKey(), currentPrice.getValue());
                        }
                        else{
                            mergedPrices.put(new Period(currentPrice.getKey().getBegin(), newPrice.getKey().getBegin()), currentPrice.getValue());
                            mergedPrices.put(new Period(newPrice.getKey().getEnd(), currentPrice.getKey().getEnd()), currentPrice.getValue());
                        }
                        break;
                }
                mergedPrices.putIfAbsent(newPrice.getKey(), newPrice.getValue());
            }
        }

        return mergedPrices;
    }
}