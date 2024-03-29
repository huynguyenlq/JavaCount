package Utils;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.bc.customfield.CreateValidationResult;
import com.atlassian.jira.bc.customfield.CustomFieldDefinition;
import com.atlassian.jira.bc.customfield.CustomFieldService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;

@ExportAsService({ICustomFieldUtils.class})
@Named
public class CustomFieldUtils implements ICustomFieldUtils {
    private static final Logger log = LoggerFactory.getLogger(CustomFieldUtils.class);
    @JiraImport
    private CustomFieldService _customFieldService;
    @JiraImport
    private CustomFieldManager _customFieldManager;
    @JiraImport
    private JiraAuthenticationContext _loginContext;
    @JiraImport
    private ProjectManager _projectManager;
    private OptionsManager _optionsManager;

    @Inject
    public CustomFieldUtils(
        CustomFieldService customFieldService,
        CustomFieldManager customFieldManager,
        JiraAuthenticationContext jiraAuthenticationContext,
        ProjectManager projectManager
    )
    {
        _customFieldService = customFieldService;
        _customFieldManager = customFieldManager;
        _loginContext = jiraAuthenticationContext;
        _projectManager = projectManager;
        _optionsManager = ComponentAccessor.getOSGiComponentInstanceOfType(OptionsManager.class);
    }

    @Override
    public CustomField create(String name, String description, String typeKey, List<String> optionsForMultiSelect, List<Long> projectId, Boolean isGlobal) {
        if(name == null ||
                typeKey == null)
            return null;
        Collection<CustomField> existsFields = _customFieldManager.getCustomFieldObjectsByName(name);
        if(existsFields!=null && existsFields.size() > 0)
            return existsFields.stream().findFirst().get();
        CustomFieldDefinition.Builder newCustomFieldBuilder = CustomFieldDefinition.builder();
        newCustomFieldBuilder
                .name(name)
                .isGlobal(isGlobal)
                .defaultSearcher()
                .isAllIssueTypes(true)
                .cfType(typeKey);
        if(projectId!=null)
            projectId.forEach(newCustomFieldBuilder::addProjectId);
        if(description != null)
            newCustomFieldBuilder.description(description);
        CustomFieldDefinition newCustomField = newCustomFieldBuilder.build();
        ServiceOutcome<CreateValidationResult> validationCreate = _customFieldService.validateCreate(_loginContext.getLoggedInUser(), newCustomField);
        if(!validationCreate.isValid())
            return null;
        ServiceOutcome<CustomField> result = _customFieldService.create(validationCreate.get());
        if(!result.isValid())
            return null;
        CustomField ret = result.get();
        if((typeKey.contains("multicheckboxes") || typeKey.contains("radiobuttons")) && optionsForMultiSelect != null){
            createOption(ret,optionsForMultiSelect);
        }
        return ret;
    }

    @Override
    public CustomField update(Long customFieldId, String name, String description) {
        if(customFieldId < 1 ||
        name == null)
            return null;
        JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(_loginContext.getLoggedInUser());
        _customFieldService.validateUpdate(jiraServiceContext, customFieldId, name, description, null);
        if(jiraServiceContext.getErrorCollection().hasAnyErrors())
            return null;
        CustomField oldField = _customFieldManager.getCustomFieldObject(customFieldId);
        if(name==null)
            name = oldField.getName();
        if(description==null)
            description = oldField.getDescription();
        _customFieldManager.updateCustomField(customFieldId, name, description, null);
        return _customFieldManager.getCustomFieldObject(customFieldId);
    }

    @Override
    public Boolean delete(Long customFieldId) {
        JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(_loginContext.getLoggedInUser());
        _customFieldService.validateDelete(jiraServiceContext, customFieldId);
        if(jiraServiceContext.getErrorCollection().hasAnyErrors())
            return false;
        CustomField removeItem = _customFieldManager.getCustomFieldObject(customFieldId);
        if(removeItem==null)
            return false;
        try {
            _customFieldManager.removeCustomField(removeItem);
        } catch (RemoveException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<Long> addCustomFieldToScreenTabs(Long customFieldId, List<Long> tabIds) {
        _customFieldService.addToScreenTabs(_loginContext.getLoggedInUser(), customFieldId, tabIds);
        return null;
    }

    @Override
    public List<CustomFieldType<?, ?>> getAllCustomFieldType() {
        return _customFieldManager == null ? null : _customFieldManager.getCustomFieldTypes();
    }

    private void createOption(CustomField customField, List<String> option) {
        FieldConfigSchemeManager fieldConfigSchemeManager =
                ComponentAccessor.getComponent(FieldConfigSchemeManager.class);
        List<FieldConfigScheme> schemes =
                fieldConfigSchemeManager.getConfigSchemesForField(customField);
        FieldConfigScheme fieldConfigScheme = schemes.get(0);
        FieldConfig config = fieldConfigScheme.getOneAndOnlyConfig();
        _optionsManager.createOptions(config, null, Long.valueOf(1), option);
    }
}
