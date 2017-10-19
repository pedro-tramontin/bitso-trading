package br.com.pedront.bitsotrading.core.client.api.bitso;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pedront.bitsotrading.core.client.api.bitso.dto.AvailableBooksResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TickerResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeResponseDTO;
import feign.Headers;

@FeignClient(name = "bitso-public-rest-api", url = "http://api.bitso.com/", configuration = FeignConfiguration.class)
@Headers("User-agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
public interface BitsoApiIntegration {

    @RequestMapping(value = "/v3/available_books/", method = RequestMethod.GET)
    AvailableBooksResponseDTO getAvailableBooks();

    @RequestMapping(value = "/v3/ticker/", method = RequestMethod.GET)
    TickerResponseDTO getTicker(@RequestParam("book") String book);

    @RequestMapping(value = "/v3/order_book/", method = RequestMethod.GET)
    OrderResponseDTO getOrder(@RequestParam(value = "book") String book);

    @RequestMapping(value = "/v3/trades/", method = RequestMethod.GET)
    TradeResponseDTO getTrade(@RequestParam(value = "book") String book);
}