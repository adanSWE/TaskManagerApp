package backendPackage;

import java.util.ArrayList;
import java.util.InputMismatchException; // importing InputMismatchException class for invalid input exceptions
import java.util.Scanner;  // importing Scanner class for user input
import java.sql.SQLException;  // importing SQLException class for SQL exceptions
import java.time.format.DateTimeParseException; // importing to catch parsing


public class MainMenu {
	
	// Class variable:
	private static boolean createUserSuccessful = false; // using this for robustness
	private static boolean createTaskSuccessful = false; // using this for robustness
	private static String currentUserUsername = ""; // using this for user login
	private static String taskNumber = ""; // dk y eclipse saying its not used, its literally used 
	
	/**
	 * main method where the magic happens
	 * @param args
	 */
    public static void main(String[] args) {
        mainMenu(); // calling main menu method
    }
    
    /**
     * This is the first menu to be displayed, this menu has three options:
     * - registering a user profile
     * - logging in to an already registered user profile
     * - exiting from the application
     * 
     */
	public static void mainMenu() {
		//TaskManager taskManager = new TaskManager(); // Creating an instance of TaskManager
		UserManager userManager = new UserManager(); // Creating an instance of UserManager
		Scanner scanner = new Scanner(System.in); // same with scanner

		boolean flag = false; // Using a flag to control the loop
		int choice=0; // variable for user choice

		while (!flag) { // While loop to keep displaying the menu until a valid option has been entered
			// Displaying Menu options
			System.out.println();
			System.out.println("+-----------------------------+"); 
			System.out.println("|     Task Management App     |");
			System.out.println("+-----------------------------+");
			System.out.println("|1. Register                  |");
			System.out.println("|2. Login                     |");
			System.out.println("|3. Exit                      |");
			System.out.println("+-----------------------------+");

			try {
				System.out.print("Please select an option: "); // prompting user for input
				choice = scanner.nextInt(); // reading user input as integer
				scanner.nextLine(); // consume newline

				switch (choice) { // handling user choice
				case 1:
					createUser(scanner, userManager); // create a new user
					if (createUserSuccessful) { // checking if the user is succesfully created
						createUserSuccessful = false; // reset the flag after creating a user profile
						userMenu(userManager); // return to menu after user is successfully created
					}
					flag = true; // using this as an indicator that THIS task is done
					break; // to avoid going to other cases
				case 2:
					authenticateUser(scanner, userManager); // log into an existing users acc
					if (createUserSuccessful) { // checking if the user is succesfully created
						createUserSuccessful = false; // reset the flag after logging in a user profile
						userMenu(userManager); // return to menu after user is successfully created
					}
					flag = true; // using this as an indicator that THIS task is done
					break; // to avoid going to other cases
				case 3:
					System.out.println("Exiting the application."); // exiting the application
					flag = true; // setting flag to true to exit the loop.
					System.exit(0); // terminating the application
					break; // to avoid going to other cases
				default:
					System.out.println("Invalid option. Please try again."); // handling invalid input
				}

			} catch (InputMismatchException exception) { // catching input mismatch exceptions
				System.out.print("Invalid Input. Please enter an integer "); // informing the user of the expected input type
				System.out.println(exception); // printing the exception message
				scanner.nextLine(); // clearing the scanner for a fresh input
			}
		}
		}
    
