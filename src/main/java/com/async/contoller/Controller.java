package com.async.contoller;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.async.service.AsyncTest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

	@Autowired
	AsyncTest asyncTest;
	
//	Test to know if controller is enganged then will it take another request and process?
//	Ans=> Yes, it will take new request and process it
	@GetMapping("test/{i}")
	public void multipleRequestTest(@PathVariable int i) {
		while(true) {
			System.out.println("P => " + i);
		}
	}

	@GetMapping("users")
	public ResponseEntity<String> users() {
		return new ResponseEntity<String>(asyncTest.m1(), HttpStatus.ACCEPTED);
	}

	@GetMapping("users/obo")
	public ResponseEntity<List<String>> usersM2() {
		return new ResponseEntity<>(asyncTest.m2(), HttpStatus.ACCEPTED);
	}
	
//	@GetMapping("users/obo/web")
//	public ResponseEntity<List<String>> usersM2_v2() {
//		return new ResponseEntity<>(asyncTest.m2_v2(), HttpStatus.ACCEPTED);
//	}

	@GetMapping("users/async")
	public ResponseEntity<List<String>> usersV3() throws InterruptedException, ExecutionException {
		return new ResponseEntity<>(asyncTest.makeMultipleRequests(), HttpStatus.ACCEPTED);
	}

	@GetMapping(value = "users/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> fluxCalls() {
		return asyncTest.asyncFlux();
	}

//	return a message to the client every second, and the client can subscribe to this stream and receive the messages in real-time.
	@GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> stream() {
		return Flux.interval(Duration.ofSeconds(1)).map(i -> "message - " + i);
	}

}
