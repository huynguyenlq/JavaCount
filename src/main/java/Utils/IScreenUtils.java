package Utils;

import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeEntity;
import com.atlassian.jira.issue.operation.ScreenableIssueOperation;
import com.atlassian.jira.project.Project;

import java.util.List;

public interface IScreenUtils {
    List<FieldScreenTab> getAllScreenTab();

    FieldScreenTab createScreenTab(FieldScreen screen, String name);

    FieldScreenSchemeItem createSchemeItem(FieldScreen screen, FieldScreenScheme scheme, ScreenableIssueOperation operation);

    FieldScreenScheme createScreenScheme(String name, String description);

    FieldScreen createFieldScreen(String name, String description);

    IssueTypeScreenScheme createScreenIssueScheme(String name, String description);

    IssueTypeScreenSchemeEntity createScreenIssueSchemeEntity(IssueTypeScreenScheme issueTypeScreenScheme, FieldScreenScheme screenScheme, String issueTypeId);

    void addIssueTypeScreenToProject(IssueTypeScreenScheme scheme, Project project);
}