    /**
     * This method is used to display the user menu, after logging in/registering
     * @param userManager: takes the usermanager instance created in main menu as parameter
     * 
     */
    public static void userMenu(UserManager userManager) {
    	Scanner scanner = new Scanner(System.in); // creating scanner object
    	
        boolean flag = false; // using a flag to control the loop

        while (!flag) { // while loop to keep displaying the menu until a valid option has been entered
            System.out.println();
            
			if (currentUserUsername != null) { // surrounding with if statements cus when closing application, exception is thrown if user does NOT enter anything
				int topRow = currentUserUsername.length();

				// top row
				System.out.print("+-------------");
				System.out.println("-".repeat(topRow) + "--+");

				// username display row
				System.out.println("|Logged In: " + currentUserUsername + "    |");

				// mid row
				System.out.print("+-------------");
				System.out.println("-".repeat(topRow) + "--+");

				// menu rows
				System.out.print("|1. Create Task ");
				System.out.println(" ".repeat(topRow) + "|");
				System.out.print("|2. View Task   ");
				System.out.println(" ".repeat(topRow) + "|");
				System.out.print("|3. Settings    ");
				System.out.println(" ".repeat(topRow) + "|");
				System.out.print("|4. Logout      ");
				System.out.println(" ".repeat(topRow) + "|");

				// top row
				System.out.print("+-------------");
				System.out.println("-".repeat(topRow) + "--+");

				System.out.println(); // leaving line for formatting purposes
			}
			
            try {
                System.out.print("Please select an option: "); // prompting user for input
                int choice = scanner.nextInt(); // reading user input as integer
                scanner.nextLine(); // consume newline

                switch (choice) { // handling user choice
                    case 1:
                    	
                    	createTask(scanner); // create a new user
    					if (createTaskSuccessful) { // checking if the task is succesfully created
    						createTaskSuccessful = false; // reset the flag after creating a task
    						userMenu(userManager); // return to same menu after task is successfully created
    					}
                        flag = true; // using this as an indicator that THIS task is done
                        break; // to avoid going to other cases
                        
					case 2:
						
						try {
							viewAllTasksForCurrentUser(scanner, userManager); // viewing all tasks for the current user
						} catch (SQLException e) { // catching any sql exceptions that might occur
							System.out.println(e);
						}
						flag = true; // using this as an indicator that THIS task is done
						break; // to avoid going to other cases
						
                    case 3:
                    	
                        settingsMenu(userManager); // going to the settings menu
                        flag = true; // using this as an indicator that THIS task is done
                        break; // to avoid going to other cases
                        
                    case 4:
                    	
                        System.out.println("Logging out.."); // Logging out
                        currentUserUsername = null; // turning the currentUserUsername variable to null, since user has logged out
                        //scanner.close();
                        mainMenu(); // returning to main menu
                        flag = true; // setting flag to true to exit the loop.
                        break; // to avoid going to other cases
                        
                    default:
                        System.out.println("Invalid option. Please try again."); // handling invalid input
                        System.out.println(); // leaving a line for formatting purposes
                }
            } catch (InputMismatchException exception) { // catching input mismatch exceptions
                System.out.print("Invalid Input. Please enter an integer "); // informing the user of the expected input type
                System.out.println(exception); // printing the exception message
                //System.out.println(); // Leaving a line for spacing
                scanner.nextLine(); // Clearing the scanner for a fresh input
            }
        }
    }
    
