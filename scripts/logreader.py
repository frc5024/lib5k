# 5024 logfile reader | (c) Raider Robotics 2020
#
# This python script is designed to connect to a RoboRIO or simulation
# environment, and let the user filter the large amount of log data that
# is produced my a robot program.

import argparse
import os
from typing import Iterator
import re

try:
    import paramiko
except:
    print("Paramiko not installed. Please install it with the following command:")
    print("python3 -m pip install paramiko")
    exit(1)
    
# In case NI chenges the filename:
LOGFILE_NAME = "FRC_UserProgram.log"

def simulationLogsExist() -> bool:
    """Checks to see if a local simulation log output file can be found

    Returns:
        bool: Does the file exist?
    """
    return os.path.exists(f"./{LOGFILE_NAME}")

def localFollow(file) -> Iterator[str]:
    """ Yield each line from a file as they are written. """
    line = ''
    while True:
        tmp = file.readline()
        if tmp is not None:
            line += tmp
            if line.endswith("\n"):
                yield line
                line = ''
        else:
            time.sleep(0.1)

def filterLogLine(line: str, levels: list, classes: list, methods: list) -> str:
    """Filter a log line based on settings. This is probably the only logic that might change year-to-year

    Args:
        line (str): Raw line from logfile
        levels (list): Log levels to let through
        classes (list): Classes to let through
        methods (list): Methods to let through

    Returns:
        str: Filtered line
    """

    # Pass through regex
    data = re.findall(r"(.*) at (.*)s: (.*)::(.*)\(\) -> (.*)", line, re.M)
    if len(data) == 0:
        return ""
    else:
        data = data[0]
    
    level, timestamp, _class, method, message = data[0], data[1], data[2], data[3], data[4]

    # Parse package name
    package = _class.split(".")

    # This will be modified, then used to determine if we log this line
    doLog = True

    # Check log level
    if not level in levels:
        doLog = False

    # Check class
    if classes != [''] and package[-1].upper() not in classes:
        doLog = False
    
    # Check method
    if methods != [''] and method.upper() not in methods:
        doLog = False

    # Shorten package name
    # if len(package) >= 3:
    #     package_short = f"{package[0]}...{package[-2]}.{package[-1]}"
    # elif len(package) is 2:
    #     package_short = f"{package[-2]}.{package[-1]}"
    # else:
    #     package_short = package[-1]

    # TMP:
    package_short = ".".join(package)
    

    if doLog:
        return f"{level} at {timestamp}s: {package_short}::{method}() -> {message}\n"
    else:
        return ""


if __name__ == "__main__":

    # Build a CLI argument parser
    ap = argparse.ArgumentParser(prog="logreader",
        description="A tool for reading and sorting log data in real time over a network")
    
    # Add all arguments
    ap.add_argument("team", help="FRC Team number of the robot or simulation")
    ap.add_argument("-f", "--follow", help="Setting this flag will follow the logs instead of just dumping a snapshot", required=False, action="store_true")
    ap.add_argument("-i", "--ip", help="Robot or simulation IP address", required=False)
    ap.add_argument("-l", "--local-simulation", help="Use this flag if connecting to a simulation running on the same computer", required=False, action="store_true")
    ap.add_argument("-t", "--level-types", help="Comma-seperated list of allowed log levels. Options: DEBUG, INFO, WARNING", required=False)
    ap.add_argument("-c", "--classes", help="Comma-seperated list of class names. This can be used to only view logs from a specific list of classes", required=False)
    ap.add_argument("-m", "--methods", help="Comma-seperated list of method names. This can be used to only view logs from a specific list of methods", required=False)
    ap.add_argument("-v", "--verbose", help="For logreader debugging", required=False, action="store_true")

    # Parse the args
    args = ap.parse_args()

    # Determine IP address to use
    ip = ""

    # Parse team number
    te = int(int(args.team) / 100)
    am = int(int(args.team) % 100)

    ip = f"10.{te}.{am}.2"

    # If we have a local simulation, override
    if args.local_simulation:
        ip = "127.0.0.1"

    # Finally, if an IP is manually set, use that
    if args.ip:
        ip = args.ip
    
    # Determine list of levels allowed
    if args.level_types:
        levels = [x.strip().upper() for x in args.level_types.split(",")]
    else:
        levels = ["INFO","WARNING"]

    # Determine list of classes allowed
    args.classes = args.classes if args.classes else ""
    classes = [x.strip().upper() for x in args.classes.split(",")]

    # Determine list of methods allowed
    args.methods = args.methods if args.methods else ""
    methods = [x.strip().upper() for x in args.methods.split(",")]


    # If debugging enabled, list parsed args
    if args.verbose:
        print("-- Argparse Debug --")
        print(f"Team: {args.team}")
        print(f"IP address: {ip}")
        print(f"Follow Logs: {args.follow}")
        print(f"Log levels: {levels}")
        print(f"Classes: {classes}")
        print(f"Methods: {methods}")

    # Handle simulation vs real
    if args.local_simulation:

        # Check for local sim file
        if not simulationLogsExist():
            print("Can not find local simulation logfile. Please check that:")
            print(" - A simulation is running")
            print(" - The simulation is using the Lib5K RobotLogger")
            print(" - This script is running from the root of the simulation source directory")
            print(f" - A '{LOGFILE_NAME}' file exists")
            exit(1)

        # Load file
        logfile = open(LOGFILE_NAME, "r")

        # Handle follow vs non-follow
        if args.follow:
            print("Following logfile. Press CTRL+C to stop.")

            try:
                # Print all lines as they are written
                for line in localFollow(logfile):
                    print(filterLogLine(line, levels, classes, methods), end="")

            except KeyboardInterrupt as e:
                exit(0)

        else:

            # Just dump the file
            for line in logfile.read().split("\n"):
                print(filterLogLine(line, levels, classes, methods), end="")
            exit(0)

    # Remote robot
    else:

        # Open SSH connection
        print(f"Connecting to lvuser@{ip} over SSH")
        client = paramiko.SSHClient()
        # client.load_system_host_keys()
        client.set_missing_host_key_policy(paramiko.WarningPolicy())
        client.connect(ip, username='lvuser', password='')
        print("Connected. Press CTRL+C to stop.")

        # Run correct command
        if args.follow:
            stdin, stdout, stderr = client.exec_command(f"tail -f ~/{LOGFILE_NAME}")
        else:
            stdin, stdout, stderr = client.exec_command(f"cat ~/{LOGFILE_NAME}")
            
        # Parse file
        try:
            # Print all lines as they are written
            for line in stdout:
                print(filterLogLine(line, levels, classes, methods), end="")

        except KeyboardInterrupt as e:
            client.close()

        
