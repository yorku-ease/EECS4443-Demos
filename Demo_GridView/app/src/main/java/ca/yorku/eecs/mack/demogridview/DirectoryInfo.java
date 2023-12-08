package ca.yorku.eecs.mack.demogridview;

import java.io.File;

/**
 * A simple class to hold information about a directory.  We need (i) the name of the directory that
 * holds at least one image/JPG file, (ii) the number of image files in the directory, and (iii) the
 * name of a sample image file which will be shown as thumbnail in the GridView.
 */
public class DirectoryInfo
{
    int numberOfImageFiles;
    String sampleImageFileName;
    private File directory;

    DirectoryInfo(File directoryArg, int numberOfImageFilesArg, String sampleImageFileNameArg)
    {
        directory = directoryArg;
        numberOfImageFiles = numberOfImageFilesArg;
        sampleImageFileName = sampleImageFileNameArg;
    }

    public String toString()
    {
        return directory.toString();
    }
}
