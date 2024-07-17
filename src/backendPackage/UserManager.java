package backendPackage;

import java.sql.Connection; // importing connection class to connect to database
import java.sql.DriverManager; // importing driverManager class to manage database connections using JDBC
import java.sql.PreparedStatement; // importing the preparedStatement class to create sql queries
import java.sql.ResultSet; // importing the resultSet class to handle SQL query results
import java.sql.SQLException; // importing the SQLException class to catch SQL exceptions

/**
 * This class is used to handle database operations related to user profile management.
 * 
 * Methods:
 * - createUser: used to create a new user profile in the database.
 * - getHighestUserID: used by the createUser method, to fetch the latest userID in the database, so that the consecutive userID can be returned
 * 
 * - authenticateUser: this method is used to authenticate / verify a users identity
 * 
 * - updateUserByUsername: used to update a user's username.
 * - updateUserByEmail: used to update a user's email.
 * - updateUserByPassword: used to update a user's password.
 * 
 * - deleteUserByIdentifier: used to delete a user profile using username and password verification
 * 
 * - findByUsername: this method is used by the update methods
 */

public class UserManager {
	// Setting up a database connection
	private final String url = DatabaseConfig.getUrl();
    private final String user = DatabaseConfig.getUser();
    private final String password = DatabaseConfig.getPassword();
	
	/**
	 * This method is used to create a user (using user details like username, password and email)
	 * @param user: the user object containing user details
	 * @throws SQLException: throws sqlexception if a database error occurs
	 */
    public void createUser(User user) throws SQLException {
    	// Connecting to database
        try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) {
        	conn.setAutoCommit(true); // Setting auto-commit to true to make this easier (each query is committed after execution)
        	
        	// want to check if mysql returns an empty set, and if the case is so, then proceed with creating a user, or else, give an error saying this user already exists
        	String usernameCheck = "SELECT * FROM app_users where username=?";
        	PreparedStatement pstmtCheck = conn.prepareStatement(usernameCheck); // creating a prepared statement object using string usernameCheck
        	pstmtCheck.setString(1, user.getUsername()); // assigning the username to the placeholder in the preparedStatement
        	
        	ResultSet rs = pstmtCheck.executeQuery(); // storing the result set in the resultset rs
            if (rs.next()) { // If the resultset returns true (not empty set, i.e username exists) then throw exception, else user is created
                throw new SQLException("Username already exists.");
            }
            
        	// Creating a string to insert the user details in the database
            String sql = "INSERT INTO app_users (id, username, password, email) VALUES (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql); // creating a prepared statement object using string sql
            
            int highestUserID = getHighestUserID(); // getting the latest user ID in the database (to maintain integrity, also cus ID is primary key)
            user.setUserID(highestUserID + 1); // setting userID as => highestUserID in db + 1
            
            // replacing the values in the prepared statement with the user inputs
            pstmt.setInt(1, user.getUserID());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getEmail());
            
