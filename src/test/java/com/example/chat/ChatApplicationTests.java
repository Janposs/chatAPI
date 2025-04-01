package com.example.chat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class ChatApplicationTests {

	@Autowired
	private Controller controller;

	@Test
	void contextLoads() {
		assertNotNull(controller);
	}
}
