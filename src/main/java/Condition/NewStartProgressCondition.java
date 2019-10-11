package Condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.opensymphony.module.propertyset.PropertySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.StringTokenizer;

@Scanned
public class NewStartProgressCondition extends AbstractJiraCondition
{
    private static final Logger log = LoggerFactory.getLogger(NewStartProgressCondition.class);
    @JiraImport
    private IssueManager _issueManager;
    public static final String FIELD_WORD = "word";

    public NewStartProgressCondition(IssueManager issueManager){
        _issueManager = issueManager;
    }

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps)
    {
        Issue issue = getIssue(transientVars);
        Issue parentIssue = issue.getParentObject();
        if(parentIssue == null)
            return true;

        String statuses = (String) args.get("statuses");
                StringTokenizer st = new StringTokenizer(statuses, ",");

                while (st.hasMoreTokens()) {
                    String statusId = st.nextToken();
                    if (parentIssue.getStatus().getId().equals(statusId)) {
                        return true;
            }
        }

        return false;
    }
}
