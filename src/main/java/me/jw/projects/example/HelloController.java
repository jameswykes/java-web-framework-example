package me.jw.projects.example;

import me.jw.mvc.core.*;

public class HelloController extends Controller {
    @Override
    public void init() {
        get("/", new IRouteHandler() {
            @Override
            public Action handle(Request request, Response response) {
                View home = new View("src/main/webapp/templates/home.html");
                home.setValue("{name}", "james");
                return home;
            }
        });
    }
}
