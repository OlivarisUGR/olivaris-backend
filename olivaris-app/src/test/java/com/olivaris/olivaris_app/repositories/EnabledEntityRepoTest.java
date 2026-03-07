package com.olivaris.olivaris_app.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.olivaris.olivaris_app.controllers.EntityController;
import com.olivaris.olivaris_app.dto.CreateEntity;
import com.olivaris.olivaris_app.services.EntityService;
import com.olivaris.olivaris_app.services.JwtService;

import tools.jackson.databind.ObjectMapper;

// This only can load the basic Spring web layer (controllers, basic Spring MVC configuration  and Spring Security), 
// but it does not load the service, repository, jpa, database
@WebMvcTest(EntityController.class)
// Annotation that creates the MockMvc object to simulate HTTP requests
@AutoConfigureMockMvc
public class EnabledEntityRepoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // The entityController has the entityService as attribute, so it need this service to works,
    // so it is necessary to create an mock service and use it as a bean component
    @MockitoBean
    private EntityService entityServ;

    // jwtService and userRep are necessary as a mockbean because Spring tries to create JwtAuthFilter
    // and this filter needs JwtService and UserRepository
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    // Annotation that simulates an authenticated user on application with the farmer role
    @Test
    @WithMockUser(roles = "FARMER")
    public void farmerUserCannotCreateEntity() throws Exception {
        CreateEntity entity = new CreateEntity(
            "los olivos",
            "A1234567B",
            null,
            "losolivos@gmail.com"
        );
        
        String json = objectMapper.writeValueAsString(entity);  

        // Do a request to create the specific entity
        mockMvc.perform(MockMvcRequestBuilders.post("/api/entity/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isForbidden());     
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminCanCreateEntity() throws Exception  {

    }
}
