package Utils;

import com.atlassian.jira.bc.project.ProjectCreationData;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({IProjectUtils.class})
@Named
public class ProjectUtils implements IProjectUtils {

    @JiraImport
    private ProjectManager _projectManager;
    @JiraImport
    private ProjectService _projectService;
    @JiraImport
    JiraAuthenticationContext _loginContext;

    @Inject
    public ProjectUtils(
            ProjectManager projectManager,
            ProjectService projectService,
            JiraAuthenticationContext jiraAuthenticationContext
    ){
        _projectManager = projectManager;
        _projectService = projectService;
        _loginContext = jiraAuthenticationContext;
    }

    @Override
    public Project create(String name, ApplicationUser lead, String key, String description, ProjectTypeKey projectTypeKey, Long assigneeTypeId, Long avatarId, String url) {
        ProjectCreationData.Builder newProjectBuilder = new ProjectCreationData.Builder();
        newProjectBuilder.withName(name)
                .withLead(lead)
                .withKey(key)
                .withType(projectTypeKey);
        if(assigneeTypeId!=null)
            newProjectBuilder.withAssigneeType(assigneeTypeId);
        if(avatarId!=null)
            newProjectBuilder.withAvatarId(avatarId);
        if(url!=null)
            newProjectBuilder.withUrl(url);
        if(description!=null)
            newProjectBuilder.withDescription(description);
        ProjectCreationData newProject = newProjectBuilder.build();
        ProjectService.CreateProjectValidationResult validationResult = _projectService.validateCreateProject(_loginContext.getLoggedInUser(), newProject);
        if(!validationResult.isValid())
            return null;
        return _projectService.createProject(validationResult);
    }

    @Override
    public Boolean delete(String key) {
        ProjectService.DeleteProjectValidationResult validationResult = _projectService.validateDeleteProject(_loginContext.getLoggedInUser(), key);
        if(!validationResult.isValid())
            return null;
        ProjectService.DeleteProjectResult ret = _projectService.deleteProject(_loginContext.getLoggedInUser(), validationResult);
        if(ret.isValid())
            return true;
        return false;
    }

    @Override
    public Project update(Project oldProject, String name, ApplicationUser lead, String key, String description, String url, Long assigneeType) {
        if(oldProject==null)
            return null;
        ProjectService.UpdateProjectRequest updateRequest = new ProjectService.UpdateProjectRequest(oldProject);
        if(name!=null)
            updateRequest.name(name);
        if(lead!= null)
            updateRequest.leadUserKey(lead.getKey());
        if(key!=null)
            updateRequest.key(key);
        if(description!=null)
            updateRequest.description(description);
        if(url!=null)
            updateRequest.url(url);
        if(assigneeType!=null)
            updateRequest.assigneeType(assigneeType);
        ProjectService.UpdateProjectValidationResult validationResult = _projectService.validateUpdateProject(_loginContext.getLoggedInUser(), updateRequest);
        if(!validationResult.isValid())
            return null;
        return _projectService.updateProject(validationResult);
    }
}
