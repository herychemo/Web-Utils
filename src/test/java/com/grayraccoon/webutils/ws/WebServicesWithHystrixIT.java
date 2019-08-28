package com.grayraccoon.webutils.ws;

import com.grayraccoon.webutils.config.HystrixEnabledConfig;
import com.grayraccoon.webutils.config.WebUtilsAppContext;
import com.grayraccoon.webutils.config.beans.entities.UsersEntity;
import com.grayraccoon.webutils.config.beans.services.SimpleUserService;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.grayraccoon.webutils.config.utils.AuthUtils.getUserAccessToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Heriberto Reyes Esparza <hery.chemo@gmail.com>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebUtilsAppContext.class, HystrixEnabledConfig.class})
public class WebServicesWithHystrixIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServicesWithHystrixIT.class);


    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private Flyway flyway;

    private MockMvc mockMvc;


    @MockBean
    private SimpleUserService simpleUserService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();

        // We need to reapply migrations for tests to work
        flyway.clean();
        flyway.migrate();

        Mockito.when(simpleUserService.findByUsernameOrEmail(ArgumentMatchers.anyString()))
                .thenReturn(UsersEntity.builder()
                        .userId(UUID.randomUUID())
                        .email("cusotom@mail.com")
                        .username("admin")
                        .build());

        Mockito.when(simpleUserService.findAll())
                .thenThrow(new RuntimeException("Something happened"));
    }

    @Test
    public void contextLoads() {
        Assertions.assertThat(simpleUserService).isNotNull();
    }

    @Test
    public void findAllUsers_Success_Test() throws Exception {
        final String access_token = getUserAccessToken(mockMvc, "admin","password");

        mockMvc.perform(get("/ws/secured/users")
                .header("Authorization", "Bearer " + access_token)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
        ;
    }

}