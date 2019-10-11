package TestCPN;

import TestCPN.ITestCPN;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({ITestCPN.class})
@Named
public class TestCPN implements ITestCPN{
    private static final Logger log = LoggerFactory.getLogger(TestCPN.class);

    public TestCPN() {

    }

    @Override
    public String getName() {
        return "Nam of function";
    }
}