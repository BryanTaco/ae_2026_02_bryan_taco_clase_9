package com.pucetec.events

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = [
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.us-east-1.amazonaws.com/us-east-1_yzwNALI2A"
])
class EventsApplicationTests {

    @Test
    fun contextLoads() {
    }
}
