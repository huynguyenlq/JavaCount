package wFlows;

import Utils.*;
import com.atlassian.jira.bc.customfield.CustomFieldService;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.IssueTypeService;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.*;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.type.ProjectTypeManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.edit.WorkflowStatuses;
import com.atlassian.jira.workflow.edit.WorkflowTransitions;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
@Scanned
public class OnSubTaskDone extends AbstractJiraFunctionProvider
{
    private static final Logger log = LoggerFactory.getLogger(OnSubTaskDone.class);
    public static final String FIELD_MESSAGE = "messageField";
    @JiraImport
    IssueManager _issueManager;
    @JiraImport
    JiraAuthenticationContext _loginContext;
    @JiraImport
    ConstantsManager _constantsManager;
    @JiraImport
    WorkflowManager _workflowManager;
    @JiraImport
    StatusManager _statusManager;
    @JiraImport
    WorkflowStatuses _workflowStatuses;
    @JiraImport
    WorkflowTransitions _workflowTransitions;
    @JiraImport
    ProjectManager _projectManager;
    @JiraImport
    ProjectTypeManager _projectTypeManager;
    @JiraImport
    FieldScreenManager _fieldScreenManager;
    @JiraImport
    CustomFieldManager _customFieldManager;
    @JiraImport
    CustomFieldService _customFieldService;
    @JiraImport
    FieldScreenSchemeManager _fieldScreenSchemeManager;
    @JiraImport
    IssueTypeService _issueTypeService;
    @JiraImport
    IssueTypeManager _issueTypeManager;
    @JiraImport
    PluginAccessor _p;
    ICustomFieldUtils _iCustomFieldUtils;
    IScreenUtils _iScreenUtils;
    IIssueUtils _iIssueUtils;
    IProjectUtils _iProjectUtils;
    IWorkflowUtils _iWorkflowUtils;

