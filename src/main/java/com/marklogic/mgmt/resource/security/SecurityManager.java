package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import org.springframework.http.ResponseEntity;

public class SecurityManager extends AbstractManager {
	private ManageClient manageClient;

	private static String SECURITY_OPERATION_PATH = "/manage/v2/security";

	public SecurityManager(ManageClient manageClient) {
		this.manageClient = manageClient;
	}


	public ResponseEntity<String> performSecurityOperation(String payload) {
		return postPayload(manageClient, SECURITY_OPERATION_PATH, payload);
	}

	public ResponseEntity<String> importWallet(String filename, String password) {
		String payload =
			new StringBuilder("{\"operation\":\"import-wallet\", \"filename\":\"")
				.append(filename)
				.append("\", \"password\":\"")
				.append(password)
				.append("\"}")
				.toString();
		return performSecurityOperation(payload);
	}

	public ResponseEntity<String> exportWallet(String filename, String password) {
		String payload =
			new StringBuilder("{\"operation\":\"export-wallet\", \"filename\":\"")
				.append(filename)
				.append("\", \"password\":\"")
				.append(password)
				.append("\"}")
				.toString();
		return performSecurityOperation(payload);
	}

	public ResponseEntity<String> rotateConfigEncryptionKey() {
		return performSecurityOperation("{\"operation\": \"rotate-config-encryption-key\"}");
	}

	public ResponseEntity<String> rotateDataEncryptionKey() {
		return performSecurityOperation("{\"operation\": \"rotate-data-encryption-key\"}");
	}

	public ResponseEntity<String> rotateLogsEncryptionKey() {
		return performSecurityOperation("{\"operation\": \"rotate-logs-encryption-key\"}");
	}

}
