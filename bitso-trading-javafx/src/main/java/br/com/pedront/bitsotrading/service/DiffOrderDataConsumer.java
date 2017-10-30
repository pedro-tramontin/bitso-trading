package br.com.pedront.bitsotrading.service;

import br.com.pedront.bitsotrading.service.dto.DiffOrderData;

@FunctionalInterface
public interface DiffOrderDataConsumer {

    void apply(DiffOrderData data);
}
