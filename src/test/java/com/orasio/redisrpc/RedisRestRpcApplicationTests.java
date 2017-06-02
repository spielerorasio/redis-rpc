package com.orasio.redisrpc;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisRestRpcApplicationTests {
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testRPCSingleResponse() {
		String body = this.restTemplate.getForObject("/testRPC", String.class);
		assertThat(body).contains("RPCResponse");
	}

	@Test(timeout = 4000) //milliseconds
	public void testRPCConcurrencyResponse() {
		IntStream.rangeClosed(1,10).parallel().forEach((i)-> {
//			System.out.println(i+" start "+System.currentTimeMillis());
			String body = this.restTemplate.getForObject("/testRPC", String.class);
			assertThat(body).contains("RPCResponse");
//			System.out.println(i+" end "+System.currentTimeMillis());
		});
	}


	@Ignore
	@Test
	public void testRPCPerformance() {
		int EXECUTIONS = 100000;
		long startTime = System.currentTimeMillis();
		IntStream.rangeClosed(1,EXECUTIONS).parallel().forEach((i)-> {
//			System.out.println(i+" start "+System.currentTimeMillis());
			String body = this.restTemplate.getForObject("/testRPC", String.class);
			assertThat(body).contains("RPCResponse");
//			System.out.println(i+" end "+System.currentTimeMillis());
		});
		double executionTime = (System.currentTimeMillis() - startTime )/1000;
		System.out.println("****************************************************************************************************************" );
		System.out.println("execution count rest call to /testRPC = " + EXECUTIONS);
		System.out.println("executionTime in seconds = " + executionTime);
		int average = (int)   (EXECUTIONS / executionTime);
		System.out.println("average per second = " + average);
		System.out.println("****************************************************************************************************************" );

	}
}
