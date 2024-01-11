package com.example.demo;

import com.example.demo.handlers.*;
import io.micrometer.tracing.*;
import io.micrometer.tracing.contextpropagation.reactor.ReactorBaggage;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.util.context.Context;

public class ContextEnhancingWebFilter implements WebFilter {
    private Tracer tracer;
    private ContextData contextData;

    public ContextEnhancingWebFilter(Tracer tracer, ContextData contextData) {
        this.tracer = tracer;
        this.contextData = contextData;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .transformDeferred(mono -> Mono.just(contextData)
                        .flatMap(data -> {
                            Span currentSpan = tracer.currentSpan();
                            if (currentSpan != null) {
                                currentSpan.tag(data.getName(), data.getValue());
                            }
                          System.out.println("@@@@ CONTEXT WRITE [" + data.getName() + ", " + data.getValue() + "]");
                              return mono.contextWrite(
                                  ReactorBaggage.append(data.getName(), data.getValue()));
                        }))
                .contextWrite(context -> setContextValue(contextData, context));
    }

    private Context setContextValue(ContextData contextData, Context context) {
        return context.put(contextData.getName(), contextData.getValue());
    }
}

