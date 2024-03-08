package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.Optional;
import java.time.LocalDate;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserInfo;
import ch.uzh.ifi.hase.soprafs24.entity.Login;
import ch.uzh.ifi.hase.soprafs24.entity.UserName;
import ch.uzh.ifi.hase.soprafs24.entity.UserId;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }



  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@PathVariable("id") Long userId, @RequestBody UserInfo userInfo){

      UserInfo userInf = new UserInfo();
      userInf.setId(userId);
      userInf.setUsername(userInfo.getUsername());
      userInf.setBirthday(userInfo.getBirthday());


      userService.updateUser(userInf);

  }

  @PatchMapping("/users/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Login loginInfo(@RequestBody UserPostDTO userPostDTO){
      Login loginInf = new Login(false,false,null, null);
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

      try{
          User userInDb = userService.getUser(userInput.getUsername());
          if(userInDb.getUsername() != null){
              loginInf.setUsernameExists(true);
              if(userInput.getPassword().equals(userInDb.getPassword())){
                  userService.changeStatus(userInDb.getId(),UserStatus.ONLINE);
                  loginInf.setPasswordCorrect(true);
                  loginInf.setId(userInDb.getId());
                  loginInf.setToken(userInDb.getToken());
              }
          }
      }catch(Exception e){

      }


      return loginInf;
  }

  @PatchMapping("/users/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logout(@RequestBody UserId id){
        userService.changeStatus(id.getId(),UserStatus.OFFLINE);
    }

  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public User getUserWithId(@RequestParam("id") Long userId){
      return userService.getUserbyId(userId);
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }
}