            pstmt.executeUpdate(); // executing the statement
        }
    }
    
    /**
	 * This method is used to get the highest userID, such that when the new user is created, the user gets the ID after the latest one
	 * @return returns the highest User ID
	 * @throws SQLException: throws sqlexception if a database error occurs
	 */
	public int getHighestUserID() throws SQLException {
        int highestUserID = 1; // If no records exist, then the first record in the db will have this value (1)
        
        // Connecting to database
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT MAX(id) AS max_id FROM app_users";
            try (PreparedStatement pstmt = conn.prepareStatement(sql); // creating a prepared statement for the query (sql)
                 ResultSet rs = pstmt.executeQuery()) { // executing the qiery and storing result in rs
                if (rs.next()) { // if the result set is not empty, then assign the value of max_id to the variable highestUserID
                    highestUserID = rs.getInt("max_id");
                }
            }
        }
        return highestUserID; // returning the highest userID
    }
    
    /**
     * This method is used to authenticate a user (checking if the user is the user themself lol)
     * @param username: the username of the user of an instance
     * @param password: the password of the user of an instance
     * @return true is returned if the user is authenticated, or else false
     * @throws SQLException: throws sqlexception if a database error occurs
     */
    public boolean authenticateUser(String username, String password) throws SQLException {
    	// Connecting to database
        try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) {
        	conn.setAutoCommit(true); // Setting auto-commit to true to make this easier (each query is committed after execution)
        	
        	// This query is used to select all the records from the database where the username is the inputted username
            String sql = "SELECT * FROM app_users WHERE username=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql); // using a preparedstatement object to hold the string query 'sql'
            pstmt.setString(1, username); // placing the username variable in the placeholder
            ResultSet rs = pstmt.executeQuery(); // executing the query and storing the result set in rs
            
            if (rs.next()) { // If the user record exists, then the hashed password is stored in the dbPassword string variable
            	String dbHashedPassword = rs.getString("password");
            	
                if (SimpleBcrypt.checkPassword(password, dbHashedPassword)) { // if the inputted password is equal to the hashed password from the database, then user is authenticated
                    return true; // Authentication successful
                } else {
                    return false; // Incorrect password, authentication not successful
                }
            }
            return false; // User not found
        }
    }
    
    
    /**
     * This method is used to update a user's username
     * @param oldUsername: current username of the user instance
     * @param newUsername: the updated username that is to be set
    */
    public void updateUserByUsername(String oldUsername, String newUsername) {
        try {
            User user = findByUsername(oldUsername); // creating a user instance by finding the user record using the username
            if (user != null) { // Checking if the user record exists
                try {
                    User tempUser = new User(newUsername, user.getPassword(), user.getEmail()); // Checking if the inputted username is valid (satisifes all conditions)
                    tempUser.setUsername(newUsername); // If no exceptions thrown, then the new username of the user is valid
                } catch (IllegalArgumentException e) {
                	// displaying error message if exception is found
                    System.out.println(e.getMessage());
                    return;
                }
                
                // Proceed with the update if validation passed
                try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) { // connecting to the database
                    String query = "UPDATE app_users SET username =? WHERE username =?";
                    PreparedStatement pstmt = conn.prepareStatement(query); // using prepared statement object to hold the query string
                    pstmt.setString(1, newUsername); // replacing the placeholder value with the new username
                    pstmt.setString(2, oldUsername); // replacing the placeholder value with the old username
                    int rowsAffected = pstmt.executeUpdate(); // storing the number of result set rows affected in int variable
                    if (rowsAffected > 0) {
                        System.out.println("Username updated successfully."); // if rows are affected, then the username is updated
                    } else {
                        System.out.println("Failed to update username. Username may already be taken or incorrect.");
                    }
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException | NullPointerException e) {
        	// displays error message if exception is caught
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }
	
    /**
     * This method is used to update the email of a specific user profile / instance
     * follows the same updation method as updateUserByUsername
     * 
     * @param username: username of the user instance
     * @param oldEmail: current email of the user profile
     * @param newEmail: updated email that the user wishes to set
     */
    public void updateUserByEmail(String username, String oldEmail, String newEmail) {
        try {
            User user = findByUsername(username); // finding the user record using the username
            if (user!= null) { // checking if user record exists
            	
                // Validate newEmail using User class setEmail method
                try {
                    User tempUser = new User(user.getUsername(), user.getPassword(), newEmail);
                    tempUser.setEmail(newEmail);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    return;
                }
                // Proceed with the update if validation passed
                try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) {
                    String query = "UPDATE app_users SET email =? WHERE (email =? AND username =?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, newEmail);
                    pstmt.setString(2, oldEmail);
                    pstmt.setString(3, username);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Email updated successfully.");
                    } else {
                        System.out.println("Failed to update email. Email may already be in use or incorrect.");
                    }
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }

    /**
     * This method is used to update the password of a specific user profile / instance
     * Follows the same updation method as updateUserByUsername
     * 
     * @param username: username of the current user instance
     * @param oldPassword: current password of the user instance
     * @param newPassword: new password of the user instance
     */
    public void updateUserByPassword(String username, String oldPassword, String newPassword) {
        try {
            User user = findByUsername(username); // finding the user profile using the username
            if (user!= null) { // checking if user record exists
            	
                // Validate newPassword using User class setPassword method
                try {
                    User tempUser = new User(user.getUsername(), newPassword, user.getEmail()); // see if the new password fits all the criteria for a password
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    return;
                }
                // Proceed with the update if validation passed
                try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) {
                    String query = "UPDATE app_users SET password =? WHERE username =?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, SimpleBcrypt.hashPassword(newPassword));
                    pstmt.setString(2, username);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Password updated successfully.");
                    } else {
                        System.out.println("Failed to update password. Incorrect current password.");
                    }
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }


    /**
     * This method is used to delete a users detail by crosschecking the inputted password
     * @param identifier: userID of the user
     * @param password: password of the user
     * @throws SQLException: throws sqlexception if a database error occurs
     */
	public boolean deleteUserByIdentifier(String identifier, String password) throws SQLException {
		User user = findByUsername(identifier); // Getting the created user instance from the findByUsername method
		String storedPassword=""; // variable to store the retrieved password from the database.
		
		if (user != null) { // Checking if the user is not null
			
			// first getting the stored password right from the database for the specified user
			try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) {
				String query = "SELECT password FROM app_users WHERE username =?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, identifier);
				ResultSet rs = pstmt.executeQuery(); // Using executeQuery() for select statements
			    if (rs.next()) { // Move the cursor to the first row of the result set
			        storedPassword = rs.getString("password");
			    } else {
			        System.out.println("No user found with the specified username.");
			    }
			}
			
			// now deleting the user details from the db
			if (SimpleBcrypt.checkPassword(password, storedPassword)) { // if the inputted Password is the same as the password in the database (verifying user)
				try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) { // establishing a connection
					conn.setAutoCommit(true); // setting auto-commit to true
					String sql = "DELETE FROM app_users WHERE username=?"; 
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, identifier);
					pstmt.executeUpdate();
				}
			} else {
	            System.out.println("Passwords do not match. Deletion denied."); 
	            return false; // if passwords dont match
	        }
			return true; // user is found
		}
		return false; // User not found
	}
    
	/**
	 * This method is used to find a user by their username.
	 * 
	 * @param identifier: the username of the user to find.
	 * @return if the user object is found, return it, else return null
	 * @throws SQLException: throws sqlexception if a database error occurs
	 */
	private User findByUsername(String identifier) throws SQLException {
		User user = null; // Initially assuming a user with the inputted username does not exist (hence null)
		
		// Connecting to database
		try (Connection conn = DriverManager.getConnection(this.url, this.user, this.password)) {
			String sql = "SELECT * FROM app_users WHERE username=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, identifier); // placing the username on the placeholder in sql string
			ResultSet rs = pstmt.executeQuery(); // executing the query and storing the result set in rs

			if (rs.next()) { // If result set is true, then the user instance is set by assigning the values
				user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
			}
		}
		return user; // returning the user instance
	}

}
