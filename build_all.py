
import os
import re
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

    expected_gradlerio_version = "2019.1.1-beta-1"
    expected_snobotsim_version = "2019-0.0.0"
    expected_wpilib_version = "2019.1.1-beta-2"

    found_gradlerio_version = None
    found_snobotsim_version = None
    found_wpilib_version = None

    with open("build.gradle") as f:
        for line in f.readlines():
            line = line.strip()
            gradle_rio_success, found_gradlerio_version = check_regex(r"edu.wpi.first.GradleRIO.*?([0-9]+\.[0-9]+\.[0-9]+-.*)\"", line, found_gradlerio_version)
            snobot_sim_success, found_snobotsim_version = check_regex(r"SnobotSimulatorPlugin.*?([0-9]+-[0-9]+\.[0-9]+\.[0-9]+)", line, found_snobotsim_version)
            wpilib_success, found_wpilib_version = check_regex(r"""wpilibVersion *=? *(?:'|")(.*)(?:'|")""", line, found_wpilib_version)

            success = success and (gradle_rio_success and snobot_sim_success and wpilib_success)

    if found_gradlerio_version != expected_gradlerio_version:
        print("  Unexpected GradleRIO version '%s' (%s)" % (found_gradlerio_version, expected_gradlerio_version))
        success = False

    if found_snobotsim_version != expected_snobotsim_version:
        print("  Unexpected SnobotSim version '%s' (%s)" % (found_snobotsim_version, expected_snobotsim_version))
        success = False
        
    # This one doesn't have to be manually specified, so call not specifying it OK
    if found_wpilib_version != None and found_wpilib_version != expected_wpilib_version:
        print("  Unexpected wpilib version '%s' (%s)" % (found_wpilib_version, expected_wpilib_version))
        success = False

    return success


def main():
	
    projects = [os.path.abspath(d) for d in os.listdir('.') if os.path.isdir(d) and d != ".git"]

    failures = []
    warnings = []
    for project in projects:
        os.chdir(project)
        args = ["gradlew", "build"]
        if subprocess.call(args, shell=True) != 0:
            failures.append(project)
        elif not check_versions(project):
            warnings.append(project)

    print("Failed %s" % failures)
    print("Warnings %s" % warnings)


if __name__ == "__main__":
    main()