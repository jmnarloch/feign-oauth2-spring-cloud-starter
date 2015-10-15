/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.cloud.feign;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import io.jmnarloch.spring.cloud.feign.app.client.AuthenticationClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the usage of OAuth2 Feign interceptor.
 *
 * @author Jakub Narloch
 */
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
@SpringApplicationConfiguration(classes = {OAuth2HttpClientTest.Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class OAuth2HttpClientTest {

    @Autowired
    private AuthenticationClient authenticationClient;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Test
    public void authenticate() {

        // given
        final String token = UUID.randomUUID().toString();
        oauth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken(token));

        // when
        final ResponseEntity response = authenticationClient.authenticate();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Authorization"));
        assertEquals(token, response.getHeaders().getFirst("Authorization").split(" ")[1]);
    }

    @EnableFeignClients
    @RibbonClient(name = "local", configuration = LocalRibbonClientConfiguration.class)
    @ComponentScan("io.jmnarloch.spring.cloud.feign.app")
    @EnableAutoConfiguration
    @Configuration
    public static class Application {

        @Bean
        public OAuth2ClientContext auth2ClientContext() {
            return new DefaultOAuth2ClientContext();
        }
    }

    @Configuration
    static class LocalRibbonClientConfiguration {

        @Value("${local.server.port}")
        private int port = 0;

        @Bean
        public ILoadBalancer ribbonLoadBalancer() {
            BaseLoadBalancer balancer = new BaseLoadBalancer();
            balancer.setServersList(Collections.singletonList(new Server("localhost", this.port)));
            return balancer;
        }
    }
}