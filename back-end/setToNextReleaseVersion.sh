#============================================================================
# - COPYRIGHT NOTICE -
# Copyright (c) CookedSpecially, All Rights Reserved
#
# Author: Abhishek Kumar  abhishek@cookedspecially.com
#============================================================================
SCRIPT=$(readlink -f $0)
SCRIPT_DIR=`dirname $SCRIPT`
cd $SCRIPT_DIR

. common.sh

echo "Increment 3rd digit and set last digit to 1 of version."

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}.1-SNAPSHOT $USERCRED

echo "commit pom.xml if modified"
mvn scm:checkin -Dmessage="Updated project to next release version" $USERCRED || die 'Failed to commit changes to pom.xml!'

echo "done."

