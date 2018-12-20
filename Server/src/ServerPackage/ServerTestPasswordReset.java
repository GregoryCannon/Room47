package ServerPackage;

import org.junit.Test;

import static SSLPackage.Action.REQUEST_TEMP_PASSWORD;
import static SSLPackage.ServerPacket.REQUEST_TEMP_PASSWORD_SUCCESSFUL;
import static ServerPackage.ServerTestAccountData.GREG_USERNAME;

/**
 * Created by Greg on 12/19/18.
 */
public class ServerTestPasswordReset extends ServerTest{

    @Test
    public void canResetPassword() {
        // Register Greg
        testAction(REQUEST_TEMP_PASSWORD, GREG_USERNAME, null, null, null, REQUEST_TEMP_PASSWORD_SUCCESSFUL);
    }
}
