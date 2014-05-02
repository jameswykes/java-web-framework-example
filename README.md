Example web application using the Java Web Framework project

### Build / Run

```
./mvnw compile
```

```
./mvnw jetty:start
```

### Viewing the Application

```
http://localhost:8080
```

### Using the framework

The aim of this project is to make it easier and faster to develop web applications in Java. Traditionally web applications in Java require a lot of configuration and setup, and often a lot of boilerplate code is required before even a simple application can be run.

With this framework, you can create and deploy web applications in Java quickly. The ideal use case is for prototyping simple web services.

Build tools bundled with the framework

Simple project setup, versatile, easy to test

### Controllers

Controllers are used to define routes (URLs) that are used in your application, and functionality behind them.

Example: Hello World Controller

```java
public class DefaultController extends Controller {
	public void init() {
		 get("/", new IRouteHandler() {
            public Action handle(Request request, Response response) {
            	return new Raw(“Hello World!”);
            }
});
	}
}
```

#### Defining Routes

Routes are defined in the init method of the Controller. Routes are defined by calling get(), post(), put() or delete() methods.

Parameters:
String route - a pattern used to match the route to a URL
IRouteHandler handler - an implementation of IRouteHandler that defines how the application handles the request

Example Routes

```
Route: /home
URL: /home

Route: /person/:personid
URL: /person/1
Route: /features/:featureid/gallery
URL: /features/123456/gallery
```

Route Parameters are parts of a route that begin with a colon (:), and essentially allow many URLs to be handled by a single route, using variables to control the functionality. In the example above, the parameter :personid is used to match all routes that start with /person, and are followed by a variable that we’ll capture as :personid.

If a route is matched, it’s parameters are collected, and are accessible in the Request object.

For example:

The value of :personid in the route /person/:personid can be accessed as follows:

```java
request.getRouteParam(“:personid”);
```

Inside a controller, it could be used like this:

```java
public class PersonController extends Controller {
	public void init() {
		 get("/person/:personid", new IRouteHandler() {
            public Action handle(Request request, Response response) {
            	return new Raw(“person id: “ + request.getRouteParam(“:personid”));
            }
         });
	}
}
```

Asterisks can be used in a route to match whole parts of a URL, for example:

```
/images/*
/images/*.png
```

#### Route Execution Order

With route parameters and wildcards, it is possible for 2 routes to match the same URL. In this case, the order in which they are defined in the controller dictates which one will be matched. Consider the following example:

```
/contacts/:contactid
/contacts/grid
```

If these two routes are defined in this order in the controller, /contacts/grid will never be called. The routes are checked in order, and the first match will always be the wildcard with /contacts/:contactid

Therefore, the order define them is in reverse:

```
/contacts/grid
/contacts/:contactid
```

If /contacts/grid is requested, it will be matched first. In any other case, the route /contacts/:contactid will be matched.

#### POST Routes

POST routes can be defined by calling the post() method to add the route, rather than get(). See the example below.

```java
public class PersonController extends Controller {
	public void init() {
		 post("/person", new IRouteHandler() {
            public Action handle(Request request, Response response) {
            	return new Raw(“This is a POST route”);
            }
         });
	}
}
```

#### Accessing POST data

The Request object in the route handler contains some methods to easily access POST data. Consider an HTML registration form:

```html
<html>
<body>
<form method=”post” action=”/person”>
<input type=”text” name=”name” />
<input type=”text” name=”email” />
<input type=”submit” value=”Submit” />
</form>
```

Accessing the POST data value “personName” can be accessed as follows:

```java
post("/person", new IRouteHandler() {
    public Action handle(Request request, Response response) {
        String personName = request.getPostParam(“personName”);
        // ..
    }
});
```
#### ViewModels

For cases where you want to access multiple POST values, or populate a ViewModel directly from the POST data itself, you can make use of the getModel() method in the controller. Imagine a ViewModel to store registration information:

```java
public class RegistrationViewModel {
    private String name;
    private String email;
}
```

You could populate this by calling request.getPostParam() for both name and email. Alternatively you could use the getModel method to populate it automatically:

```java
post("/person", new IRouteHandler() {
            public Action handle(Request request, Response response) {
            	RegistrationViewModel vm = getModel(request, RegistrationViewModel.class);
		// ..
            }
});
```

The vm variable has now been populated with the POST data. The framework automatically maps POST data keys to ViewModel fields based on name.

#### Parsing JSON requests

If your POST data is JSON, rather than x-www-form-urlencoded, you can still automatically populate the ViewModel using the getModelFromJson() method:

```java
post("/person", new IRouteHandler() {
    public Action handle(Request request, Response response) {
        RegistrationViewModel vm = getModelFromJson(request, RegistrationViewModel.class);
        // ..
    }
});
```


### Actions

Actions are the objects that are returned from Route Handlers. They contain text or binary data and their contents may eventually be displayed in a browser or consumed by an API (as two examples).

Why Actions?

Introducing the concept of Actions allows you to offload some work onto the framework, and focus on the functionality of the application.

There are currently five types of actions that are available:

View, Html, Text, Json, Binary

Depending on the type of Action you return from a Route Handler, the framework will behave accordingly, for example setting the appropriate Content-type header (if you have not set one yourself) or converting the output to JSON.

#### Text

The most simple action is Text.

Consider the following example:

```java
public class DefaultController extends Controller {
	public void init() {
		 get("/hello-world", new IRouteHandler() {
            public Action handle(Request request, Response response) {
            	return new Text(“Hello World!”);
            }
         });
	}
}
```

Running the application and viewing http://localhost:8080/hello-world will display the text “Hello World!” (unsurprisingly).

Specifying the Action as Text will automatically set the Content-type of the HTTP response to text/plain.

#### Html

Html is another simple action that can be used to return HTML content from a Route Handler. The Content-type of the HTTP response will be automatically set to text/html.

#### Json

JSON is an action that can be used to return a JSON representation of a Java object. Simply supply the object to be returned as the constructor to the Json action.

#### View

View is an action that can be used to merge data into an HTML template file, before being returned to the client. The constructor to the View action can be used to specify an HTML template file to be used for the basis for the response. If an empty constructor is supplied, the framework will try and load a template with the same name as the View class (in the case you are subclassing View).

View contains methods called setValue(), which are used to set values on the View. Before the view is returned to the client, the framework will automatically merge values that are found in the template, with the values supplied to the View object.

The example below demonstrates basic View functionality:

```java
public class HelloController extends Controller {
    @Override
    public void init() {
        get("/", new IRouteHandler() {
            @Override
            public Action handle(Request request, Response response) {
                View home = new View("src/main/webapp/templates/home.html");
                home.setValue("{name}", "world");
                return home;
            }
        });
}
```

You can create a strongly-typed View by extending the View class that will handle a custom data type or model, in the case that the
standard functionality of setValue() is not sufficient.

The example below demonstrates a strongly-typed View:

PersonView.java

```java
public class PersonView extends View {
    private Person model;

    public PersonView(Person model) {
        this.model = model;
    }

}
```

HelloController.java

```java
public class HelloController extends Controller {
    @Override
    public void init() {
        get("/", new IRouteHandler() {
            @Override
            public Action handle(Request request, Response response) {
                Person model = new Person();
                model.setName(“James”);

                PersonView view = new PersonView(model);
                return view;
            }
        });
    }
}
```
