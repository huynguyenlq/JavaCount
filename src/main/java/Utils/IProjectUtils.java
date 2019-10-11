package Utils;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.user.ApplicationUser;

public interface IProjectUtils {
    Project create(String name,
                   ApplicationUser lead,
                   String key,
                   String description,
                   ProjectTypeKey projectTypeKey,
                   Long assigneeTypeId,
                   Long avatarId,
                   String url);

    Boolean delete(String key);

    Project update(Project oldProject, String name, ApplicationUser lead, String key, String description, String url, Long assigneeType);
}
