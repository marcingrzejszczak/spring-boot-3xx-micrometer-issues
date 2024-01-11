package com.example.demo;

import io.micrometer.context.ContextRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.contextpropagation.ObservationAwareBaggageThreadLocalAccessor;
import io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import reactor.core.publisher.*;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(DemoApplication.class, args);
    }
    
    @Autowired
    Tracer tracer;

    @Autowired
    ObservationRegistry observationRegistry;

    @PostConstruct
    void setup() {
        ContextRegistry.getInstance().registerThreadLocalAccessor(new ObservationAwareSpanThreadLocalAccessor(observationRegistry, tracer));
        ContextRegistry.getInstance().registerThreadLocalAccessor(new ObservationAwareBaggageThreadLocalAccessor(observationRegistry, tracer));
    }
}

