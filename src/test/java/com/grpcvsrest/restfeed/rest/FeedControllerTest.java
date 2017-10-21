package com.grpcvsrest.restfeed.rest;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc;
import com.grpcvsrest.restfeed.service.AggregatedContentResponse;
import com.grpcvsrest.restfeed.service.AggregatorServiceSelector;
import com.grpcvsrest.restfeed.service.GrpcAggregatorService;
import com.grpcvsrest.restfeed.service.RestAggregatorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(FeedController.class)
public class FeedControllerTest {

    private static final int CONTENT_ID = 1;
    private static final AggregatedContentResponse CONTENT_RESPONSE =
            new AggregatedContentResponse(CONTENT_ID, "Pokemon", "Pikachu", "/content/2");
    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "aggr-service")
    private AggregatorServiceSelector aggregatorService;
    @MockBean(name = "rest-aggr-service")
    private RestAggregatorService restAggregatorService;
    @MockBean(name = "grpc-aggr-service")
    private GrpcAggregatorService grpcAggregatorService;
    @MockBean
    private AggregationStreamingServiceGrpc.AggregationStreamingServiceStub grpcStub;

    @Test
    public void testFeed() throws Exception {
        // given
        itemFetched();

        // when
        mockMvc.perform(get("/content/1")
        ) // then
                .andExpect(status().is(200))
                .andExpect(content().json(expectedContent()));
    }

    @Test
    public void testFeed404() throws Exception {
        // given
        itemMissing();

        // when
        mockMvc.perform(get("/content/1")
        ) // then
                .andExpect(status().is(404));
    }

    private String expectedContent() throws IOException {
        return Resources.toString(
                Resources.getResource("content.json"),
                Charsets.UTF_8);
    }

    private void itemFetched() {
        when(aggregatorService.fetch(eq(CONTENT_ID), isNull())).thenReturn(CONTENT_RESPONSE);
    }

    private void itemMissing() {
        when(aggregatorService.fetch(eq(CONTENT_ID), isNull())).thenReturn(null);
    }

}