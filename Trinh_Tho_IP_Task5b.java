import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DatabaseIP {

    // Database connection string
    final static String URL = "jdbc:sqlserver://trin0003-sql-server.database.windows.net:1433;database=cs-dsa-4513-sql-db;user=trin0003@trin0003-sql-server;password=****;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    // Query Templates Begin
    final static String QUERY_TEMPLATE_1 = "INSERT INTO Customer " +
            "VALUES (?, ?, ?);";

    final static String QUERY_TEMPLATE_2 = "INSERT INTO Department " +
            "VALUES (?, ?);";

    final static String QUERY_TEMPLATE_3A = "INSERT INTO Process " +
            "VALUES (?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_3FIT = "INSERT INTO Fit_Process " +
            "VALUES (?, ?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_3PAINT = "INSERT INTO Paint_Process " +
            "VALUES (?, ?, ?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_3CUT = "INSERT INTO Cut_Process " +
            "VALUES (?, ?, ?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_4 = "INSERT INTO Assembly1 " +
            "VALUES (?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_4B = "UPDATE Process\n" + "SET assembly_id = ?\n" + "WHERE process_id = ?;";
    final static String QUERY_TEMPLATE_4BFIT = "UPDATE Fit_Process\n" + "SET assembly_id = ?\n" + "WHERE process_id = ?;";
    final static String QUERY_TEMPLATE_4BPAINT = "UPDATE Paint_Process\n" + "SET assembly_id = ?\n" + "WHERE process_id = ?;";
    final static String QUERY_TEMPLATE_4BCUT = "UPDATE Cut_Process\n" + "SET assembly_id = ?\n" + "WHERE process_id = ?;";

    final static String QUERY_TEMPLATE_5A = "INSERT INTO Account " +
            "VALUES (?, ?);";

    final static String QUERY_TEMPLATE_5B = "INSERT INTO Assembly_Account " +
            "VALUES (?, ?, ?, ?);";
    final static String QUERY_TEMPLATE_5C = "INSERT INTO Process_Account " +
            "VALUES (?, ?, ?, ?);";
    final static String QUERY_TEMPLATE_5D = "INSERT INTO Department_Account " +
            "VALUES (?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_6 = "INSERT INTO Job " +
            "VALUES (?, ?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_7 = "UPDATE Job\n" + "SET date_completed = ?\n" + "WHERE job_number = ?;";
    final static String QUERY_TEMPLATE_7FIT = "INSERT INTO Fit_Job " +
            "VALUES (?, ?);";
    final static String QUERY_TEMPLATE_7PAINT = "INSERT INTO Paint_Job " +
            "VALUES (?, ?, ?, ?);";
    final static String QUERY_TEMPLATE_7CUT = "INSERT INTO Cut_Job " +
            "VALUES (?, ?, ?, ?, ?);";

    final static String QUERY_TEMPLATE_8 = "INSERT INTO Transaction1 " +
            "VALUES (?, ?, ?, ?);";
    final static String QUERY_TEMPLATE_8ASSEMBLY = "UPDATE Assembly_Account\n" + "SET cost1 = cost1 + ?\n" + "WHERE account_number = ?;";
    final static String QUERY_TEMPLATE_8PROCESS = "UPDATE Process_Account\n" + "SET cost2 = cost2 + ?\n" + "WHERE account_number = ?;";
    final static String QUERY_TEMPLATE_8DEPARTMENT = "UPDATE Department_Account\n" + "SET cost3 = cost3 + ?\n" + "WHERE account_number = ?;";

    final static String QUERY_TEMPLATE_9 = "SELECT cost1 FROM Assembly_Account WHERE assembly_id = ?;";

    final static String QUERY_TEMPLATE_10FIT = "SELECT  SUM(( DATEPART(hh, labor_time) * 3600 ) + ( DATEPART(mi, labor_time) * 60 ) + DATEPART(ss, labor_time))/60 as minute\n"
            + "FROM Fit_Job WHERE Fit_Job.job_number in (\n"
            + "SELECT distinct(job_number) FROM Job\n"
            + "WHERE Job.process_id in (SELECT distinct(process_id) FROM Process WHERE Process.department_number = ?) AND Job.date_completed = ? );";

    final static String QUERY_TEMPLATE_10PAINT = "SELECT  SUM(( DATEPART(hh, labor_time) * 3600 ) + ( DATEPART(mi, labor_time) * 60 ) + DATEPART(ss, labor_time))/60 as minute\n"
            + "FROM Paint_Job WHERE Paint_Job.job_number in (\n"
            + "SELECT distinct(job_number) FROM Job\n"
            + "WHERE Job.process_id in (SELECT distinct(process_id) FROM Process WHERE Process.department_number = ?) AND Job.date_completed = ? );";

    final static String QUERY_TEMPLATE_10CUT = "SELECT  SUM(( DATEPART(hh, labor_time) * 3600 ) + ( DATEPART(mi, labor_time) * 60 ) + DATEPART(ss, labor_time))/60 as minute\n"
            + "FROM Cut_Job WHERE Cut_Job.job_number in (\n"
            + "SELECT distinct(job_number) FROM Job\n"
            + "WHERE Job.process_id in (SELECT distinct(process_id) FROM Process WHERE Process.department_number = ?) AND Job.date_completed = ? );";

    final static String QUERY_TEMPLATE_11 = "SELECT Job.process_id, Process.department_number, Job.date_commenced\n"
            + "    FROM Job, Process\n"
            + "    WHERE Job.assembly_id = ? AND Process.process_id = Job.process_id \n"
            + "    ORDER BY 1 ;";

    final static String QUERY_TEMPLATE_12A = "SELECT DISTINCT(Job.job_number), Job.assembly_id, Fit_Job.labor_time\n"
            + "    FROM Job, Fit_Job\n"
            + "    WHERE date_completed = ? and Job.process_id in (SELECT process.process_id FROM Process WHERE department_number = ?) AND Fit_Job.job_number = Job.job_number;";

    final static String QUERY_TEMPLATE_12B = "SELECT DISTINCT(Job.job_number), Job.assembly_id, Paint_Job.color, Paint_Job.volume, Paint_Job.labor_time\n"
            + "    FROM Job, Paint_Job\n"
            + "    WHERE date_completed = ? and Job.process_id in (SELECT process.process_id FROM Process WHERE department_number = ?) AND Paint_Job.job_number = Job.job_number;";

    final static String QUERY_TEMPLATE_12C = "SELECT DISTINCT(Job.job_number), Job.assembly_id, Cut_Job.type_machine_used, Cut_Job.amount_of_time_machine_used, Cut_Job.material_used, Cut_Job.labor_time\n"
            + "    FROM Job, Cut_Job\n"
            + "    WHERE date_completed = ? and Job.process_id in (SELECT process.process_id FROM Process WHERE department_number = ?) AND Cut_Job.job_number = Job.job_number;";

    final static String QUERY_TEMPLATE_13 = "SELECT name, category AS name FROM Customer\n"
            + "    WHERE category >= ? AND category <= ?\n"
            + "    ORDER BY 1 ;";

    final static String QUERY_TEMPLATE_14 = "DELETE FROM Cut_Job WHERE job_number >= ? AND job_number <= ?";

    final static String QUERY_TEMPLATE_15 = "UPDATE Paint_Job SET color = ? WHERE job_number= ?;";

    // User input prompt //
    final static String PROMPT =
            "\nWELCOME TO THE JOB-SHOP ACCOUNTING DATABASE SYSTEM \n" +
            "(1) Enter a new customer \n" +
            "(2) Enter a new department \n" +
            "(3) Enter a new process and department together \n" +
            "(4) Enter a new assembly and associate it with one or more processes \n" +
            "(5) Create a new account and associate it with a process, assembly, or department \n" +
            "(6) Enter a new job and date the job began\n" +
            "(7) At the completion of a job, enter the date completed and relevant info \n" +
            "(8) Enter a transaction and update all the costs of affected accounts with the transaction cost \n" +
            "(9) Retrieve the total cost incurred on a specific assembly \n" +
            "(10) Retrieve the total labor time within a department for jobs completed in the department during a specific date \n" +
            "(11) Retrieve the processes which a specific assembly has passed so far and the department responsible for each process \n" +
            "(12) Retrieve the jobs(with its associated assembly and type) completed during a specific date in a specific department \n" +
            "(13) Retrieve the customers(in name order) whose category is in a specific range \n" +
            "(14) Delete all cut-jobs whose job-no is in a specific range \n" +
            "(15) Change the color of a specific paint job \n" +
            "(16) Import: Enter new customers from a data file until the file is empty (the user must be asked to enter the input file name) \n" +
            "(17) Export: Retrieve the customers(in name order) whose category is in a given range and output them to a data file instead of screen \n" +
            "(18) Quit \n";


    public static void main(String[] args) throws SQLException {
        // Read in user input
        final Scanner sc = new Scanner(System.in); // Scanner is used to collect the user input
        String option = ""; // Initialize user option selection as nothing
        while (!option.equals("18")) { // Ask user for options until option 18 is selected to quit
            System.out.println(PROMPT); // Print the available options
            option = sc.next(); // Read in the user option selection

            switch (option) { // Switch between different options
                case "1": // Enter a new Customer
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Collect data to input for the Customer
                        System.out.println("Please enter name of the new customer:");
                        sc.nextLine();
                        final String name = sc.nextLine();

                        System.out.println("Please enter address of the new customer:");

                        final String address = sc.nextLine();

                        System.out.println("Please enter Customer category (a number from 1-10): ");
                        final int category = sc.nextInt();

                        sc.nextLine();

                        System.out.println("Connecting to the database...");
                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_1)){
                                // Make a new customer and populate the database with it.
                                statement.setString(1, name);
                                statement.setString(2, address);
                                statement.setInt(3, category);

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }

                    break;
                case "2":
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for Department information
                        System.out.println("Please enter new department number:");
                        final int department_number = sc.nextInt();

                        System.out.println("Please enter department data:");
                        sc.nextLine();

                        final String department_data = sc.nextLine();



                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add department first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_2)) {

                                statement.setInt(1, department_number);
                                statement.setString(2, department_data);

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }


                    break;

                case "3": // Enter a Process
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        System.out.println("Please enter a new process ID: ");
                        final int process_id = sc.nextInt();

                        System.out.println("Please enter the corresponding process_data: ");
                        sc.nextLine();
                        final String process_data = sc.nextLine();


                        System.out.println("Please enter new department number associated with process:");
                        final int department_number3 = sc.nextInt();

                        System.out.println("Please enter type of process (Fit, Paint, Cut)? ");
                        sc.nextLine();
                        final String type = sc.nextLine();

                        String fit_type = "";
                        String paint_type = "";
                        String paint_method = "";
                        String cutting_type = "";
                        String machine_type = "";

                        if (type.equals("Fit")) {

                            System.out.println("Please enter the corresponding fit_type: ");
                            fit_type = sc.nextLine();

                        } else if (type.equals("Paint")) {

                            System.out.println("Please enter the corresponding paint_type: ");
                            paint_type = sc.nextLine();
                            System.out.println("Please enter the corresponding paint_method: ");
                            paint_method = sc.nextLine();

                        } else if (type.equals("Cut")) {

                            System.out.println("Please enter the corresponding cutting type: ");
                            cutting_type = sc.nextLine();
                            System.out.println("Please enter the corresponding machine type: ");
                            machine_type = sc.nextLine();

                        }

                        System.out.println("Connecting to the database...");

                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add department first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_3A)) {

                                statement.setInt(1, process_id);
                                statement.setString(2, process_data);
                                statement.setNull(3,java.sql.Types.INTEGER);
                                statement.setInt(4, department_number3);



                                final int rows_inserted2 = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted2));

                                if (type.equals("Fit")) {

                                    //Add fit Process next
                                    final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_3FIT);
                                    statement3.setInt(1, process_id);
                                    statement3.setString(2, process_data);
                                    statement3.setString(3,fit_type);
                                    statement3.setNull(4,java.sql.Types.INTEGER);
                                    statement3.setInt(5, department_number3);

                                    final int rows_inserted3 = statement3.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted.", rows_inserted3));

                                } else if (type.equals("Paint")) {

                                    //Add paint Process next
                                    final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_3PAINT);
                                    statement3.setInt(1, process_id);
                                    statement3.setString(2, process_data);
                                    statement3.setString(3,paint_type);
                                    statement3.setString(4, paint_method);
                                    statement3.setNull(5,java.sql.Types.INTEGER);
                                    statement3.setInt(6, department_number3);

                                    final int rows_inserted3 = statement3.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted.", rows_inserted3));

                                } else if (type.equals("Cut")) {

                                    //Add cut Process next
                                    final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_3CUT);
                                    statement3.setInt(1, process_id);
                                    statement3.setString(2, process_data);
                                    statement3.setString(3,cutting_type);
                                    statement3.setString(4, machine_type);
                                    statement3.setNull(5,java.sql.Types.INTEGER);
                                    statement3.setInt(6, department_number3);

                                    final int rows_inserted3 = statement3.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted.", rows_inserted3));

                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }

                    break;
                case "4": // Enter a new assembly and associate it with one or more processes
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for assembly information
                        System.out.println("Please enter new assembly id");
                        final int assembly_id = sc.nextInt();

                        System.out.println("Please enter date ordered of assembly in yyyy-mm-dd format:");
                        sc.nextLine();

                        final String date_ordered= sc.nextLine();

                        System.out.println("Please enter assembly_details: ");
                        final String assembly_details = sc.nextLine();

                        System.out.println("Please enter the customer name who ordered the asesembly: ");
                        final String customer_name = sc.nextLine();

                        System.out.println("How many processes is this assembly associated with: ");
                        final int num_of_processes = sc.nextInt();

                        int process_id_array[] = new int[num_of_processes];

                        System.out.println("List all the process ID's associated. Please click enter after each ID: ");

                        for(int i = 0; i < num_of_processes; ++i)
                        {
                            process_id_array[i] = sc.nextInt();
                        }

                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add assembly first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_4)) {

                                statement.setInt(1, assembly_id);
                                statement.setDate(2, java.sql.Date.valueOf(date_ordered));
                                statement.setString(3, assembly_details);
                                statement.setString(4, customer_name);

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Inserted Assembly.", rows_inserted));

                                for(int i = 0; i < num_of_processes; ++i)
                                {
                                    // Now associate the processes with the new assembly id
                                    final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_4B);
                                    statement2.setInt(1, assembly_id);
                                    statement2.setInt(2, process_id_array[i]);
                                    final int rows_inserted2 = statement2.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted. Process associated.", rows_inserted2));

                                    // Associate every Fit Process
                                    final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_4BFIT);
                                    statement3.setInt(1, assembly_id);
                                    statement3.setInt(2, process_id_array[i]);
                                    final int rows_inserted3 = statement3.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted. Fit Process associated.", rows_inserted3));

                                    // Associate every Cut Process
                                    final PreparedStatement statement4 = connection.prepareStatement(QUERY_TEMPLATE_4BCUT);
                                    statement4.setInt(1, assembly_id);
                                    statement4.setInt(2, process_id_array[i]);
                                    final int rows_inserted4 = statement4.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted. Cut Process associated.", rows_inserted4));

                                    // Associated every Paint Process
                                    final PreparedStatement statement5 = connection.prepareStatement(QUERY_TEMPLATE_4BPAINT);
                                    statement5.setInt(1, assembly_id);
                                    statement5.setInt(2, process_id_array[i]);
                                    final int rows_inserted5 = statement5.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted. Paint Process associated.", rows_inserted5));

                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }

                    break;

                case "5": //Enter a new account and associate it with the process, assembly, or department
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for Account information
                        System.out.println("Please enter new account number:");
                        final int account_number = sc.nextInt();

                        System.out.println("Please enter date account established in yyyy-mm-dd format:");
                        sc.nextLine();

                        final String date_account_established = sc.nextLine();

                        System.out.println("Please enter associated process_id for the account:");
                        final int account_process_id = sc.nextInt();

                        System.out.println("Please enter associated assembly_id for the account:");
                        final int account_assembly_id = sc.nextInt();

                        System.out.println("Please enter associated department_number for the account:");
                        final int account_department_number = sc.nextInt();

                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add department first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_5A)) {

                                statement.setInt(1, account_number);
                                statement.setDate(2, java.sql.Date.valueOf(date_account_established));

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Account created.", rows_inserted));

                                // Insert Assembly Account
                                final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_5B);
                                statement2.setInt(1, account_number);
                                statement2.setDate(2, java.sql.Date.valueOf(date_account_established));
                                statement2.setInt(3, 0);
                                statement2.setInt(4, account_assembly_id);
                                final int rows_inserted2 = statement2.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Assembly Account created.", rows_inserted2));

                                // Insert Process Account
                                final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_5C);
                                statement3.setInt(1, account_number);
                                statement3.setDate(2, java.sql.Date.valueOf(date_account_established));
                                statement3.setInt(3, 0);
                                statement3.setInt(4, account_process_id);
                                final int rows_inserted3 = statement3.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Process Account created.", rows_inserted3));

                                // Insert Department Account
                                final PreparedStatement statement4 = connection.prepareStatement(QUERY_TEMPLATE_5D);
                                statement4.setInt(1, account_number);
                                statement4.setDate(2, java.sql.Date.valueOf(date_account_established));
                                statement4.setInt(3, 0);
                                statement4.setInt(4, account_department_number);
                                final int rows_inserted4 = statement4.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Department Account created.", rows_inserted4));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }

                    break;

                case "6":
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for Job information
                        System.out.println("Please enter new job number:");
                        final int job_number = sc.nextInt();

                        System.out.println("Please enter date commenced for the job in yyyy-mm-dd format:");
                        sc.nextLine();

                        final String date_commenced = sc.nextLine();

                        System.out.println("Please enter the associated assembly id for the job: ");
                        final int job_assembly_id = sc.nextInt();

                        System.out.println("Please enter the associated process id for the job: ");
                        final int job_process_id = sc.nextInt();

                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add department first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_6)) {

                                statement.setInt(1, job_number);
                                statement.setDate(2, java.sql.Date.valueOf(date_commenced));
                                statement.setNull(3, java.sql.Types.DATE);
                                statement.setInt(4, job_process_id);
                                statement.setInt(5, job_assembly_id);

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }
                    break;

                case "7": // Enter date completed and information related to type of job

                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        System.out.println("Please enter the job number completed :");
                        final int job_number_completed = sc.nextInt();

                        System.out.println("Please enter date completed for job in yyyy-mm-dd format:");
                        sc.nextLine();
                        final String date_completed = sc.nextLine();

                        System.out.println("Please enter type of job (Fit, Paint, Cut)? ");
                        final String job_type = sc.nextLine();

                        // Have to intialize all variables
                        String color, type_machine_used, material_used, labor_time, amount_of_time_machine_used;
                        color = "";
                        type_machine_used = "";
                        material_used = "";
                        labor_time = "";
                        amount_of_time_machine_used = "";

                        int volume = 0;

                        if(job_type.equals("Fit")) {

                            System.out.println("Please enter labor time in hh:mm:ss format");
                            labor_time = sc.nextLine();

                        } else if (job_type.equals("Paint")) {

                            System.out.println("Please enter labor time in hh:mm:ss format");
                            labor_time = sc.nextLine();

                            System.out.println("Please enter color for Paint job: ");
                            color = sc.nextLine();

                            System.out.println("Please enter the volume of paint: ");
                            volume = sc.nextInt();

                        } else if (job_type.equals("Cut")) {

                            System.out.println("Please enter labor time in hh:mm:ss format");
                            labor_time = sc.nextLine();

                            System.out.println("Please enter the type of machine used: ");
                            type_machine_used = sc.nextLine();

                            System.out.println("Please enter amount of time machine used in hh:mm:ss format");
                            amount_of_time_machine_used = sc.nextLine();

                            System.out.println("Please enter material used");
                            material_used = sc.nextLine();
                        }

                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add department first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_7)) {

                                statement.setDate(1, java.sql.Date.valueOf(date_completed));
                                statement.setInt(2, job_number_completed);

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));

                                if(job_type.equals("Fit")) {

                                    final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_7FIT);
                                    statement2.setInt(1, job_number_completed);
                                    statement2.setTime(2, java.sql.Time.valueOf(labor_time));

                                    final int rows_inserted2= statement2.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted. Fit Job Added.", rows_inserted2));

                                } else if (job_type.equals("Paint")) {

                                    final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_7PAINT);
                                    statement2.setInt(1, job_number_completed);
                                    statement2.setTime(2, java.sql.Time.valueOf(labor_time));
                                    statement2.setString(3, color);
                                    statement2.setInt(4, volume);

                                    final int rows_inserted2= statement2.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted. Paint Job Added.", rows_inserted2));

                                } else if (job_type.equals("Cut")) {

                                    final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_7CUT);
                                    statement2.setInt(1, job_number_completed);
                                    statement2.setTime(2, java.sql.Time.valueOf(labor_time));
                                    statement2.setString(3, type_machine_used);
                                    statement2.setTime(4, java.sql.Time.valueOf(amount_of_time_machine_used));
                                    statement2.setString(5, material_used);

                                    final int rows_inserted2= statement2.executeUpdate();
                                    System.out.println(String.format("Done. %d rows inserted. Cut Job Added.", rows_inserted2));

                                }
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }
                    break;
                case "8": // Enter transaction and update costs of affected accounts
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for transaction info
                        System.out.println("Please enter new transaction number");
                        final int transaction_number = sc.nextInt();

                        System.out.println("Please enter cost:");
                        final int transaction_cost = sc.nextInt();

                        System.out.println("Enter the associated account number: ");
                        final int transaction_account_number = sc.nextInt();

                        System.out.println("Please enter the associated job number: ");
                        final int transaction_job_number = sc.nextInt();


                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add department first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_8)) {

                                statement.setInt(1, transaction_number);
                                statement.setInt(2, transaction_cost);
                                statement.setInt(3, transaction_job_number);
                                statement.setInt(4, transaction_account_number);

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));

                                final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_8ASSEMBLY);
                                statement2.setInt(1, transaction_cost);
                                statement2.setInt(2, transaction_account_number);

                                final int rows_inserted2 = statement2.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Assembly Account updated", rows_inserted2));

                                final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_8PROCESS);
                                statement3.setInt(1, transaction_cost);
                                statement3.setInt(2, transaction_account_number);

                                final int rows_inserted3 = statement3.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Process Account updated", rows_inserted3));

                                final PreparedStatement statement4 = connection.prepareStatement(QUERY_TEMPLATE_8DEPARTMENT);
                                statement4.setInt(1, transaction_cost);
                                statement4.setInt(2, transaction_account_number);

                                final int rows_inserted4 = statement4.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted. Department Account updated", rows_inserted4));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }

                    break;

                case "9":
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for assembly id
                        System.out.println("Please enter assembly ID you want to find cost of:");
                        final int cost_assembly_id = sc.nextInt();


                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            // Add department first
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_9)) {

                                statement.setInt(1, cost_assembly_id);
                                final ResultSet resultSet = statement.executeQuery();

                                resultSet.next();
                                System.out.println(String.format("The cost associated with the assembly ID is %s", resultSet.getInt(1)));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }
                    break;
                case "10": // Retrieve total labor time
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for department and completion date
                        System.out.println("Please enter the department number:");
                        final int time_department_number = sc.nextInt();

                        System.out.println("Please enter the completion date in yyyy-mm-dd format:");
                        sc.nextLine();
                        final String time_completion_date = sc.nextLine();

                        System.out.println("Connecting to the database...");

                        int fit_time = 0;
                        int paint_time = 0;
                        int cut_time = 0;
                        int total_time_in_minutes = 0;

                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_10FIT)){  // get total fit labor time

                                statement.setInt(1, time_department_number);
                                statement.setDate(2, java.sql.Date.valueOf(time_completion_date));

                                final ResultSet resultSet = statement.executeQuery();

                                resultSet.next();
                                fit_time += resultSet.getInt(1);

                                // get total paint labor time
                                final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_10PAINT);
                                statement2.setInt(1, time_department_number);
                                statement2.setDate(2, java.sql.Date.valueOf(time_completion_date));

                                final ResultSet resultSet2 = statement2.executeQuery();

                                resultSet2.next();
                                paint_time += resultSet2.getInt(1);

                                // get total cut labor time
                                final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_10CUT);
                                statement3.setInt(1, time_department_number);
                                statement3.setDate(2, java.sql.Date.valueOf(time_completion_date));

                                final ResultSet resultSet3 = statement3.executeQuery();

                                resultSet3.next();
                                cut_time += resultSet3.getInt(1);

                                total_time_in_minutes = fit_time + paint_time + cut_time;
                                System.out.println(String.format("The total labor time for jobs completed in the department during a given date is %s"
                                                        , total_time_in_minutes));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }
                    break;

                case "11":
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Read in the user input for assembly id
                        System.out.println("Please enter the assembly id:");
                        final int process_assembly_id = sc.nextInt();

                        System.out.println("Connecting to the database...");
                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_11)){  // get processes

                                statement.setInt(1, process_assembly_id);
                                final ResultSet resultSet = statement.executeQuery();

                                System.out.println("Processes through which a given assembly ID has passed so far in date commenced order: ");
                                System.out.println(" Process ID | Department Number | Date Commenced ");

                                while (resultSet.next()) {
                                    System.out.println(String.format("%s | %s | %s",
                                            resultSet.getInt(1),
                                            resultSet.getInt(2),
                                            resultSet.getDate(3)));
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }
                    break;

                case "12": // Retrieve jobs completed during given date and given department
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                           // Read in the user input for job information
                           System.out.println("Please enter the completion date in yyyy-mm-dd format: ");
                           sc.nextLine();
                           final String job_completion_date = sc.nextLine();

                           System.out.println("Please enter the department number: ");
                           final int job_department_number = sc.nextInt();

                           System.out.println("Connecting to the database...");
                           // Get a database connection and prepare a query statement
                           try (final Connection connection = DriverManager.getConnection(URL)) {
                               try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_12A)){  // search fit jobs

                                   statement.setDate(1, java.sql.Date.valueOf(job_completion_date));
                                   statement.setInt(2, job_department_number);
                                   final ResultSet resultSet = statement.executeQuery();

                                   // Print out Fit Jobs
                                   System.out.println("Fit Jobs completed during a given date in a given department: ");
                                   System.out.println("Job Number | Assembly ID | Labor Time (hh:mm:ss)");
                                   while (resultSet.next()) {
                                       System.out.println(String.format("%s | %s | %s",
                                               resultSet.getInt(1),
                                               resultSet.getInt(2),
                                               resultSet.getTime(3)));
                                   }
                                   System.out.println("");

                                   // Print out Paint Jobs
                                   final PreparedStatement statement2 = connection.prepareStatement(QUERY_TEMPLATE_12B);
                                   statement2.setDate(1, java.sql.Date.valueOf(job_completion_date));
                                   statement2.setInt(2, job_department_number);
                                   final ResultSet resultSet2 = statement2.executeQuery();

                                   System.out.println("Paint Jobs completed during a given date in a given department: ");
                                   System.out.println("Job Number | Assembly ID | color | volume | Labor Time (hh:mm:ss)");
                                   while (resultSet2.next()) {
                                       System.out.println(String.format("%s | %s | %s | %s | %s",
                                               resultSet2.getInt(1),
                                               resultSet2.getInt(2),
                                               resultSet2.getString(3),
                                               resultSet2.getInt(4),
                                               resultSet2.getTime(5)));
                                   }
                                   System.out.println("");

                                   // Print out Cut Jobs
                                   final PreparedStatement statement3 = connection.prepareStatement(QUERY_TEMPLATE_12C);
                                   statement3.setDate(1, java.sql.Date.valueOf(job_completion_date));
                                   statement3.setInt(2, job_department_number);
                                   final ResultSet resultSet3 = statement3.executeQuery();

                                   System.out.println("Cut Jobs completed during a given date in a given department: ");
                                   System.out.println("Job Number | Assembly ID | Type Machine Used | Amount of Time Machine Used (hh:mm:ss) | Material Used | Labor Time (hh:mm:ss)");
                                           while (resultSet3.next()) {
                                               System.out.println(String.format("%s | %s | %s | %s | %s | %s",
                                                       resultSet3.getInt(1),
                                                       resultSet3.getInt(2),
                                                       resultSet3.getString(3),
                                                       resultSet3.getTime(4),
                                                       resultSet3.getString(5),
                                                       resultSet3.getTime(6)));
                                           }

                               }
                           }
                        } catch (Exception e) {
                            System.out.println("You got an error! Returning to the main menu.");
                        }
                        break;
                case "13": // Retrieve customers in name order whose category is in a given range
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Collect data to input for the Customer retrieval
                        System.out.println("Please enter desired lower bound of category range: ");
                        final int lower_bound = sc.nextInt();

                        System.out.println("Please enter desired upper bound of category range:");
                        final int upper_bound = sc.nextInt();

                        System.out.println("Connecting to the database...");
                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_13)){
                                // Make a new customer and populate the database with it.
                                statement.setInt(1,lower_bound);
                                statement.setInt(2, upper_bound);

                                final ResultSet resultSet = statement.executeQuery();

                                // Print out Customers
                                System.out.println("Customers (in name order)");
                                while (resultSet.next()) {
                                    System.out.println(String.format("%s",
                                            resultSet.getString(1)));
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }

                    break;
                case "14": // Delete all cut jobs in a given range
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Collect data to input for the job deletion
                        System.out.println("Please enter desired lower bound of job number range: ");
                        final int job_lower_bound = sc.nextInt();

                        System.out.println("Please enter desired upper bound of job number range:");
                        final int job_upper_bound = sc.nextInt();

                        System.out.println("Connecting to the database...");
                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_14)){
                                // Make a new customer and populate the database with it.
                                statement.setInt(1,job_lower_bound);
                                statement.setInt(2, job_upper_bound);

                                statement.executeUpdate();

                                // Print out Customers
                                System.out.println(String.format("Job numbers from %s to %s are deletd.",
                                        job_lower_bound, job_upper_bound));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }
                    break;
                case "15": // Change color of a given paint job
                    try { // In case of an error, this returns it to the main menu instead of terminating program
                        // Collect data to input for the color change
                        System.out.println("What is the paint job number you wish to update: ");
                        final int color_job_number = sc.nextInt();

                        System.out.println("Please enter the color you wish to update to:");
                        sc.nextLine();
                        final String new_color = sc.nextLine();

                        System.out.println("Connecting to the database...");
                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_15)){
                                // Make a new customer and populate the database with it.
                                statement.setString(1, new_color);
                                statement.setInt(2, color_job_number);

                                int rows_updated = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows updated.", rows_updated));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("You got an error! Returning to the main menu.");
                    }
                    break;

                case "16":
                    // try and catch are used to not terminate loop in case of error.
                    try {
                    String line;

                    // Retrieve input file name from user
                    System.out.println("Enter the file-name to import data.");
                    sc.nextLine();
                    String file_name = sc.nextLine();

                    File file = new File(file_name);

                    // Creating new Scanner Object to read in file
                    Scanner filescanner = new Scanner(file);

                    // Read in input file
                    while(filescanner.hasNextLine()) {
                        line = filescanner.nextLine();

                        // Dividing line to parts separated by Delimiter (",")
                        String[] parts = line.split(",");

                        String file_customer_name = parts[0];
                        String file_address = parts[1];
                        int file_category= Integer.parseInt(parts[2]);

                        System.out.println("Connecting to the database...");
                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_1)){
                             // Make a new customer and populate the database with it.
                                statement.setString(1, file_customer_name);
                                statement.setString(2, file_address);
                                statement.setInt(3, file_category);

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }


                    }

                    } catch (Exception e) {
                        System.out.print("You got an error!. Returning to main menu");
                    }
                break;
                case "17":
                    // try and catch are used to not terminate loop in case of error.
                    try {

                        // Enter the filename to output result
                        System.out.println("Enter the file-name to Export data.");
                        sc.nextLine();
                        final String file_name1 = sc.nextLine();

                        // Taking lower bound of category from user
                        System.out.println("Enter the lower bound of category.");
                        final int lower_bound2 = sc.nextInt();

                        // Taking upper bound of category from user
                        System.out.println("Enter the upper bound of category.");
                        final int upper_bound2 = sc.nextInt();

                        // Creating new file writer Object
                        FileWriter fw = new FileWriter(file_name1);

                        System.out.println("Connecting to the database...");
                        // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_13)){
                                // Make a new customer and populate the database with it.
                                statement.setInt(1,lower_bound2);
                                statement.setInt(2, upper_bound2);
                                final ResultSet resultSet = statement.executeQuery();

                                // Output customer name to data file
                                while (resultSet.next()) {
                                    fw.write("Customer name is " + resultSet.getString(1) + "\n");
                                }
                                fw.close();
                            } catch (SQLException e) {
                                e.getCause().getMessage();
                            }
                        }

                        } catch (Exception e) {
                            System.out.println("You got an error!. Returning to main menu");
                        }
                    break;
                case "18": // Do nothing, the while loop will terminate upon the next iteration
                    System.out.println("Quitting.");
                    break;

                default: // Unrecognized option, re-prompt the user for the correct one
                    System.out.println(String.format(
                        "Unrecognized option: %s\n" +
                        "Please try again!",
                        option));
                    break;
            }
        }

        sc.close(); // Close the scanner before exiting the application
    }
}
