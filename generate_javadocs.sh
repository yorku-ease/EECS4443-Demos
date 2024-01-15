##!/bin/bash

# Directory containing all the projects
projects_dir="./"

# Output directory for the generated Javadoc
output_dir="./docs"

# Create a temporary file to hold the links
temp_file=$(mktemp)

# Iterate over all directories in the projects directory
for dir in $projects_dir*/ ; do
 # Extract the project name from the directory path
 project_name=$(basename $dir)

 # Convert the project name to lowercase and join the words back together
 project=$(echo $project_name | awk '{gsub(/_/, "", $0); print tolower($0)}')
 echo "project is $project"
 echo "project_name is $project_name"

 # Run the javadoc command for each directory
 javadoc -d ./docs/$project_name -sourcepath $dir/app/src/main/java -subpackages ca.yorku.eecs.mack

 # Copy all .jpg files to the appropriate location
 find $dir/app/src/main/java/ca/yorku/eecs/mack/$project/javadoc_images/ -type f -name "*.jpg" -exec sh -c 'mkdir -p "$2" && cp "$1" "$2"' _ {} $output_dir/$project_name/ca/yorku/eecs/mack/$project/javadoc_images/ \;

 # Append a link to the project's index.html file to the temporary file
 echo "* [$project_name]($project_name/index.html)" >> $temp_file
done

# Write the header and footer to the index.md file
echo "## Project Javadocs

$(cat $temp_file)" > $output_dir/index.md

# Remove the temporary file
rm $temp_file
