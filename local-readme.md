1. Run Application Locally: docker compose up --build
    Main Portal: http://localhost:8080
    Auth-service: http://localhost:9090

2. If there are any changes in .java classes:
    cd backend && mvn clean package -DskipTests
    cd ../auth-service && mvn clean package -DskipTests

    #If running otherwise
    cd ..
    docker-compose down
    docker-compose build --no-cache
    docker-compose up

    #To check the Timestamp of latest war:
    ls -la backend/target/*.war

3. Functionalities:

    Login & Security

    1. Users log in through a separate auth service, but it still works if that service is down
    2. Three types of users: Student, Employer, Admin 
    3. New users can sign up, passwords are stored safely using Hashing
    4. Users can reset their password if they forget it

    Student Features

    1. Upload or Update resume
    2. Search and filter jobs
    3. Apply to jobs or Withdraw from Jobs  
    4. See status of your applications (Pending, Under Review, Accepted, Rejected)

    Employer Features

    1. Post job listings
    2. See who applied and view their resumes
    3. Change applicant status to various states

    Admin Features

    1. See overall stats (users, jobs, applications)
    2. Turn user accounts on or off - activate/deactivate users

4. Redis:

    Redis Connection is configured in RedisConfig.java - for every operation, a connection is borrowed from RedisConnectionFactory interface and later returned.

    @EnableRedisHttpSession - every session is assigned a SessionID and stored as a cookie in the Browser as well as Redis using this SessionID

    Session Created:
    In backend/auth-controller - login method: the method session.setAttribute("user", userName) saves the logged in user session in Redis

    Session Read:
    In SessionInterceptor.java - every incoming HTTP request is monitored:
    1. Listed pages are allowed for all the users
    2. Check if the request contains a valid JWT Token - then allow else deny
    3. Check Session - If the SessionID is valid, user is allowed else redirected to login page
    4. Role based Access - if the user is of type Admin/Employer/Student, only listed       authorised pages can be accessed.

    5. In EmployerController.java in postJob Method -
        session.getAttribute("User") first fetches the user from redis, and then let's the user create a job posting
    6. In EmployerController.java in viewProfile Method -
        session.getAttribute("User) first checks the user session, and then returns the page employer/profile
