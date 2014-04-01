#!/bin/bash
#
#
help_info()
{
  echo "Usage:" 
  echo "$0 -k <Freebase API key> -q <query> -t <infobox|question>"
  echo "$0 -k <Freebase API key> -f <file of queries> -t <infobox|question>"
  echo "$0 -k <Freebase API key>"
  exit 1
}

#initialize
key=""
query=""
file=""
method=""
#
#
if [ $# -lt 2 -o $# -gt 6 ]
then
  help_info
fi

while getopts k:q:f:t: opt
do
  case "$opt" in
    k) key="$OPTARG";;
    q) query="$OPTARG";;
    f) file="$OPTARG";;
    t) method="$OPTARG";;
    k) key="$OPTARG";;
    *) echo help_info;;
  esac
done

# a single query
if [ "$key" != "" -a "$query" != "" -a "$file" = "" -a "$method" != "" ]
then
  java -cp $CP2  freebase.proj2 $2 "$4"
# a file of queries
elif [ "$key" != "" -a "$query" = "" -a "$file" != "" -a "$method" != "" ]
then
  while read LINE
  do
    java -cp $CP2 freebase.proj2 $2 "$LINE" $method
  done < $file
# iteratively 
elif [ "$key" != "" -a "$query" = "" -a "$file" = "" -a "$method" = "" ]
then
  java -cp $CP2 freebase.proj2 $2
else
  help_info
fi
