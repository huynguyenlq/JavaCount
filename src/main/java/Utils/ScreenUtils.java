package Utils;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.fields.screen.*;
import com.atlassian.jira.issue.fields.screen.issuetype.*;
import com.atlassian.jira.issue.operation.ScreenableIssueOperation;
import com.atlassian.jira.project.Project;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@ExportAsService({IScreenUtils.class})
@Named
public class ScreenUtils implements IScreenUtils {

    @JiraImport
    private FieldScreenManager _fieldScreenManager;
    @JiraImport
    FieldScreenSchemeManager _fieldScreenSchemeManager;
    @JiraImport
    IssueTypeScreenSchemeManager _issueTypeScreenSchemeManager;
    @JiraImport
    ConstantsManager _constantsManager;

    @Inject
    public ScreenUtils(
            FieldScreenManager fieldScreenManager,
            FieldScreenSchemeManager fieldScreenSchemeManager,
            IssueTypeScreenSchemeManager issueTypeScreenSchemeManager,
            ConstantsManager constantsManager
    ) {
        _fieldScreenManager = fieldScreenManager;
        _fieldScreenSchemeManager = fieldScreenSchemeManager;
        _issueTypeScreenSchemeManager = issueTypeScreenSchemeManager;
        _constantsManager = constantsManager;
    }

    public List<FieldScreenTab> getAllScreenTab() {
        ArrayList<FieldScreenTab> tabs = new ArrayList<FieldScreenTab>();
        _fieldScreenManager.getFieldScreens().forEach(e->{
            tabs.addAll(_fieldScreenManager.getFieldScreenTabs(e));
        });
        return tabs;
    }

    @Override
    public FieldScreenTab createScreenTab(FieldScreen screen, String name) {
        FieldScreenTab newTab = new FieldScreenTabImpl(_fieldScreenManager);
        newTab.setFieldScreen(screen);
        newTab.setName(name);
        _fieldScreenManager.createFieldScreenTab(newTab);
        return newTab;
    }

    @Override
    public FieldScreenSchemeItem createSchemeItem(FieldScreen screen, FieldScreenScheme scheme, ScreenableIssueOperation operation) {
        FieldScreenSchemeItem newSchemeItem = new FieldScreenSchemeItemImpl(_fieldScreenSchemeManager, _fieldScreenManager);
        newSchemeItem.setFieldScreen(screen);
        if (scheme != null)
            newSchemeItem.setFieldScreenScheme(scheme);
        if (operation != null)
            newSchemeItem.setIssueOperation(operation);
        _fieldScreenSchemeManager.createFieldScreenSchemeItem(newSchemeItem);
        return newSchemeItem;
    }

    @Override
    public FieldScreenScheme createScreenScheme(String name, String description) {
        FieldScreenScheme newScheme = new FieldScreenSchemeImpl(_fieldScreenSchemeManager);
        newScheme.setName(name);
        if(description!=null)
            newScheme.setDescription(description);
        _fieldScreenSchemeManager.createFieldScreenScheme(newScheme);
        return newScheme;
    }

    @Override
    public FieldScreen createFieldScreen(String name, String description) {
        FieldScreen newScreen = new FieldScreenImpl(_fieldScreenManager);
        newScreen.setName(name);
        newScreen.setId((long) 900);
        if(description!=null)
            newScreen.setDescription(description);
        _fieldScreenManager.createFieldScreen(newScreen);
        return newScreen;
    }

    @Override
    public IssueTypeScreenScheme createScreenIssueScheme(String name, String description) {
        IssueTypeScreenScheme newTypeScheme = new IssueTypeScreenSchemeImpl(_issueTypeScreenSchemeManager);
        newTypeScheme.setName(name);
        if(description!=null)
            newTypeScheme.setDescription(description);
        _issueTypeScreenSchemeManager.createIssueTypeScreenScheme(newTypeScheme);
        return newTypeScheme;
    }

    @Override
    public IssueTypeScreenSchemeEntity createScreenIssueSchemeEntity(IssueTypeScreenScheme issueTypeScreenScheme, FieldScreenScheme screenScheme, String issueTypeId) {
        IssueTypeScreenSchemeEntity schemeEntity = new IssueTypeScreenSchemeEntityImpl(_issueTypeScreenSchemeManager, _fieldScreenSchemeManager, _constantsManager);
        if(issueTypeId != null)
            schemeEntity.setIssueTypeId(issueTypeId);
        schemeEntity.setFieldScreenScheme(screenScheme);
        schemeEntity.setIssueTypeScreenScheme(issueTypeScreenScheme);
        _issueTypeScreenSchemeManager.createIssueTypeScreenSchemeEntity(schemeEntity);
        return schemeEntity;
    }

    @Override
    public void addIssueTypeScreenToProject(IssueTypeScreenScheme scheme, Project project) {
        _issueTypeScreenSchemeManager.addSchemeAssociation(project, scheme);
    }
}
