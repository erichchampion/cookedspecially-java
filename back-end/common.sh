#============================================================================
# - COPYRIGHT NOTICE -
# Copyright (c) CookedSpecially, All Rights Reserved
#
# Author: Abhishek Kumar  abhishek@cookedspecially.com
#============================================================================

DUSERNAME='-Dusername='
DPASSWORD='-Dpassword='
USERCRED=''
RED="\033[0;31m"
GREEN="\033[0;30m"
YELLOW="\033[0;33m"
NC="\e[0m"

function die() {
  echo "FATAL: $1"
  exit 1
}

function usage(){
        echo "Script run in below formate...."
        echo ""
        echo "$0  [USERNAME]"
        exit 1
}

if [ $# -eq 0 ]
  then
    usage
fi

USERNAME=$1
PASSWORD=$2

if [  -z "$PASSWORD"  ]; then
  echo -n  "Please enter password for User : $USERNAME :"
  read -s pswd
  echo 
  if [ ! -z $pswd ]; then
    PASSWORD=$pswd
  else
     echo -e "\033[0;31m  PASSWORD cant not be null or empty! \e[0m"
	 exit 1
  fi
fi

if [  -z "$USERNAME"   ] || [  -z "$PASSWORD"  ]; then
        usage
		exit 1	   
fi


USERCRED=`printf "%s%s %s%s" "$DUSERNAME" "$USERNAME" "$DPASSWORD" "$PASSWORD" `

DPASSWORD+="$PASSWORD"
DUSERNAME+="$USERNAME" 

