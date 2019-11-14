SUKISentencePipeline
====================

These are the scripts and code for making sentence corpora from texts downloaded from the web. The pipeline has been build for finding sentences in small Uralic languages. With some changes it can be used for other language as well. We wanted each step of the workflow to be independent and do only one thing for better quality control and modularity. Each step can, thus, also be performed independently (see scripts/startSentenceMaker.sh for how to start each step).

### Starting the pipeline

The pipeline assumes that MultiLi, a language set identification method, is set up. It also assumes one or several files the name of which ends with '_digit' (starting from 1 and in order) and which are in a folder the name of which is given as third argument to this script. 


The files should contain the texts of the web pages, without code, and each page has the following format:
   1. 'WARC-Target-URL: '+<url> (of the page)
   2. text of the page without html or other code
   3. 'WARC-Date:' with or without the date the page was downloaded.

The pipeline can be started from the command line:

`sh startSentenceMaker.sh <server> <port> <folder>` where

   * server is the server where MultiLi is situated
   * port is the port where MultiLi is listening
   * folder is the name of the directory where the files are

### Steps of the pipeline

The pipeline performs the following steps:
1. SukiMiltiliPages sends the texts of each page to MultiLi to be identified and writes the result and the text to a file corresponding to the digit in the filename to folder *1_tested*.
2. SukiGetWantedPages gets all pages in relevant Uralic languages from each file to corresponding file in folder .*2_wantedPages*. Leaves out all lines that do not contain at least one uppercase letter and one punctuation mark.
3. SukiGetLines extracts all the lines from the files and writes them to *3_allLines_withUrl* in the format:
   * Text of the line \[languages,given,by,MultiLI]Url.
4. SukiMakeUnique removes all duplicate lines using a thumbprint, languages are a union of the duplicates.
5. SukiFileDivider dives the lines that are in one file, into 50 files in folder *5_UniquedLines*.
6. SukiMultiliLines sends the lines in each file to MultiLi and writes the result in a corresponding file in folder *6_MultiliLines*.
7. SukiSentenceMaker divides the lines into sentences using Mikheev's and other heuristics.
8. SukiSentenceSelector selects all lines that actually start with an uppercase letter and end in punctuation.
9. SukiMakeUnique is used again to remove duplicate sentences.
10. SukiMultiLiSentences sends one line at a time to MultiLi to be identified. Each line in relevant Uralic language is written to the file corresponding to the language identified.


The pipeline has been described in:
Jauhiainen, Heidi, Tommi Jauhiainen, Krister Lindén. 2019. Wanca in Korp: Text Corpora for Under-Resourced Uralic Languages. Jantunen et al. *Proceedings of the Research data and humanities (RDHUM) 2019 conference : data, methods and tools*, 21-40. Oulu.

## MultiLi

MultiLi can be found on page https://github.com/tosaja/TunnistinPalveluMulti
