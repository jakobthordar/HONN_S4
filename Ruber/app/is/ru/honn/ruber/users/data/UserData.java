package is.ru.honn.ruber.users.data;

import is.ru.honn.ruber.domain.History;
import is.ru.honn.ruber.domain.Trip;
import is.ru.honn.ruber.domain.User;
import is.ru.honn.ruber.users.service.TripNotFoundException;
import is.ru.honn.ruber.users.service.UserNotFoundException;
import is.ru.honn.ruber.users.service.UsernameExistsException;
import is.ruframework.data.RuData;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

public class UserData extends RuData implements UserDataGateway
{
    public int addUser(User user)
    {

        SimpleJdbcInsert insert =
                new SimpleJdbcInsert(getDataSource())
                        .withTableName("ru_users")
                        .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<String, Object>(6);
        parameters.put("username", user.getUsername());
        parameters.put("firstname", user.getFirstName());
        parameters.put("lastname", user.getLastName());
        parameters.put("password", user.getPassword());
        parameters.put("email", user.getEmail());
        parameters.put("registered", new Date());

        int returnKey;

        try
        {
            returnKey = insert.executeAndReturnKey(parameters).intValue();
        }
        catch(DataIntegrityViolationException divex)
        {
            throw new UsernameExistsException("User " + user.getUsername() + " already exits", divex);
        }

        user.setId(returnKey);
        return returnKey;
    }

    public User getUserByUsername(String username)
    {
        Collection<String> users;
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());

        User user;
        try
        {
            user = (User)jdbcTemplate.queryForObject( "select * from ru_users where username = '" + username + "'", new UserRowMapper());
        }
        catch (EmptyResultDataAccessException erdaex)
        {
            throw new UserNotFoundException("No user found with username: " + username);
        }
        return user;
    }

    @Override
    public List<Trip> getTripsByUserId(int userId) throws TripNotFoundException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());

        List<Trip> trips;
        try
        {
            trips = jdbcTemplate.query("select * from ru_trips where userId = '" + userId + "'", new TripRowMapper());
        }
        catch (EmptyResultDataAccessException erdaex)
        {
            throw new TripNotFoundException("No trips were found", erdaex);
        }
        return trips;
    }

	@Override
	public Trip getTripById(int tripId) throws TripNotFoundException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());

		Trip trip;

		try
		{
			trip = (Trip)jdbcTemplate.queryForObject("select * from ru_trips where id = '" + tripId + "'", new TripRowMapper());
		}
		catch (EmptyResultDataAccessException erdaex)
		{
			throw new TripNotFoundException("No trip found with that ID", erdaex);
		}

		return trip;
	}


}
