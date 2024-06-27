package backendPackage;

import java.sql.Connection; // importing connection class to connect to database
import java.sql.DriverManager; // importing driverManager class to manage database connections using JDBC
import java.sql.PreparedStatement; // importing the preparedStatement class to create sql queries
import java.sql.ResultSet; // importing the resultSet class to handle SQL query results
import java.sql.SQLException; // importing the SQLException class to catch SQL exceptions
import java.time.LocalDate; // importing the LocalDate class to handle dates
import java.time.format.DateTimeFormatter; // importing the DateTimeFormatter class to format and parse date strings.
import java.util.ArrayList; // importing the arrayList class to store a resizable list of tasks.

/**
 * This class manages tasks of a particular user profile.
 * (creating, updating, deleting, and retrieving tasks from a database, ensuring proper handling of task-related data according to the application's requirements)
 * 
 * Methods:
 * - createTask: used to create a new task instance in the database
 * - settingUserTaskID: used to generate a unique identifier for a task
 * - getNextSequenceNumber: Determines the next sequence number for a user's tasks (to maintain a consistent and incremental numbering order)
 * - updateSequenceNumber: Adjusts the sequence number for a user's tasks (either initializing a number, 
 * 
 * - updateTaskStatus: Modifies the status of a task
 * - deleteTask: used to remove a particular task associated with a specific user
 * 
 * 
 * - getAllTasksForUser: used to retrieve all tasks of a specific user
 * - getUserTaskID:
 * 
 * - updateTask: used to update the details of an existing task
 * - findUserTaskIDByUsername: used to search for a userTaskID by the current users username
 * 
 */

public class TaskManager {
	// Connecting to the database:
	private final String url = "jdbc:mysql://localhost:3306/reminders?useSSL=false&serverTimezone=UTC"; // Database connection URL
	private final String user = "root"; // Database username
	private final String password = "myDatabase105!"; // Database password
	
	/**
	 * This method is used to create a task using prepared statement
	 * @param task: this variable is used to refer to the task class instance
	 * @throws SQLException: used to throw errors encoutered while trying to link to the database
	 */
    public void createTask(Task task) throws SQLException { 
        try (Connection conn = DriverManager.getConnection(url, user, password)) { // Establishing a connection to the database using a connection object
        	conn.setAutoCommit(true); // After each query is passed, the connection commits the query on its own
            String sql = "INSERT INTO app_tasks (id, username, title, description, due_date, status) VALUES (?,?,?,?,?,?)"; // Creating a sql query using a string object
            
            PreparedStatement pstmt = conn.prepareStatement(sql); // Using prepared statement variable to compile a query using the connection object (it can have placeholders (?))
            
            // Setting parameters for the prepared statement (pstmt) from the task object class
            pstmt.setString(1, settingUserTaskID(task));
            pstmt.setString(2, task.getUsername());
            pstmt.setString(3, task.getTitle());
            pstmt.setString(4, task.getDescription());
            pstmt.setDate(5, java.sql.Date.valueOf(task.getDueDate()));
            pstmt.setString(6, task.getStatus().name());
            
            pstmt.executeUpdate(); // this basically executes the insert operation
        }
    }
    
    /**
     * This method is used to set the UserTaskID of a user
     * 
     * @param task: task object
     * @return returns the userTaskID
     */
    public String settingUserTaskID(Task task) {
    	// setting taskID
        String baseId = "T"; // Base ID prefix
        String userIdSuffix = task.getUsername().toLowerCase().replace(" ", "_"); // Convert username to lowercase and replace spaces with underscores
        String userTaskID="";
        
        try {
			userTaskID = baseId + "-"+ userIdSuffix + "-" + getNextSequenceNumber(task.getUsername()); // creating userTaskID
		} catch (SQLException e) {
			System.out.println(e); // print out exceptions if any occur
		}
    	return userTaskID; // return userTaskID
    }
    
