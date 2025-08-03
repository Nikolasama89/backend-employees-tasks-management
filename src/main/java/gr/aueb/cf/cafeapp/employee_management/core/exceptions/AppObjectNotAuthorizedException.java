package gr.aueb.cf.cafeapp.employee_management.core.exceptions;

/**
 * Thrown when a user attempts an action on an entity
 * for which they lack the required authorization.
 */
public class AppObjectNotAuthorizedException extends EntityGenericException {

    private static final String DEFAULT_CODE = "NotAuthorized";

    public AppObjectNotAuthorizedException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
