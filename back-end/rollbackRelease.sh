#============================================================================
# - COPYRIGHT NOTICE -
# Copyright (c) British Telecommunications plc, 2010, All Rights Reserved
#
# Author: Abhishek Kumar
#============================================================================
SCRIPT=$(readlink -f $0)
SCRIPT_DIR=`dirname $SCRIPT`
cd $SCRIPT_DIR

source common.sh

echo
echo "Rollback release."
mvn release:rollback release:clean "${USERCRED}"

echo
echo "done."
echo
