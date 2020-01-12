
import os
import re
import sys
import subprocess


def check_regex(regex, line, previous_result):

    success = True

    matches = re.search(regex, line)
    if matches:
        if previous_result:
            print("  Found a duplicate version %s (%s)" % (matches.group(1), previous_result))
            success = False
        previous_result = matches.group(1)

    return success, previous_result


def check_versions(project):
    print("Checking version for %s" % project)

    success = True

    expected_gradlerio_version = "2020.1.2"
    expected_snobotsim_version = "2020-0.0.0"
    expected_wpilib_version = "2020.1.2"

    found_gradlerio_version = None
    found_snobotsim_plugin_version = None
    found_wpilib_version = None

    with open("build.gradle") as f:
        for line in f.readlines():
            line = line.strip()
            if "buildscript" in line:
                print("  Found build script, probably an intermediate build")
                success = False
            if "snobotSimCompile" in line:
                print("  snobotSimCompile shouldn't be here anymore")
                success = False
                
            gradle_rio_success, found_gradlerio_version = check_regex(r"edu.wpi.first.GradleRIO.*?([0-9]+\.[0-9]+\.[0-9])\"", line, found_gradlerio_version)
            snobot_sim_plugin_success, found_snobotsim_plugin_version = check_regex(r"SnobotSimulatorPlugin.*?([0-9]+-[0-9]+\.[0-9]+\.[0-9]+)", line, found_snobotsim_plugin_version)
            wpilib_success, found_wpilib_version = check_regex(r"""wpilibVersion *=? *(?:'|")(.*)(?:'|")""", line, found_wpilib_version)

            success = success and (gradle_rio_success and snobot_sim_plugin_success and wpilib_success)

    if found_gradlerio_version != expected_gradlerio_version:
        print("  Unexpected GradleRIO version '%s' (%s)" % (found_gradlerio_version, expected_gradlerio_version))
        success = False

    if found_snobotsim_plugin_version != expected_snobotsim_version:
        print("  Unexpected SnobotSim plugin version '%s' (%s)" % (found_snobotsim_plugin_version, expected_snobotsim_version))
        success = False
        
    # This one doesn't have to be manually specified, so call not specifying it OK
    if found_wpilib_version != None and found_wpilib_version != expected_wpilib_version:
        print("  Unexpected wpilib version '%s' (%s)" % (found_wpilib_version, expected_wpilib_version))
        success = False

    return success


def build_functor(project, gradle_command, use_shell):
    failures = []
    warnings = []

    subprocess.call([gradle_command, "updateSnobotSimConfig"], shell=use_shell)
 
    if "cpp" in project.lower():
        subprocess.call([gradle_command, "installRoboRioToolchain"], shell=use_shell)
    if subprocess.call([gradle_command, "build"], shell=use_shell) != 0:
        failures.append(project)
    elif not check_versions(project):
        warnings.append(project)

    return failures, warnings

def clean_functor(project, gradle_command, use_shell):
    failures = []
    warnings = []
    
    if subprocess.call([gradle_command, "clean"], shell=use_shell) != 0:
        failures.append(project)

    return failures, warnings


def main():
    
    run_function = build_functor
#     run_function = clean_functor

    projects = [os.path.abspath(d) for d in os.listdir('.') if os.path.isdir(d) and d != ".git" and d != "build" and d != "styleguide"]

    failures = []
    warnings = []
    use_shell = sys.platform == 'win32'
    gradle_command = "gradlew" if sys.platform == 'win32' else "./gradlew"
    for project in projects:
        print("Running project '" + project + "'")
        
        os.chdir(project)
        failure, warning = run_function(project, gradle_command, use_shell)
        
        failures.extend(failure)
        warnings.extend(warning)

    print("Failed %s" % failures)
    print("Warnings %s" % warnings)
    
    sys.exit(len(failures))


if __name__ == "__main__":
    main()

