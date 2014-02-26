# Little script to get the number of citations given a file of PMIDs (pmids.txt)
# See http://europepmc.org/RestfulWebService#cites
# Tested on tcsh
# curl -s makes it run in "silent" mode
# <hitCount>\K.*?(?=<) is the magic that gets text between <hitCount> ... </hitCount>
while read p; do
#  echo $p
  curl -s http://www.ebi.ac.uk/europepmc/webservices/rest/MED/$p/citations | grep -Po "<hitCount>\K.*?(?=<)"
done < pmids.txt
