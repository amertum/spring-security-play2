package controllers;

import com.github.amertum.springframework.security.play2.EnableSessionTimeout;
import com.github.amertum.springframework.security.play2.EnableSpringSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.anonymousPage;
import views.html.publicPage;
import views.html.securedPage;

@EnableSessionTimeout
@EnableSpringSecurity
public class Main
        extends Controller {

    public Result index() {
        logger.info("Main.publicPage");

        return redirect(routes.Main.publicPage());
    }

    public Result publicPage() {
        logger.info("Main.publicPage");

        return ok(publicPage.render());
    }

    @PreAuthorize("hasRole('ROLE_ANONYMOUS')")
    public Result anonymousPage() {
        logger.info("Main.anonymousPage");

        return ok(anonymousPage.render());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public Result securedUserPage() {
        logger.info("Main.securedUserPage");

        return ok(securedPage.render("user"));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result securedAdminPage() {
        logger.info("Main.securedAdminPage");

        return ok(securedPage.render("admin"));
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}