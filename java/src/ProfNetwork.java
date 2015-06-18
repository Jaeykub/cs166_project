/*
* Jacob Jimenez
* ID: 860982401
* email: jjime022@ucr.edu
* Katharina Kaesmacher
* ID: 613758
* email: K.kaesmacher@gmx.de
* Group ID: 23
*/

/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               	case 1: CreateUser(esql); break;
               	case 2: authorisedUser = LogIn(esql); break;
               	case 9: keepon = false; break;
               	default : System.out.println("Unrecognized choice!"); break;
	    }//end switch
            if (authorisedUser != null) {
              	boolean usermenu = true;
              	while(usermenu) {
                	System.out.println("\nMAIN MENU");
	                System.out.println("---------");
        	        System.out.println("1. Goto Friend List");
                	System.out.println("2. Update Profile");
	                System.out.println("3. Messages");
			System.out.println("4. Search People");
			System.out.println("5. Requests");
			System.out.println("6. View Profile");
	                System.out.println(".........................");
        	        System.out.println("9. Log out");
                	switch (readChoice()){
				case 1: FriendList(esql, authorisedUser); break;
				case 2: UpdateProfile(esql, authorisedUser); break;
				case 3: Message(esql, authorisedUser); break;
				case 4: SearchPeople(esql,authorisedUser); break;
				case 5: Requests(esql, authorisedUser); break; 
				case 6: ViewProfile(esql, authorisedUser, authorisedUser); 						break;
				case 9: usermenu = false; break;
				default : System.out.println("Unrecognized choice!"); break;
                	}
              	}
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with provided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
	 String email = null;
	 boolean valid = false;
	
	while( !valid )
	{
	         System.out.print("\tEnter user email: ");
	        email = in.readLine();
		for( int i = 0; i < email.length(); ++i )
		{
			if( email.charAt(i) == '@' )
				valid = true;
		}

		if( !valid )
			System.out.println("Not a valid email, try again.");
	}
		

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end


/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 	Start Update Profile
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

	//Changes the users password if the user enters their correct password, and the user enters the new password correctly twice.
	public static void ChangePassword(ProfNetwork esql, String user)
	{
		try{
			System.out.print("\tEnter old password: ");
			String passwordOld = in.readLine();
			System.out.print("\tEnter new password: ");
			String passwordNew1 = in.readLine();
			System.out.print("\tEnter new password again: ");
			String passwordNew2 = in.readLine();
			String query = String.format("UPDATE USR SET Password = '%s' WHERE userId = '%s' AND password = '%s' AND '%s' = '%s'",user, passwordNew1, passwordOld, passwordNew1, passwordNew2 );
			esql.executeUpdate(query);

			if(passwordNew1.equals(passwordNew2)){
				System.out.println ("Password successfully changed!"); }
			else{ 
				System.out.println("Did not change Password: Passwords did not match."); }

		      }catch(Exception e){
         		System.err.println (e.getMessage ()); }
	}

	//Prints out the user friend list and returns the list.
	public static List<List<String>> FriendList(ProfNetwork esql, String user){
		try{
			String query = String.format("select userid, name from usr where userid IN (select userid from connection_usr where connectionid = '%s' AND status = 'Accept' UNION select connectionid from connection_usr where userid = '%s' AND status = 'Accept')", user, user );
	    		List<List<String>> result = esql.executeQueryAndReturnResult(query);
			System.out.println("\nFriend List:");
			makeList(result);
			return result;
		}catch(Exception e){
			System.err.println(e.getMessage()); return null; }
	}

	//Similar to the function above but doesn't print out the user's friend list, just returns the list.
	public static List<List<String>> FriendListRequest(ProfNetwork esql, String user){
		try{
			String query = String.format("select userid as FRIENDS from connection_usr where connectionid = '%s' AND status = 'Accept' UNION select connectionid from connection_usr where userid = '%s' AND status = 'Accept'", user, user );
 	    		List<List<String>> result = esql.executeQueryAndReturnResult(query);
		
			return result;
		}catch(Exception e){
			System.err.println(e.getMessage()); 
			return null;}
	}

	//Determines if the target is 3 level away from the user. If so the function returns true, else returns false.
	public static boolean FriendOf(ProfNetwork esql, String user, String target)
	{
		//getting user's friend list.
		List<List<String>> result = FriendListRequest(esql, user);
		
		Set<String> explored = new HashSet<String>();
		explored.add(user);

		target = target.trim();

		Queue<String> friends = new LinkedList<String>();
		Queue<String> friends2 = new LinkedList<String>();
		//add userid's to the queue
		for( int i = 0; i < result.size(); ++i)
			friends.add(result.get(i).get(0));
		
		String tempfriend = friends.poll(); 
		//expand each friends' friend list and look for target
		while( tempfriend != null )
		{
			if( !explored.contains(tempfriend) ){
				List<List<String>> temp = FriendListRequest(esql, tempfriend);
				explored.add(tempfriend);
			
				//search for target
				for( int j=0; j < temp.size(); ++j)
				{
					String res = temp.get(j).get(0).trim();
					//if target found return true
					if( res.equals(target) )
						return true;
					//if not add the res to the queue
					else if( !explored.contains(res) ) 
						friends2.add(res);
				}
			}
			tempfriend = friends.poll();
		}
	
		tempfriend = friends2.poll();

		while( tempfriend != null )
		{
			List<List<String>> temp = FriendListRequest(esql, tempfriend);
			explored.add(tempfriend);
			
			//search for target
			for( int j=0; j < temp.size(); ++j)
			{
				String res = temp.get(j).get(0).trim();
				//if target found return true
				if( res.equals(target) )
					return true;
			}
			tempfriend = friends2.poll();
		}

		return false;
	}

	//Updates the user's name.
	public static void UpdateName(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE NAME");
			System.out.println("-------");
			System.out.print("\tEnter your name: ");
			String name = in.readLine();
			String query = String.format("UPDATE USR SET name = '%s' where userid = '%s'", name, user);
			esql.executeUpdate(query);
		 }catch(Exception e){ System.err.println(e.getMessage()); }
	}
	
	//Update Profile Menu. Presents options for user to change password, update name, email, date of birth, work experience and educational details.
	public static void UpdateProfile(ProfNetwork esql, String user){
		boolean keepon = true;
		while(keepon) {
            // These are sample SQL statements
			System.out.println("\nUPDATE PROFILE");
			System.out.println("---------");
			System.out.println("1. Change Password");
			System.out.println("2. Update Name");
			System.out.println("3. Update Email");
			System.out.println("4. Update Date of birth");
			System.out.println("5. Update Work Experience");
			System.out.println("6. Update Educational Details");
			System.out.println("9. < EXIT MENU");
			switch(readChoice())
			{
				case 1: ChangePassword(esql, user); break;
				case 2: UpdateName(esql, user); break;
				case 3: UpdateEmail(esql, user); break;
				case 4: UpdateDateOfBirth(esql, user); break;
				case 5: UpdateWorkExperience(esql, user); break;
				case 6: UpdateEducationalDetails(esql, user); break;
				case 9: keepon=false; break;
				default: System.out.println("Invalid Input"); break;
			}
		}
	}
	
	//Updates the user's email.
	public static void UpdateEmail(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE EMAIL");
			System.out.println("-------");
			System.out.print("\tEnter your Email address: ");
			String email = in.readLine();
			String query = String.format("UPDATE USR SET email = '%s' where userid = '%s'", email, user);
			esql.executeUpdate(query);
		 }catch(Exception e){ System.err.println(e.getMessage()); }
	}

	//Updates the user's date of birth.
	public static void UpdateDateOfBirth(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE DATE OF BIRTH");
			System.out.println("-------");
			System.out.print("\tEnter your date of birth (YYYY-MM-DD): ");
			String birthdate = in.readLine();
			String[] parts = birthdate.split("-");
			int year = Integer.parseInt(parts[0]);
			int month = Integer.parseInt(parts[1]);
			int day = Integer.parseInt(parts[2]);

			if((year > 1900) && (year < 2016)  && (month > 0) && (month < 13) && (day > 0) && (day < 32) ){
				String query = String.format("UPDATE USR SET dateofbirth = '%s' where userid = '%s'", birthdate, user);
				esql.executeUpdate(query);
			}
			else{ 
				System.out.println("Date of birth not changed: Format was not (YYYY.MM.DD)");
			}
		 }catch(Exception e){ System.err.println(e.getMessage()); }
	}

