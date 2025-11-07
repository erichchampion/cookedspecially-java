#============================================================================
# - COPYRIGHT NOTICE -
# Copyright (c) CookedSpecially, All Rights Reserved
#
# Author: Abhishek Kumar  abhishek@cookedspecially.com
#============================================================================

echo 
echo "Take Latest Upadte"
source updateWorkspace.sh


echo
echo "Deploying Snapshot Code For Testing...." 
mvn deploy $USERCRED

echo
echo "done."
echo
