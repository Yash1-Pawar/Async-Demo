package com.async.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AsyncTest {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	WebClient webClient;
	
	static String url = "https://jsonplaceholder.typicode.com/comments";
	
	public String m1() {
		System.out.println(new Date());
		ResponseEntity<String> reponse =  restTemplate.getForEntity(AsyncTest.url, String.class);
		System.out.println(new Date());
		return reponse.getBody();
	}
	
	
	public List<String> m2() {
		System.out.println(new Date());
		List<String> responselList = new ArrayList<>();
		for(int i=1;i<=100;i++) {
	    	String urlString = AsyncTest.url + "/" + i;
//	    	System.out.println(urlString);
			String reponse = this.getCommentById(urlString);
			System.out.println(reponse);
			responselList.add(reponse);
		}
		System.out.println(new Date());
		return responselList;
	}
	
//	public List<String> m2_v2() {
//		System.out.println(new Date());
//		List<String> responselList = new ArrayList<>();
//		
//		for(int i=1;i<=100;i++) {
//	    	String urlString = AsyncTest.url + "/" + i;
////	    	System.out.println(urlString);
//			this.getCommentByIdWeb(urlString).doOnSuccess(e-> responselList.add(e));
////			System.out.println(reponse);
//		}
//		System.out.println(new Date());
//		return responselList;
//	}
	
	public List<String> makeMultipleRequests() throws InterruptedException, ExecutionException {
		System.out.println(new Date());
		List<String> responselList = new ArrayList<>();
	    List<CompletableFuture<String>> futures = new ArrayList<>();
	    for (int i=1;i<=100;i++) {
	    	String urlString = AsyncTest.url + "/" + i;
//	    	System.out.println(urlString);
	        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> this.getCommentById(urlString));
	        futures.add(future);
	    }
	    // Wait for all requests to complete
	    CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
	    for (CompletableFuture<String> future : futures) {
	        String response = future.get();
	        responselList.add(response);
	    }
		System.out.println(new Date());
		return responselList;
	}
	
	
	
	public String getCommentByIdWebClient(String url) {
		 webClient.get().uri(url).retrieve().bodyToMono(String.class).subscribe(
				 response -> {
					System.out.println("next: " +response);
				 },
				 error -> {
					 System.out.println("error: " + error.getMessage());
				 },
				 () -> System.out.println("Completed" + new Date())
				 );
		 return "";
	}
	
	public String getCommentByIdWebClientSyso(String url) {
		webClient.get().uri(url).retrieve().bodyToMono(String.class)
		.doOnNext(e -> {
			System.out.println("next: " +e);
		})
		.doOnError(e->System.out.println("error: " + e.getMessage()))
		.subscribe();
		return "";
	}
	
	public String getCommentById(String url) {
		return restTemplate.getForObject(url, String.class);
	}
	
	public Mono<String> getCommentByIdWeb(String url) {
		return webClient.get().uri(url).retrieve().bodyToMono(String.class);
	}
	
	public Flux<String> asyncFlux() {
		return Flux.range(1, 100) 
				.map((e) -> {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e1) {
//						e1.printStackTrace();
//					}
					return this.getCommentById(AsyncTest.url + "/" + e);
				});
	}
	
	
	
	
	
	

}