/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 	End Update Profile
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */
	
/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		START	Update Work Experience
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

	//Menu for user to update Work experience.
	public static void UpdateWorkExperience(ProfNetwork esql, String user) {

	boolean keepon = true;
		while(keepon) {
			System.out.println("\nUPDATE WORK EXPERIENCE");
			System.out.println("---------");
			System.out.println("1. Create New Work Experience");
			System.out.println("2. Update Location");
			System.out.println("3. Update End Date");
			System.out.println("4. Delete Work Experience");
			System.out.println("9. < EXIT MENU");
			switch(readChoice())
			{
				case 1: CreateWorkExperience(esql, user); break;
				case 2: UpdateLocation(esql, user); break;
				case 3: UpdateEndDate(esql, user); break;
				case 4: DeleteWorkEx(esql, user); break;
				case 9: keepon=false; break;
				default: System.out.println("Invalid Input"); break;
			}
		}
}


	public static void CreateWorkExperience(ProfNetwork esql, String user){
	try{
		System.out.print("\tEnter Company name: ");
        String company = in.readLine();
        System.out.print("\tEnter role: ");
        String role = in.readLine();
        System.out.print("\tEnter Start Date (YYYY.MM.DD): ");
        String startDate = in.readLine();

		String query = String.format("INSERT INTO WORK_EXPR (userId, company, role, startDate) VALUES ('%s','%s','%s','%s')", user, company, role, startDate);
        esql.executeUpdate(query);
        System.out.println ("Work Experience successfully created!");
    }catch(Exception e){
		System.err.println (e.getMessage ());
      }
	}

	public static void UpdateLocation(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE LOCATION");
			System.out.println("-------");
			System.out.print("\tEnter Company name: ");
			String company = in.readLine();
			System.out.print("\tEnter Location: ");
			String location = in.readLine();
			String query = String.format("UPDATE WORK_EXPR SET location = '%s' where userid = '%s' AND company = '%s'", location, user, company);
			esql.executeUpdate(query);
		}catch(Exception e){ System.err.println(e.getMessage()); }
	}

	public static void UpdateEndDate(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE EndDate");
			System.out.println("-------");
			System.out.print("\tEnter Company name: ");
			String company = in.readLine();
			System.out.print("\tEnter EndDate (YYYY.MM.DD): ");
			String endDate = in.readLine();
			String query = String.format("UPDATE WORK_EXPR SET endDate = '%s' where userid = '%s' AND company = '%s'", endDate, user, company);
			esql.executeUpdate(query);
		}catch(Exception e){ System.err.println(e.getMessage()); }
	}

	public static void DeleteWorkEx(ProfNetwork esql, String user){
		try{
			System.out.println("Delete Work Experience");
			System.out.println("-------");
			System.out.print("\tEnter Company name: ");
			String company = in.readLine();
			System.out.print("\tEnter Start Date (YYYY.MM.DD): ");
			String startDate = in.readLine();
			String query = String.format("DELETE FROM WORK_EXPR WHERE userid = '%s' AND company = '%s' AND startDate = '%s'", user, company, startDate);
			esql.executeUpdate(query);
		}catch(Exception e){ System.err.println(e.getMessage()); }
	}

