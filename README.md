# Rest API using Java SpringBoot and Postgresql

## Backend focused TODO application

The following description will guide you through the application and showing you it's capabilities.

Briefly it is a TODO app, which can manage multiple users and attached todos. You are able to register, upload a profile picture, modify, view, or update a profile.

You can add tasks (todos) to your users as well, it supports all the CRUD operations.

It can send notifications via email, and you can manage the roles ( admin <-> user ).

There are Unit and Integration tests implemented, to ensure the quality of the code.

---
As for the database, I used Postgres for this project as the database and I didn't use docker for this, but feel free to do so,
in that case make sure to spin up a docker instance in which you can execute the following commands.

Under a superuser like postgres create user with a name, like todomanager, and the database afterwards.

`CREATE USER todomanager with password 'password'`

**Note**, that If you want the username to be case-sensitive put it in double quotes, like: "userName"

`CREATE DATABASE todoappdb WITH OWNER todomanager;`

`GRANT ALL PRIVILEGES ON DATABASE todoappdb TO todomanager;`

**Note**, that at postgres *version 15 or above* you need to execute the following command to be able to modify the public schema.

`GRANT ALL ON SCHEMA public TO todomanager;`

After these you can go ahead and get into your chosen IDE, I will work with IntelliJ.

Before we begin launching the application, let's make sure, that we set the environment variables.

In IntelliJ you should be able to configure them to the left of the play button in the upper right corner, at *Edit Configurations*.

We should set the following variables:

- DB_USERNAME
- DB_PASSWORD
- EMAIL_ADDRESS
- EMAIL_PASSWORD
- IS TESTING

Set your chosen names and passwords we've just created for postgres to the DB username and password.

Set your email address and password for the email address and password. This will be the email, tha sends the notifications.

Set IS_TESTING false.

**Note**, that if you use **Two-factor authentication**, make sure, to give an app code as a password.

To set an app password, go into your **Account settings**, select **Security**, and  look for **App Passwords**, here you may need to sign in.
Then choose a name to your liking, and generate a 16 digit password. 

Paste it to EMAIL_PASSWORD, and you're good to go.

[Here](https://support.google.com/accounts/answer/185833?hl=en) is an official article to help you with that if the described way is not working.

---

After all this, everything is set up for the project.

Open RestApiTodoAppApplication.java, and click on the play button, and everything should fire up automatically.

I used [Postman](https://www.postman.com/downloads/) to test the API endpoints manually. 
Feel free to use HTTPie or any other software.

Let's go through a user's lifecycle visiting all the endpoints.

---

First, lets register on endpoint with a **POST** request: `http://localhost:8080/api/users/register`.

Send a raw, _json_ payload, with content: 
```
{
    "name": "John",
    "email": "johnie@gmail.com",
    "password": "customPassword",
    "admin": true
}
```

---

We can check our newly created user on url with a **GET** request: `http://localhost:8080/api/users`
This should give us back a list containing our one user, with and id of 1.
We can always check this endpoint, as it will provide us details of all of our users.

---

Add a todo to our user, on endpoint with a **POST** request: `http://localhost:8080/api/todos/add/1`

Send a raw, _json_ payload, with content (actualize date and time, has to be greater than current time, otherwise en exception gets thrown): 
```
{
    "userId": 1,
    "title": "Programming All Day!",
    "description": "Long Description",
    "dueDate": "2023-01-10T10:00:00",
    "notified": false,
    "completed": false
}
```

At the end of the URL, use the user's id, who is adding this todo to the user. In this example, that is the user itself.

---

Next add a profile picture to the user, using a **POST** request on endpoint: `http://localhost:8080/api/images/upload/1`

We need to provide **userIdToModify** parameter under _Params_, it is going to be 1.

And the actual picture under **Body**, as **form-data** add a key **image**, and choose **File** format instead of Text. At **Value** we can upload our chosen image and send the request.

---

To confirm, that our image is uploaded, visit endpoint with **GET** request: `http://localhost:8080/api/images/1`

Provide image ID at the end.

---

Let's try to log in with our user on endpoint login with **POST** request: `http://localhost:8080/api/users/login`

And provide raw, _json_ payload with content:
```
{
    "email": "johnie@gmail.com",
    "password": "customPassword"
}
```

---

Let's try to update our TODO on endpoint with a **PUT** request: `http://localhost:8080/api/todos/update/1?todoId=1&title=Im gonna be a programmer&completed=true&dueDate=2023-01-10T20:50:00&description=I love programming`

At the end of the URL we need the user id that is sending the modifying request, this  will be our created user.

Provide **Params**. First todoid which is required, this will be 1 in this case. Then provide optional parameters, such as **title**, **completed** or **dueDate**.

---

Let's get all todo's for a certain user with a **GET** request on endpoint: `http://localhost:8080/api/todos?userId=1`

Where we need to give a param under **Params** of userId.

---

Let's update our user with a **POST** request on endpoint: `http://localhost:8080/api/users/update/1?name=Another Name&userIdToModify=2&email=anotheremail@gmail.com`

At the end of the URL we will have the user id, who is changing another user (we have one user on himself in this case).
And we can provide optional parameters under **Params**, such as **name**, or **email**.

---

We can delete an IMAGE with a **DELETE** request on endpoint: `http://localhost:8080/api/images/delete/1?imageId=1&userIdToModify=1`

At the end of the URL we will have the user id, and we can provide required parameters under **Params**: **imageId** and **userIdToModify**.

---

We can delete a TODO with a **DELETE** request on endpoint: `http://localhost:8080/api/todos/delete/1?todoId=1`

At the end of the URL we will have the user id who is deleting, and we can provide the required parameter under **Params**: **todoId**.

---

We can delete a USER with a **DELETE** request on endpoint: `http://localhost:8080/api/users/delete/1?userIdToDelete=1`

At the end of the URL we will have the user id who is deleting, and we can provide the required parameter under **Params**: **userIdToDelete**.

---

The tests are all up and running, but before you run all of them, specify an **environment variable** **IS_TESTING** as **true**.
Because of mocking issues, I chose this solution to prevent the Logger to run.

We have 2 type of test, 2 unit tests and 1 end-to-end integration test. I used jUnit 5 and Mockito for the tests ,
as well as an in-memory H2 database.

To make the user-side "fully" tested, I made some tests for the repositories as well.

I chose to implement unit testing for the userService, as the main Object of our project. 
I was mocking everything aside our focused userService (because we tested the repositories earlier), 
but later decided that I will not mock the repository.

Last, but not least we have the integration tests.

---

The email observer class by default checks every 30 minutes ( 0th and 30th minute of every hour ) whether it needs to send notifications.
Adjust it to your liking (EmailSendingObserver class), so you won't have to wait ~20-30 minutes until execution.

### Author: Zsombor Töreky