    /**
     * This method is used to view the taskMenu (when handling tasks, a mini menu, this one, pops up)
     * 
     * @param scanner: scanner object
     * @param tasks: arraylist of task type holding all the tasks of the current user
     * @param taskManager: taskManager object
     * @param userManager: userManager object
     * @throws SQLException: throws sqlexception if any db error occurs
     * 
     */
    private static void taskMenu(Scanner scanner, ArrayList<Task> tasks, TaskManager taskManager, UserManager userManager) throws SQLException {
        boolean flag = false; // using a flag to control the loop
        
        while (!flag) { // while loop to keep displaying the menu until a valid option has been entered
        	System.out.println("+----------------------------+");
    		System.out.println("|         Task Menu          |");
    		System.out.println("+----------------------------+");
    		System.out.println("|1. Mark a Task as Completed |");
    		System.out.println("|2. Update a Task            |");
    		System.out.println("|3. Return to User Menu      |");
    		System.out.println("+----------------------------+");
			
			try {
                System.out.print("Please select an option: "); // Prompting user for input
                int choice = scanner.nextInt(); // Reading user input as integer
                scanner.nextLine(); // Consume newline left-over

                switch (choice) { // Handling user choice
                    case 1:
                    	
                    	markTaskComplete(scanner, tasks, taskManager, userManager); // calling marking task as complete method
                    	flag = true; // using this as an indicator that THIS task is done
                    	break; // to avoid going to other cases
                    	
					case 2:
						
						updateTask(scanner, tasks, taskManager, userManager); // calling updateTask method
						flag = true; // using this as an indicator that THIS task is done
						break; // to avoid going to other cases
						
                    case 3:
                    	
                    	System.out.println("Returning to User Menu.."); // Exiting the application
                        flag = true; // Setting flag to true to exit the loop.
                        userMenu(userManager); // going back to the userMenu
                        break; // to avoid going to other cases
                        
                    default:
                    	
                        System.out.println("Invalid option. Please try again."); // Handling invalid input
                        System.out.println(); // leaving a line for formatting purposes
                }
            } catch (InputMismatchException exception) { // catching input mismatch exceptions
                System.out.print("Invalid Input. Please enter an integer "); // informing the user of the expected input type
                System.out.println(exception); // printing the exception message
                scanner.nextLine(); // clearing the scanner for a fresh input
            }
		}
	}
    
    /**
     * This is the settings menu that is called in the userMenu when option 4 is selected
     * 
     * @param userManager: takes the userManager instance from the userMenu
     */
    public static void settingsMenu(UserManager userManager) {
    	Scanner scanner = new Scanner(System.in); // creating scanner object
        boolean flag = false; // using a flag to control the loop
        
        while (!flag) { // while loop to keep displaying the menu until a valid option has been entered
        	// displaying settings menu
        	System.out.println("+---------------------------+"); 
    		System.out.println("|         Settings          |");
    		System.out.println("+---------------------------+");
    		System.out.println("|1. Update User Profile     |");
    		System.out.println("|2. Delete account          |");
    		System.out.println("|3. Return to Task Menu     |");
    		System.out.println("+---------------------------+");
			
			try {
                System.out.print("Please select an option: "); // prompting user for input
                int choice = scanner.nextInt(); // reading user input as integer
                scanner.nextLine(); // consume newline

                switch (choice) { // handling user choice
                    case 1:
                    	
                        updateUserDetails(userManager); // calling the update menu
                        flag = true; // using this as an indicator that THIS task is done
                        break; // to avoid going to other cases
                        
                    case 2:
                    	
                    	deleteUser(scanner, userManager); // calling method to delete current users account
                        flag = true; // using this as an indicator that THIS task is done
                        break; // to avoid going to other cases
                        
                    case 3:
                    	
                    	System.out.println("Returning to Task Menu.."); // Exiting the application
                        flag = true; // Setting flag to true to exit the loop.
                        userMenu(userManager); // going back to the userMenu
                        break; // to avoid going to other cases
                        
                    default:
                        System.out.println("Invalid option. Please try again."); // handling invalid input
                        System.out.println();
                }
            } catch (InputMismatchException exception) { // catching input mismatch exceptions
                System.out.print("Invalid Input. Please enter an integer "); // informing the user of the expected input type
                System.out.println(exception); // printing the exception message
                scanner.nextLine(); // clearing the scanner for fresh input
            }
		}
    }
    
    
    
