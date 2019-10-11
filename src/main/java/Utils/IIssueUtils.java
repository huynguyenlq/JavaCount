package Utils;

import com.atlassian.jira.config.IssueTypeService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.operation.ScreenableIssueOperation;

import java.util.Collection;
import java.util.List;

public interface IIssueUtils {
    Collection<ScreenableIssueOperation> getAllOperation();

    IssueType createIssueType(String name, String description, IssueTypeService.IssueTypeCreateInput.Type type);

    MutableIssue create(IssueInputParameters params);

    IssueType updateIssueType(IssueType issueType, String name, String description, Long avatarId);

    FieldConfigScheme createIssueTypeScheme(String schemeName, String schemeDescription, List<String> optionIDs);

    Boolean deleteIssueType(String typeId);

    MutableIssue update(Long issueId, IssueInputParameters params);

    Boolean delete(Long issueId);

    IssueInputParameters newInputParameters();

    IssueTypeService.IssueTypeCreateInput.Type getIssueTypes();
}
