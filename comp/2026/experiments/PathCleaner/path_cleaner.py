# deletes all unused paths

import os
import json

AUTOS_DIR = "autos"
PATHS_DIR = "paths"

used_paths = []

# recursively find all used paths
def find_pathnames(obj):
    if isinstance(obj, dict):
        for key in obj:
            if key == "pathName":
                used_paths.append(obj[key])

            find_pathnames(obj[key])

    elif isinstance(obj, list):
        for item in obj:
            find_pathnames(item)


for filename in os.listdir(AUTOS_DIR):
    if not filename.endswith(".auto"):
        continue

    file_path = os.path.join(AUTOS_DIR, filename)

    with open(file_path, "r") as f:
        data = json.load(f)

    find_pathnames(data)

print(used_paths)

# Delete unused .path files
for filename in os.listdir(PATHS_DIR):
    if not filename.endswith(".path"):
        continue

    path_name = os.path.splitext(filename)[0]

    if path_name not in used_paths:
        full_path = os.path.join(PATHS_DIR, filename)

        print("Deleting:", filename)
        os.remove(full_path)