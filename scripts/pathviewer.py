import argparse
import os
import matplotlib.pyplot as plt

# Get args
ap = argparse.ArgumentParser(description="A tool to display saved pathfollowing CSV data as graphs")
ap.add_argument("file", help="CSV file from robot")
args = ap.parse_args()

# Check for the file
if not os.path.exists(args.file):
    print("Provided filepath does not exist")
    exit(1)

# Load the file
data = []
with open(args.file, "r") as fp:
    for line in fp.read().strip().split("\n"):
        data.append(line.split(", "))
    fp.close()

# Convert into two datasets
goalData = {
    "x": [],
    "y":[]
}

robotData = {
    "x": [],
    "y":[]
}

xs = []

for line in data[1:]:
    robotData["x"].append(line[1])
    robotData["y"].append(line[2])
    goalData["x"].append(line[4])
    goalData["y"].append(line[5])


# Create graph
# fig = plt.figure()
# ax1 = fig.add_subplot(111)

plt.suptitle("Goal vs Measured Pose")

plt.subplot(1, 2, 1)
plt.plot(goalData["x"], goalData["y"], label="goal")
plt.legend(loc='upper left')
plt.xlabel("X (meters)")
plt.ylabel("Y (meters)")

plt.subplot(1, 2, 2)
plt.plot(robotData["x"], robotData["y"], label="measured")
plt.legend(loc='upper left')
plt.xlabel("X (meters)")
plt.ylabel("Y (meters)")


plt.show()