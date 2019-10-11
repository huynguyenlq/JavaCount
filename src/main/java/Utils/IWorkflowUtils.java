package Utils;

import com.atlassian.jira.workflow.JiraWorkflow;

public interface IWorkflowUtils {
    JiraWorkflow ImportFromXMLFile(String pathOfFile,String workflowName);
}
