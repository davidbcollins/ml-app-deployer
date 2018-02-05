package com.marklogic.appdeployer.command.security;

import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.appdeployer.command.taskservers.UpdateTaskServerCommand;
import com.marklogic.mgmt.resource.taskservers.TaskServerManager;

import com.marklogic.rest.util.Fragment;
import org.junit.Test;
import com.marklogic.mgmt.resource.security.SecurityManager;

public class ManageSecurityTest extends AbstractMgmtTest {

	private static String ENCRYPTION_KEYS_DIRECTORY = "/data/MarkLogic/EncryptionKeys/";

	private static String WALLET_FILE_1 = "exportWalletTest_wallet.txt";
	private static String WALLET_FILE_2 = "importWalletTest_wallet.txt";

	private static String ENCRYPTION_PASSWORD_1 = "1234";

	@Test
	public void exportWalletTest() {
		SecurityManager mgr = new SecurityManager(manageClient);
		mgr.exportWallet(ENCRYPTION_KEYS_DIRECTORY + WALLET_FILE_1, ENCRYPTION_PASSWORD_1);
	}

	@Test
	public void importWalletTest() {
		SecurityManager mgr = new SecurityManager(manageClient);
		mgr.exportWallet(ENCRYPTION_KEYS_DIRECTORY + WALLET_FILE_2, ENCRYPTION_PASSWORD_1);
		mgr.importWallet(ENCRYPTION_KEYS_DIRECTORY + WALLET_FILE_2, ENCRYPTION_PASSWORD_1);
	}

	@Test
	public void rotateConfigEncryptionKey() {
		SecurityManager mgr = new SecurityManager(manageClient);
		mgr.rotateConfigEncryptionKey();
	}

	@Test
	public void rotateDataEncryptionKey() {
		SecurityManager mgr = new SecurityManager(manageClient);
		mgr.rotateDataEncryptionKey();
	}

	@Test
	public void rotateLogsEncryptionKey() {
		SecurityManager mgr = new SecurityManager(manageClient);
		mgr.rotateLogsEncryptionKey();
	}

	/*
    @Test
    public void getHostNamesAndIds() {
        HostManager mgr = new HostManager(manageClient);
        List<String> names = mgr.getHostNames();
        List<String> ids = mgr.getHostIds();

        assertFalse("The list of names should not be empty", names.isEmpty());
        assertFalse("The list of ids should not be empty", ids.isEmpty());
        assertEquals("The lists of names and ids should have the same number of items", names.size(), ids.size());
    }

	*/

	/*
	@Test
	public void test() {
		TaskServerManager mgr = new TaskServerManager(manageClient);

		initializeAppDeployer(new UpdateTaskServerCommand());
		deploySampleApp();

		try {
			Fragment xml = mgr.getPropertiesAsXml();
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:log-errors"));
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:debug-allow"));
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:profile-allow"));
		} finally {
			String payload = "{\n" +
				"\t\"log-errors\": true,\n" +
				"\t\"debug-allow\": true,\n" +
				"\t\"profile-allow\": true\n" +
				"}";

			mgr.updateTaskServer("TaskServer", payload);
			Fragment xml = mgr.getPropertiesAsXml();
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:log-errors"));
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:debug-allow"));
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:profile-allow"));
		}
	}
	*/
}




