package Utils;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.workflow.ConfigurableJiraWorkflow;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.WorkflowLoader;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;

@ExportAsService({IWorkflowUtils.class})
@Named
public class WorkflowUtils implements IWorkflowUtils {

    @JiraImport
    PluginAccessor _pluginAccessor;
    @JiraImport
    WorkflowManager _workflowManager;
    @JiraImport
    JiraAuthenticationContext _loginContext;

    @Inject
    public WorkflowUtils(
        PluginAccessor pluginAccessor,
        WorkflowManager workflowManager,
        JiraAuthenticationContext jiraAuthenticationContext
    ){
        _pluginAccessor = pluginAccessor;
        _workflowManager = workflowManager;
        _loginContext = jiraAuthenticationContext;
    }

    @Override
    public JiraWorkflow ImportFromXMLFile(String pathOfFile,String workflowName) {
        if(pathOfFile == null||
        workflowName == null)
            return null;
        try {
            //JiraWorkflow existsWorkflow = _workflowManager
            WorkflowDescriptor workflowDescriptor;
            InputStream inputStream = _pluginAccessor.getDynamicResourceAsStream(pathOfFile);
            workflowDescriptor = WorkflowLoader.load(inputStream, true);
            ConfigurableJiraWorkflow newWorkflow = new ConfigurableJiraWorkflow(workflowName, _workflowManager);
            newWorkflow.setDescriptor(workflowDescriptor);
            _workflowManager.createWorkflow(_loginContext.getLoggedInUser(), newWorkflow);
            return newWorkflow;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
