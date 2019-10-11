package Utils;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.IssueTypeService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.operation.IssueOperations;
import com.atlassian.jira.issue.operation.ScreenableIssueOperation;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;

@ExportAsService({IIssueUtils.class})
@Named
public class IssueUtils implements IIssueUtils {

    @JiraImport
    private JiraAuthenticationContext _loginContext;
    @JiraImport
    private IssueTypeService _issueTypeService;
    @JiraImport
    private IssueService _issueService;
    @JiraImport
    private IssueManager _issueManager;
    @JiraImport
    private IssueTypeSchemeManager _issueTypeSchemeManager;
    @JiraImport
    IssueTypeManager _issueTypeManager;


    @Inject
    public IssueUtils(
            JiraAuthenticationContext jiraAuthenticationContext,
            IssueTypeService issueTypeService,
            IssueManager issueManager,
            IssueService issueService,
            IssueTypeSchemeManager issueTypeSchemeManager,
            IssueTypeManager issueTypeManager
    ){
        _loginContext = jiraAuthenticationContext;
        _issueTypeService = issueTypeService;
        _issueManager = issueManager;
        _issueService = issueService;
        _issueTypeSchemeManager = issueTypeSchemeManager;
        _issueTypeManager = issueTypeManager;
    }

    @Override
    public Collection<ScreenableIssueOperation> getAllOperation() {
        return IssueOperations.getIssueOperations();
    }

    @Override
    public IssueType createIssueType(String name, String description, IssueTypeService.IssueTypeCreateInput.Type type) {
        if(name == null)
            return null;
        IssueTypeService.IssueTypeCreateInput.Builder newTypeBuilder = IssueTypeService.IssueTypeCreateInput.builder();
        newTypeBuilder.setName(name);
        if (type != null)
            newTypeBuilder.setType(type);
        if (description != null)
            newTypeBuilder.setDescription(description);
        IssueTypeService.CreateValidationResult validationResult = _issueTypeService.validateCreateIssueType(_loginContext.getLoggedInUser(), newTypeBuilder.build());
        if (!validationResult.isValid())
            return null;
        IssueTypeService.IssueTypeResult result = _issueTypeService.createIssueType(_loginContext.getLoggedInUser(), validationResult);
        if (result == null)
            return null;
        return result.getIssueType();
    }

    @Override
    public MutableIssue create(IssueInputParameters params) {
        if(params==null)
            return null;
        IssueService.CreateValidationResult validationResult = _issueService.validateCreate(_loginContext.getLoggedInUser(), params);
        if(!validationResult.isValid())
            return null;
        IssueService.IssueResult result = _issueService.create(_loginContext.getLoggedInUser(), validationResult);
        return result.isValid() ? result.getIssue() : null;
    }

    @Override
    public IssueType updateIssueType(IssueType issueType, String name, String description, Long avatarId) {
        IssueTypeService.IssueTypeUpdateInput.Builder updateBuilder = IssueTypeService.IssueTypeUpdateInput.builder();
        if(name!=null)
            updateBuilder.setName(name);
        if(description!=null)
            updateBuilder.setDescription(description);
        if(avatarId!=null)
            updateBuilder.setAvatarId(avatarId);
        updateBuilder.setIssueTypeToUpdateId(Long.parseLong(issueType.getId()));
        IssueTypeService.UpdateValidationResult validationResult = _issueTypeService.validateUpdateIssueType(_loginContext.getLoggedInUser(), issueType.getId(), updateBuilder.build());
        if(!validationResult.isValid())
            return null;
        IssueTypeService.IssueTypeResult result = _issueTypeService.updateIssueType(_loginContext.getLoggedInUser(), validationResult);
        return result.getIssueType();
    }

    @Override
    public FieldConfigScheme createIssueTypeScheme(String schemeName, String schemeDescription, List<String> optionIDs) {
        FieldConfigScheme result = _issueTypeSchemeManager.create(schemeName, schemeDescription, optionIDs);
        return result;
    }

    @Override
    public Boolean deleteIssueType(String typeId) {
        IssueTypeService.IssueTypeDeleteInput deleteParam = new IssueTypeService.IssueTypeDeleteInput(typeId, null);
        IssueTypeService.DeleteValidationResult validationResult = _issueTypeService.validateDeleteIssueType(_loginContext.getLoggedInUser(), deleteParam);
        if(!validationResult.isValid())
            return false;
        _issueTypeService.deleteIssueType(_loginContext.getLoggedInUser(), validationResult);
        return true;
    }

    @Override
    public MutableIssue update(Long issueId, IssueInputParameters params) {
        if(params == null)
            return null;
        IssueService.UpdateValidationResult validationResult = _issueService.validateUpdate(_loginContext.getLoggedInUser(), issueId, params);
        if(!validationResult.isValid())
            return null;
        IssueService.IssueResult result = _issueService.update(_loginContext.getLoggedInUser(), validationResult);
        return result.isValid() ? result.getIssue() : null;
    }

    @Override
    public Boolean delete(Long issueId) {
        IssueService.DeleteValidationResult validationResult = _issueService.validateDelete(_loginContext.getLoggedInUser(), issueId);
        if(!validationResult.isValid())
            return false;
        ErrorCollection result = _issueService.delete(_loginContext.getLoggedInUser(), validationResult);
        if(result.hasAnyErrors())
            return false;
        return true;
    }

    @Override
    public IssueInputParameters newInputParameters() {
        return _issueService.newIssueInputParameters();
    }

    @Override
    public IssueTypeService.IssueTypeCreateInput.Type getIssueTypes() {
        return null;
    }
}
