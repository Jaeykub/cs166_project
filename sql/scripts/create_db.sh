#Jacob Jimenez
#ID: 860982401
#email: jjime022@ucr.edu
#Katharina Kaesmacher
#ID: 613758
#email: K.kaesmacher@gmx.de
#Group ID: 23

#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -p $PGPORT $DB_NAME < $DIR/../src/create_tables.sql
psql -p $PGPORT $DB_NAME < $DIR/../src/create_index.sql
psql -p $PGPORT $DB_NAME < $DIR/../src/load_data.sql
