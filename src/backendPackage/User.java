package backendPackage;

/**
 * This class is used to represent a user entity.
 */
public class User {

	// Instance variables:
	private int userID; // holds user ID for each instance.
	private String username; // holds username of a user.
	private String password; // holds passwords of a user.
	private String email; // holds the email of a user.

	// Constructor method:
	public User(String username, String password, String email) { // Constructor takes userID, username, password and email as parameters.
		setUsername(username); // setting the username of the user instance
		setPassword(password); // setting the password of the user instance
		setEmail(email); // setting the email of the user instance
	}
	
	// Getter methods:
	public int getUserID() {
		return this.userID;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	// Setter methods:
	/**
	 * This method is used to set the UserID for a user
	 * @param userID
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	/**
	 * This method is used to set the username of a user (checking if valid username)
	 * @param username: username of the user
	 */
	public void setUsername(String username) {
		if (username != null) { // Checking if the username is a null value or not.
			username = username.trim(); // trimming excessive whitespaces
			username = username.toLowerCase(); // Turning all the characters lowercase for the string.
			if (!username.isEmpty()) { // Checking if the username is an empty string.
				if (!username.isBlank()) { // Checking if the username is blank
					if ((!(username.length() < 3)) && (!(username.length() > 20))) { // Checking if the username is less than three characters or greater than 20 characters
						
						// checking for non-alphanumeric characters: (not allowed)
						char[] characters = username.toCharArray(); // Creating a character array from the username string
						for(int i =0; i < username.length(); i++) { // Going through each index of the character array
							if (Character.isLetterOrDigit(characters[i]) || Character.isWhitespace(characters[i])) { // Checking if the character at a particular index is a letter or digit.
								this.username = username; // if valid, then the username variable is given the username.
							} else {
								this.username = "N/A"; // Giving the username a default value in case of invalid input.
								throw new IllegalArgumentException("username can NOT have special characters!"); // exception is thrown is username contains special characters.
							}
						}
					
					} else {
						this.username = "N/A"; // Giving the username a default value in case of invalid input.
						throw new IllegalArgumentException("username must be between 3 and 20 characters long!"); // exception is thrown is username is less than 3 or more than 20 characters.
					}
				} else {
					this.username = "N/A"; // Giving the username a default value in the case of invalid input.
					throw new IllegalArgumentException("username can NOT be blank!"); // exception is thrown is username is blank
				}
			} else {
				this.username = "N/A"; // Giving the username a default value in case of invalid input.
				throw new IllegalArgumentException("username can NOT be empty!"); // exception is thrown if username is an empty string
			}
		} else {
			this.username = "N/A"; // Giving the username a default value in case of invalid input.
			throw new IllegalArgumentException("username can NOT be null!"); // exception is thrown if the username is null
		}
	}
	
	/**
	 * This method is used to set the password of a user (checking if valid password)
	 * @param password: password of the username of a user
	 */
	public void setPassword(String password) {
		boolean flag1 = false; // To check if atleast one uppercase letter exists
		boolean flag2 = false; // To check if atleast one lowercase letter exists
		boolean flag3 = false; // To check if atleast one special character exists
		boolean flag4 = false; // To check if atleast one digit exists
		
		if (password != null) { // Checking if the password is a null or not.
			password = password.trim(); // trimming off excessive whitespace characters
			if (!password.isEmpty()) { // Checking if the password is an empty string.
				if (password.length() >= 8) { // Checking if the password is atleast 8 characters long
					
					// going through each character of the password
					for(int i =0; i < password.length();i++) { // Going through each index of the password string
						char character = password.charAt(i); // Assigning a character of the string to 'character' variable
						if (Character.isUpperCase(character)) { // Checking if atleast one character is uppercase
							flag1 = true;
						} else if (Character.isLowerCase(character)) { // Checking if atleast one character is lowercase
							flag2 = true;
						} else if (!Character.isLetterOrDigit(character)) { // Checking if atleast one special character exists
							flag3 = true;
						} else if (Character.isDigit(character)) { // Checking if atleast one numerical value exists
							flag4 = true;
						}
					}
					if (flag1 == true && flag2 == true && flag3 == true && flag4 == true) { // Checking if all conditions are met
						this.password = SimpleBcrypt.hashPassword(password); // hash and store the password after checking if the password satisfies all conditions
						
					} else {
						this.password = "N/A"; // Giving the password a default value in case of invalid input.
						throw new IllegalArgumentException("Please Check if:\n- The password contains ATLEAST ONE uppercase letter\n- The password contains ATLEAST ONE lowercase letter\n- The password contains ATLEAST ONE special character\n- The password contains ATLEAST ONE Numerical value");
					}
				} else {
					this.password = "N/A"; // Giving the password a default value in case of invalid input.
					throw new IllegalArgumentException("password should be atleast 8 characters long!"); // exception is thrown if the password is atleast 8 characters long
				}
			} else {
				this.password = "N/A"; // Giving the password a default value in case of invalid input.
				throw new IllegalArgumentException("password can NOT be empty!"); // exception is thrown if the password is an empty string
			}
		} else {
			this.password = "N/A"; // Giving the password a default value in case of invalid input.
			throw new IllegalArgumentException("password can NOT be null!"); // exception is thrown if the password is null
		}
	}
	
	/**
	 * This method is used to set the email of a user (checking if valid email ID)
	 * @param email: email address of the user
	 */
	public void setEmail(String email) {
		boolean flag1 = false; // assuming email doesnt have '@' symbol (for validation)
		boolean flag2 = false; // assuming email doesnt have '.' symbol after @ "@gmail.com" (for validation)
		// kind of weak validation for this one, but doesnt even matter ngl
		
		if (email != null) { // Checking if the email string is null or not.
			int dotIndex = email.lastIndexOf('.'); // Assigning the index of the last '.' that appears in the email string.
			int symbolIndex = email.indexOf('@'); // Assigning the index of '@' symbol that appears in the email string.
			email = email.trim(); // trimming off excessive whitespace characters
			if (!email.isEmpty()) { // Checking if the email string is empty or not
				
				// going through each character of email string
				for(int i =0; i < email.length();i++) { // Going through each index of the email string
					char character = email.charAt(i); // character variable holds a character of the email string at a time
					if(character == '@') { // Since its not a string we can compare with == operator
						flag1 = true; // If the email contains '@' then it is valid
					}
				}
				if(symbolIndex < dotIndex) { // Checking if the '.' symbol appears after the '@' symbol
					flag2 = true; // If the email contains '.' after @
				}
				
				if(flag1 == true && flag2 == true) { // Checking if both conditions are met
					this.email = email;
				} else {
					this.email = "N/A";
					throw new IllegalArgumentException("email address is invalid!"); // exception thrown if either one of the conditions are not met
				}
				
			} else {
				this.email = "N/A";
				throw new IllegalArgumentException("email field can NOT be empty"); // exception thrown if email string is empty
			}
		} else {
			this.email = "N/A";
			throw new IllegalArgumentException("email can NOT be null"); // exception thrown if email string variable is null
		}
	}
	
}
