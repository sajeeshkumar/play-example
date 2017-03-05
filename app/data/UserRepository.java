/**
 * 
 */
package data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import exception.UserNotFoundException;
import models.User;

/**
 * This class is based off data access object (DAO) pattern and abstracts out
 * calls to the data base.
 * 
 * @author Sajeesh
 *
 */
public final class UserRepository {
	
	//Singleton instance of User Repository.
	private static UserRepository instance;
	
	private Map<Integer, User> users = new HashMap<Integer, User>();

	private UserRepository(){
		super();
	}
	
	/**
	 * Return single instance of the repository.
	 * @return singleton.
	 */
	public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }
	/**
	 * Add a user record.
	 * @param user User entity record to add.
	 * @return Updated user with identifier set.
	 */
	public User add(final User user) {
		int id = users.size();
		user.setId(id);
		users.put(id, user);
		return user;
	}

	/**
	 * Returns a user by a identifier passed in.
	 * @param id The identifier of the user to be retrieved.
	 * @return User that matches this identifier.
	 */
	public User get(final int id) {
		return users.get(id);
	}

	/**
	 * Returns all the users configured in the database.
	 * @return all the users.
	 */
	public Set<User> getAll() {
		return new HashSet<User>(users.values());
	}

	/**
	 * Update user.
	 * @param user user to update.
	 * @return Updated user.
	 * @throws UserNotFoundException User not found.
	 */
	public User update(final User user) throws UserNotFoundException{
		int id = user.getId();
		if (users.containsKey(id)) {
			users.put(id, user);
			return user;
		}else{
			throw new UserNotFoundException();
		}
		
	}

	/**
	 * Delete a user that matches the identifier passed in.
	 * @param id id of the user that needs to be removed.
	 * @return
	 */
	public boolean delete(final int id) {
		return users.remove(id) != null;
	}
}
