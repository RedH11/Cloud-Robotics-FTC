package org.firstinspires.ftc.teamcode.Vision;

import android.os.Environment;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.NNMode;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * TODO
 * 1. Figure out how to mitigate NN error by averaging the readings of a few frames
 *      Possibly I may take ~3-5 frames and make a new array based on the blocks they read
 *          The same blocks are only kept once, but new readings from some frames are analyzed as possible missed readings in the other frames
 *              This can be done through finding the average width and height of the blocks seen by the NN to see what is a false reading and what is real
 * 2. Possibly have the NN run until all 6 of the array slots are filled with something
 * 3. Implement the other sizes of NN to see if they run faster and their accuracy
 * 4. Make methods to detect the distance and angle away from the robot that the blocks are
 */

public class NNVision {

    private HardwareMap hardwareMap;
    private OpenCvCamera phoneCam;

    private final int rows = 640;
    private final int cols = 480;

    private Telemetry telemetry;

    // YOLO Configuration files
    //private String configFile = "yolo-tiny_custom.cfg.txt";
    //private String weightsFile = "yolo-tiny_custom_13000.weights";

    private String configFile = "yolo-obj_256.cfg.txt";
    private String weightsFile = "yolo-tiny_256_custom_14000.weights"; // Change # to the correct number from the file

    /*
    Smaller NN File Presets for Testing

    private String configFile = "yolo-tiny_160_custom.cfg.txt";
    private String weightsFile = "yolo-tiny_160_custom_#.weights"; // Change # to the correct number from the file

    private String configFile = "yolo-tiny_320_custom.cfg.txt";
    private String weightsFile = "yolo-tiny_320_custom_#.weights"; // Change # to the correct number from the file

    private String configFile = "yolo-tiny_256_custom.cfg.txt";
    private String weightsFile = "yolo-tiny_256_custom_#.weights"; // Change # to the correct number from the file

    private String configFile = "yolo-tiny_64_custom.cfg.txt";
    private String weightsFile = "yolo-tiny_64_custom_#.weights"; // Change # to the correct number from the file
     */

    // Get the path to the files
    File sdcard = Environment.getExternalStorageDirectory();
    File config = new File(sdcard, "FIRST/" + configFile);
    File weights = new File(sdcard, "FIRST/" + weightsFile);

    private Net net;

    private NNMode mode;

