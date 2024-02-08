package ca.yorku.eecs.mack.demovoiceemail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * <style> pre {font-size:110%} </style>
 * <p>
 * Demo_VoiceEmail - demonstrate recording and playing back audio and sending audio as a voice email <p>
 *
 * Related information: <p>
 *
 * <blockquote> API Guides: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/guide/topics/media/audio-capture.html"> Audio Capture</a> <li> <a
 * href="http://developer.android.com/guide/topics/media/mediaplayer.html"> Media Playback</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul> <li> <a href= "http://developer.android.com/reference/android/media/MediaPlayer.html">
 * <code>MediaPlayer</code></a> <li> <a href= "http://developer.android.com/reference/android/media/MediaRecorder.html">
 * <code>MediaRecorder</code></a> <li> <a href= "http://developer.android.com/reference/android/os/CountDownTimer.html">
 * <code>CountDownTimer</code></a> <li> <a href="http://developer.android.com/reference/android/os/Environment.html">
 * <code>Environment</code></a> <li> <a href="http://developer.android.com/reference/android/net/Uri.html">
 * <code>Uri</code></a> </ul> </blockquote>
 *
 * The UI for this demo is straight forward: <p>
 *
 * <center> <a href="./javadoc_images/DemoVoiceEmail-1.jpg"><img src="./javadoc_images/DemoVoiceEmail-1.jpg" width="300"></a> </center> <p>
 *
 * When the user clicks the Start button (under Record), recording begins. This occurs via the following code in the
 * <code>onButtonClick</code> method (abbreviated, with comments): <p>
 *
 * <pre>
 *      // get date and time info for the file name, making it unique
 *      String timeStamp = new SimpleDateFormat(&quot;yyyyMMdd_HHmmss&quot;, Locale.CANADA).format(System
 *      .currentTimeMillis());
 *
 *      // name of the file to store the voice message (includes full path)
 *      audioFilename = dataDirectory.getPath() + File.separator + &quot;VoiceMessage_&quot; + timeStamp +
 * &quot;.3gp&quot;;
 *
 *      // create a new instance of a MediaRecorder
 *      voiceRecorder = new MediaRecorder();
 *
 *      // set the audio source (we'll used the device's built-in microphone)
 *      voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
 *
 *      // set the output file format (we'll use &quot;3gp&quot;; see
 *      <a href="http://en.wikipedia.org/wiki/3gp">http://en.wikipedia.org/wiki/3gp</a>)
 *      voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
 *
 *      // set the output file name (see above)
 *      voiceRecorder.setOutputFile(audioFilename);
 *
 *      // set the audio encoder (we're using the AMR (Narrowband) audio codec; see <a
 * href="http://en.wikipedia.org/wiki/Adaptive_Multi-Rate_audio_codec">http://en.wikipedia
 * .org/wiki/Adaptive_Multi-Rate_audio_codec</a>)
 *      voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
 *
 *      // prepare the MediaRecorder for recording (the above code must have worked just fine!)
 *      voiceRecorder.prepare(); // will throw an exception if there are problems (see the source code herein)
 *
 *      // OK, we're ready to go --&gt; start recording
 *      voiceRecorder.start();
 * </pre>
 *
 * The duration of the recording appears in a text field between the Start and Stop buttons. Updates appear each second
 * via the <code>onTick</code> callback of a <code>CountDownTimer</code>. The duration will range from "0:00" to "9:59".
 * If the recording exceeds ten minutes, the minutes appear as "?". <p>
 *
 * Not surprisingly, <code>onButtonClick</code> executes the following code in response to the Stop button (under
 * Record): <p>
 *
 * <pre>
 *      if (voiceRecorder != null)
 *      {
 *         voiceRecorder.stop();
 *         voiceRecorder.release();
 *         voiceRecorder = null;
 *      }
 * </pre>
 *
 * Consult the source code herein or the API Guides and References noted above for details on starting and stopping
 * playback. <p>
 *
 * The audio data file is saved in a directory called "VoiceEmailStuff" created in the directory returned by: <p>
 *
 * <pre>
 *      Environment.getExternalStorageDirectory()
 * </pre>
 *
 * An example file with full path is <p>
 *
 * <pre>
 *      /mnt/shell/emulated/0/VoiceEmailStuff/VoiceMessage_20170228_112345.3gp
 * </pre>
 *
 * Playback is possible using the Start and Stop buttons under Playback. The duration of the recording appears between
 * the buttons, formatted as explained above. <p>
 *
 * When the user taps the Send button, the voice message just recorded is sent as a voice email; that is, as an audio
 * file in an email attachment. This occurs by launching an email intent as follows: <p>
 *
 * <pre>
 *      Uri voiceMessage = Uri.parse(&quot;file://&quot; + audioFilename);
 *      Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
 *      emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 *      emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, &quot;Voice message from &quot;); // add your name
 * when the email intent launches
 *      emailIntent.putExtra(Intent.EXTRA_STREAM, voiceMessage);
 *      emailIntent.setType(&quot;audio/3gp&quot;);
 *      startActivity(Intent.createChooser(emailIntent, &quot;Send email using...&quot;));
 * </pre>
 *
 * The action associated with the intent is <code>ACTION_SEND</code>. This means that the intent has the goal of
 * delivering some data to another activity. This process begins by presenting the user with a chooser to select the
 * options available for <code>ACTION_SEND</code> (below left). Most Android devices include a Gmail application.
 * Selecting this will choose the Gmail client with the user's email address in the "From" field (below right). The
 * string given as the intent's <code>EXTRA_SUBJECT</code> is used as the "Subject" and the audio file provided as the
 * intent's <code>EXTRA_STREAM</code> is used as an attachment (see above). <p>
 *
 * <center> <a href="./javadoc_images/DemoVoiceEmail-2.jpg"><img src="./javadoc_images/DemoVoiceEmail-2.jpg" width="300"></a> <a
 * href="./javadoc_images/DemoVoiceEmail-3.jpg"><img src="./javadoc_images/DemoVoiceEmail-3.jpg" width="300"></a> </center> <p>
 *
 * The device's text entry method appears with focus on the "To" field. The sender enters the "To" email address and
 * perhaps their name in the subject line (after "from"). Tapping the send arrow at the top sends the email. <p>
 *
 * <b>Activity and Progress Indicators</b>
 * <p>
 *
 * The UI for this demo use Android's <code>ProgressBar</code> class to provide an Activity Indicator when
 * recording a
 * message and a Progress Indicator when playing back the message.  Both indicators are instances of the
 * <code>ProgressBar</code> class.  The difference is that an Activity Indicator is used then the duration of the
 * operation is unknown, or indeterminate.  This is the case when recording the message:
 * <p>
 *
 * <center>
 * <img src="./javadoc_images/DemoVoiceEmail-4.jpg">
 * </center>
 * <p>
 *
 * The animation continues as long as recording continues.
 * <p>
 *
 * A Progress Indicator is used then the duration of the operation is known, or determinate:
 * <p>
 *
 * <center>
 * <img src="./javadoc_images/DemoVoiceEmail-5.jpg">
 * </center>
 * <p>
 * When playing back the
 * recorded message, the duration of the playback is known, since the message has already been recorded.  The
 * animation in this case is a yellow bar that grows in size, filling the gray bar when the playback is
 * finished.  Consult the source code and XML file for complete details.
 * <p>
 *
 * <p>
 * NOTE: Jan 6, 2016.  This demo app stopped working when I tried it on my new device, a Nexus 5x running Android 6. The
 * message "can't attach empty file" was generated when tapping the Gmail icon to send the voice message by email. Once
 * again, StackOverflow provided a quick fix:
 * (<a href="http://stackoverflow.com/questions/32318692/android-attaching-a-file-to-gmail-cant-attach-empty-file">StackOverflow</a>).
 * The fix is required when running this app on Android 6.  Go to Settings > Apps > Gmail > Permissions,  and enable the
 * "Storage" permission manually.
 *
 * @author (c) Scott MacKenzie, 2013-2018
 */
