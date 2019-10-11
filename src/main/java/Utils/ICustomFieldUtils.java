package Utils;

import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;

import java.util.List;

public interface ICustomFieldUtils {
    CustomField create(String name,
                       String description,
                       String typeKey,
                       List<String> optionsForMultiSelect,
                       List<Long> projectId,
                       Boolean isGlobal
            /*,
                       List<String> issueTypeKeyAffected,
                       String searcherKey*/);

    CustomField update(Long customFieldId, String name, String description);

    Boolean delete(Long customFieldId);

    List<Long> addCustomFieldToScreenTabs(Long customFieldId, List<Long> tabIds);

    List<CustomFieldType<?, ?>> getAllCustomFieldType();
}
