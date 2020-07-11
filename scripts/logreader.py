# 5024 logfile reader | (c) Raider Robotics 2020
#
# This python script is designed to connect to a RoboRIO or simulation
# environment, and let the user filter the large amount of log data that
# is produced my a robot program.

import argparse

if __name__ == "__main__":

    # Build a CLI argument parser
    ap = argparse.ArgumentParser(prog="logreader",
        description="A tool for reading and sorting log data in real time over a network")
    
    # Add all arguments
    ap.add_argument("team", help="FRC Team number of the robot or simulation")
    ap.add_argument("-i", "--ip", help="Robot or simulation IP address", required=False)
    ap.add_argument("-l", "--local-simulation", help="Use this flag if connecting to a simulation running on the same computer", required=False, action="store_true")
    ap.add_argument("-d", "--no-driverstation", help="This flag must be set if connecting to a robot without a DriverStation connected", required=False, action="store_true")
    ap.add_argument("-t", "--level-types", help="Comma-seperated list of allowed log levels. Options: DEBUG, INFO, WARNING", required=False)
    ap.add_argument("-c", "--class", help="Comma-seperated list of class names. This can be used to only view logs from a specific list of classes", required=False)
    ap.add_argument("-m", "--mathod", help="Comma-seperated list of method names. This can be used to only view logs from a specific list of methods", required=False)
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
    

    # If debugging enabled, list parsed args
    if args.verbose:
        print(f"Team: {args.team}")
        print(f"Ip address: {ip}")

