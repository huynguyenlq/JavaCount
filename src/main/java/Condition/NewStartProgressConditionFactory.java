package Condition;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comparator.ConstantsComparator;
import com.atlassian.jira.issue.status.SimpleStatus;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import org.apache.velocity.runtime.directive.Foreach;

import java.util.*;

/**
 * This is the factory class responsible for dealing with the UI for the post-function.
 * This is typically where you put default values into the velocity context and where you store user input.
 *
 */
@Scanned
public class NewStartProgressConditionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginConditionFactory
{
    public static final String FIELD_WORD = "word";
    @JiraImport
    private ConstantsManager _constantsManager;
    private Collection<Status> statuses;
    private Collection<Status> ownStatuses;


    public NewStartProgressConditionFactory(ConstantsManager constantsManager){
        _constantsManager = constantsManager;
        statuses = _constantsManager.getStatuses();
    }

    protected void getVelocityParamsForInput(Map velocityParams)
    {
        //the default message
        velocityParams.put("statuses", statuses);
    }

    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor)
    {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
    {
        Collection selectedStatusIds = getSelectedStatusIds(descriptor);
        List<Status> selectedStatuses = new ArrayList<>();
        for (Object selectedStatusId : selectedStatusIds) {
            String statusId = (String) selectedStatusId;
            Status selectedStatus = _constantsManager.getStatus(statusId);
            if (selectedStatus != null) {
                selectedStatuses.add(selectedStatus);
            }
        }
        selectedStatuses.sort(new ConstantsComparator());

        velocityParams.put("statuses", Collections.unmodifiableCollection(selectedStatuses));
    }

    public Map getDescriptorParams(Map conditionParams)
    {
        Collection statusIds = conditionParams.keySet();
        StringBuilder statIds = new StringBuilder();

        for (Object statusId : statusIds) {
            statIds.append((String) statusId).append(",");
        }

        return MapBuilder.build("statuses", statIds.substring(0, statIds.length() - 1));
    }

    private Collection getSelectedStatusIds(AbstractDescriptor descriptor) {
        Collection<String> selectedStatusIds = new ArrayList<>();
        if (!(descriptor instanceof ConditionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        }

        ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;

        String statuses = (String) conditionDescriptor.getArgs().get("statuses");
        StringTokenizer st = new StringTokenizer(statuses, ",");

        while (st.hasMoreTokens()) {
            selectedStatusIds.add(st.nextToken());
        }
        return selectedStatusIds;
    }
}
