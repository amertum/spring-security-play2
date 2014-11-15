import com.github.amertum.springframework.security.play2.context.HttpRequestSecurityContextHolderStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import play.Application;
import play.GlobalSettings;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.lang.reflect.Method;

public class Global
        extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        logger.info("Start application...");

        HttpRequestSecurityContextHolderStrategy.setSecurityStrategy();

        applicationContext = new GenericXmlApplicationContext("classpath:/spring/application-context.xml");
        applicationContext.registerShutdownHook();
    }

    @Override
    public void onStop(Application app) {
        logger.info("Stop application...");
        applicationContext.stop();
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return applicationContext.getBean(controllerClass);
    }

    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        logger.debug("onRequest uri {} to method {}", request.uri(), actionMethod);
        // return applicationContext.getBean(SpringSecurityAction.class);
        return super.onRequest(request, actionMethod);
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable throwable) {
        if (throwable.getCause() instanceof AccessDeniedException) {
            return F.Promise.pure(Controller.unauthorized("UNAUTHORIZED"));
        }

        return super.onError(request, throwable);
    }

    private GenericXmlApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
