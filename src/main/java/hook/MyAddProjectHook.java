package hook;

import com.atlassian.jira.project.template.hook.AddProjectHook;
import com.atlassian.jira.project.template.hook.ConfigureData;
import com.atlassian.jira.project.template.hook.ConfigureResponse;
import com.atlassian.jira.project.template.hook.ValidateData;
import com.atlassian.jira.project.template.hook.ValidateResponse;

public class MyAddProjectHook implements AddProjectHook
{
    @Override
    public ValidateResponse validate(final ValidateData validateData)
    {
        ValidateResponse validateResponse = ValidateResponse.create();

        return validateResponse;
    }

    @Override
    public ConfigureResponse configure(final ConfigureData configureData)
    {
        return ConfigureResponse.create();
    }
}