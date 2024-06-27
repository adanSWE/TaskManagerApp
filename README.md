# taskNUser_ap
This application provides a basic framework for managing personal tasks and deadlines. (in short, a task management app)

Referenced Libraries used:
mysql-connector-j-8.1.0.jar

database schema:
3 tables:
- app_users: to hold the user information
+----------+--------------+------+-----+---------+-------+
| Field    | Type         | Null | Key | Default | Extra |
+----------+--------------+------+-----+---------+-------+
| id       | int          | NO   | PRI | NULL    |       |
| username | varchar(255) | YES  |     | NULL    |       |
| password | varchar(255) | YES  |     | NULL    |       |
| email    | varchar(255) | YES  |     | NULL    |       |
+----------+--------------+------+-----+---------+-------+
  
- app_tasks: to hold the tasks of all the user profiles
+-------------+---------------------------------------+------+-----+---------+-------+
| Field       | Type                                  | Null | Key | Default | Extra |
+-------------+---------------------------------------+------+-----+---------+-------+
| id          | varchar(255)                          | NO   | PRI | NULL    |       |
| username    | varchar(255)                          | YES  |     | NULL    |       |
| title       | varchar(255)                          | YES  |     | NULL    |       |
| description | text                                  | YES  |     | NULL    |       |
| due_date    | date                                  | YES  |     | NULL    |       |
| status      | enum('PENDING','OVERDUE','COMPLETED') | YES  |     | NULL    |       |
+-------------+---------------------------------------+------+-----+---------+-------+

- user_sequences: to generate the userTaskID of a user
+-----------------+--------------+------+-----+---------+-------+
| Field           | Type         | Null | Key | Default | Extra |
+-----------------+--------------+------+-----+---------+-------+
| username        | varchar(255) | NO   | PRI | NULL    |       |
| sequence_number | int          | NO   | PRI | NULL    |       |
+-----------------+--------------+------+-----+---------+-------+



