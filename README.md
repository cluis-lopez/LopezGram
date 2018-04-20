Tired of Facebook stealing your personal data?

Why not creating your own?

This is a small Facebook-like project made of a single-page HTML5 application
and a bunch of servlets to deploy into Google App Engine.

Goals:

- **Serverless**: server-side is a bunch of Java servlets that you may deploy on any cloud provider that allows standard J2EE (Tomcat, Jetty, etc.)

- **MicroServices**: each servlet returns one short and specific function.

- **Stateless**: servlets do not maintain any state (session) information.

- **Secure**: use a server generated token to authorize each transaction from clients.

- **Data Management** is disagregated from the main code. A single Java class comprised of static procedures (DataStore.java) is provided to manage data using Google DataStore. Moving to a different data source (MySQL, Mongo, etc.) should be easy

- **Single Page** : client is a single HTML5 page. Javascript (JQuery) manages the full lifecycle of the client. HTML5 LocalStorage is used to manage auth info and client state.

- **PWA**: final goal is to make this a [Progessive Web App](https://en.wikipedia.org/wiki/Progressive_Web_Apps)  (in progress)
