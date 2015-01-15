package com.springmvcut.common.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.springmvcut.service.TodoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml", "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml","file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@WebAppConfiguration
public class ErrorControllerTest {
	private MockMvc mockMvc;
	 
    @Autowired
    private TodoService todoServiceMock;

 
    //Add WebApplicationContext field here
    @Autowired
    private WebApplicationContext webApplicationContext;
 
    //The setUp() method is omitted.
    @Before
    public void setUp() {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.

        Mockito.reset(todoServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    public void show404Page() throws Exception {
        mockMvc.perform(get("/error/404"))
                .andExpect(status().isOk())
                .andExpect(view().name(ErrorController.VIEW_NOT_FOUND))
                .andExpect(forwardedUrl("/WEB-INF/views/error/404.jsp"));
    }

    @Test
    public void showInternalServerErrorPage() throws Exception {
        mockMvc.perform(get("/error/error"))
                .andExpect(status().isOk())
                .andExpect(view().name(ErrorController.VIEW_INTERNAL_SERVER_ERROR))
                .andExpect(forwardedUrl("/WEB-INF/views/error/error.jsp"));
    }
}
