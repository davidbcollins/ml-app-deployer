package com.marklogic.appdeployer.command.groups;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.groups.GroupManager;

public class CreateGroupsCommand extends AbstractResourceCommand {

    public CreateGroupsCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_GROUPS);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getBaseDir(), "groups");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new GroupManager(context.getManageClient());
    }

    /**
     * While groups should be created right away, we don't want to undelete them until the very end, as we won't be 
     * able to delete one unless all of its app servers have been deleted.
     */
    @Override
    public Integer getUndoSortOrder() {
        return Integer.MAX_VALUE;
    }

}
