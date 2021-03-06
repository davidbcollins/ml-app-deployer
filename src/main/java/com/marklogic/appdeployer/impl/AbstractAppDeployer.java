package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Abstract base class that just needs the subclass to define the list of Command instances to use. Handles executing
 * commands in sorted order.
 */
public abstract class AbstractAppDeployer extends LoggingObject implements AppDeployer {

    private ManageClient manageClient;
    private AdminManager adminManager;

    /**
     * Can use this constructor when the default config used by ManageClient and AdminManager will work.
     */
    public AbstractAppDeployer() {
        this(new ManageClient(), new AdminManager());
    }

    public AbstractAppDeployer(ManageClient manageClient, AdminManager adminManager) {
        super();
        this.manageClient = manageClient;
        this.adminManager = adminManager;
    }

    /**
     * The subclass just needs to define the list of commands to be invoked.
     *
     * @return
     */
    protected abstract List<Command> getCommands();

	/**
	 * Calls execute on each of the configured commands.
	 *
	 * @param appConfig
	 */
	public void deploy(AppConfig appConfig) {
		List<String> configPaths = new ArrayList<>();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			configPaths.add(configDir.getBaseDir().getAbsolutePath());
		}
        logger.info(format("Deploying app %s with config dirs: %s\n", appConfig.getName(), configPaths));

        List<Command> commands = getCommands();
        Collections.sort(commands, new ExecuteComparator());

        CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

        for (Command command : commands) {
            String name = command.getClass().getName();
            logger.info(format("Executing command [%s] with sort order [%d]", name, command.getExecuteSortOrder()));
            prepareCommand(command, context);
            executeCommand(command, context);
            logger.info(format("Finished executing command [%s]\n", name));
        }

        logger.info(format("Deployed app %s", appConfig.getName()));
    }

	/**
	 * Prepare the given command before either execute or undo is called on it.
	 *
	 * @param command
	 * @param context
	 */
	protected void prepareCommand(Command command, CommandContext context) {
	    if (command instanceof AbstractCommand) {
	    	AppConfig appConfig = context.getAppConfig();
		    String[] filenamesToIgnore = appConfig.getResourceFilenamesToIgnore();
		    Pattern excludePattern = appConfig.getResourceFilenamesExcludePattern();
		    Pattern includePattern = appConfig.getResourceFilenamesIncludePattern();

		    AbstractCommand abstractCommand = (AbstractCommand)command;
		    if (filenamesToIgnore != null) {
			    abstractCommand.setFilenamesToIgnore(filenamesToIgnore);
		    }
		    if (excludePattern != null) {
			    abstractCommand.setResourceFilenamesExcludePattern(excludePattern);
		    }
		    if (includePattern != null) {
			    abstractCommand.setResourceFilenamesIncludePattern(includePattern);
		    }
	    }
    }

	/**
	 * Executes the command, catching an exception if desired.
	 *
	 * @param command
	 * @param context
	 */
	protected void executeCommand(Command command, CommandContext context) {
    	try {
    		command.execute(context);
	    } catch (RuntimeException ex) {
    		if (context.getAppConfig().isCatchDeployExceptions()) {
    			logger.error(format("Command [%s] threw exception that was caught; cause: %s", command.getClass().getName(), ex.getMessage()), ex);
		    } else {
    			throw ex;
		    }
	    }
    }

	/**
	 * Calls undo on each of the configured commands that implements the UndoableCommand interface.
	 *
	 * @param appConfig
	 */
	public void undeploy(AppConfig appConfig) {
		List<String> configPaths = new ArrayList<>();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			configPaths.add(configDir.getBaseDir().getAbsolutePath());
		}
        logger.info(format("Undeploying app %s with config dirs: %s\n", appConfig.getName(), configPaths));

        List<Command> commands = getCommands();

        List<UndoableCommand> undoableCommands = new ArrayList<UndoableCommand>();
        for (Command command : commands) {
            if (command instanceof UndoableCommand) {
                undoableCommands.add((UndoableCommand) command);
            }
        }

        Collections.sort(undoableCommands, new UndoComparator());
        CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

        for (UndoableCommand command : undoableCommands) {
            String name = command.getClass().getName();
            logger.info(format("Undoing command [%s] with sort order [%d]", name, command.getUndoSortOrder()));
            prepareCommand(command, context);
            undoCommand(command, context);
            logger.info(format("Finished undoing command [%s]\n", name));
        }

        logger.info(format("Undeployed app %s", appConfig.getName()));
    }

	/**
	 * Calls undo on the command, catching an exception if desired.
	 *
	 * @param command
	 * @param context
	 */
	protected void undoCommand(UndoableCommand command, CommandContext context) {
		try {
			command.undo(context);
		} catch (RuntimeException ex) {
			if (context.getAppConfig().isCatchUndeployExceptions()) {
				logger.error(format("Command [%s] threw exception that was caught; cause: %s", command.getClass().getName(), ex.getMessage()), ex);
			} else {
				throw ex;
			}
		}
	}
}

class ExecuteComparator implements Comparator<Command> {
    @Override
    public int compare(Command o1, Command o2) {
        return o1.getExecuteSortOrder().compareTo(o2.getExecuteSortOrder());
    }
}

class UndoComparator implements Comparator<UndoableCommand> {
    @Override
    public int compare(UndoableCommand o1, UndoableCommand o2) {
        return o1.getUndoSortOrder().compareTo(o2.getUndoSortOrder());
    }
}
