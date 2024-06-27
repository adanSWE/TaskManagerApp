package backendPackage;

public enum Status {
	// Initializing all the Image Genres (along with a wee string name)
	PENDING("PENDING"), OVERDUE("OVERDUE"), COMPLETED("COMPLETED"); // Completed type will be used to remove a task when its done from the front end ( will delete the corresponding record in backend too)
	
	// Instance variable:
	private String status;
	
	// Constructor method:
	private Status(String status) {
		this.status = status;
	}
	
	// toString() method:
	public String toString() {
		String string = ""; // Initializing a string variable
		string += this.status; // concatenating the string variable with the current status of the task
		return string; // returning a string which holds the genre name
	}
}
