package exception;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(IOException e) {
        super(e);
    }
}
