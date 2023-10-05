//package com.nitish.rabbitmq;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import okhttp3.mockwebserver.RecordedRequest;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import reactor.core.publisher.Mono;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration
//@TestPropertySource(locations = {"classpath:application.properties"})
//public class IntegrationTest {
//
//	ObjectMapper objectMapper = new ObjectMapper();
//
//	public MockWebServer mockBackEnd;
//
//	@BeforeEach
//	void setUp() throws IOException {
//		mockBackEnd = new MockWebServer();
//		mockBackEnd.start();
//	}
//
//	@AfterEach
//	void tearDown() throws IOException {
//		mockBackEnd.shutdown();
//	}
//
//	@Test
//	public void checkWebClientGetConnection() throws JsonProcessingException, URISyntaxException {
//		Map<String, Object> resp = new HashMap<>();
//		resp.put("a", 100);
//		resp.put("b", true);
//		resp.put("c", "Hello");
//
//		mockBackEnd.enqueue(new MockResponse().setResponseCode(200)
//				.setBody(objectMapper.writeValueAsString(resp))
//				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//		);
//
//		URI uri = new URI("http", null, "localhost", mockBackEnd.getPort(),
//				"", null, null);
//
//		HttpHeaders httpHeaders = new HttpHeaders();
//
//		Mono<DefaultWebResultResponse<Map>> mono =  MonoCallback.createMonoOutput(HttpMethod.GET, uri, httpHeaders, new HashMap<>());
//		DefaultWebResultResponse<Map> defaultWebResultResponse = mono.block();
//		System.out.println(objectMapper.writeValueAsString(defaultWebResultResponse.getData()));
//		assertEquals(defaultWebResultResponse.getData(), resp);
//	}
//
//	@Test
//	public void checkWebClientPostConnection() throws JsonProcessingException, URISyntaxException, InterruptedException {
//
//
//		mockBackEnd.enqueue(new MockResponse().setResponseCode(200)
//				.setBody((recordedRequest, bufferedSource, bufferedSink) -> bufferedSink.write(bufferedSource.readByteArray()))
//				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//		);
//
//
//		URI uri = new URI("http", null, "localhost", mockBackEnd.getPort(),
//				"", null, null);
//
//		HttpHeaders httpHeaders = new HttpHeaders();
//
//		Map<String, Object> resp = new HashMap<>();
//		resp.put("a", 100);
//		resp.put("b", true);
//		resp.put("c", "Hello");
//
//		Mono<DefaultWebResultResponse<Map>> mono =  MonoCallback.createMonoOutput(HttpMethod.POST, uri, httpHeaders, resp);
//		DefaultWebResultResponse<Map> defaultWebResultResponse = mono.block();
//		RecordedRequest request = mockBackEnd.takeRequest();
//		assertEquals(objectMapper.writeValueAsString(resp), request.getBody().readUtf8());
//	}
//
//	@Test
//	public void checkWebClientPutConnection() throws JsonProcessingException, URISyntaxException, InterruptedException {
//
//
//		mockBackEnd.enqueue(new MockResponse().setResponseCode(200)
//				.setBody((recordedRequest, bufferedSource, bufferedSink) -> bufferedSink.write(bufferedSource.readByteArray()))
//				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//		);
//
//
//		URI uri = new URI("http", null, "localhost", mockBackEnd.getPort(),
//				"", null, null);
//
//		HttpHeaders httpHeaders = new HttpHeaders();
//
//		Map<String, Object> resp = new HashMap<>();
//		resp.put("a", 100);
//		resp.put("b", true);
//		resp.put("c", "Hello");
//
//		Mono<DefaultWebResultResponse<Map>> mono =  MonoCallback.createMonoOutput(HttpMethod.PUT, uri, httpHeaders, resp);
//		DefaultWebResultResponse<Map> defaultWebResultResponse = mono.block();
//		RecordedRequest request = mockBackEnd.takeRequest();
//		assertEquals(objectMapper.writeValueAsString(resp), request.getBody().readUtf8());
//	}
//
//}
