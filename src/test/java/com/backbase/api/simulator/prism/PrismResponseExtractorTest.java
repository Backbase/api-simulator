package com.backbase.api.simulator.prism;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.backbase.api.simulator.prism.response.CopyResponseHandler;
import com.backbase.api.simulator.prism.response.ResponseHandler;
import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;

class PrismResponseExtractorTest {

    @Test
    void copyResponseHandlerShouldBeLast() {
        ResponseHandler lastResponseHandler = Iterables.getLast(PrismResponseExtractor.RESPONSE_HANDLERS);
        assertTrue(lastResponseHandler instanceof CopyResponseHandler);
    }
}
