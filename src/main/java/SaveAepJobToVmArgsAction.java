import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
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

        String aepJob = Messages.showInputDialog(project, "Enter AEP Job Name", "Enter AEP Job Name", Messages.getQuestionIcon());

        if(StringUtils.isEmpty(aepJob)){
            showMessageDialog(project, "Nothing to update");
            return;
        }

        TestNGConfiguration defaultTestNGConfiguration =  null;
        try {
            defaultTestNGConfiguration = getDefaultTestNgConfiguration(runManager);
        } catch (IllegalArgumentException e){
            showMessageDialog(project, e.getMessage());
            return;
        }
        updateVMArgs(project, defaultTestNGConfiguration, aepJob);

        TestNGConfiguration selectedTestNGConfiguration =  getSelectedTestNgConfiguration(runManager);
        if(selectedTestNGConfiguration != null) {
            updateVMArgs(project, selectedTestNGConfiguration, aepJob);
        }
        //showMessageDialog(project, "Default Arguments Updated with AEP Job: " + aepJob);
    }

    private void updateVMArgs(Project project, TestNGConfiguration defaultTestNGConfiguration, String aepJob) {
        String oldVMParameters = defaultTestNGConfiguration.getVMParameters();

        String newVMParameters = null;

        try {
            newVMParameters = VmArgsUpdateUtil.getNewVMArgs(oldVMParameters, aepJob);
        } catch (IllegalArgumentException e){
            showMessageDialog(project, e.getMessage());
            return;
        }

        defaultTestNGConfiguration.setVMParameters(newVMParameters);
    }

    private void showMessageDialog(Project project, String message) {
        Messages.showMessageDialog(project, message, "VM Args Saved", Messages.getInformationIcon());
    }

    private TestNGConfiguration getDefaultTestNgConfiguration(RunManager runManager){

        ConfigurationType testNgConfigurationType = null;
        for(ConfigurationType configurationType : runManager.getConfigurationFactories()){
            if(configurationType instanceof TestNGConfigurationType){
                testNgConfigurationType = configurationType;
                break;
            }
        }

        if(testNgConfigurationType == null){
            throw new IllegalArgumentException("Please install TestNg Plugin");
        }

        TestNGConfiguration testNGConfiguration =
                (TestNGConfiguration)runManager.getConfigurationTemplate(testNgConfigurationType.getConfigurationFactories()[0]).getConfiguration();

        return testNGConfiguration;
    }

    private TestNGConfiguration getSelectedTestNgConfiguration(RunManager runManager){
        TestNGConfiguration testNGConfiguration = null;

        RunnerAndConfigurationSettings selectedConfiguration = runManager.getSelectedConfiguration();

        if (selectedConfiguration != null && selectedConfiguration.getConfiguration() instanceof  TestNGConfiguration) {
            testNGConfiguration = (TestNGConfiguration) selectedConfiguration.getConfiguration();
        }

        return testNGConfiguration;
    }
}