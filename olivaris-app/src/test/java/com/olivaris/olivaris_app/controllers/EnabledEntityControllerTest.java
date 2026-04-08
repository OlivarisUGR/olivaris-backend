package com.olivaris.olivaris_app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.olivaris.olivaris_app.dto.CreateEntity;
import com.olivaris.olivaris_app.dto.EntityDto;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.EntityRoleRepository;
import com.olivaris.olivaris_app.repositories.RoleRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;
import com.olivaris.olivaris_app.security.SecurityConfig;
import com.olivaris.olivaris_app.services.EntityService;
import com.olivaris.olivaris_app.services.JwtService;

import tools.jackson.databind.ObjectMapper;

// This only can load the basic Spring web layer (controllers, basic Spring MVC configuration  and Spring Security), 
// but it does not load the service, repository, jpa, database
@WebMvcTest(EntityController.class)
// Annotation that creates the MockMvc object to simulate HTTP requests
@AutoConfigureMockMvc
// Import the custom SecurityFilterChain
@Import(SecurityConfig.class)
public class EnabledEntityControllerTest {

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
    private UserRepository userRep;

    @MockitoBean
    private EntityRepository entityRep;

    @MockitoBean
    private RoleRepository roleRep;

    @MockitoBean 
    private EntityRoleRepository entityRoleRep;

    @MockitoBean
    private UserEntityRoleRepository userEntRoleRep;

    @Test
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
                    // Create an UserDetails object and injecting this authentication in SecurityContext
                    // before the request enters the JWT filter. It is better when I have a custom filter chain
                    // because the user is authenticated and added to SecurityContext only for this request;
                    // not injecting it in the global context 
                    .with(user("farmer@test.com").roles("FARMER"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isForbidden());     
    }

    @Test
    public void adminCanCreateEntity() throws Exception  {
        CreateEntity entity = new CreateEntity(
            "los olivos",
            "A1234567B",
            null,
            "losolivos@gmail.com"
        );
        
        // Mock the service response. I am only trying to execute the controller endpoint
        // and check the return value so the service will be mocked
        EntityDto mockResponse = new EntityDto(
            1L,
            "los olivos",
            "A1234567B",
            null,
            "losolivos@gmail.com",
            true,
            null
        );
        
        // This is a way to tell "when create from entityServ will be executed with any 
        // CreateEntity object, return this mock response". If the service is not mocked,
        // Spring will try to executed the real service, repository and the test will be 
        // broken probably
        when(entityServ.create(any(CreateEntity.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(mockResponse));
        
        String json = objectMapper.writeValueAsString(entity);  

        mockMvc.perform(MockMvcRequestBuilders.post("/api/entity/")
                    .with(user("admin@test.com").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isCreated());  
    }

    @Test
    public void createEntityNeedsNameNifEmail() throws Exception {
        CreateEntity entity = new CreateEntity(
            "",
            "",
            null,
            ""
        );
        
        String json = objectMapper.writeValueAsString(entity);  

        mockMvc.perform(MockMvcRequestBuilders.post("/api/entity/")
                    .with(user("admin@test.com").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                // It returns bad request because the nif, name and email request fields 
                // are necessary
                .andExpect(status().isBadRequest());  
    }
}
