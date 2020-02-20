package org.firstinspires.ftc.teamcode.Vision;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;
import org.tensorflow.lite.TensorFlowLite;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Helpful Resource:
// https://github.com/suddh123/YOLO-object-detection-in-java/blob/code/yolo.java

public class NNVision {

    private HardwareMap hardwareMap;
    private OpenCvCamera phoneCam;

    private final int rows = 640;
    private final int cols = 480;

    private Telemetry telemetry;

    public NNVision(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        initCamera();
    }

    public void initCamera() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        phoneCam.openCameraDevice();
        phoneCam.setPipeline(new StageSwitchingPipeline());
        // Phone Streaming
        phoneCam.startStreaming(rows, cols, OpenCvCameraRotation.SIDEWAYS_RIGHT); //display on RC
    }

    // ---------------------------------------------------------------------------------------------
    // SUB-CLASS FOR PIPELINE
    // ---------------------------------------------------------------------------------------------

    class StageSwitchingPipeline extends OpenCvPipeline {

        // Neural Network Outputs
        private List<Mat> result = new ArrayList<>();
        private List<String> outBlobNames;
        private List<Integer> clsIds = new ArrayList<>();
        private List<Float> confs = new ArrayList<>();
        private List<Rect> rects = new ArrayList<>();

        // Neural Network Variables
        float detectionThreshold = 0.3f; // Threshold beyond which the model will detect objects
        private Size size = new Size(640, 640);

        // Image processing
        private List<MatOfPoint> contoursList = new ArrayList<>();

        private Stage stageToRenderToViewport = Stage.detection;
        private Stage[] stages = Stage.values();

        // My training
        //private String configFile = "yolo-obj.cfg.txt";
        //private String weightsFile = "yolo-obj_4000.weights";

        // Darknet Training
        private String configFile = "darknet.cfg.txt";
        private String weightsFile = "darknet.weights";

        // Get the path to the files
        File sdcard = Environment.getExternalStorageDirectory();
        File config = new File(sdcard, "FIRST/" + configFile);
        File weights =new File(sdcard, "FIRST/" + weightsFile);

        boolean frameProcessing = false;

        @Override
        public void onViewportTapped() {
            /*
             * Note that this method is invoked from the UI thread
             * so whatever we do here, we must do quickly.
             */

            int currentStageNum = stageToRenderToViewport.ordinal();

            int nextStageNum = currentStageNum + 1;

            if (nextStageNum >= stages.length) nextStageNum = 0;

            stageToRenderToViewport = stages[nextStageNum];
        }

        @Override
        public Mat processFrame(Mat frame) {
            if (!frameProcessing) {
                printToTel("1");
                try {
                    //runNN(frame);
                } catch (Exception ex) {
                    printToTel(ex.getMessage());
                }
                frameProcessing = true;
            }

            //Imgproc.cvtColor(frame, yCbCrChan2Mat, Imgproc.COLOR_RGB2YCrCb); //converts rgb to ycrcb
            return frame;
        }

        @TargetApi(Build.VERSION_CODES.N)
        private List<String> getOutputNames(Net net) {
            List<String> names = new ArrayList<>();

            List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
            List<String> layersNames = net.getLayerNames();

            outLayers.forEach((item) -> names.add(layersNames.get(item - 1)));//unfold and create R-CNN layers from the loaded YOLO model//
            return names;
        }

        private void runNN(Mat frame) throws InterruptedException {
            contoursList.clear();

            printToTel("2");
            Net net = Dnn.readNetFromDarknet(config.getAbsolutePath(), weights.getAbsolutePath());
            printToTel("3");
            outBlobNames = getOutputNames(net);
            printToTel("4");

            Mat blob = Dnn.blobFromImage(frame, 0.00392, size, new Scalar(0), true, false); // We feed one frame of video into the network at a time, we have to convert the image to a blob. A blob is a pre-processed image that serves as the input.//
            net.setInput(blob);
            net.forward(result, outBlobNames); //Feed forward the model to get output

            for (int i = 0; i < result.size(); ++i) {
                // Each row is a candidate detection, the 1st 4 numbers are [center_x, center_y, width, height], followed by (N-4) class probabilities
                Mat level = result.get(i);

                for (int j = 0; j < level.rows(); j++) {
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());
                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                    float confidence = (float) mm.maxVal;
                    Point classIdPoint = mm.maxLoc;

                    if (confidence > detectionThreshold) {
                        int centerX = (int)(row.get(0,0)[0] * frame.cols()); // Scaling for drawing the bounding boxes
                        int centerY = (int)(row.get(0,1)[0] * frame.rows());
                        int width   = (int)(row.get(0,2)[0] * frame.cols());
                        int height  = (int)(row.get(0,3)[0] * frame.rows());
                        int left    = centerX - width  / 2;
                        int top     = centerY - height / 2;

                        clsIds.add((int) classIdPoint.x);
                        confs.add(confidence);
                        rects.add(new Rect(left, top, width, height));
                    }
                }
            }

            float nmsThresh = 0.5f;
            MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
            Rect[] boxesArray = rects.toArray(new Rect[0]);
            MatOfRect boxes = new MatOfRect(boxesArray);
            MatOfInt indices = new MatOfInt();
            Dnn.NMSBoxes(boxes, confidences, detectionThreshold, nmsThresh, indices); // We draw the bounding boxes for objects here

            int[] ind = indices.toArray();

            for (int i = 0; i < ind.length; i++) {
                int idx = ind[i];
                Rect box = boxesArray[idx];
                Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(0,0,255), 2);
            }

            //return new Mat();
        }

        private void printToTel(String num) {
            telemetry.addData("Test Break: ", num);
            telemetry.update();
        }
    }
}

enum Stage {//color difference. grayscale
    detection,//includes outlines
    THRESHOLD,//b&w
    RAW_IMAGE,//displays raw view
}

