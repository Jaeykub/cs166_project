--Jacob Jimenez
--ID: 860982401
--email: jjime022@ucr.edu
--Katharina Kaesmacher
--ID: 613758
--email: K.kaesmacher@gmx.de
--Group ID: 23

COPY USR
FROM '/tmp/jjime022/CS166_Project/data/Final_Data1.csv'
WITH DELIMITER ',';

COPY WORK_EXPR
FROM '/tmp/jjime022/CS166_Project/data/Final_Data2.csv'
WITH DELIMITER ',';

COPY EDUCATIONAL_DETAILS
FROM '/tmp/jjime022/CS166_Project/data/Final_Data3.csv'
WITH DELIMITER ',';

COPY MESSAGE
FROM '/tmp/jjime022/CS166_Project/data/Final_Data4.csv'
WITH DELIMITER '/';

COPY CONNECTION_USR
FROM '/tmp/jjime022/CS166_Project/data/Final_Data5.csv'
WITH DELIMITER ',';


