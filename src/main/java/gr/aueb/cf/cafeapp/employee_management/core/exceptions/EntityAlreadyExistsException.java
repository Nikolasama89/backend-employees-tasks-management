package gr.aueb.cf.cafeapp.employee_management.core.exceptions;

public class EntityAlreadyExistsException extends EntityGenericException {
    private static final String DEFAULT_CODE = "EntityAlreadyExists";

    public EntityAlreadyExistsException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
