package backendPackage;

import java.time.LocalDate; // importing LocalDate class for handling dates

public class Task {

	// Instance variables:
	private String userTaskID; // holds the taskID  (Universally Unique ID)
	private String username; // holds the username of the current user
	private String title; // holds the title of the task of a user
	private String description; // holds the description of a task
	private LocalDate dueDate; // holds the dueDate of a task (by when the task has to be finished)
	private Status status; // takes value from enum Status
	
	// Constructor method:
	public Task(String username, String title, String description, String dueDate) {
		setUsername(username); // setting the username of the current user, who is creating the task instance
		setTitle(title); // setting the title of the task instance
		setDescription(description); // setting the description of the task instance
		setDueDate(dueDate); // setting the dueDate of the task
		setStatus(); // setting the status of the task
	}
	
	// Getter methods:
	public String getUserTaskID() {
		return this.userTaskID;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public LocalDate getDueDate() {
		return this.dueDate;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	// Setter methods:
	public void setUserTaskID(String userTaskID) {
		this.userTaskID = userTaskID; // taskID variable takes the currentID value (ID variables value)
	}
	
	/**
	 * This method is used to set the title of the task (after validating it)
	 * @param title: takes the title of task inputted by user
	 */
	public void setTitle(String title) {
		if (title != null) { // checking if the title string is not null
			if (!title.trim().isEmpty()) { // checking if title empty or not (after trimming whitespace)
				if (title.length() <= 1000) { // chekcing if the length of the title is less than or equal to 1000
					this.title = title; // setting the title 
				} else {
					this.title = "N/A";
					throw new IllegalArgumentException("title can NOT exceed 1000 characters."); // exception thrown if title exceeds limit
				}
			} else {
				this.title = "N/A";
				throw new IllegalArgumentException("title can NOT be an empty string."); // exception thrown if title is empty string
			}
		} else {
			this.title = "N/A";
			throw new IllegalArgumentException("title can NOT be null."); // exception thrown if null encountered
		}

	}
	
	/**
	 * This method is used to set the description of a task
	 * @param description: takes the decription of task inputted by the user
	 */
	public void setDescription(String description) {
		if (description != null) { // checking to see if the description string is null or not
			if (!description.trim().isEmpty()) { // checking to see if the description is an empty string or not (after trimming)
				if (description.length() <= 5000) { // checking to see if the length of the description is less than or equal to 5000
					this.description = description; // setting the description
				} else {
					this.description = "N/A";
					throw new IllegalArgumentException("description can NOT exceed 5000 characters."); // throwing exception if desc exceeds limit
				}
			} else {
				this.description = "N/A";
				throw new IllegalArgumentException("description can NOT be empty."); // throwing exception if description is empty
			}
		} else {
			this.description = "N/A";
			throw new IllegalArgumentException("description can NOT be null."); // throwing exception if desc is null
		}
	}
	
	/**
	 * This method is used to set the dueDate variable of a task
	 * @param dueDate: takes the dueDate of task inputted by the user (YYYY-MM-DD)
	 */
	public void setDueDate(String dueDate) {
		boolean flag = false; // flag to check if there are three parts (year, month and day) in date string
		boolean flag1 = false; // flag to check for appropriate format
		boolean flag2 = false; // flag to check for year format
		boolean flag3 = false; // flag to check for month format
		boolean flag4 = false; // flag to check for day format

		if (dueDate != null) { // Checking if the due date is a null value
			dueDate.trim(); // trimming off the excess whitespace characters
			if ((!dueDate.isEmpty()) || (!dueDate.isBlank())) { // Checking if the due date is empty string
				String[] parts = dueDate.split("-"); // Splitting the date on the delimiter: '-'
				
				if (parts.length == 3) { // Checking if the date has all three aspects (also helps preventing blank strings)
					flag = true;
					
					for (String part : parts) { // for each string part in parts
						if (part.matches("\\d+")) { // Checking if the format matches the required format
							flag1 = true;
						}
						if (parts[0].length() == 4) { // Year must be written in like YYYY format
							flag2 = true;
						}
						if (parts[1].length() == 2) { // Month must be written in like: 02 OR 12
							flag3 = true;
						}
						if (parts[2].length() == 2) { // Day must be wrriten in like :01 OR 11
							flag4 = true;
						}
					}
				} else {
					throw new IllegalArgumentException("Due Date is invalid");
				}

				if (flag == true && flag1 == true && flag2 == true && flag3 == true && flag4 == true) { // Checking if all conditions are met
					this.dueDate = LocalDate.parse(dueDate); // turning the string into locadate value as it just makes it 100 times easier to handle
					setStatus(); // set the status accordingly
					
				} else {
					throw new IllegalArgumentException("Due Date must be:\nYYYY-MM-DD format."); // throwing exception if wrong format
				}

			} else {
				throw new IllegalArgumentException("Due Date can NOT be empty"); // throwing exception if date is empty string
			}

		} else {
			throw new IllegalArgumentException("dueDate cannot be null."); // throwing exception if date is null
		}
	}

	/**
	 * This method is used to set the status of the task
	 * @param status: this parameter takes the status ENUM 
	 */
	public void setStatus() {
		LocalDate currentDate = LocalDate.now();
		if (currentDate.isAfter(this.dueDate)) { // checking if the current date is after the due date
			this.status = Status.OVERDUE; // setting status as overdue
		} else if (currentDate.isBefore(this.dueDate)) { // checking if the current date is after the duedate
			this.status = Status.PENDING; // setting status as pending
		} else {
			this.status = Status.PENDING; // setting status as pending if the current date matches todays date
		}
	}
	
	/*
	 * This method is used to set the assigneeID (foreign key that references the userID from the Users class)
	 * Since its a forieng key value in the database, validation checks are not required
	 * @param assigneeID
	*/
	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
