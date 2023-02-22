package com.videorecorder


import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX
import com.videorecorder.databinding.ActivityMainBinding
import java.io.IOException


class MainActivity : AppCompatActivity() {

   private lateinit var binding:ActivityMainBinding
    val REQUEST_VIDEO_CAP = 324

    lateinit var mediaRecorder:MediaRecorder
    lateinit var  holder:SurfaceHolder
    var recording = false
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionX.init(this)
            .permissions(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }

        mediaRecorder= MediaRecorder()
        initRecorder();
        val cameraView:SurfaceView = binding.videoView
        holder = cameraView.holder
        holder.addCallback(object:SurfaceHolder.Callback{
            override fun surfaceCreated(p0: SurfaceHolder) {
                prepareRecorder();
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                if (recording) {
                    mediaRecorder.stop();
                    recording = false;
                }
                mediaRecorder.release();

            }

        })

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        cameraView.setClickable(true);
        cameraView.setOnClickListener{

        };

        binding.startRecording.setOnClickListener {
            startRecording()
        }

        binding.btnStop.setOnClickListener {

            if (recording) {
                mediaRecorder.stop();
                recording = false;
            }
            mediaRecorder.release();





        }

//        val mediaController = MediaController(this)
//        mediaController.setAnchorView(binding.videoView)
//        binding.videoView.setMediaController(mediaController)
    }

    private fun initRecorder() {



        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)



        val cpHigh = CamcorderProfile
            .get(CamcorderProfile.QUALITY_HIGH)
        mediaRecorder.setProfile(cpHigh)
        mediaRecorder.setVideoFrameRate(240);
        mediaRecorder.setVideoEncodingBitRate(35000000);
        mediaRecorder.setVideoSize(1920, 1080);


        Toast.makeText(this, cpHigh.videoFrameRate.toString(), Toast.LENGTH_SHORT).show()



        mediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath+"/vid${System.currentTimeMillis()}.mp4")
        mediaRecorder.setOnErrorListener { mr, what, extra ->
            Log.e("error", "MediaRecorder error: what = $what, extra = $extra")
        }

    }

    private fun prepareRecorder() {
        mediaRecorder.setPreviewDisplay(holder.surface)
        try {
            mediaRecorder.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            finish()
        } catch (e: IOException) {
            e.printStackTrace()
            finish()
        }
    }

    private fun startRecording(){
//        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//
//        if(intent.resolveActivity(packageManager)!=null){
//
//            startActivityForResult(intent,REQUEST_VIDEO_CAP)
//        }

//        val cam: Camera =Camera.open()
//        cam.unlock()
//
//        var mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath+"/vid.mp4"
//
//
//
//        mediaRecorder.reset()
//        mediaRecorder.setCamera(cam)
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
//        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
//        mediaRecorder.setOutputFile(mFileName)
//
//        try {
//            mediaRecorder.prepare()
//            mediaRecorder.start()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

        if (recording) {
            mediaRecorder.stop();
            recording = false;


            // Let's initRecorder so we can record again
            initRecorder();
            prepareRecorder();
        } else {
            recording = true;
            try {

                mediaRecorder.start()
            } catch (e: Exception) {
                Log.e("error", "Error starting MediaRecorder: ${e.message}")
            }

        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        if(requestCode==REQUEST_VIDEO_CAP && resultCode == RESULT_OK){
//            val videoUri = data?.data
//            binding.videoView.setVideoURI(videoUri)
//            binding.videoView.start()
//        }
//
//        super.onActivityResult(requestCode, resultCode, data)
//
//    }


}