    /**
     * This method is used to display an update menu and to update users detail for the current user
     * This method is called in the settings menu
     * 
     * @param userManager: userManager object
     */
     private static void updateUserDetails(UserManager userManager) {
     	Scanner scanner = new Scanner(System.in); // creating scanner object
     	
         boolean flag = false; // using flag for loop
         int choice = 0; // user choice variable

         while (!flag) {
             System.out.println();
             System.out.println("+-------------------------+");
             System.out.println("|       Update Menu       |"); // displaying menu options
             System.out.println("+-------------------------+");
             System.out.println("|1.   Update Username     |");
             System.out.println("|2.   Update Email        |");
             System.out.println("|3.   Update Password     |");
             System.out.println("|4.   Return to Settings  |");
             System.out.println("+-------------------------+");
             System.out.println(); 
             System.out.print("Please select an option: "); // prompting user for input
             try {
                 choice = scanner.nextInt(); // taking user choice
                 scanner.nextLine(); // consume newline left-over

                 switch (choice) {
                     case 1:
                    	 
                         updateUsername(scanner, userManager); // using the updateUsername method to update users username
                         break; // using break to prevent the code flowing to the other cases
                         
                     case 2:
                    	 
                         updateEmail(scanner, userManager); // using the updateEmail method to update users email
                         break; // using break to prevent the code flowing to the other cases
                         
                     case 3:
                    	 
                         updatePassword(scanner, userManager); // using the updatePassword method to update users password
                         break; // using break to prevent the code flowing to the other cases
                         
                     case 4:
                    	 
                         flag = true; // Change flag to exit the loop
                         break; // using break to prevent the code flowing to the other cases (in this case, default)
                         
                     default:
                         System.out.println("Invalid option. Please try again."); // error message for invalid input
                         System.out.println();
                         break;
                 }
             } catch (InputMismatchException exception) { // using this to catch exceptions (robust menu, tackling all invalid inputs)
                 System.err.print("Invalid Input. Please enter an integer "); // inform the user of the expected input type
                 System.out.println(exception); // print the exception message
                 System.out.println(); // leave a line for spacing
                 scanner.nextLine(); // clear the scanner for a fresh input
             }
         }
         if (choice == 4 || flag) { // check if the user chose to go back to the settings menu
         	settingsMenu(userManager); // going back to the settings menu
         }
     }
    
    
    // ------------------------------------------------------------------------------------------------------------------------------------
    
   /**
    * This method is used to create a user profile
    * 
    * @param scanner: scanner object
    * @param userManager: userManager object
    * 
    */
	private static void createUser(Scanner scanner, UserManager userManager) {
		boolean flag = false; // using a flag to check if the user profile is created or not
		
		while (!flag) { // using while to keep asking for input until user profile isnt created
			try { // using this to catch exceptions
				System.out.print("Enter username: "); // asking user for username input
				String username = scanner.nextLine(); // storing username input in variable
				
				System.out.print("Enter password: "); // asking user for password input
				String password = scanner.nextLine(); // storing password input in variable
				
				System.out.print("Enter email: "); // asking user for email input
				String email = scanner.nextLine(); // storing email string

				User user = new User(username, password, email); // creating a new User object
				userManager.createUser(user); // creating user in the database
				System.out.println("User Registered successfully."); // success message
				
				currentUserUsername = username; // setting current users username for this class
				createUserSuccessful = true; // setting the flag true after user is created (this variable is used in main menu)
				
				flag = true; // setting flag to true to exit loop
				
			} catch (IllegalArgumentException | SQLException ex) { // Catching multiple exceptions (sql exceptions and illegalargument exceptions)
				// displaying the error message
				System.out.println("Error: " + ex); 
				System.out.println("Please try again.");
				System.out.println();
			}
		}
	}
	
	/**
	 * This method is used to create a task
	 * 
	 * @param scanner: scanner object
	 * 
	 */
	private static void createTask(Scanner scanner) {
		boolean flag = false; // using a flag to check if the task is created or not

		while (!flag) { // using while to keep asking for input until a task is NOT created
			try { // using this to catch exceptions
				System.out.print("Enter task title: ");
				String title = scanner.nextLine();

				System.out.print("Enter task description: ");
				String description = scanner.nextLine();

				System.out.print("Enter due date (YYYY-MM-DD): ");
				String dueDate = scanner.nextLine();

				Task task = new Task(currentUserUsername, title, description, dueDate); // creating a task instance with the inputted details

				TaskManager taskManager = new TaskManager(); // creating a taskManager instance
				taskManager.createTask(task); // and creating a task in the db using the taskManager instance
				System.out.println("Task created successfully.");

				createTaskSuccessful = true; // turning this class variable to true, to exit out of the loop in which it is called

				flag = true; // setting flag to true to exit loop

			} catch (IllegalArgumentException | SQLException ex) { // Catching multiple exceptions (sql exceptions and illegalargument exceptions)
				// displaying the error message
				System.out.println("Error: " + ex);
				System.out.println("Please try again.");
				System.out.println();
			}
		}
	}

