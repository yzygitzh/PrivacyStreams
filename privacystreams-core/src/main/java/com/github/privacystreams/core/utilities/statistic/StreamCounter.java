package com.github.privacystreams.core.utilities.statistic;

import java.util.ArrayList;
import java.util.List;

import com.github.privacystreams.core.Item;

/**
 * Created by yuanchun on 23/12/2016.
 * A function that counts the items in the stream
 */
final class StreamCounter extends StreamStatistic<Integer> {
    @Override
    public Integer calculate(List<Item> items) {
        return items.size();
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = new ArrayList<>();
        return parameters;
    }
}