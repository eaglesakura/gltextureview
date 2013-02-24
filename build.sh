#! /bin/sh
rm -rf release
mkdir release

cd GLTextureView
android update lib-project -p .
ant release
mv ./bin/classes.jar ../release/GLTextureView.jar
cd ..
