package com.someco.action.executer;

import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author vishal.zanzrukia
 *
 */
public class AutoBackupActionExecuter extends ActionExecuterAbstractBase {
	
	private boolean isActive;
	
	private static Log logger = LogFactory.getLog(AutoBackupActionExecuter.class);
    public static final String NAME = "auto-backup";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    
    private FileFolderService fileFolderService;
    
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        paramList.add(
        	new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER,
        								DataTypeDefinition.NODE_REF,
        								true,
        								getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
    }

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef) {
    	
    	logger.info("Enter -> MoveReplacedActionExecuter.executeImpl");
    	if(getIsActive()){
    		NodeRef destinationNodeRef = (NodeRef) ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
        	
        	FileInfo sourceFileInfo = fileFolderService.getFileInfo(actionedUponNodeRef);
        	String sourceFileName = sourceFileInfo.getName();
        	
        	try {
        		if(!sourceFileInfo.isFolder()){
        			fileFolderService.copy(actionedUponNodeRef, destinationNodeRef,  sourceFileName + ".backup");
        			logger.info("Successfully backup taken for file : " + sourceFileName);	
        		}
        		else{
        			logger.warn("We have skipped the directory for backup : " + sourceFileName);
        		}
    		} catch (FileExistsException | FileNotFoundException e) {
    			logger.error("Error while taking backup for file : " + sourceFileName, e);
    		}
    	}
    	else{
    		logger.warn("This functionality is currently not available..!");
    	}

        logger.info("Exit -> MoveReplacedActionExecuter.executeImpl");
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
}

