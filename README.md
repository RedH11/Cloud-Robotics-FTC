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

