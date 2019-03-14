#!bin/bash

#Assumes at least one file the name of which ends with '_<number>' (starting from 1 and in order)
#	in a folder the name of which is given as third an to this script
# The file(s) contains the texts of the web pages that are to be divided into sentences 
#	and each page has:
# 1) 'WARC-Target-URL: '+<url> (of the page)
# 2) text of the page without html or other code
# 3) 'WARC-Date:' with or without the date the page was downloaded.
#
#The script creates 10 different files or folders

#arguments of the script
#pages=$1
#nrOfFiles=$2

server=$1
port=$2

notAvailable=0
wrongLang=no
notUralic=no
verified=no
#folder=$2

#
##performs MultiLI testing to all webpages in files in the folder given as argument
#
for file in $(ls ${pages}/); do
	java -jar ~/Java/SukiMultiliPages.jar $file $server $port
done

#
##waits for all the testing to have finished
#
wait

#
## gets all pages in relevant Uralic languages from each tested file 
## leaving out the lines that do not contain at least one letter and one punctuation symbol
#
for ((i=1; i<$nrOfFiles+1; i++));do
	java -jar ~/Java/SukiGetWantedPages.jar $1
done

#
## gets all the lines with the multiLI languages and the url of the page they were found in > 3_allLines_withUrl
#
for file in $(ls 2_wantedPages/); do
	java -jar ~/Java/SukiGetLines.jar 2_wantedPages/${file}
done

#
## gets all unique lines, one line and url represent all duplicates, the multiLI languages is an union
#
java -jar ~/Java/SukiMakeUnique.jar 3_allLines_withUrl $notAvailable > 4_allLines_uniqued

#
## divides the lines evenly to 50 files in folder 5_UniquedLines
#
mkdir -p 5_UniquedLines
java -jar ~/Java/SukiFileDivider.jar 4_allLines_uniqued 5_UniquedLines

#
## performs MultiLI testing to the 50 files in folder 5_UniquedLines
#
mkdir -p 6_MultiliLines
for ((i=1; i<51; i++));do
	java -Xmx4000m -jar ~/Java/SukiMultiliLines.jar $i $server $port &
done

#
## waits for all the testing to finish
#
wait

#
## divides lines to possible sentences
#
touch 7_allSents_original
for ((i=1; i<51; i++));do
	java -jar ~/Java/SukiSentenceMaker.jar 6_MultiliLines/lines_multilied.${i} >> 7_allSents_original
done

#
## selects proper sentences
#
java -jar ~/Java/SukiSentenceSelector.jar 7_allSents_original > 8_allSents_selected

#
## gets unique sentences
#
java -jar ~/Java/SukiMakeUnique.jar 8_allSents_selected $notAvailable > 9_allSents_uniqued

#
## performs MultiLI testing on the unique sentences and writes them to the files corresponding to the final language identification
#
mkdir -p A_MultiLiSents
java -jar ~/Java/SukiMultiLiSentences.jar 9_allSents_uniqued $server $port A_MultiLiSents $wrongLang &