	/**
	 * This method is used to view all the tasks for the current user
	 * 
	 * @param scanner: scanner object
	 * @param userManager: userManager object
	 * @throws SQLException: throws sql exception if db error occurs
	 * 
	 */
	private static void viewAllTasksForCurrentUser(Scanner scanner, UserManager userManager) throws SQLException {
	    TaskManager taskManager = new TaskManager(); // creating a taskManager instance
	    ArrayList<Task> tasks = taskManager.getAllTasksForUser(currentUserUsername); // creating an arraylist of object Task by using the taskManager

	    if (!tasks.isEmpty()) { // checking if the arraylist is empty
	    	
	    	System.out.println(); // leaving a line for formatting purposes
	        System.out.println("Your Tasks: ");
	        
	        // printing out all the tasks
	        for (int i = 0; i < tasks.size() ; i++) {
	            Task task = tasks.get(i);

	            taskNumber = (i + 1) + ". " + task.getUsername(); // will use this numbering
	            
	            System.out.println((i + 1) + ". " + task.getTitle());
	            System.out.println("Description: " + task.getDescription());
	            System.out.println("Due Date: " + task.getDueDate());
	            System.out.println("Status: " + task.getStatus());
	            System.out.println();
	            
	        }
	        taskMenu(scanner, tasks, taskManager, userManager); // aftering printing out all the tasks of the user, go to the task menu
	    } else {
	        System.out.println("You have no tasks at the moment."); // if no tasks, return to the userMenu
	        userMenu(userManager);
	    }
	}
	
	/**
	 * This method is used to mark a task as complete
	 * 
	 * @param scanner: scanner object
	 * @param tasks: arraylist of object type Task
	 * @param taskManager: taskManager object
	 * @param userManager: userManager object
	 * @throws SQLException: throws sql exception if db error has occured
	 * 
	 */
	private static void markTaskComplete(Scanner scanner, ArrayList<Task> tasks, TaskManager taskManager, UserManager userManager) throws SQLException {
		if (!tasks.isEmpty()) { // checking if the arraylist is empty
			System.out.print("Select Task (enter task no.): "); // prompting user to select a task
			int selectedIndex = scanner.nextInt(); // storing user choice in variable
			selectedIndex = selectedIndex -1; // Adjust the index since the arrayList starts from 0
			
			// removing the selected task Index from the arrayList
			if (selectedIndex >= 0 && selectedIndex < tasks.size()) {
				tasks.remove(selectedIndex);

				try {
					// now removing the selected task from the database
					taskManager.updateTaskStatus(currentUserUsername, Status.COMPLETED, taskManager.getUserTaskID(currentUserUsername)); // delete the selectedTask from the database
					System.out.println("Task Marked as Complete.");
					
				} catch (SQLException e) {
					System.out.print(e); // if exceptions occur, print them out
				}

			} else {
				System.out.println("Sorry, that task doesn't exist."); // print if invalid task number
			}
		} else {
			System.out.println("No tasks available."); // if arraylist is empty this is printed.
		}
		viewAllTasksForCurrentUser(scanner, userManager); // finally go and print out all the tasks of the current user
	}
	
