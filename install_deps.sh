#!/usr/bin/env sh

wget --no-check-certificate -O ./dependencies.zip "https://www.dropbox.com/s/tmv73gcbrxkuwdt/dependencies.zip?dl=1"

cygwin=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
esac

zippath=./dependencies.zip
# For Cygwin, ensure paths are in UNIX format before anything is touched.
if $cygwin ; then
    zippath=dependencies.zip
fi

jar xvf $zippath
