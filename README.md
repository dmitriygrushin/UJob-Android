# UJOB
## Where you can find the oddest of the odd jobs

## Design Pattern:
Our project used the MVC pattern. We use the User, JobListing, and Zipcode radius as Models to store in the database and to use though out the codebase to stay consistent. The views are the XML files in the layout folder this seems to be android's best practice to put them on the layout folder. Each controller class had a corresponding XML file for it which it was responsible. The controllers were split into 3 groups: employer, worker, and generalized to make it easier to distinguish which mode the user was in. This project was WAY too ambitious but the MVC structure made it easier to collaborate and complete the project.

## First Iteration:
For this first iteration, the three user stories we completed are Authentication(user story 1), Authentication(user story 2), and User-Modes(user story 3).

The first two user stories included setting up the Firebase database for user authentication, that way users of the app can create accounts.
The database stores all user accounts and authenticates them so users are able to later log in using their username and password.
There are also error messages put in so that if an account already exists, users can't create duplicate accounts, and the usual error messages for incorrect login info.

The third user story focused on user activity after they created an account and/or logged in. They were greeted with a welcome page that said their name
The page allowed users to select a mode and continue from there.

We were able to deliver on all the user stories we planned on completing, the main difficulty was during the testing process. It was hard to make tests that properly caught
error messages when users would input incorrect login info or tried to make duplicate accounts.

## Second Iteration:
For the second iteration, the five user stories we complete are User Profiles(user story 1-3), User Modes(user story 2-3), and Employer/Employee Communication (User Story 1).

The first three user stories involved updating our Firebase database to support more user fields that could be displayed in a user profile description. While using the app, the user would have the ability to view and edit their profiles. When the user requests a job or creates one, their user profile would be available for viewing by the employer/employee.

The User Modes user stories were implemented in order for the user to be able to both create jobs (Employer Mode) and/or sign up to partake in a job (Worker Mode). These user stories also involved updating our Firebase database to support the new information needed for these features. When users enter Employer Mode, they are presented with the option to create a new job or edit/cancel an existing one. When the user enters Worker Mode, they have the option to select the desired radius for the available jobs in their area. By default, the user will be presented with available jobs within a 15mi radius from their current location. The location data is pulled from the users' device and location permissions are required.

The Employer/Employee Communication user story is implemented to allow indirect employer/employee communication before they exchange information. If gives 3 states, potential, accepted, pending for jobs and employees to show what stage they're at in the process of completing a job. Workers can request a job which then signals the Employer if they would like to accept the worker. The worker then can fully accept the job which puts them on the pending list of the job and puts the job in their pending list. This system helps keep track of employees and employers and their states.

Issues Faced and Overcome: As the project grew we had to overcome difficulties with features depending on each other. We overcame the issue by creating pre-made values in our Firebase Database which allowed us to use data that wouldn't be available until a feature was completed.

### NOTE:
Considering this was the team's first crack at a full-scale (OVERLY AMBITIOUS) Android project within a short deadline we did well.

### RUN:
To run the project you must add "google-services.json" provided by firebase into the "app" folder.