	/**
	 * This method is used to update a particular task of the current user
	 * 
	 * @param scanner: scanner object
	 * @param tasks: arraylist of object type task
	 * @param taskManager: taskManager object
	 * @param userManager: userManager object
	 * @throws SQLException: throws sql exception when db error occured
	 * 
	 */
	private static void updateTask(Scanner scanner, ArrayList<Task> tasks, TaskManager taskManager, UserManager userManager) throws SQLException {
	    System.out.print("Select a Task to Update: ");
	    int selectedIndex = scanner.nextInt() - 1; // adjusting the index since arrayLists start from 0
	    
	    if (selectedIndex >= 0 && selectedIndex < tasks.size()) { // checking if the selectedIndex is in range of the arraylist
	        Task selectedTask = tasks.get(selectedIndex); // storing the selected task in task type variable
	        
	        // getting the UserTaskID for the current task, of the current user.
	        String currentUserTaskID = taskManager.findUserTaskIDByUsername(currentUserUsername, selectedTask.getTitle(), selectedTask.getDescription());
	        
	        // prompting user to input the new data they wish to update
	        System.out.print("New Title: ");
	        scanner.nextLine();
	        String newTitle = scanner.nextLine();
	        
	        System.out.print("New Description: ");
	        String newDescription = scanner.nextLine();
	        
	        System.out.print("New Due Date (YYYY-MM-DD): ");
	        String newDueDateString = scanner.nextLine();
	        
	        // validating all the inputs
	        if ((newTitle != null) && (!newTitle.trim().isEmpty()) && (newTitle.length() <= 1000)) { // validating new title
	            selectedTask.setTitle(newTitle); // setting the title
	        } else {
	            System.out.println("Invalid title."); 
	            System.out.println("Title must not be null, empty, or exceed 1000 characters.");
	            return;
	        }
	        
	        if ((newDescription != null) && (!newDescription.trim().isEmpty()) && (newDescription.length() <= 5000)) { // validating new description
	            selectedTask.setDescription(newDescription); // setting the new description
	        } else {
	            System.out.println("Invalid description.");
	            System.out.println("Description must not be null, empty, or exceed 5000 characters.");
	            return;
	        }

	        if ((newDueDateString != null) && (!newDueDateString.trim().isEmpty())) { // validating thew new date
	            try {
	                selectedTask.setDueDate(newDueDateString); // setting the new dueDate
	            } catch (DateTimeParseException e) { // catching any conversion exceptions
	                System.out.println("Invalid due date format. Please enter in YYYY-MM-DD format.");
	                return;
	            }
	        } else {
	            System.out.println("Due date cannot be null or empty.");
	            return;
	        }
	        
	        // finally updating the task details:
	        try {
	            taskManager.updateTask(currentUserTaskID, currentUserUsername, selectedTask.getTitle(), selectedTask.getDescription(), selectedTask.getDueDate(), selectedTask.getStatus());
	        } catch (SQLException e) {
	            System.err.println("Failed to update task: " + e.getMessage());
	        }
	    } else {
	        System.out.println("Sorry, that task doesn't exist.");
	        System.out.println();
	    }
	    taskMenu(scanner, tasks, taskManager, userManager); // going back to the taskMenu
	}
	

	/**
	 * This method is used to log into a users profile
	 * 
	 * @param scanner: scanner object
	 * @param userManager: userManager object
	 * 
	 */
    private static void authenticateUser(Scanner scanner, UserManager userManager) {
    	boolean flag = false; // to check if user has been created or not
    	
        System.out.print("Enter username: "); // Prompting for username
        String username = scanner.nextLine(); // Reading username
        System.out.print("Enter password: "); // Prompting for password
        String password = scanner.nextLine(); // Reading password

        try {
            if (userManager.authenticateUser(username, password)) { // Authenticating user using the authenticateUser method in userManager class
                System.out.println("Login successful."); // Success message
                
                currentUserUsername = username;
                createUserSuccessful = true; // setting the flag true after user is created (this variable is used in main menu)
                flag = true;
                
            } else {
                System.out.println("Login failed. Incorrect username or password."); // Failure message
                createUserSuccessful = false;
                flag = false;
            }
        } catch (SQLException e) { // catching sql exceptions
        	// displaying error
            System.out.println("Error authenticating user: " + e); 
        }
        
        // checking if the user has been successfully created or not, and if not, then the user is sent back to the main menu
        if (flag == true) {
        	userMenu(userManager); // returning back to main menu
        } else {
        	mainMenu();
        }
    }