/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		END	Update Work Experience
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		Start	Update Educational Details
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

	public static void UpdateEducationalDetails(ProfNetwork esql, String user) {
		boolean keepon = true;
		while(keepon) {    
			System.out.println("\nUPDATE EDUCATIONAL DETAILS");
			System.out.println("---------");
			System.out.println("1. Create Educational Details");
			System.out.println("2. Update Institution Name");
			System.out.println("3. Update start date");
			System.out.println("4. Update end date");
			System.out.println("5. Delete Educational Detail");
			System.out.println("9. < EXIT MENU");
			switch(readChoice())
			{
				case 1: CreateEducationalDetails(esql, user); break;
				case 2: UpdateInstitution(esql, user); break;
				case 3: UpdateStartYear(esql, user); break;
				case 4: UpdateEndYear(esql, user); break;
				case 5: DeleteEduDetail(esql, user); break;
				case 9: keepon=false; break;
				default: System.out.println("Invalid Input"); break;
			}
		}
	}
	public static void CreateEducationalDetails(ProfNetwork esql, String user){
		try{
			System.out.print("\tEnter your Major: ");
			String major = in.readLine();
			System.out.print("\tEnter degree: ");
			String degree = in.readLine();
			System.out.print("\tEnter the name of institution: ");
			String institution = in.readLine();

			String query = String.format("INSERT INTO educational_details (userId, major, degree, instituitionName) VALUES ('%s','%s','%s', '%s')", user, major, degree, institution);

			esql.executeUpdate(query);
			System.out.println ("Educational Detail successfully created!");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}

	public static void UpdateInstitution(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE INSTITUTION");
			System.out.println("-------");
			System.out.print("\tEnter Major: ");
			String major = in.readLine();
			System.out.print("\tEnter the name of institution: ");
			String institution = in.readLine();
			String query = String.format("UPDATE educational_details SET institutionName = '%s' where userid = '%s' AND major = '%s'", institution, user, major);
			esql.executeUpdate(query);
		 }catch(Exception e){ System.err.println(e.getMessage()); }
	}

	public static void UpdateStartYear(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE START DATE");
			System.out.println("-------");
			System.out.print("\tEnter Major: ");
			String major = in.readLine();
			System.out.print("\tEnter the start date (YYYY.MM.DD): ");
			String startyear = in.readLine();
			String query = String.format("UPDATE educational_details SET startdate = '%s' where userid = '%s' AND major = '%s'", startyear, user, major);
			esql.executeUpdate(query);
		}catch(Exception e){ System.err.println(e.getMessage()); }
	}

	public static void UpdateEndYear(ProfNetwork esql, String user){
		try{
			System.out.println("UPDATE END DATE");
			System.out.println("-------");
			System.out.print("\tEnter Major: ");
			String major = in.readLine();
			System.out.print("\tEnter the end date (YYYY.MM.DD): ");
			String endyear = in.readLine();
			String query = String.format("UPDATE educational_details SET enddate = '%s' where userid = '%s' AND major = '%s'", endyear, user, major);
			esql.executeUpdate(query);
		}catch(Exception e){ System.err.println(e.getMessage()); }
	}

	public static void DeleteEduDetail(ProfNetwork esql, String user){
		try{
			System.out.println("Delete Educational Detail");
			System.out.println("-------");
			System.out.print("\tEnter major: ");
			String major = in.readLine();
			System.out.print("\tEnter degree");
			String degree = in.readLine();
			String query = String.format("DELETE FROM educational_details WHERE userid = '%s' AND major = '%s' AND degree = '%s'", user, major, degree);
			esql.executeUpdate(query);
		}catch(Exception e){ System.err.println(e.getMessage()); }
	}