    public NNVision(HardwareMap hardwareMap, Telemetry telemetry, NNMode mode) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.mode = mode;
        createNN();
        initCamera();
    }

    public void initCamera() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        phoneCam.openCameraDevice();
        phoneCam.setPipeline(new StageSwitchingPipeline());
        phoneCam.startStreaming(rows, cols, OpenCvCameraRotation.UPRIGHT); //display on RC

    }

    private void createNN() {
        net = Dnn.readNetFromDarknet(config.getAbsolutePath(), weights.getAbsolutePath());
    }

    // ---------------------------------------------------------------------------------------------
    // SUB-CLASS FOR CAMERA PIPELINE
    // ---------------------------------------------------------------------------------------------

    class StageSwitchingPipeline extends OpenCvPipeline {

        // Display Variables for YOLO
        private List<String> classNames = new ArrayList<String>() {
            {
                add("SkyStone");
                add("Stone");
            }
        };

        private List<Scalar> colors = new ArrayList<Scalar>() {
            {
                add(randomColor());
                add(randomColor());
            }
        };

        private int objFound = 0;

        // Results of YOLO Prediction
        private Stage stageToRenderToViewport = Stage.detection;
        private Stage[] stages = Stage.values();


        // My training
        private String configFile = "yolo-obj_64.cfg.txt";
        private String weightsFile = "yolo-tiny_custom_10000.weights";

        // Darknet Training
        //private String configFile = "darknet.cfg.txt";
        //private String weightsFile = "darknet.weights";

        //private String configFile = "yolo-obj.cfg.txt";
        //private String weightsFile = "yolo-obj_4000.weights";

        // Darknet Training
        //private String configFile = "darknet.cfg.txt";
        //private String weightsFile = "darknet.weights";

        // For Frame Input testing (saving files to the phone to see what the NN sees)
        private int frameCount = 0;
        private int fileCount = 0;

        int[] blockArrangements;

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
        public Mat processFrame(Mat frame){

            try {

                if (mode == NNMode.display) {
                    runNNWithDisplay(frame);
                } else if (mode == NNMode.initial) {

                    blockArrangements = getStartingBlockPositions(frame);

                    // Make an output from what it read
                    String output = "";
                    for (int i = 0; i < blockArrangements.length; i++) output += blockArrangements[i] + ", ";

                    printToTel("Block Arrangement: ", output);
                } else if (mode == NNMode.running) {
                    // Create a method for on the go identification of blocks if needed
                }

                //telemetry.addData("Obj Found:", objFound);
                //telemetry.update();
            } catch (InterruptedException ex) {
                printToTel("CAMERA ERROR", ex.getMessage() + " " + ex.getCause());
            }

            return frame;
        }

        /**
         * Saves the phone's input to an
         * @param frame
         */
        private void saveMAT(Mat frame) {

            File file = new File(sdcard, "FIRST/filename"+ fileCount + ".png");

            if (frameCount % 20 == 0) {
                //Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2BGR);
                Imgcodecs.imwrite(file.getAbsolutePath(), frame);
                telemetry.addData("File Num:", fileCount);
                telemetry.update();
                fileCount++;
            }

            frameCount++;
        }

        /**
         * Runs the neural network while displaying the output to the camera
         * @param frame The input image taken from the phone's camera
         * @throws InterruptedException
         */
        private void runNNWithDisplay(Mat frame) throws InterruptedException {
            objFound = 0;

            // For some reason, the frame messes with the RGBA values where yellow became blue, and this conversion fixes that
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2BGR);
            // The standard size of images processed by the neural network
            Size frame_size = new Size(416, 416);
            // Clean up image for the neural network
            Mat blob = Dnn.blobFromImage(frame, 0.00392, frame_size, new Scalar(0), true, false);
            // Input the image into the neural network
            net.setInput(blob);

            List<Mat> result = new ArrayList<>();
            List<String> outBlobNames = net.getUnconnectedOutLayersNames();

            // Process the input frame
            net.forward(result, outBlobNames);

            // How confident the NN has to be to have an object be registered
            float confThreshold = 0.5f;

            for (int i = 0; i < result.size(); ++i) {
                // each row is a candidate detection, the 1st 4 numbers are
                // [center_x, center_y, width, height], followed by (N-4) class probabilities
                 Mat level = result.get(i);
                for (int j = 0; j < level.rows(); ++j) {
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());
                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                    float confidence = (float) mm.maxVal;
                    Point classIdPoint = mm.maxLoc;
                    if (confidence > confThreshold) {
                        objFound++;
                        int centerX = (int) (row.get(0, 0)[0] * frame.cols());
                        int centerY = (int) (row.get(0, 1)[0] * frame.rows());
                        int width = (int) (row.get(0, 2)[0] * frame.cols());
                        int height = (int) (row.get(0, 3)[0] * frame.rows());

                        int left = (int) (centerX - width * 0.5);
                        int top =(int)(centerY - height * 0.5);
                        int right =(int)(centerX + width * 0.5);
                        int bottom =(int)(centerY + height * 0.5);

                        Point left_top = new Point(left, top);
                        Point right_bottom=new Point(right, bottom);
                        Point label_left_top = new Point(left, top-5);
                        DecimalFormat df = new DecimalFormat("#.##");

                        int class_id = (int) classIdPoint.x;
                        String label = classNames.get(class_id) + ": " + df.format(confidence);
                        Scalar color = colors.get(class_id);

                        Imgproc.rectangle(frame, left_top,right_bottom , color, 3, 2);
                        Imgproc.putText(frame, label, label_left_top, Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 4);
                        Imgproc.putText(frame, label, label_left_top, Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 255), 2);
                    }
                }
            }
        }

        private int[] getStartingBlockPositions(Mat frame) {

            // Used to determine which axis should be used for the block positions
            boolean phoneIsSideways = false;
            boolean checkedOrientation = false;

            int index = 0;

            int[] blockType = new int[6]; // -1: Empty, 0: Stone, 1: SkyStone from left to right
            int[] blockPositions = new int[6];

            for (int i = 0; i < 6; i++) blockPositions[i] = -1;

            // For some reason, the frame messes with the RGBA values where yellow became blue, and this conversion fixes that
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2BGR);
            // The standard size of images processed by the neural network
            Size frame_size = new Size(416, 416);
            // Clean up image for the neural network
            Mat blob = Dnn.blobFromImage(frame, 0.00392, frame_size, new Scalar(0), true, false);
            // Input the image into the neural network
            net.setInput(blob);

            List<Mat> result = new ArrayList<>();
            List<String> outBlobNames = net.getUnconnectedOutLayersNames();

            // Process the input frame
            net.forward(result, outBlobNames);

            // How confident the NN has to be to have an object be registered
            float confThreshold = 0.5f;

            for (int i = 0; i < result.size(); ++i) {
                // each row is a candidate detection, the 1st 4 numbers are
                // [center_x, center_y, width, height], followed by (N-4) class probabilities
                Mat level = result.get(i);
                for (int j = 0; j < level.rows(); ++j) {
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());
                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                    float confidence = (float) mm.maxVal;
                    Point classIdPoint = mm.maxLoc;

                    if (confidence > confThreshold) {

                        int centerX = (int) (row.get(0, 0)[0] * frame.cols());
                        int centerY = (int) (row.get(0, 1)[0] * frame.rows());
                        int width = (int) (row.get(0, 2)[0] * frame.cols());
                        int height = (int) (row.get(0, 3)[0] * frame.rows());

                        if (width < height && !checkedOrientation) {
                            checkedOrientation = true;
                            phoneIsSideways = true; // If the blocks perceived width is less than their height, then the phone cam is sideways
                        }

                        DecimalFormat df = new DecimalFormat("#.##");
                        int class_id = (int) classIdPoint.x;
                        String label = classNames.get(class_id) + ": " + df.format(confidence);

                        // Use the image's y axis to determine the block placements
                        if (phoneIsSideways) {

                            blockPositions[index] = centerY;

                            if (label.contains("Sky")) {
                                blockType[index] = 1;
                            } else {
                                blockType[index] = 0;
                            }

                            index++;

                            // Use the image's x axis to determine the block placements
                        } else {

                            blockPositions[index] = centerX;

                            if (label.contains("Sky")) {
                                blockPositions[index] = 1;
                            } else {
                                blockPositions[index] = 0;
                            }

                            index++;
                        }
                    }
                }
            }

            return sortBlocks(blockType, blockPositions);
        }

        /**
         * Bubble sorts the blocks so that the ones with the smallest x value are on the left
         * @param blockType An array of the different types of blocks (-1: Nothing, 0: Stone, 1: Skystone)
         * @param blockPositions An array of the positions of the blocks (x or y center based on phone orientation)
         * @return A sorted array of the block types representing what the NN perceives from left to right
         */
        private int[] sortBlocks(int[] blockType, int[] blockPositions) {

            for (int i = 0; i < blockPositions.length - 1; i++) {
                if (blockPositions[i + 1] < blockPositions[i]) {

                    int tempBlockType = blockType[i];
                    int tempBlockPos = blockPositions[i];

                    blockPositions[i] = blockPositions[i + 1];
                    blockType[i] = blockType[i + 1];

                    blockPositions[i + 1] = tempBlockPos;
                    blockType[i] = tempBlockType;
                }
            }

            return blockType;
        }

        private Scalar randomColor() {
            Random random = new Random();
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);
            return new Scalar(r,g,b);
        }


        private void printToTel(String caption, String message) {
            telemetry.addData(caption, message);
            telemetry.update();
        }
    }
}

enum Stage {//color difference. grayscale
    detection,//includes outlines
    THRESHOLD,//b&w
    RAW_IMAGE,//displays raw view
}

