# Cloud-Robotics FTC Coded by Hunter Webb (README author) and Bryce Lutz

Code from the 2020 FTC team of Cloud Robotics. 

## Acknowledgements

The pathing code to spline together curved paths is provided by [Road Runner](https://github.com/acmerobotics/road-runner).

The Neural Network is done with [Easy Opencv](https://github.com/OpenFTC/EasyOpenCV) in tangent with the [YOLO](https://github.com/AlexeyAB/darknet) RNN framework from darknet

## Installation

For more detailed instructions on getting Road Runner setup in your own project, see the [Road Runner README](https://github.com/acmerobotics/road-runner#core).

1. Download or clone this repo with `git clone https://github.com/acmerobotics/road-runner-quickstart`.

2. Open the project in Android Studio and build `TeamCode` like any other `ftc_app` project.

3. Add in the dependencies from [Road Runner's README](https://github.com/acmerobotics/road-runner#core) and [EasyOpenCV's README](https://github.com/OpenFTC/EasyOpenCV/blob/master/readme.md) 

## Steps to Create a Custom NN

1. Download the dependencies mentioned by [AlexeyAB](https://github.com/AlexeyAB/darknet) to install darknet
2. Make a dataset of images (a hundred / object for simple objects works, and include images without the object to leave blank)
3. Label the images (I used [LabelImg](https://github.com/tzutalin/labelImg)) with the yolo setting turned on (for the images without the objects 
4. Create the configuration files for training an object [instructions here](https://github.com/AlexeyAB/darknet#how-to-train-to-detect-your-custom-objects)
5. Download [darknet53.conv.74](https://pjreddie.com/media/files/darknet53.conv.74)
6. (I used Windows PowerShell) Navigate to the darknet file and start training (example code shown below, but change files based on what you named them)

```
.\darknet detector train data/obj.data yolo-obj.cfg darknet53.conv.74
```

## Tips for Creating a Custom Neural Network for FTC

Follow AlexeyAB's [instructions](https://github.com/AlexeyAB/darknet) for training a custom neural network

###### Dataset Creation / Labeling:

It is **very** important to label all of the object that you want to identify in the images you have, otherwise the neural network will have a very hard training when it keeps identifying an object correctly and being told that it is wrong 

The best labeling tool I found for creating my custom dataset for Skystone and Stones (of around 325 photos) was [LabelImg](https://github.com/tzutalin/labelImg)

For objects that are obscured, label the part that is visible

###### Neural Network Size:

I found that it was necessary to use the YOLO-tiny configuration he explains for the processing power of our SamsungGalaxyS5

## Helpful Sources

Road Runner's [online quickstart documentation](https://acme-robotics.gitbook.io/road-runner/quickstart).

Darknet's [YOLO documentation](https://pjreddie.com/darknet/yolo/) 

For more information on how YOLO works, watch Joseph Redmon's TED Talk:

<a href="http://www.youtube.com/watch?feature=player_embedded&v=Cgxsv1riJhI
" target="_blank"><img src="http://img.youtube.com/vi/Cgxsv1riJhI/0.jpg" 
alt="IMAGE ALT TEXT HERE" width="240" height="180" border="10" /></a>

## Bugs

Currently there is a bug where files duplicate themselves causing names which break the gradle build, and to fix this type 
```
.\gradlew clean
```
into the terminal of the project (for IntelliJ, the terminal can be found with View-ToolWindows-Terminal) 
