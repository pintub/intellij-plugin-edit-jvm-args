import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.theoryinpractice.testng.configuration.TestNGConfiguration;
import com.theoryinpractice.testng.configuration.TestNGConfigurationType;
import org.apache.commons.lang3.StringUtils;
import vmargs.replacement.VmArgsUpdateUtil;

public class SaveAepJobToVmArgsAction extends AnAction {

    public SaveAepJobToVmArgsAction() {
        super("SaveAepJobToVmArgsAction");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        RunManager runManager = RunManager.getInstance(project);
        TestNGConfiguration testNGConfiguration =  getTestNgConfiguration(runManager);

        String oldVMParameters = testNGConfiguration.getVMParameters();
        String aepJob = Messages.showInputDialog(project, "Enter AEP Job Name", "Enter AEP Job Name", Messages.getQuestionIcon());

        if(StringUtils.isEmpty(aepJob)){
            Messages.showMessageDialog(project, "Nothing to update", "VM Args Saved", Messages.getInformationIcon());
            return;
        }
        String newVMParameters = null;

        try {
            newVMParameters = VmArgsUpdateUtil.getNewVMArgs(oldVMParameters, aepJob);
        } catch (IllegalArgumentException e){
            showMessageDialog(project, "Current VM Arguments Pattern is not proper --> " + oldVMParameters + "\n" +
                    "Expected Pattern is --> " + VmArgsUpdateUtil.VM_ARGS_PATTERN);
            throw e;
        }

        testNGConfiguration.setVMParameters(newVMParameters);

        showMessageDialog(project, "Default Arguments Updated with AEP Job: " + aepJob);
    }

    private void showMessageDialog(Project project, String message) {
        Messages.showMessageDialog(project, message, "VM Args Saved", Messages.getInformationIcon());
    }

    private TestNGConfiguration getTestNgConfiguration(RunManager runManager){
        ConfigurationType testNgConfigurationType = null;
        for(ConfigurationType configurationType : runManager.getConfigurationFactories()){
            if(configurationType instanceof TestNGConfigurationType){
                testNgConfigurationType = configurationType;
                break;
            }
        }

        TestNGConfiguration testNGConfiguration =
                (TestNGConfiguration)runManager.getConfigurationTemplate(testNgConfigurationType.getConfigurationFactories()[0]).getConfiguration();

        return testNGConfiguration;
    }
}