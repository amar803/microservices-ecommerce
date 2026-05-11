package com.ecommerce.apigateway.filter;

import com.ecommerce.common.tracing.CorrelationIdConstants;
import com.ecommerce.common.tracing.CorrelationIdGenerator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String incoming = exchange.getRequest().getHeaders().getFirst(CorrelationIdConstants.HEADER_NAME);
        String correlationId = (incoming == null || incoming.isBlank())
                ? CorrelationIdGenerator.generate()
                : incoming.trim();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(CorrelationIdConstants.HEADER_NAME, correlationId)
                        .build())
                .build();

        mutatedExchange.getResponse().getHeaders().set(CorrelationIdConstants.HEADER_NAME, correlationId);

        return chain.filter(mutatedExchange)
                .then(Mono.fromRunnable(() ->
                        mutatedExchange.getResponse().getHeaders().set(CorrelationIdConstants.HEADER_NAME, correlationId)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

