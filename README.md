# Room47 #

Language: Java (for server and Android client)

GitHub repo: https://github.com/GregoryCannon/Room47/

## .jar files ##
* activation.jar
* bcprov-jdk15on-159.jar
* commons-lang3-3.8.1.jar
* hamcrest-all-1.3.jar
* junit-4.12.jar
* lettuce-core-5.1.1.RELEASE.jar
* mail.jar
* netty-all-4.1.5.Final.jar
* netty-transport-native-epoll-5.0.0.Alpha2.jar
* reactive-streams-1.0.1.jar
* reactor-core-3.2.1.RELEASE.jar
* rxjava-1.0.2.jar

## Android ##
We used android studio for the android client: https://developer.android.com/studio/

## Redis Database ##
We used Redis for our DB: https://redis.io/

## Students ##
### Register ###

As a Pomona student, you must register with a valid username, password, and student ID. They can also reset their password through a temporary password emailed to their vaild email. 

### Draw a room ### 

To draw a room, a Pomona student must login with their username and password. Then, they can view a list of available rooms and choose a room to draw into during their alloted room draw time.

## Admins ##

Admins must register/login with a valid username, password, and ID. Admins can read any rooming-related data and modify any students’ rooming assignments data at any time. They can also reset their password through a temporary password emailed to their valid email.  

## Audit Logs ##
The following audit logs are available:
* Who registered with a certain ID
* All logs for 1 room
* All logs for 1 student
* All logs for 1 admin
* All logs for a time frame
## Error Prone ##
The results of our static analysis tool 'ErrorProne' is in the file 'errorProneResults.txt'

## Code Coverage ##
Code coverage can be found in the file 'Code Coverage.png'



