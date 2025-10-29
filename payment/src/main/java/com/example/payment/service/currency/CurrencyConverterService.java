package com.example.payment.service.currency;

import com.example.payment.contorller.dto.rate.LatestRatesResponse;
import com.example.payment.model.enums.CurrencyType;
import com.example.payment.service.client.CurrencyFreaksService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
    private final CurrencyFreaksService currencyFreaksService;

    public CurrencyConvertResult convert(CurrencyType from, CurrencyType to, BigDecimal amount) {
        LatestRatesResponse ratesResp = currencyFreaksService.getLatest(from, to);

        String toCode = to.getName();
        Double rateValue = ratesResp.getRates().get(toCode);

        BigDecimal rate = BigDecimal.valueOf(rateValue);


        Map<String, Double> rates = ratesResp.getRates();
        BigDecimal rateFrom = BigDecimal.valueOf(rates.get(from.getName()));
        BigDecimal rateTo = BigDecimal.valueOf(rates.get(to.getName()));

        // 2) Кросс-курс: (amount / rateFrom) * rateTo
        var convertedAmount = amount
                .divide(rateFrom, 8, RoundingMode.HALF_UP)
                .multiply(rateTo)
                .setScale(2, RoundingMode.HALF_UP);

        return new CurrencyConvertResult(rate, convertedAmount);
    }

    @Data
    @AllArgsConstructor
    public class CurrencyConvertResult {
        private BigDecimal exchangeRate;
        private BigDecimal amount;
    }

}
