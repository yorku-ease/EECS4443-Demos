#!/bin/bash

# Directory containing all the projects
projects_dir="./"

# Output directory for the generated Javadoc
output_dir="./docs"

# Iterate over all directories in the projects directory
for dir in $projects_dir*/ ; do
  # Run the javadoc command for each directory
  # echo $dir
  javadoc -d ./docs -sourcepath $dir/app/src/main/java -subpackages ca.yorku.eecs.mack
done
