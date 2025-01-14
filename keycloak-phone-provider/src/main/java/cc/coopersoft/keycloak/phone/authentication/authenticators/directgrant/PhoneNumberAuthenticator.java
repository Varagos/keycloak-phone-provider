package cc.coopersoft.keycloak.phone.authentication.authenticators.directgrant;

import cc.coopersoft.keycloak.phone.utils.UserUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.events.Errors;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.Response;

public class PhoneNumberAuthenticator extends BaseDirectGrantAuthenticator {

    private static final Logger logger = Logger.getLogger(PhoneNumberAuthenticator.class);

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        user.addRequiredAction("PHONE_NUMBER_GRANT_CONFIG");
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        System.out.println("PhoneNumberAuthenticator=>authenticate: " + context);

        String phoneNumber = getPhoneNumber(context);
        System.out.println("Authenticating number: " + phoneNumber);

        if (Validation.isBlank(phoneNumber)){
            System.out.println("Validation.isBlank(phoneNumber)");
            invalidCredentials(context);
            return;
        }
        UserModel user = UserUtils.findUserByPhone(context.getSession().users(),context.getRealm(),phoneNumber);
        if (user == null) {
            System.out.println("user == null");
            invalidCredentials(context);
            return;
        }

        logger.info("Grant authenticator valid phone success");
        context.setUser(user);
        context.success();
    }
}
