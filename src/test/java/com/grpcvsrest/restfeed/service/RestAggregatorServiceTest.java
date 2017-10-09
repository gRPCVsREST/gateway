package com.grpcvsrest.restfeed.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

public class RestAggregatorServiceTest {

    private static final int CONTENT_ID = 1;
    private static final AggregatedContentResponse CONTENT =
            new AggregatedContentResponse(CONTENT_ID, "Pokemon", "Pikachu", "/content/2");

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private RestTemplate restTemplate;

    private RestAggregatorService service;

    @Before
    public void setup() {
        service = new RestAggregatorService("http://fake.url", restTemplate);
    }

    @Test
    public void testFetch() {
        // given
        contentFound();

        // when
        AggregatedContentResponse result = service.fetch(eq(CONTENT_ID), isNull());

        // then
        assertThat(result).isEqualTo(CONTENT);
    }


    @Test
    public void testFetch_404() {
        // given
        contentNotFound();

        // when
        AggregatedContentResponse result = service.fetch(eq(CONTENT_ID), isNull());

        // then
        assertThat(result).isNull();
    }

    private void contentNotFound() {
        when(restTemplate.getForEntity("http://fake.url/content/{id}", AggregatedContentResponse.class, CONTENT_ID))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    private void contentFound() {
        when(restTemplate.getForEntity("http://fake.url/content/{id}", AggregatedContentResponse.class, CONTENT_ID))
                .thenReturn(new ResponseEntity<>(CONTENT, HttpStatus.OK));
    }
}