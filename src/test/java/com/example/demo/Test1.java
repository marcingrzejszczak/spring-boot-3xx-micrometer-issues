package com.example.demo;

import io.micrometer.tracing.contextpropagation.BaggageThreadLocalAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.*;
import org.springframework.boot.test.autoconfigure.web.reactive.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureObservability
@AutoConfigureWebTestClient(timeout = "10m")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS, classes = Test1Configuration.class)
@ExtendWith(OutputCaptureExtension.class)
public class Test1 {
    public static String contextDataValue = "value1";
    private final String contextDataNameInMdcLogPattern = "\\[contextDataName=" + contextDataValue + "\\]";
    private final String contextDataNameInPlainLogPattern = "contextDataName: " + contextDataValue;

    @Autowired
    private WebTestClient testClient;

    @Test
    public void shouldReturn200OkAndLogMessageWithContextDataInMdcContext(CapturedOutput output) {
        testClient.get()
                .uri("/helloWorld")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
        assertThat(output).containsPattern(contextDataNameInMdcLogPattern);
    }

    @Test
    public void shouldReturn200OkAndLogMessageWithContextDataInPlain(CapturedOutput output) {
        testClient.get()
                .uri("/helloWorld")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
        assertThat(output).containsPattern(contextDataNameInPlainLogPattern);
    }

    @Test
    public void regexShouldMatchContextDataNameInMdc() {
        String sampleOutput = "[occurredAt: 2023-12-29 15:39:54,776] [traceId=658eda3acf8710f4c1d38348b33ee35b] [span=c1d38348b33ee35b] [contextDataName=" + contextDataValue + "] severity=INFO   [thread=parallel-2] logger=HelloWorldHandler - Should be logged with the value in the MDC context\n";
        assertThat(sampleOutput).containsPattern(contextDataNameInMdcLogPattern);
    }

    @Test
    public void regexShouldMatchContextDataNameInPlain() {
        String sampleOutput = "[occurredAt: 2023-12-29 15:39:54,776] [traceId=658eda3acf8710f4c1d38348b33ee35b] [span=c1d38348b33ee35b] [contextDataName=" + contextDataValue + "] severity=INFO   [thread=parallel-2] logger=HelloWorldHandler - Should be logged with a value logged in plain contextDataName: " + contextDataValue + "\n";
        assertThat(sampleOutput).containsPattern(contextDataNameInPlainLogPattern);
    }
}

