# About

Project is a simple NFT generator using java and javaFX and include the abilities to:

- Have a layer occur only every n layers
- Limit the max occurrence of a specific image in a layer
- Uses weighted random generation for image occurrence
- Avoid duplicate generations with the ability to ignore background images
- Outputs a csv of NFTs image files and their trait list which can be parsed to generate metadata

Project is fairly bare bones and ugly, but I needed a generator myself and decided to go the extra mile and release an open 
source generator with a GUI to make it easy for people to generate their own NFT collections without scripting knowledge 
or paying the ridiculous amounts that some services that provide near the same functionality charge.

#Requirments

To run the jar file you will need openJDK 17 installed (oracleJDK should work fine as well). To have the modern java JRE.

A wrapped .exe with the JRE included is available in releases.

###Building

If you wish to build from source, ideally you would want a modern IntelliJ release, set up for javaFX 17. Maven wasn't used 
for the project as I had some issues occur (that I later figured out) with javafx using it. In later releases I will
try to port it to maven to allow for easier dealing with the javaFX dependencies.

That said it should be fairly easy to import it into a maven project with the javafx dependency and build if one wishes to.

To mirror the IDE set up I used:

- Download javaFX 17.0.2  https://gluonhq.com/products/javafx/.
- In Project->Settings:Path Variables | Create Path_To_FX and point it to the JavaFx SDK lib folder.
- In File->Project Structure:Libraries | add the javaFX SDK lib folder.
- In your run configuration Modify Options->Add VM Options | Under the new field add: 
  - "--module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics" without quotes.
- Set up your build artifact like this https://www.youtube.com/watch?v=F8ahBtXkQzU to export as jar.

I'm not sure if the path/vm options are needed just to build but that how my IDE was set up for testing.


#How To Use

###Layers
Opens with a fresh layout, once a layer is added it will generate a default collection name and attributes which should be
replaced before generation.

Layer# 0 will always be considered the background layer for the duplications disregard setting (This allows for unique NFTs, 
that don't take into account the background).

Layers will be added with the default name Layer_n, this should be changed if you want the outputted csv to have proper 
layer/attribute name for metadata scraping.

Layer # is the level in which overlaying the layers/attributes will occur, this needs to be set correct to get a proper 
overlaying of your attributes for their visual representation.

Occurrence is how often a layer will occur. 

Example: If set to 4 a layer will occur every 4 NFT generations.

*Be sure to save for every layer change before switch to a new layer*

###Images

Import Files is used to import all files in a directory into a layer.

Open File is used to import/add a single file.

Image names are mirrored from the file name and are what will be outputted to the csv, this can be changed.

Image weight is a float value between 0-1 (Can be though of as a percentage), this value is used to add weight to specific 
traits. This doesn't need to be changed from the default of 1 if you want every layer to have the same random chance of 
occurring.

If you want to have a specific ration it is ideal to change the value and make sure that all your values for the layer add 
up to 1. Though you don't necessarily have to do it this way it is the most intuitive. There is still a random factor so
image occurrence is not going to be exact to the weight, but it will be close. True rarity can be found in the final csv.

File lets you replace and image file and keep all the existing settings for that image.

*Be sure to save for every image change before switch to a new image*

###Collection

Collection Name isn't really used at the moment, but will be used once saving is added.

Collection Prefix is the prefix for the files generated.

Example prefix(n) so a prefix of prefix_ would generate prefix_1,prefix_2 etc. as the file names.

Output Directory is the directory to output the finished generation files.

Collection Size is the size of the collection.

###Generation

No Duplicates can be selected to avoid duplicate generation.

Disregard Background can be selected to disregard backgrounds when checking the uniqueness(Not a duplicate) during generation.

Test generates a random NFT and output it to the image view to test that everything is set correctly with your layers before 
generation.

Stop will stop a generation in progress.

Generate will begin the final generation process.

###Things to consider

You'll want to make sure that your layer occurrences and image max values, as well as images/layers in general are aligned
with your collection size. Generation will stop after generated + Collection Size * 2 iterations of generation attempts occur.

This isn't and issue if you have enough unqiue elements to fit the collection size if you are wanting to avoid duplicates.

This can easily be calculated by multiplying the total amount of images in each layer. But will be more convoluted if you
are using Layer Occurrence and Image Max.

#Future Updates

- Serialization of project settings to allow for saving and opening in progress collection edits.
- Scripts to parse trait csv and generate Ethereum, Solana and Chia metadata.


#License 

Project and all code is free to use in both a hobbyist and profession setting. Project is open source and changes can be 
made to suit your needs.

*Sell of any project containing the original or edited code is prohibited.*





