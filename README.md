# A simple chat app
I never really build a complete web project.
I wanted to keep the setup simple, so this is just spring boot with a postgres database.

# Project scope
- ## Authentication with JWT
- ## Simple REST endpoint 
	This means that the chat is polling based.
	A websocket connection would make sense but I'm more interested in learning REST in spring boot than making this
	API production ready

- ## Secure storage of user account data
	I want to implement a secure user registration and login system.
	I'm intentionally ignoring botting and other spam issues here.
	It is more about user data beeing stored in a safe way.
	
	### Message encryption
	I haven't really thought about it. It's probably a client siede thing.
	But the server should have some basic encryption just in case of a databreach.


# TODO
 - ## some of the test are failing and stuff isn't working correctly.
 - ## take a closer look at how the data repository layer fetches data. Do i need some form of in memory storage or does hibernate do that out of the box?
 - ## sessions. I currently have 1 JWT that expires after 1 hour. logging in again seems unpractical. So second JWT with a 24 hour live time could be used to keep the session alive.
 - ## the API paths and some functionalities need some refining.
 - ## test are not enough and written a bit clunky. Also there are currently only integration tests. I would like to add some more tests that only test a specific part.
 - ## dockerfiles could probably be better
 - ## I think spring can generate a keypair for the authentication on start up so there is probably no need to have a script for that.
   
 - ## I don't think I need adminer anymore.
 