/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		END	Update Work Experience
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 	Start Message
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

	public static void Message(ProfNetwork esql, String user){
		try{
			boolean keepon = true;
			while(keepon) {
				System.out.println("MESSAGES");
				System.out.println("---------");
				System.out.println("1. Write New Message");
				System.out.println("2. Read New Messages");
				System.out.println("3. Read Received Messages");
				System.out.println("4. Read Sent Messages");
				System.out.println("9. < EXIT MENU");
				switch(readChoice())
				{
					case 1: NewMessage(esql, user,null); break;
					case 2: ReadNewMessage(esql, user); break;
					case 3: ReadReceivedMessage(esql, user); break;
					case 4: ReadSentMessage(esql,user); break;
					case 9: keepon=false; break;
					default: System.out.println("Invalid Input"); break;
				}
			}
		}catch(Exception e){ System.err.println(e.getMessage()); }
	}

	public static void NewMessage(ProfNetwork esql, String user, String receiver){
		try{
			System.out.println("WRITE A NEW MESSAGE");
			String query = String.format("Select max(msgId)+1 from MESSAGE");
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			int msgId = Integer.parseInt(result.get(0).get(0));
			System.out.println("-------");
			if( receiver == null ){
				System.out.print("\tMessage to: ");
				receiver = in.readLine();
			}
			System.out.print("\tEnter your message: ");
			String content = in.readLine();
			query = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverid, contents, sendTime, deleteStatus, status) VALUES ('%s','%s','%s','%s', CURRENT_TIMESTAMP,'0','Sent')", msgId, user, receiver, content);
			esql.executeUpdate(query);
		}catch(Exception e){ 
			System.err.println(e.getMessage()); }
	}

	public static void ReadNewMessage(ProfNetwork esql, String user){
		try{
			System.out.println("READ NEW MESSAGES");
			System.out.println("-------");
			String query = String.format("Select senderId AS FROM, sendTime, contents from MESSAGE Where receiverId = '%s' AND status = 'Sent'", user);
			esql.executeQueryAndPrintResult(query);
			
			query = String.format("UPDATE MESSAGE SET status = 'Read' Where receiverId = '%s' AND status = 'Sent'", user);
			esql.executeUpdate(query);
			System.out.println("Press a button to go back!");
			String goback = in.readLine();
		}catch(Exception e){ 
			System.err.println(e.getMessage()); 
		}
	}

	public static void ReadReceivedMessage(ProfNetwork esql, String user){
		try{
			System.out.println("MESSAGES RECEIVED");
			System.out.println("-------");
			String query = String.format("Select senderId AS FROM, sendTime, contents from MESSAGE Where receiverId = '%s' AND status = 'Read' AND (deletestatus = 1 OR deletestatus = 0) ORDER BY sendtime DESC ", user);

			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			makeList(result);
			int delete = 2;

			if( result.size() > 0 )
				DeleteMessage(esql, user,result, delete);

		}catch(Exception e){ 
			System.err.println(e.getMessage()); }
	}

	public static void ReadSentMessage(ProfNetwork esql, String user){
		try{
			System.out.println("MESSAGES SENT");
			System.out.println("-------");
			String query = String.format("Select receiverId AS TO, sendTime, contents from MESSAGE Where senderId = '%s' AND (status = 'Read' or status = 'Sent') AND (deletestatus = 2 OR deletestatus = 0) ORDER BY sendtime DESC", user);

			int delete = 1;

			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			makeList(result);
			if( result.size() > 0 )	
			DeleteMessage(esql,user,result, delete);
		}catch(Exception e){ 
			System.err.println(e.getMessage()); }
	}

	public static void DeleteMessage(ProfNetwork esql, String user, List<List<String>> result, int delete)
	{
		try{
			System.out.println("Would you like to delete a message? [Y]es or press any key to exit.");
			String choice = in.readLine();
			if( choice.equals("Y") )
			{
				System.out.print("Enter number of message you would like to delete: ");
				choice = in.readLine();
				int n = Integer.parseInt(choice);
				n--;
				System.out.println("Sendtime: " + result.get(n).get(1));
				String query = String.format("UPDATE MESSAGE SET deletestatus = %s Where sendtime = '%s'",delete, result.get(n).get(1) );
				esql.executeUpdate(query);
			}
		}catch(Exception e){ 
			System.err.println(e.getMessage());}
}
/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 	END Message
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */
	
	public static void SearchPeople(ProfNetwork esql, String user){
	   try{
			System.out.print("\tEnter Name: ");
			String name = in.readLine();
			String query = String.format("Select userid, name from USR where name like '%s%%'", name );
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			
			if( result.size() > 0 )
			{
				makeList(result);
				String userid = ChooseProfile(esql, user, result);
				ViewProfile( esql, user, userid );
			}
			else
				System.out.println("No results found!\n");
		}catch(Exception e){
			System.err.println (e.getMessage ()); }	
	}

