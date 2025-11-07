#============================================================================
# - COPYRIGHT NOTICE -
# Copyright (c) CookedSpecially, All Rights Reserved
#
# Author: Abhishek Kumar  abhishek@cookedspecially.com
#============================================================================
SCRIPT=$(readlink -f $0)
SCRIPT_DIR=`dirname $SCRIPT`
cd $SCRIPT_DIR

source common.sh

echo
echo "update workspace to latest svn revision." 
mvn scm:update $USERCRED

echo
echo -e "${GREEN}Local repository has been updated succeessfully...${NC}"
echo
