package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.unsecuredPage;

public class NotSecured
        extends Controller {

    public Result unsecuredPage() {
        logger.info("NotSecured.unsecuredPage");

        return ok(unsecuredPage.render());
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}