package is.ru.honn.ruber.users.service;

import is.ru.honn.ruber.domain.History;
import is.ru.honn.ruber.domain.Trip;
import is.ru.honn.ruber.domain.User;

import java.util.Date;

public interface UserService
{
    public User userSignup(String username, String firstName, String lastName,
                           String password, String email, Date registered)
            throws UsernameExistsException;

    public User getUser(String username) throws UserNotFoundException;

    public History getUserHistory(int userId, int offset, int limit) throws TripNotFoundException;

	public Trip getTripById(int tripId) throws TripNotFoundException;
}
