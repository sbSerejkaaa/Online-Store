package com.example.payment.client;

import com.example.payment.contorller.dto.rate.LatestRatesResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(
        name = "currencyFreaks",
        url = "${currencyfreaks.api.url}"
)

public interface CurrencyFreaksClient {
    /**
     * GET /v1/latest?apikey=KEY&base={base}&symbols={symbols}
     * Получить актуальные курсы (можно ограничить symbols через запятую).
     */
    @GetMapping("/latest")
    LatestRatesResponse getLatestRates(
            @RequestParam("apikey") String apiKey,
            @RequestParam(value = "symbols", required = false) String symbols
    );

}
