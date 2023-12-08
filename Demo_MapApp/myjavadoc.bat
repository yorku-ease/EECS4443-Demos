@echooff
rem
rem This is my attempt to generate javadoc for Demo_MapApp, given the 
rem difficulty I've had trying to do so from Tools > Generate JavaDoc... in 
rem Android Studio.  Running this batch files gives the same warnings as
rem when using Android Studio's generate javadoc feature.  However, the
rem command below actually generates the javadoc file.  If generate javadoc
rem is run from Android Studio, the command terminates with "exit code 1" 
rem and no documentation is generated.
rem
rem Scott MacKenzie, 25-May-2015
rem
javadoc -d ../Javadoc app/src/main/java/ca/yorku/cse/mack/demomapapp/*.java -author