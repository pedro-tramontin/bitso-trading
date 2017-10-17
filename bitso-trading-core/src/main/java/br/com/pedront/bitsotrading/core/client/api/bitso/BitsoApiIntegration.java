package br.com.pedront.bitsotrading.core.client.api.bitso;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.pedront.bitsotrading.core.client.api.bitso.dto.AvailableBooksDTO;

@FeignClient(name = "bitso-public-rest-api", url = "https://api.bitso.com/")
public interface BitsoApiIntegration {

    @RequestMapping(value = "/v3/available_books/", method = RequestMethod.GET)
    AvailableBooksDTO getAvailableBooks();
}