/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 		Start Requests
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

	//Requests menu.
	public static void Requests(ProfNetwork esql, String user){
		boolean keepon = true;
		while(keepon) 
		{
			System.out.println("\nREQUESTS MENU");
			System.out.println("---------");
			System.out.println("1. Send Request");
			System.out.println("2. Read Request");
			System.out.println("9. < EXIT MENU");
			switch(readChoice())
			{
				case 1: SendRequest(esql, user, null); break;
				case 2: readRequest(esql, user); break;
				case 9: keepon=false; break;
				default: System.out.println("Invalid Input"); break;
			}
		}	
	}

	//allows a user to send a connection request to the target. If the user has less than 6 friends or the target is 3 level away the 
	//request will go thru. If not the request will not go thru.
	public static void SendRequest(ProfNetwork esql, String user, String target){
		try{
			System.out.println("\nSend Requests");
			System.out.println("---------");
			if( target == null ){
				System.out.println("\nRequest To: ");
				target = in.readLine();
			}

			List<List<String>> result = FriendListRequest(esql, user);

			if( result.size() < 6 || FriendOf(esql,user,target) == true )
			{
				String query = String.format("Insert into CONNECTION_USR (userid,connectionid,status) values('%s','%s','Request')", user, target );
				esql.executeUpdate(query);
				System.out.println("Request Sent");
			}
			else
				System.out.println("Not allowed to send request!");
		
		}catch(Exception e){
			System.err.println (e.getMessage ()); }
	}

	//Will display any new requests that the user has. If there are none will state there are none.
	public static void readRequest(ProfNetwork esql, String user){
		try{
			String query = String.format("Select userId from CONNECTION_USR where connectionId = '%s' AND status = 'Request'", user );
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			
			if( result.size() > 0 )
			{
	    			System.out.println("\nYour Requests");
				System.out.println("---------");
				makeList(result);
				System.out.println("\nDo you want to answer a request? [Y]ES or any key to exit");
				String answer = in.readLine();
				if( answer.equals("Y") )
					AnswerRequest(esql, user);
			}
			else
				System.out.println("\nNo New Requests.");
		}catch(Exception e){
			System.err.println (e.getMessage ()); }
	}

	public static void AnswerRequest(ProfNetwork esql, String user){
		try{
			System.out.println("\nAnswer Request of: ");
			String name = in.readLine();
			System.out.println("\nPress A for Accept \nand R for Reject");
			String answer = in.readLine();
			if((answer.equals("A") || (answer.equals("R"))))
			{
				if(answer.equals("A")){
					String query = String.format("UPDATE CONNECTION_USR SET status = 'Accept' where	userid = '%s' AND connectionId = '%s'", name, user);
					esql.executeUpdate(query);
					System.out.println("Freindship accepted!");
				}		
				else if(answer.equals("R")){
					String query = String.format("UPDATE CONNECTION_USR SET status = 'Reject' where userid = '%s' AND connectionId = '%s'",	name, user);
					esql.executeUpdate(query);
					System.out.println("Freindship rejected!");
				}
			}
			else{
				System.out.println("Your answer is invalid. Please try it again!");
				AnswerRequest(esql, user);
			}	
		}catch(Exception e){
			System.err.println (e.getMessage ()); }
	}

	//returns the chosen profile's user id.
	public static String ChooseProfile(ProfNetwork esql, String user, List<List<String>> result)
	{
		try{
			System.out.println("Choose a profile: ");	
			String name = in.readLine();
			int choice = Integer.parseInt(name);
			--choice;
			if( choice >= 0 && choice < result.size() )
				return result.get(choice).get(0);

			else
			{
				System.out.println("Invalid Input! Returning first profile.\n");
				return result.get(0).get(0);
			}
		}catch(Exception e){
			System.err.println (e.getMessage ()); return null;}
	}

	//takes in result List and prints it out in a nice format.
	public static void makeList(List<List<String>> result){
		for( int i = 0; i < result.size(); ++i )
		{
			System.out.print(i+1 + ": ");
			for( int j=0; j < result.get(i).size(); ++j){  
				if( j != 0 && result.get(i).get(j) != null){ System.out.print(", "); }
				if( result.get(i).get(j) != null )
					System.out.print(result.get(i).get(j).trim());
			}
			System.out.println();
		}
		//return result;
	}
