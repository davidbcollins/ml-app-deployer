package com.rjrudin.marklogic.appdeployer.command.databases;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.rest.util.Fragment;

/**
 * The REST API command can be used to create a server with a content database, but that doesn't give any control over
 * the details of the forests. DeployForestsCommand can be used for that kind of control.
 */
public class CreateDatabaseWithCustomForestsTest extends AbstractAppDeployerTest {

    @Test
    public void contentDatabaseWithNoForestFile() {
        // We want both main and test databases
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

        final int numberOfForests = 4;

        DeployContentDatabasesCommand command = new DeployContentDatabasesCommand();
        command.setForestsPerHost(numberOfForests);
        command.setForestFilename(null);

        initializeAppDeployer(command, new DeploySchemasDatabaseCommand(), new DeployTriggersDatabaseCommand());

        ForestManager forestMgr = new ForestManager(manageClient);
        DatabaseManager dbMgr = new DatabaseManager(manageClient);

        try {
            appDeployer.deploy(appConfig);

            assertTrue(dbMgr.exists(appConfig.getContentDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getTestContentDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getTriggersDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getSchemasDatabaseName()));

            Fragment mainDb = dbMgr.getAsXml(appConfig.getContentDatabaseName());
            Fragment testDb = dbMgr.getAsXml(appConfig.getTestContentDatabaseName());

            // Assert that the content forests and test content forests were all created
            for (int i = 1; i <= numberOfForests; i++) {
                String mainForestName = appConfig.getContentDatabaseName() + "-" + i;
                assertTrue(forestMgr.exists(mainForestName));
                assertTrue(mainDb.elementExists(format("//db:relation[db:nameref = '%s']", mainForestName)));

                String testForestName = appConfig.getTestContentDatabaseName() + "-" + i;
                assertTrue(forestMgr.exists(testForestName));
                assertTrue(testDb.elementExists(format("//db:relation[db:nameref = '%s']", testForestName)));
            }

        } finally {
            undeploySampleApp();

            assertFalse(dbMgr.exists(appConfig.getContentDatabaseName()));
            assertFalse(dbMgr.exists(appConfig.getTestContentDatabaseName()));
            assertFalse(dbMgr.exists(appConfig.getTriggersDatabaseName()));
            assertFalse(dbMgr.exists(appConfig.getSchemasDatabaseName()));

            for (int i = 1; i <= numberOfForests; i++) {
                assertFalse(forestMgr.exists(appConfig.getContentDatabaseName() + "-1"));
                assertFalse(forestMgr.exists(appConfig.getTestContentDatabaseName() + "-1"));
            }
        }
    }
}
