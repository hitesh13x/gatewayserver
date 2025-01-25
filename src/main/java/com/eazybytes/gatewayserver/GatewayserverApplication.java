package com.eazybytes.gatewayserver;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserverApplication {

	private static final String REMAINING_PATH = "/${segment}";
	private static final String XRESPONSETIME = "X-RESPONSE-TIME";

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator eazyBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/eazybank/accounts/**")
						.filters(f -> f.rewritePath("/eazybank/accounts/(?<segment>.*)", REMAINING_PATH)
								.addResponseHeader(XRESPONSETIME, LocalDateTime.now().toString())
								.circuitBreaker(crct -> crct.setName("ACCOUNTS-CB")
										.setFallbackUri("forward:/contactSupport"))
										)
						.uri("lb://ACCOUNTS"))
				.route(p -> p
						.path("/eazybank/loans/**")
						.filters(f -> f.rewritePath("/eazybank/loans/(?<segment>.*)", REMAINING_PATH)
								.addResponseHeader(XRESPONSETIME, LocalDateTime.now().toString()))
						.uri("lb://LOANS"))
				.route(p -> p
						.path("/eazybank/cards/**")
						.filters(f -> f.rewritePath("/eazybank/cards/(?<segment>.*)", REMAINING_PATH)
								.addResponseHeader(XRESPONSETIME, LocalDateTime.now().toString()))
						.uri("lb://CARDS"))
				.build();
	}

}
