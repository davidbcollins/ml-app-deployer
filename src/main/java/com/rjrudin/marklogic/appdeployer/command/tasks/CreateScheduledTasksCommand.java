package com.rjrudin.marklogic.appdeployer.command.tasks;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.tasks.TaskManager;

public class CreateScheduledTasksCommand extends AbstractResourceCommand {

    private String groupName;

    public CreateScheduledTasksCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_SCHEDULED_TASKS);
    }

    /**
     * While scheduled tasks are usually created at the end of a deployment process, it's typical to delete them at the
     * start of an undeployment, since nothing depends on them.
     */
    @Override
    public Integer getUndoSortOrder() {
        return 0;
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getBaseDir(), "tasks");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        TaskManager mgr = new TaskManager(context.getManageClient());
        if (groupName != null) {
            mgr.setGroupName(groupName);
        }
        return mgr;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}