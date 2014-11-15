package controllers;

import com.github.amertum.springframework.security.play2.EnableSessionTimeout;
import com.github.amertum.springframework.security.play2.EnableSpringSecurity;
import com.github.amertum.springframework.security.play2.context.HttpSessionSecurityContextRepository;
import models.MemberAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.loginPage;

import static com.github.amertum.springframework.security.play2.SpringSecurityAction.requestContinueUrlOrDefaultRoute;

@EnableSessionTimeout(redirectToConfiguredPageOnTimeout = false)
@EnableSpringSecurity
public class Auth
        extends Controller {

    public Auth(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Result showLogin() {
        logger.info("Auth.showLogin");

        if (authenticationTrustResolver.isAnonymous(SecurityContextHolder.getContext().getAuthentication())) {
            logger.debug("ShowLogin, not authenticated : show login page");

            final Form<MemberAuthenticationToken> form = Form.form(MemberAuthenticationToken.class);

            return ok(loginPage.render(form));
        }

        return redirect(requestContinueUrlOrDefaultRoute(request(), routes.Main.publicPage()));
    }

    public Result login() {
        logger.info("Auth.login");

        Form<MemberAuthenticationToken> form = Form.form(MemberAuthenticationToken.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(loginPage.render(form));
        }

        MemberAuthenticationToken token = form.get();

        try {
            final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(token.pseudo, token.secretKey));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (AuthenticationException e) {
            HttpSessionSecurityContextRepository.setAuthenticationFailure();

            return badRequest(loginPage.render(form));
        }

        return redirect(requestContinueUrlOrDefaultRoute(request(), routes.Main.publicPage()));
    }

    public Result logout() {
        logger.info("Auth.logout");

        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();

        return redirect(routes.Main.publicPage());
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthenticationManager authenticationManager;
    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

}