    @Inject
    public OnSubTaskDone(IssueManager issueManager,
                         JiraAuthenticationContext loginContext,
                         ConstantsManager constantsManager,
                         WorkflowManager workflowManager,
                         StatusManager statusManager,
                         WorkflowStatuses workflowStatuses,
                         WorkflowTransitions workflowTransitions,
                         ProjectManager projectManager,
                         ProjectTypeManager projectTypeManager,
                         FieldScreenManager fieldScreenManager,
                         CustomFieldManager customFieldManager,
                         CustomFieldService customFieldService,
                         FieldScreenSchemeManager fieldScreenSchemeManager,
                         IssueTypeService issueTypeService,
                         ICustomFieldUtils iCustomFieldUtils,
                         IScreenUtils iScreenUtils,
                         IIssueUtils iIssueUtils,
                         IssueTypeManager issueTypeManager,
                         IProjectUtils iProjectUtils,
                         PluginAccessor p,
                         IWorkflowUtils iWorkflowUtils){
        _issueManager = issueManager;
        _loginContext = loginContext;
        _constantsManager = constantsManager;
        _workflowManager = workflowManager;
        _statusManager = statusManager;
        _workflowStatuses = workflowStatuses;
        _workflowTransitions = workflowTransitions;
        _projectManager = projectManager;
        _projectTypeManager = projectTypeManager;
        _fieldScreenManager = fieldScreenManager;
        _customFieldManager = customFieldManager;
        _customFieldService = customFieldService;
        _fieldScreenSchemeManager = fieldScreenSchemeManager;
        _issueTypeService = issueTypeService;
        _iCustomFieldUtils = iCustomFieldUtils;
        _iScreenUtils = iScreenUtils;
        _iIssueUtils = iIssueUtils;
        _issueTypeManager = issueTypeManager;
        _iProjectUtils = iProjectUtils;
        _p = p;
        _iWorkflowUtils = iWorkflowUtils;
    }

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException
    {
        MutableIssue issue = getIssue(transientVars);
        MutableIssue parentIssue = _issueManager.getIssueObject(issue.getParentId());
        if(parentIssue==null)
            return;
        Collection<Status> statuses = _constantsManager.getStatuses();
        Status closedStatus = statuses.stream().filter(e -> Integer.parseInt(e.getId())==IssueFieldConstants.CLOSED_STATUS_ID).findFirst().get();
        Status openStep = statuses.stream().filter(e -> Integer.parseInt(e.getId())==IssueFieldConstants.OPEN_STATUS_ID).findFirst().get();
        JiraWorkflow parentWorkflow = _workflowManager.getWorkflow(parentIssue);
        Collection<ActionDescriptor> parentActions = parentWorkflow.getAllActions();
        // look for the closed transition
        ActionDescriptor closeAction = parentActions.stream().filter(e -> e.getName().contains("Close Issue")).findFirst().get();
        if (closeAction != null) {
            ApplicationUser currentUser = _loginContext.getLoggedInUser();
            IssueService issueService = ComponentAccessor.getIssueService();
            IssueInputParameters parameters = issueService.newIssueInputParameters();
            parameters.setStatusId(closedStatus.getId());
            IssueService.TransitionValidationResult validationResult =
                    issueService.validateTransition(currentUser, parentIssue.getId(),
                            closeAction.getId(), parameters);
            if(validationResult.isValid()) {
                /*IssueService.IssueResult result = issueService.transition(currentUser, validationResult);
                Status st = _statusManager.createStatus("New Status Custom", "New Status Create By Code", closedStatus.getIconUrl(),closedStatus.getStatusCategory());
                ConfigurableJiraWorkflow newWFlow = new ConfigurableJiraWorkflow("New Test WorkFlow", _workflowManager);
                _workflowManager.createWorkflow(_loginContext.getLoggedInUser(), newWFlow);
                JiraWorkflow ret = _workflowManager.getWorkflow(newWFlow.getName());
                _workflowStatuses.addStatusToWorkflow(st, ret.getName());
                ServiceOutcome<TransitionData> r = _workflowTransitions.addTransitionToWorkflow(
                        "New Test Transistion",
                        "descritopn", (long) 0,
                        Integer.parseInt(openStep.getId()),
                        Integer.parseInt(st.getId()),
                        ret.getName());*/
                /*List<ProjectType> types = _projectTypeManager.getAllProjectTypes();
                ProjectCreationData pData = new ProjectCreationData.Builder()
                        .withName("new test projet")
                        .withKey("NTP")
                        .withLead(_loginContext.getLoggedInUser())
                        .withType(types.get(0).getKey())
                        .build();
                _projectManager.createProject(_loginContext.getLoggedInUser(), pData);*/
                /*Project p = _projectManager.getProjects().stream().filter(e -> e.getKey().equals("NTP")).findFirst().get();
                _projectManager.removeProject(p);*/
                /*ArrayList<String> issueTypeKeys = new ArrayList<String>();
                Iterable<IssueType> issueTypes = _issueTypeService.getIssueTypes(_loginContext.getLoggedInUser());
                Project p = _projectManager.getProjects().stream().filter(e -> e.getKey().equals("TEST")).findFirst().get();
                FieldScreen newField = new FieldScreenImpl(_fieldScreenManager);
                List<CustomFieldType<?, ?>> types = _customFieldManager.getCustomFieldTypes();
                CustomFieldType type = types.stream().filter(e -> e.getDescriptor().getKey().equals("new-custom-type")).findFirst().get();
                CustomFieldDefinition.Builder tmp =
                        CustomFieldDefinition.builder().name("new Custom field by code")
                                .cfType(type.getKey())
                                .addProjectId(p.getId())
                                .defaultSearcher();
                issueTypes.forEach(e->tmp.addIssueTypeId(e.getId()));
                ServiceOutcome<CreateValidationResult> srvOutcome = _customFieldService.validateCreate(
                        _loginContext.getLoggedInUser(),tmp.build()
                );
                _customFieldService.create(srvOutcome.get());
                newField.setName("New Screen For Custom Field By Code");
                _fieldScreenManager.createFieldScreen(newField);
                FieldScreenScheme newScreenScheme = new FieldScreenSchemeImpl(_fieldScreenSchemeManager);
                FieldScreenSchemeItem sItem = new FieldScreenSchemeItemImpl(_fieldScreenSchemeManager, _fieldScreenManager);
                sItem.setFieldScreen(newField);
                newScreenScheme.setName("New Scheme By Code");
                newScreenScheme.setDescription("Description By ...");
                _fieldScreenSchemeManager.createFieldScreenScheme(newScreenScheme);
                sItem.setFieldScreen(newField);
                sItem.setFieldScreenScheme(newScreenScheme);
                _fieldScreenSchemeManager.createFieldScreenSchemeItem(sItem);
                FieldScreenTab tab = new FieldScreenTabImpl(_fieldScreenManager);
                tab.setName("Fields");
                tab.setFieldScreen(newField);
                _fieldScreenManager.createFieldScreenTab(tab);
                ArrayList<Long> ids = new ArrayList<Long>();
                ids.add((tab.getId()));
                Collection<CustomField> fields = _customFieldManager.getCustomFieldObjectsByName(srvOutcome.get().getName());
                _customFieldService.addToScreenTabs(_loginContext.getLoggedInUser(),fields.stream().findFirst().get().getIdAsLong() , ids);*/
                JiraWorkflow j = _iWorkflowUtils.ImportFromXMLFile("/Workflow/test.xml", "new workflow from xml file test");
                int x = 5;
            }
        }
    }
}