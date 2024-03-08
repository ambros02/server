package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserInfo;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void putUser_withInvalidId_returnsNOTFOUND() throws Exception{
      UserInfo userInf = new UserInfo();
      userInf.setUsername("baba");
      userInf.setBirthday("123");

      doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"the user with the id has not been found")).when(userService).updateUser(Mockito.any());

      MockHttpServletRequestBuilder putRequest = put("/users/4")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userInf));

      mockMvc.perform(putRequest)
              .andExpect(status().is(404));

  }


  @Test
  public void putUser_withValidId_returnsNOCONTENT() throws Exception{

      User user = new User();
      user.setId(1L);
      user.setUsername("baba");
      user.setPassword("123");
      user.setToken("1");


      UserInfo userInf = new UserInfo();
      userInf.setUsername("baba");
      userInf.setBirthday("123");

      doNothing().when(userService).updateUser(Mockito.any());

      MockHttpServletRequestBuilder putRequest = put("/users/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userInf));

      mockMvc.perform(putRequest)
              .andExpect(status().is(204));

  }

  @Test
  public void getUser_withInvalid_throwNotFound() throws Exception{

      Long id = 2L;

      given(userService.getUserbyId(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"the user with the id was not found"));

      MockHttpServletRequestBuilder getRequest = get("/users/2")
              .contentType(MediaType.APPLICATION_JSON)
              .param("id",id.toString());

      mockMvc.perform(getRequest)
              .andExpect(status().is(404));

  }

  @Test
  public void getUser_withValidId_returnUser() throws Exception{
      //given
      User user = new User();
      user.setId(1L);
      user.setUsername("baba");
      user.setPassword("123");

      given(userService.getUserbyId(Mockito.any())).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get("/users/1")
              .contentType(MediaType.APPLICATION_JSON)
              .param("id",user.getId().toString());

      //when-then + validate
      mockMvc.perform(getRequest)
              .andExpect(status().is(200))
              .andExpect(jsonPath("$.id",is(Integer.parseInt(user.getId().toString()))))
              .andExpect(jsonPath("$.username",is(user.getUsername())))
              .andExpect(jsonPath("$.password",is(user.getPassword())));

  }

  @Test
  public void createUser_alreadyExists_throwErrorConflict() throws Exception {
      //given

      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("firstname@lastname");
      userPostDTO.setPassword("password");

      given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT,"the username already exists"));

      //whenthen + validate
      MockHttpServletRequestBuilder postRequest = post("/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      mockMvc.perform(postRequest).andExpect(status().is(409));

  }


  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setPassword("password");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}