import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Admin admin=new Admin();
        User user = new User();
        String mailid,name,theatreName,theatrePlace,password,phone;
        int row,column;
        boolean loop;
        Scanner sc = new Scanner(System.in);
        Scanner in = new Scanner(System.in);

        String url = "jdbc:mysql://localhost:3306/moviebooking";
        String uname = "root";
        String pass = "test";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url,uname,pass);
        Statement st = con.createStatement();

        while (true) {
            System.out.println("Enter option  1.Admin  2.User");
            switch (in.nextInt()) {
                case 1:
                    System.out.println("Enter option  1.Registration  2.Login  3.Forget Password");
                    switch (in.nextInt()) {
                        case 1:
                            admin = new Admin(con);
                            break;
                        case 2:
                            System.out.println("Enter userid or mailid");
                            mailid = sc.nextLine();
                            System.out.println("Enter password");
                            password = sc.nextLine();
                            if (admin.login(mailid, password, con)) {
                                loop = true;
                                while (loop) {
                                    System.out.println("Enter option\n1.Schedule Movies  2.Remove Movies  \n" +
                                                                     "3.Add Food         4.Remove Food     5.Exit");
                                    ResultSet rs = st.executeQuery("select theatrename from admin where userid='" + mailid + "'");
                                    rs.next();
                                    switch (in.nextInt()) {
                                        case 1:
                                            admin.scheduleMovies(rs.getString("theatrename"), con);
                                            break;
                                        case 2:
                                            admin.removeMovies(rs.getString("theatrename"), con);
                                            break;
                                        case 3:
                                            admin.addFood(rs.getString("theatrename"), con);
                                            break;
                                        case 4:
                                            admin.removeFood(rs.getString("theatrename"), con);
                                            break;
                                        default:
                                            loop = false;
                                            break;
                                    }
                                }
                            }
                            break;
                        case 3:
                            admin.forgetPassword(con);
                            break;
                    }
                    break;
                case 2:
                    System.out.println("Enter option  1.Registration  2.Login  3.Forget Password");
                    switch (in.nextInt()) {
                        case 1:
                            System.out.println("Enter mailid");
                            mailid = sc.nextLine();
                            System.out.println("Enter your name");
                            name = sc.nextLine();
                            System.out.println("Enter Phone Number");
                            phone = sc.nextLine();
                            System.out.println("Enter password");
                            password = sc.nextLine();
                            user.addUser(mailid, name, phone,password,con);
                            break;
                        case 2:
                            loop = true;
                            System.out.println("Enter userid or mailid");
                            mailid = sc.nextLine();
                            System.out.println("Enter password");
                            password = sc.nextLine();
                            if (user.login(mailid, password,con)) {
                                loop = true;
                                while (loop)
                                {
                                    System.out.println("Enter option\n1.BookMovie  2.PrintTicket  3.CancelMovie  4.Exit");
                                    switch (in.nextInt())
                                    {
                                        case 1:
                                            user.bookMovie(mailid,con);
                                            break;
                                        case 2:
                                            user.printTicket(mailid,con);
                                            break;
                                        case 3:
                                            user.cancelMovie(mailid,con);
                                            break;
                                        default:
                                            loop = false;
                                            break;
                                    }
                                }
                            }
                            break;
                        case 3:
                            user.forgetPassword(con);
                            break;
                    }
                    break;
            }
        }
    }
}
