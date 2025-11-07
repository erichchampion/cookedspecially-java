#============================================================================
# - COPYRIGHT NOTICE -
# Copyright (c) CookedSpecially, All Rights Reserved
#
# Author: Abhishek Kumar  abhishek@cookedspecially.com
#============================================================================
SCRIPT=$(readlink -f $0)
SCRIPT_DIR=`dirname $SCRIPT`
cd $SCRIPT_DIR
CURRENT_DIR=$SCRIPT_DIR

source common.sh

echo "Take update."
./updateWorkspace.sh $USERNAME $PASSWORD

echo "Create release."
mvn -B release:clean release:prepare release:perform "${DUSERNAME}" "${DPASSWORD}"

echo "Release DONE Successfully..............................."
mvn scm:checkin -Dmessage="Release made By $USERNAME" "${DUSERNAME}" "${DPASSWORD}"
echo "release done."
