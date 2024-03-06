package ch.uzh.ifi.hase.soprafs24.service;

import java.time.LocalDate;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean changeStatus(Long id, UserStatus status){
      try{
          User user = userRepository.findById(id).get();
          user.setStatus(status);
          userRepository.save(user);
          userRepository.flush();
          return true;
      } catch(Error err){
          return false;
      }

  }

  public User getUserbyId(Long id){
      Optional<User> myUser = userRepository.findById(id);
      if(myUser.isPresent()){
        return myUser.get();
      } else {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,(String.format("user with id %s was not found",id)));
      }
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDateTime.now());
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

    public void updateUser(Long userId, String username, String birthday){
        User updateValues = this.getUserbyId(userId);
        User existing = this.getUser(username);
        if(existing != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("this username already exists"));
        }else{
            try{
                LocalDate birth = LocalDate.parse(birthday);
                updateValues.setUsername(username);
                updateValues.setBirthday(birth);
                userRepository.save(updateValues);
            }catch(Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("the birthday is in a bad format"));
            }
        }
    }


    public User getUser(String username){
        return userRepository.findByUsername(username);
    }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */

  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, ("The username provided is not unique. Therefore, the user could not be created!"));
    }
  }
}
