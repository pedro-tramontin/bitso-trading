package br.com.pedront.bitsotrading.core.client.api.bitso;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.AvailableBooksResponse;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBookResponse;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.TickerResponse;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.TradesResponse;
import feign.Headers;

/**
 * Bitso REST public API Integration
 */
// Need to inform header of a tradicional browser, otherwise CloudFlare blocks the request
@FeignClient(name = "bitso-public-rest-api", url = "http://api.bitso.com/", configuration = FeignConfiguration.class)
@Headers("User-agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
public interface BitsoApiIntegration {

    /**
     * This endpoint returns a list of existing exchange order books and their respective order placement limits.
     *
     * @return
     */
    @RequestMapping(value = "/v3/available_books/", method = RequestMethod.GET)
    AvailableBooksResponse avaiableBooks();

    /**
     * This endpoint returns trading information from the specified book.
     *
     * @param book
     *            Specifies which book to use
     * @return
     */
    @RequestMapping(value = "/v3/ticker/", method = RequestMethod.GET)
    TickerResponse ticker(@RequestParam(value = "book", required = false) String book);

    /**
     * This endpoint returns a list of all open orders in the specified book. If the aggregate parameter is set to true,
     * orders will be aggregated by price, and the response will only include the top 50 orders for each side of the
     * book. If the aggregate parameter is set to false, the response will include the full order book.
     * 
     * @param book
     *            Specifies which book to use
     * @param aggregate
     *            Specifies if orders should be aggregated by price.
     * @return
     */
    @RequestMapping(value = "/v3/order_book/", method = RequestMethod.GET)
    OrderBookResponse orderBook(@RequestParam("book") String book,
            @RequestParam(value = "aggregate", required = false, defaultValue = "true") String aggregate);

    /**
     * This endpoint returns a list of recent trades from the specified book.
     * 
     * @param book
     *            Specifies which book to use
     * @param marker
     *            Returns objects that are older or newer (depending on 'sort?) than the object with this ID
     * @param sort
     *            Specifies ordering direction of returned objects ('asc?, 'desc?)
     * @param limit
     *            Specifies number of objects to return. (Max is 100)
     * @return
     */
    @RequestMapping(value = "/v3/trades/", method = RequestMethod.GET)
    TradesResponse trades(@RequestParam("book") String book,
            @RequestParam(value = "marker", required = false) Integer marker,
            @RequestParam(value = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(value = "limit", required = false, defaultValue = "25") Integer limit);
}
