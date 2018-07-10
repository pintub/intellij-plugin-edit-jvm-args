package vmargs.replacement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VmArgsUpdateUtil {

    public final static String VM_ARGS_PATTERN = "\"^(.*)(-DhudsonJobName=)(.+?\\\\s)(.*)$\"";

    public static void main(String[] args) {

        String oldVmArgs = "-Xmx4096m -DbuildHome=C:\\Workspace\\Documents\\debug\\workspace\\build-auto -DhudsonJobName=minimalSAFTestDemo -Denv.aepUrl=http://sf_aep.c.eu-de-1.cloud.sap:8080/aep -DabsoluteLogFilePath=${project_loc}/logs";
        String newVmArgs = getNewVMArgs(oldVmArgs, "minimalSAFTestDemo1");
    }

    public static String getNewVMArgs(String oldVMArgs, String newAepJob){
        String oldVMInput = oldVMArgs;
        String oldVM = oldVMInput + " ";
        StringBuilder newVM = new StringBuilder();

        Pattern pattern = Pattern.compile("^(.*)(-DhudsonJobName=)(.+?\\s)(.*)$");
        Matcher matcher = pattern.matcher(oldVM);

        if (matcher.matches()){
            for ( int groupCount = 1 ; groupCount <= matcher.groupCount() ; groupCount++ ) {
                if(groupCount == 3) {
                    newVM.append(newAepJob + " ");
                } else {
                    newVM.append(matcher.group(groupCount));
                }
            }
        } else {
            throw new IllegalArgumentException("Current VM Arguments Pattern is not proper --> " + oldVM + "\n" +
                    "Expected Pattern is --> " + VM_ARGS_PATTERN);
        }

        System.out.println("OldVM :" + oldVM);
        System.out.println("NewVM :" + newVM.toString());

        return newVM.toString();

    }
}
