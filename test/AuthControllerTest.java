import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class AuthControllerTest {

    @Test
    public void testAnonymousSecurity() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333/unsecured");
            assertThat(browser.title()).isEqualTo("Unsecured");
            assertThat(browser.findFirst("h1").getText()).isEqualTo("Unsecured");

            browser.goTo("http://localhost:3333/public");
            assertThat(browser.title()).isEqualTo("Public");
            assertThat(browser.findFirst("h1").getText()).isEqualTo("Public");

            browser.goTo("http://localhost:3333/anonymous");
            assertThat(browser.title()).isEqualTo("Anonymous");
            assertThat(browser.findFirst("h1").getText()).isEqualTo("Anonymous");

            browser.goTo("http://localhost:3333/secured/user");
            assertThat(browser.title()).isEqualTo("Login");

            browser.goTo("http://localhost:3333/secured/admin");
            assertThat(browser.title()).isEqualTo("Login");
        });

    }

}