    /**
     * This method is used to update users username for a specific profile
     * @param scanner
     * @param userManager
     */
    private static void updateUsername(Scanner scanner, UserManager userManager) {
    	System.out.print("Enter current username: "); // prompting user to input the current username
    	String oldUsername = scanner.nextLine(); // storing current username in string "oldUsername"
    	
        System.out.print("Enter new username: "); // prompting user to input the new username
        String newUsername = scanner.nextLine(); // storing updated username in string "newUsername"
        
        userManager.updateUserByUsername(oldUsername, newUsername); // using updateUserbyUsername method in userManager class to update the users username
        currentUserUsername = newUsername;
    }
    
    /**
     * This method is used to update a users email for a specific profile
     * @param scanner
     * @param userManager
     */
    private static void updateEmail(Scanner scanner, UserManager userManager) {
    	String username = currentUserUsername; // storing username in username variable
    	
    	System.out.print("Enter current email: "); // prompting user to enter the user profiles current email
    	String oldEmail = scanner.nextLine(); // storing the current email in the oldEmail variable
    	
        System.out.print("Enter new email: "); // prompting user to enter the new email they wish to update it to
        String newEmail = scanner.nextLine(); // storing the new email in the newEmail variable
        
        userManager.updateUserByEmail(username, oldEmail, newEmail); // using updateUserByEmail method in userManager class to update the users email
    }
    
    /**
     * This method is used to update a users password for a specific profile
     * @param scanner
     * @param userManager
     */
    private static void updatePassword(Scanner scanner, UserManager userManager) {
    	String username = currentUserUsername; // storing username in username variable
    	
    	System.out.print("Enter current password: "); // prompting user to enter the current user password
    	String oldPassword = scanner.nextLine(); // storing the users current password in the oldPasswords variable
    	
        System.out.print("Enter new password: "); // prompting user to enter a new updated password
        String newPassword = scanner.nextLine(); // storing the users updated password in the newPasswords variable
        
        userManager.updateUserByPassword(username, oldPassword, newPassword); // using updateUserbyPassword method in userManager class to update the users password
    }

    /**
     * This method is used to delete a users record in the database
     * @param scanner
     * @param userManager
     */
    private static void deleteUser(Scanner scanner, UserManager userManager) {
    	boolean flag = false;
    	
        System.out.print("Enter your username to delete your account: "); // asking the user to input username to delete account
        String identifier = scanner.nextLine(); // storing the username in the identifier strng reference variable
        
        System.out.print("Enter the password for this account: "); // asking the user to input the password for this current account (verifying user identity)
        String pass = scanner.nextLine(); // storing the users password in pass variable
        
        try {
            if (userManager.deleteUserByIdentifier(identifier, pass) && identifier.equalsIgnoreCase(currentUserUsername)) { // using the deleteUserbyIdentifier method to delete the users record in the database
                System.out.println("Your account has been deleted.");
                System.out.println("Logging out...");
                flag = true;
            } else {
                System.out.println("Please check your details and try again.");
                System.out.println();
                flag = false;
            }
        } catch (SQLException e) { // surrounding code w try and catch to catch sql exceptions
            System.out.println("An error occurred: " + e.getMessage());
        }
        
        if (flag == true) {
        	
        	currentUserUsername = null;
        	mainMenu();
        } else {
        	settingsMenu(userManager); // to go back to main menu
        }
    }
    
    
    
}
