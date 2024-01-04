package com.example.demo.reactor;

import io.micrometer.tracing.contextpropagation.BaggageThreadLocalAccessor;
import io.micrometer.tracing.contextpropagation.BaggageToPropagate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import reactor.util.context.Context;

public class BaggageToPropagateUtils {

    private static Context append(Context context, Map<String, String> baggage) {
        BaggageToPropagate baggageToPropagate = context.getOrDefault(BaggageThreadLocalAccessor.KEY, null);
        Map<String, String> mergedBaggage = new HashMap<>(baggage);
        if (baggageToPropagate != null) {
            mergedBaggage.putAll(baggageToPropagate.getBaggage());
        }
        BaggageToPropagate merged = new BaggageToPropagate(mergedBaggage);
        return context.put(BaggageThreadLocalAccessor.KEY, merged);
    }

    private static Context append(Context context, String key, String value) {
        BaggageToPropagate baggageToPropagate = context.getOrDefault(BaggageThreadLocalAccessor.KEY, null);
        Map<String, String> mergedBaggage = new HashMap<>();
        mergedBaggage.put(key, value);
        if (baggageToPropagate != null) {
            mergedBaggage.putAll(baggageToPropagate.getBaggage());
        }
        BaggageToPropagate merged = new BaggageToPropagate(mergedBaggage);
        return context.put(BaggageThreadLocalAccessor.KEY, merged);
    }

    public static Function<Context, Context> append(String key, String value) {
        return context -> append(context, key, value);
    }

    public static Function<Context, Context> append(Map<String, String> baggage) {
        return context -> append(context, baggage);
    }
}