    /**
     * This method is used to get the next sequence method for a user
     * 
     * @param username: username of the user
     * @return: returns the next sequence number if previous sequence number exists , or else just returns 1
     * @throws SQLException: throws sqlexception if db connectivity error occurs
     */
	public int getNextSequenceNumber(String username) throws SQLException {
		// connecting to database
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			// query to find the max sequence in the table user_sequences, for a particular user
			String sql = "SELECT MAX(sequence_number) AS max_sequence_number FROM user_sequences WHERE username =?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql); // creating prepared statement object using sql query

			pstmt.setString(1, username); // replacing the placeholders with the username of the user
			ResultSet rs = pstmt.executeQuery(); // executing the query 

			if (rs.next()) { // If any task records exist previously
				int maxSequenceNumber = rs.getInt("max_sequence_number");

				if (maxSequenceNumber == 0) { // If maxSequenceNumber is 0, it means the user hasn't had any tasks yet
					updateSequenceNumber(username, 1); // Initialize the sequence number to 1 for a new user
					return 1; // return 1

				} else {
					int newSequenceNumber = maxSequenceNumber + 1; // Increment the sequence number for existing tasks
					updateSequenceNumber(username, newSequenceNumber);
					return newSequenceNumber; // return the new sequence number
				}
			} else {
				return 1; // Default sequence number in case of unexpected behavior
			}
		} catch (SQLException e) { // lets hope this doesnt happen
			System.out.println("Error getting next sequence number: " + e.getMessage());
			throw e;
		}
	}
    
	/**
	 * This method is used to update the sequence number of the UserTaskID
	 * 
	 * @param username: username of the user
	 * @param newSequenceNumber: new sequence number
	 * @throws SQLException: throws exception if sql error occurs
	 */
	public void updateSequenceNumber(String username, int newSequenceNumber) throws SQLException {
		// connecting to the database
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			String selectSequenceSql = "SELECT sequence_number FROM user_sequences WHERE username =?";
			String updateSequenceSql = "UPDATE user_sequences SET sequence_number =? WHERE username =?";
			
			// firstly, we try to find the current sequence number for the user
			try (PreparedStatement selectSeqStmt = conn.prepareStatement(selectSequenceSql)) {
				selectSeqStmt.setString(1, username); // replacing the placeholder with the username of the user
				ResultSet rs = selectSeqStmt.executeQuery(); // executing the query

				if (rs.next()) { // if sequence number for the user exists (if such record exists)
					// update the sequence number
					try (PreparedStatement updateSeqStmt = conn.prepareStatement(updateSequenceSql)) {
						updateSeqStmt.setInt(1, newSequenceNumber);
						updateSeqStmt.setString(2, username);
						updateSeqStmt.executeUpdate();
					}
				} else { // if no sequence number exists, insert a new record
					try (PreparedStatement insertStmt = conn
							.prepareStatement("INSERT INTO user_sequences (username, sequence_number) VALUES(?,?)")) {
						insertStmt.setString(1, username);
						insertStmt.setInt(2, newSequenceNumber);
						insertStmt.executeUpdate();
					}
				}
			} catch (SQLException e) {
				System.err.println("Error updating sequence number: " + e.getMessage());
				throw e;
			}
		}
	}
    
	
    /**
     * This method is used to update the status of a task (using username and UserTaskID)
     * 
     * @param username: username of the current user
     * @param newStatus: the new status of the current user
     * @throws SQLException: used to throw errors encoutered while trying to link to the database
     */
    public void updateTaskStatus(String username, Status newStatus, String id) throws SQLException {
    	// connecting to db
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
        	conn.setAutoCommit(true); // setting auto-commit to true
        	
        	// creating a query to update the records of a given userTaskID and username
            String sql = "UPDATE app_tasks SET status=? WHERE (username=? AND id = ?)";
            
            PreparedStatement pstmt = conn.prepareStatement(sql); // creating prepared statement object using sql query
            pstmt.setString(1, newStatus.name()); // replacing the placeholder with the name of the new status 
            pstmt.setString(2, username); // replacinng the placeholder with the username of the current user
            pstmt.setString(3, id); // replacing the placeholder using the UserTaskID
            
            // execute the update operation
            int rowsAffected = pstmt.executeUpdate();
            
            // if the status was updated to Completed, delete the task
            if ("COMPLETED".equals(newStatus.name()) && rowsAffected > 0) {
                deleteTask(username); // go to deleteTask method
            }
        }
    }
	
    /**
     * This method is used to delete a task using the username of the current User
     * This method is used by updateTaskStatus method in this class
     * 
     * @param username: username of the current user
     * @throws SQLException: used to throw exceptions that occur when trying to connect to db
     */
    public void deleteTask(String username) throws SQLException {
    	// connecting to database
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
        	conn.setAutoCommit(true); // setting auto-commit to true
        	// setting 
            String sql = "DELETE FROM app_tasks WHERE username=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username); 
            //pstmt.setString(2, userTaskID); 
            pstmt.executeUpdate();
        }
    }
    
    
    /**
     * This method is used to get all the tasks for a particular user profile
     * 
     * @param username: username of the user
     * @return: returns an arrayList of type Task (object) 
     * @throws SQLException: throws exception if db error occurs
     */
    public ArrayList<Task> getAllTasksForUser(String username) throws SQLException {
    	// initializing an arrayList of type Task called tasks
        ArrayList<Task> tasks = new ArrayList<>();
        
        // connecting to the database
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
        	// creating an sql query to find all the records in app_tasks of a certain user
            String sql = "SELECT * FROM app_tasks WHERE username =?";
            PreparedStatement pstmt = conn.prepareStatement(sql); // creating a prepared statement object usint the sql query
            pstmt.setString(1, username); // replacing placeholder with username of the current user
            ResultSet rs = pstmt.executeQuery(); // executing the query
            
            while (rs.next()) { // for all the records that are returned from the execution
            	String username1 = rs.getString("username"); // setting the temp username variable from the db
                String title = rs.getString("title"); // setting the temp title variable from the db
                String description = rs.getString("description"); // setting the temp description variable from the db
                String dueDate = rs.getString("due_date"); // setting the temp dueDate variable from the db
                
                
                //String status = rs.getString("status"); // setting the temp status variable from the db
                //Status taskStatus = Status.valueOf(status.toUpperCase()); // setting the taskstatus
                
                Task task = new Task(username1, title, description, dueDate); // creating a task object using all these variables
                
                tasks.add(task); // adding the task object in the arrayList called "tasks"
            }
        }
        return tasks; // return the array list
    }
    
    /**
     * This method is used to get the UserTaskID of a user (using the username)
     * 
     * @param username: username of the user
     * @return: returns UserTaskID of the user if found, else null
     * @throws SQLException: throws sqlexception if a database error occurs
     */
    public String getUserTaskID(String username) throws SQLException {
        // connecting to database
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
        	// sql query for getting the userTaskID of a user
        	String getLastTaskIDSql = "SELECT id FROM app_tasks WHERE username =? ORDER BY id DESC LIMIT 1";
        	
            PreparedStatement pstmt = conn.prepareStatement(getLastTaskIDSql); // creating a prepared statement object using the sql query
            pstmt.setString(1, username); // replacing the placeholder with username
            ResultSet rs = pstmt.executeQuery(); // executing the query
            
            if (rs.next()) { // if the result set is true (not empty set)
                String lastTaskID = rs.getString("id"); // store the latestTaskID in lastTaskID
                
                // Extract the sequence number part of the last task ID
                String[] idParts = lastTaskID.split("-");
                String sequenceNumberPart = idParts[idParts.length - 1];
                
                // reconstruct the new UserTaskID
                String baseId = "T";
                String userIdSuffix = username.toLowerCase().replace(" ", "_");
                String newUserTaskID = baseId + "-" + userIdSuffix + "-" + sequenceNumberPart;
                
                return newUserTaskID; // return it
            } else { // lets hope we never encounter this
                System.out.println("No tasks found for user: " + username);
                return null; // Or handle this case as appropriate for your application
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user task ID: " + e.getMessage());
            throw e;
        }
    }
    
    
    /**
     * This method is used to update a specific task of the current user
     * 
     * @param userTaskID: userTaskID of the user
     * @param username: username of the user
     * @param newTitle: the updated title that is to be set
     * @param newDescription: the updated description that is to be set
     * @param dueDate: the updated dueDate that is to be set
     * @param newStatus: the status (this is based on the new dueDate that is set
     * @throws SQLException: throws sqlexception if a database error occurs
     */
    public void updateTask(String userTaskID, String username, String newTitle, String newDescription, LocalDate dueDate, Status newStatus) throws SQLException {
    	// connecting to the database
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(true); // auto-commit to true to commit each query after execution
            
            // sql query to set the username, title, desc and dueDate of a particular status (using id and username as identifiers)
            String sql = "UPDATE app_tasks SET username=?, title=?, description=?, due_date=?, status=? WHERE id=? AND username=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql); // creating a prepared statement object using the string sql
            
            pstmt.setString(1, username); // replacing the first placeholder with the current username
            pstmt.setString(2, newTitle); // replacing the second placeholder with the updated title
            pstmt.setString(3, newDescription); // replacing the second placeholder with the updated description
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // using formatter to check the format of the date
            String formattedDueDate = dueDate.format(formatter); // converting the dueDate to string
            
            pstmt.setDate(4, java.sql.Date.valueOf(formattedDueDate)); // replacing the placeholder to put dueDate
            pstmt.setString(5, newStatus.name()); // replacing the placeholder with the status name
            pstmt.setString(6, userTaskID); // replacing the placeholder with the userTaskID
            
            // might remove this
            pstmt.setString(7, username);  
            System.out.println("taskManager userTaskID: "+userTaskID);
            
            
            int rowsAffected = pstmt.executeUpdate(); // executing the query
            
            if (rowsAffected == 0) { // if 0 rows affected, then no task found in the db with the given details (userTaskID)
                System.out.println("No task found with the given ID and username.");
            } else {
                System.out.println("Task updated successfully.");
            }
        }
    }
    
    
    /**
     * This method is used to find the UserTaskID using the title and description of the task, along with username of current user
     * 
     * @param username: username of the current user
     * @param title: title of the current user
     * @param desc: description of the current user
     * @return: returns UserTaskID if found, else returns null
     * @throws SQLException: throws sqlexception if a database error occurs
     */
    public String findUserTaskIDByUsername(String username, String title, String desc) throws SQLException {
    	// Connecting to the database
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
        	
        	// creating a query to find the userTaskID
            String sql = "SELECT id FROM app_tasks WHERE username =? AND title=? AND description=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql); // creating a prepared statement object using string sql
            pstmt.setString(1, username); // replacing the first placeholder with username of the user
            pstmt.setString(2, title); // replacing the second placeholder with title of the task
            pstmt.setString(3, desc); // replacing the third placeholder with description of the task

            ResultSet resultSet = pstmt.executeQuery(); // storing the result set in the resultset rs
            if (resultSet.next()) { // If the resultset returns true (not empty set, i.e record found)
                String userTaskID = resultSet.getString("id"); // storing the userTaskID from the result row
                return userTaskID; // returning the usertaskID
            } else {
                System.out.println("No task found for username: " + username); // if no task found then print this
                return null;
            }
        } catch (SQLException e) { // catch db exceptions
            System.err.println("Error finding UserTaskID by username: " + e.getMessage()); // throw exception if nothing is found
            throw e;
        }
    }

}
