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
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class AggregatorServiceTest {

    private static final int CONTENT_ID = 1;
    private static final AggregatedContentResponse CONTENT =
            new AggregatedContentResponse(CONTENT_ID, "Pokemon", "Pikachu");

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private RestTemplate restTemplate;

    private AggregatorService service;

    @Before
    public void setup() {
        service = new AggregatorService("http://fake.url", restTemplate);
    }

    @Test
    public void testFetch() {
        // given
        contentFound();

        // when
        AggregatedContentResponse result = service.fetch(CONTENT_ID);

        // then
        assertThat(result).isEqualTo(CONTENT);
    }


    @Test
    public void testFetch_404() {
        // given
        contentNotFound();

        // when
        AggregatedContentResponse result = service.fetch(CONTENT_ID);

        // then
        assertThat(result).isNull();
    }

    private void contentNotFound() {
        when(restTemplate.getForEntity("http://fake.url/content/{id}", AggregatedContentResponse.class, CONTENT_ID))
                .thenReturn(new ResponseEntity<>(CONTENT, HttpStatus.NOT_FOUND));
    }

    private void contentFound() {
        when(restTemplate.getForEntity("http://fake.url/content/{id}", AggregatedContentResponse.class, CONTENT_ID))
                .thenReturn(new ResponseEntity<>(CONTENT, HttpStatus.OK));
    }
}