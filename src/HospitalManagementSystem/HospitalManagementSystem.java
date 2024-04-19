package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem
{
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password ="system";

    public static void main(String[] args)
    {


        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }



        Scanner scanner = new Scanner(System.in);


        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while(true)
            {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View All Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Delete Patient By ID ");
                System.out.println("6. viwe Patient By ID ");
                System.out.println("7. View Appointment Details ");
                System.out.println("8. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch(choice)
                {
                    case 1:
                        // Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patient
                        patient.viewAllPatients();
                        System.out.println();
                        break;
                    case 3:
                        // View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // Book Appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        // Delete Patient By ID
                        patient.deletePatientById();
                        System.out.println();
                        break;

                    case 6:
                        // View Patient By ID
                        patient.viewPatientById();
                        System.out.println();
                        break;
                    case 7:
                        // View Appointment Details
                        appointmentDetails(connection);
                        System.out.println();
                        break;
                    case 8:
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId))
        {
            if(checkDoctorAvailability(doctorId, appointmentDate, connection))
            {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";

                try
                {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0)
                    {
                        System.out.println("Appointment Booked!");
                    }
                    else
                    {
                        System.out.println("Failed to Book Appointment!");
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Doctor not available on this date!!");
            }
        }
        else
        {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection)
    {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                int count = resultSet.getInt(1);

                // add extra line (nit required)----->      int count1=Integer.parseInt(resultSet.getString(2)); && count1==0
               // int count1=Integer.parseInt(resultSet.getString(2));
                if(count==0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }



    public static void appointmentDetails( Connection connection)
    {
        String appointmentDetailQuery="SELECT * FROM appointments";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(appointmentDetailQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("AppointmentDetails: ");
            System.out.println("+----------------+---------+----------+-------------------+");
            System.out.println("| Appointment Id | patientId | doctorId | appointmentDate |");
            System.out.println("+----------------+---------+----------+-------------------+");
            while(resultSet.next())
            {
                int id = resultSet.getInt("id");
                int patient_id = resultSet.getInt("patient_id");
                int doctor_id = resultSet.getInt("doctor_id");
                String appointment_date=resultSet.getString("appointment_date");
                System.out.printf("| %-14s | %-9s | %-8s | %-15s |\n", id, patient_id, doctor_id, appointment_date);
                System.out.println("+----------------+---------+----------+-------------------+");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


}