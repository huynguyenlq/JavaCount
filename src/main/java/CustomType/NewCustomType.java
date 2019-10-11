package CustomType;

import TestCPN.ITestCPN;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.jira.workflow.JiraWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Scanned
public class NewCustomType extends AbstractSingleFieldType<String> {
    private static final Logger log = LoggerFactory.getLogger(NewCustomType.class);

    private ITestCPN x = null;
    @JiraImport
    private WorkflowManager _workflowManager;

    @Inject
    protected NewCustomType(@JiraImport CustomFieldValuePersister customFieldValuePersister,
                            @JiraImport GenericConfigManager genericConfigManager,
                            WorkflowManager workflowManager,
                            ITestCPN c) {
        super(customFieldValuePersister, genericConfigManager);
        x = c;
        _workflowManager = workflowManager;
    }

    @Nonnull
    @Override
    protected PersistenceFieldType getDatabaseType() {
        return PersistenceFieldType.TYPE_LIMITED_TEXT;
    }

    @Nullable
    @Override
    protected Object getDbValueFromObject(String s) {
        return getSingularObjectFromString(s);
    }

    @Nullable
    @Override
    protected String getObjectFromDbValue(@Nonnull Object o) throws FieldValidationException {
        return getStringFromSingularObject(o == null ? null : o.toString());
    }

    @Override
    public String getStringFromSingularObject(String s) {
        return s;
    }

    @Override
    public String getSingularObjectFromString(String s) throws FieldValidationException {
        return s;
    }

    @Nonnull
    @Override
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> ret = super.getVelocityParameters(issue, field, fieldLayoutItem);
        JiraWorkflow wFlow = _workflowManager.getWorkflow(issue);
        List<Status> tmp = wFlow.getLinkedStatusObjects();
        StringBuilder status = new StringBuilder();
        for (Status s : tmp){
            status.append(s.getName() + "_");
        }
        ret.put("status", status);
        ret.put("test", x);
        return ret;
    }
}