/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 		End Requests
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 		Start View Profile 
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */

	//displays the profile. If the user is viewing someone else's profile it will ask if they'd like to view another profile, submit a connection, or send a message.
	public static void ViewProfile(ProfNetwork esql, String user, String name){
		try{
			System.out.println("--------\nPROFILE");
			System.out.println("Username: " + name );
			System.out.print("Name: ");
			String query = String.format("Select name from usr where userid='%s'", name);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			if( result.get(0).get(0) != null )
				System.out.print( result.get(0).get(0).trim() + "\n" );
			else
				System.out.print("\n");
			System.out.println(" ");
			System.out.println("Education: ");
			query = String.format("Select instituitionName, major,degree,startdate,enddate from EDUCATIONAL_DETAILS WHERE userid = '%s'", name);
			result = esql.executeQueryAndReturnResult(query);
			makeList(result);
			
			System.out.println("\nWork Experience: ");
			query = String.format("Select company,role,location,startdate,enddate from WORK_EXPR WHERE userid = '%s'", name);
			result = esql.executeQueryAndReturnResult(query);
			makeList(result);

			result = FriendList(esql, name);

			if( user != name )
			{
				if( result.size() > 0 )
				{
					System.out.println("Would you like to go to [P]rofile, [M]essage, or [C]onnection or press any other key to leave.");
					String read = in.readLine();
					switch( read )
					{
						case "P": name = ChooseProfile(esql,user,result); 
							ViewProfile(esql, user,name);	break;
						case "M": NewMessage(esql,user, name);break;
						case "C": SendRequest(esql,user, name); break;
						default: System.out.println( "bye" ); break;
					}
				}
				else
				{
					System.out.println("Would you like to [M]essage, send [C]onnection or press any other key to leave.");
					String read = in.readLine();
					switch( read )
					{
						case "M": NewMessage(esql,user, name);break;
						case "C": SendRequest(esql,user, name); break;
						default: System.out.println( "bye" ); break;
					}
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage()); }
	}

/* aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
		 		End View Profile 
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa */
}//end ProfNetwork
