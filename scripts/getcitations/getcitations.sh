# Little script to get the number of citations given a file of PMIDs (pmids.txt)
# Tested on tcsh
while read p; do
#  echo $p
  curl -s http://www.ebi.ac.uk/europepmc/webservices/rest/MED/$p/citations | grep -Po "<hitCount>\K.*?(?=<)"
done < pmids.txt