public class DemoVoiceEmailActivity extends Activity implements MediaPlayer.OnErrorListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final String WORKING_DIRECTORY = "VoiceEmailStuff";
    final int TEN_MINUTES = 1000 * 60 * 10;
    final int ONE_SECOND = 1000; // ms
    final int ONE_TWENTIETH_SECOND = 50; // ms
    private static final int PERMISSIONS_RECORD_AUDIO = 1;

    ImageButton playButton, stopPlayButton, recordButton, stopRecordButton, sendButton;
    TextView recordTime, playbackTime;
    ProgressBar recordProgress, playbackProgress;
    MediaRecorder voiceRecorder;
    MediaPlayer voicePlayer;
    File dataDirectory;
    String audioFilename;
    CountDownTimer recordTimer, playbackTimer;
    boolean recording, playingback;
    int recordSeconds, playbackSeconds;
    long playbackDuration;

    /**
     * MediaPlayer is rather heavy, so getting the duration of the recorded message is a bit of a challenge.  The
     * following StackOverflow posting provides this simple static method that does the trick:
     * <p>
     * <a href="https://stackoverflow.com/questions/15394640/get-duration-of-audio-file">Getting duration of audio file</a>
     */
    private static long getDuration(File file)
    {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(durationStr); // ms
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // need to request permission at runtime for Android 6.0 (API 23) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[] {Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, PERMISSIONS_RECORD_AUDIO);
        }

        // get views by ID
        recordButton = (ImageButton)findViewById(R.id.recordbutton);
        stopRecordButton = (ImageButton)findViewById(R.id.stopbutton1);
        playButton = (ImageButton)findViewById(R.id.playbutton);
        stopPlayButton = (ImageButton)findViewById(R.id.stopbutton2);
        sendButton = (ImageButton)findViewById(R.id.sendbutton);
        recordTime = (TextView)findViewById(R.id.recordtime);
        playbackTime = (TextView)findViewById(R.id.playbacktime);

        recordProgress = (ProgressBar)findViewById(R.id.indeterminateBar);
        playbackProgress = (ProgressBar)findViewById(R.id.determinateBar);

        // if it does not yet exist, create the directory for the voice recordings to be saved into
        dataDirectory = new File(getExternalFilesDir(null), WORKING_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdir())
        {
            Log.i(MYDEBUG, "Failed to create directory: " + dataDirectory.toString());
            super.onDestroy();
            this.finish();
        }

        recordTimer = new CountDownTimer(TEN_MINUTES, ONE_SECOND)
        {
            // increment the time every second
            public void onTick(long millisUntilFinished)
            {
                incrementRecordTime();
            }

            public void onFinish()
            {
            }
        };

        playbackTimer = new CountDownTimer(ONE_SECOND, ONE_TWENTIETH_SECOND)
        {
            float elapsedTime; // ms
            int progress;

            // update the progress bar 20x per second
            public void onTick(long millisUntilFinished)
            {
                elapsedTime = 1000L * playbackSeconds + (ONE_SECOND - millisUntilFinished);
                progress = (int)(elapsedTime / playbackDuration * 100.0);
                playbackProgress.setProgress(progress);
            }

            public void onFinish()
            {
                if (voicePlayer != null && voicePlayer.isPlaying()) // playback in progress
                {
                    incrementPlaybackTime();
                    this.start();
                }
            }
        };

        recording = false;
        playingback = false;

        recordSeconds = 0;
        playbackSeconds = 0;
    }

    public void onButtonClick(View v)
    {
        // start recording the voice message
        if (v == recordButton)
        {
            recordProgress.setIndeterminate(true); // start animation for indeterminate progress

            recording = true;
            recordTimer.start();

            // get date and time info for the file name, making it unique
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());
            // bug fix: Jan 31, 2018 (fix date/time in filename: was "SystemClock.elapsedRealTime()")

            // name of the file to store the voice message (includes full path)
            audioFilename = dataDirectory.getPath() + File.separator + "VoiceMessage_" + timeStamp + ".3gp";
            // audioFilename = dataDirectory.getPath() + File.separator +
            // "VoiceMessage_" + timeStamp + ".mp4";

            // create a new instance of a MediaRecorder
            voiceRecorder = new MediaRecorder();

            // set the audio source (we'll used the device's built-in microphone)
            voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			/*
             * Set the output file format (we'll use "3gp"). See...
			 *
			 * http://en.wikipedia.org/wiki/3gp)
			 */
            voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // set the output file name (see above)
            voiceRecorder.setOutputFile(audioFilename);

			/*
             * Set the audio encoder. We're using the AMR (Narrowband) audio codec. See...
			 *
			 * http://en.wikipedia.org/wiki/Adaptive_Multi-Rate_audio_codec)
			 */
            voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			/*
             * The code above sets the stage for executing the prepare method on the MediaRecorder
			 * instance. If any of the set methods are omitted or fail in any way, the prepare
			 * method below is likely to throw an exception. If that happens, good luck! If you get
			 * stuck on this, try using StackOverflow to find the solution (see
			 * http://stackoverflow.com/questions)
			 */
            try
            {
                voiceRecorder.prepare();
            } catch (IOException e)
            {
                Log.i(MYDEBUG, "voiceRecorder prepare() failed: e=" + e);
                super.onDestroy();
                this.finish();
            }

            // OK, we're ready to go --> start recording
            voiceRecorder.start();
        }

        // stop recording the voice message
        else if (v == stopRecordButton)
        {
            recordProgress.setIndeterminate(false); // stop animation for indeterminate progress

            recording = false;
            recordTimer.cancel();
            recordSeconds = 0;
            recordTime.setText(String.format(Locale.CANADA, "%s", "0:00"));

            if (voiceRecorder != null)
            {
                voiceRecorder.stop();
                voiceRecorder.release();
                voiceRecorder = null;
            }

            /*
             * Make the saved data file visible in Windows Explorer. There seems to be bug doing
			 * this with Android 4.4. I'm using the following code, instead of sendBroadcast. See...
			 *
			 * http://code.google.com/p/android/issues/detail?id=38282
			 */
            MediaScannerConnection.scanFile(this, new String[] {audioFilename}, null, null);


            playbackDuration = DemoVoiceEmailActivity.getDuration(new File(audioFilename));
            Log.i(MYDEBUG, "Audio message duration = " + playbackDuration);
        }

        // play back the recorded message
        else if (v == playButton)
        {
            playbackSeconds = 0;
            // don't allow playback when recording
            if (voiceRecorder != null)
            {
                Log.i(MYDEBUG, "Recording in progress (playback disabled)!");
                Toast.makeText(this, "Recording in progress (playback disabled)!", Toast.LENGTH_LONG).show();
                return;
            }

            if (audioFilename == null)
            {
                Log.i(MYDEBUG, "No audio file to playback!");
                Toast.makeText(this, "No audio file to playback!", Toast.LENGTH_LONG).show();
                return;
            }

            voicePlayer = new MediaPlayer();

            // attach an error listener (yes, it's conceivable something might go wrong)
            voicePlayer.setOnErrorListener(this);

            try
            {
                voicePlayer.setDataSource(audioFilename);
                //voicePlayer.reset();
                //playbackDuration = voicePlayer.getDuration(); // returns duration in milliseconds
                voicePlayer.prepare();
                voicePlayer.start();
            } catch (IOException e)
            {
                Log.i(MYDEBUG, "voicePlayer prepare() failed: e=" + e);
                super.onDestroy();
                this.finish();
            }

            // time the playback and output the time in a text field
            playingback = true;
            playbackTimer.cancel();
            playbackTimer.start();
        }

        // stop playing back the recorded message
        else if (v == stopPlayButton)
        {
            doStopPlayback();
        }

        // send the recorded message using an email intent
        else if (v == sendButton)
        {
            if (audioFilename == null)
            {
                Log.i(MYDEBUG, "No audio file to playback!");
                Toast.makeText(this, "No audio file to send!", Toast.LENGTH_LONG).show();
                return;
            }

            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // add your name when the email intent launches
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Voice message from ");

            emailIntent.putExtra(Intent.EXTRA_STREAM, audioFilename);
            emailIntent.setType("audio/3gp");
//            emailIntent.setType("audio/mp4");
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        Log.i(MYDEBUG, "Oops! MediaPlayer error! what=" + what + ", extra=" + extra);
        return true;
    }

    /*
     * This code is put into a method since there are two situations where playback must be stopped:
     * (i) when playback is finished, or (ii) when the user taps the Stop button.
     */
    private void doStopPlayback()
    {
        playingback = false;
        playbackTimer.cancel();
        playbackSeconds = 0;
        playbackTime.setText(String.format(Locale.CANADA, "%s", "0:00"));

        if (voicePlayer != null)
        {
            voicePlayer.release();
            voicePlayer = null;
        }
    }

    // release the audio recorder and audio playback services
    private void releaseAll()
    {
        if (voiceRecorder != null)
        {
            voiceRecorder.release();
            voiceRecorder = null;
        }

        if (voicePlayer != null)
        {
            voicePlayer.release();
            voicePlayer = null;
        }

        // ... and cancel timers
        recordTimer.cancel();
        playbackTimer.cancel();
    }

    // upon each timeout, increment the time (number of seconds)
    private void incrementRecordTime()
    {
        ++recordSeconds;
        recordTime.setText(getTimeString(recordSeconds));
    }

    private void incrementPlaybackTime()
    {
        if (voicePlayer != null && voicePlayer.isPlaying()) // playback in
        // progress
        {
            ++playbackSeconds;
            playbackTime.setText(getTimeString(playbackSeconds));
        } else
        // playback has finished
        {
            Log.i("MYDEBUG", "Playback finished!");
            doStopPlayback();
        }
    }

    /*
     * Convert seconds into a time string ranging from 0:00 to 9:59. If the minutes exceeds 9, the
     * string is returned with "?" as the minutes.
     */
    private String getTimeString(int secondsArg)
    {
        String seconds = String.valueOf(secondsArg % 60);
        if (seconds.length() == 1)
            seconds = "0" + seconds;
        String minutes = secondsArg / 60 < 10 ? String.valueOf(secondsArg / 60) : "?";
        return minutes + ":" + seconds;
    }

    // if the Activity is paused for any reason, release the audio resources
    @Override
    public void onPause()
    {
        super.onPause();
        releaseAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
