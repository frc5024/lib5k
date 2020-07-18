import json
import os

print("Starting simulation wrapper")
print("Generating system simulation settings...")
os.system("./gradlew build simulateExternalJava --console=plain")

print("Loading settings file")
j:dict
with open("./build/debug/desktopinfo.json", "r") as fp:
    j = json.loads(fp.read())
    fp.close()
    
print("Loaded settings")

num_tasks = len(j)
chosen_taskname = j[0]["name"]
print(f"There are {num_tasks} available simulation tasks. Choosing option 0: {chosen_taskname}")

jar_file = j[0]["file"]
extensions = j[0]["extensions"]
libs = j[0]["librarydir"]
mainclass = j[0]["mainclass"]

print(f"Found robot JAR: {jar_file}")
print(f"Using extensions: {extensions}")
print(f"JNI path: {libs}")
print(f"Main Class: {mainclass}")

print("Generating shell script")

template = f"""
#!/bin/bash

export HALSIM_EXTENSIONS={extensions[0]}
export LD_LIBRARY_PATH={libs}
export DYLD_FALLBACK_LIBRARY_PATH={libs}
"java" -Djava.library.path="{libs}" -jar "{jar_file}"
"""

print("Built template")
print("Writing to disk")

with open("./build/debug/pysimulatesrc.sh", "w") as fp:
    fp.write(template)
    fp.close()
    
print("Running simulation")
os.system("bash ./build/debug/pysimulatesrc.sh")