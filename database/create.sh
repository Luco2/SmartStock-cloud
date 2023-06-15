#!/bin/bash
#export PGPASSWORD='postgres1'
#BASEDIR=$(dirname $0)
#DATABASE=virtual_stock_db
#psql -U postgres -f "$BASEDIR/dropdb.sql" &&
#createdb -U postgres $DATABASE &&
#psql -U postgres -d $DATABASE -f "$BASEDIR/schema.sql" &&
#psql -U postgres -d $DATABASE -f "$BASEDIR/data.sql" &&
#psql -U postgres -d $DATABASE -f "$BASEDIR/user.sql"

export PGPASSWORD='AwfgJokCvGezxSRm1Aq6'
BASEDIR=$(dirname $0)
DATABASE=railway

psql -h containers-us-west-146.railway.app -U postgres -p 6875 -d railway -f "$BASEDIR/dropdb.sql" &&
psql -h containers-us-west-146.railway.app -U postgres -p 6875 -d railway -f "$BASEDIR/schema.sql" &&
psql -h containers-us-west-146.railway.app -U postgres -p 6875 -d railway -f "$BASEDIR/data.sql" &&
psql -h containers-us-west-146.railway.app -U postgres -p 6875 -d railway -f "$BASEDIR/user.sql"