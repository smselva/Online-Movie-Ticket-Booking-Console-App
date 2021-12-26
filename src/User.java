import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class User extends Admin{
    public void addUser(String userid, String name,String phone, String password, Connection con) throws Exception{
        Statement st = con.createStatement();
        st.executeUpdate("insert into user values('"+userid+"','"+name+"','"+phone+"','"+password+"')");
    }

    public void bookMovie(String userid, Connection con) throws SQLException {
        Statement st = con.createStatement();
        Statement t = con.createStatement();
        Statement d = con.createStatement();
        Scanner sc = new Scanner(System.in);
        Scanner c = new Scanner(System.in);
        Scanner in = new Scanner(System.in);
        System.out.println("Enter your Place(Ex.salem)");
        String place = sc.nextLine();
        ResultSet movies = st.executeQuery("select distinct(moviename) from showdetails where theatreplace = '"+place+"'");
        System.out.println("         Movies List");
        System.out.println("---------------------------");
        while (movies.next())
        {
            System.out.println(movies.getString("moviename"));
        }

        try {
            System.out.println();
            System.out.println("Enter the movie name");
            String movie = sc.nextLine();
            ResultSet details = st.executeQuery("select distinct(theatrename) from showdetails where moviename='" + movie + "' and theatreplace = '" + place + "'");

            while (details.next()) {
                String theatreName = details.getString("theatrename");
                System.out.println(theatreName.toUpperCase());
                ResultSet date = d.executeQuery("select distinct(date) from showdetails where theatrename = '" + theatreName + "' and moviename='" + movie + "'");
                while (date.next()) {
                    System.out.println(date.getString("date") + "\t");
                    ResultSet time = t.executeQuery("select time from showdetails where moviename='" + movie + "' and theatrename = '" + theatreName + "' and date = '" + date.getString("date") + "'");
                    while (time.next()) {
                        System.out.print(time.getString("time") + "\t");
                    }
                    System.out.println();
                }
                System.out.println();
            }

            System.out.println("Enter the Theatre Name");
            String theatreName = sc.nextLine();
            System.out.println("Enter the Date(YYYY-MM-DD)");
            String date = sc.nextLine();
            System.out.println("Enter the Time(HH:MM)");
            String time = sc.nextLine();
            ResultSet rs = st.executeQuery("select price,screennum from showdetails where moviename='" + movie + "' and date = '" + date + "' and time = '" + time + "'");
            rs.next();
            int price = rs.getInt("price");
            int screenNum = rs.getInt("screennum");
            System.out.println("Price is :" + price);
            System.out.println("Enter the Number of Tickets");
            int noOfTickets = in.nextInt();
            int totalAmount = price * noOfTickets;

            String seats = seatBook(con, theatreName, movie, date, time, screenNum, noOfTickets);
            if (seats != null) {
                ResultSet fa = st.executeQuery("select count(foodname) from foodandbeverages where theatrename='"+theatreName+"'");
                fa.next();
                if (fa.getInt("count(foodname)") > 0) {
                    System.out.println("Enter option 1.Add food  2.Skip");
                    if (in.nextInt() == 1) {
                        HashMap<String,Object> fooddetails = new HashMap<>();
                        fooddetails = food(name, con);
                        int total = totalAmount + (int)fooddetails.get("foodprice");
                        String foodsordered = (String) fooddetails.get("foodordered");
                        System.out.println("Pay:" + total);
                        st.executeUpdate("insert into booking(userid,theatrename,screennum,moviename,date,time,price,seats,foodsordered,totalprice) values('" + userid + "','" + theatreName + "'," + screenNum + ",'" + movie + "','" + date + "','" + time + "',"+price+",'"+seats+"','"+foodsordered+"',"+total+")");
                        System.out.println("Tickets Booked Successfully");
                    } else {
                        System.out.println("Pay:" + totalAmount);
                        st.executeUpdate("insert into booking(userid,theatrename,screennum,moviename,date,time,price,seats,totalprice) values('" + userid + "','" + theatreName + "'," + screenNum + ",'" + movie + "','" + date + "','" + time + "',"+price+",'"+seats+"',"+totalAmount+")");
                        System.out.println("Tickets Booked Successfully");
                    }
                } else {
                    System.out.println("Pay:" + totalAmount);
                    st.executeUpdate("insert into booking(userid,theatrename,screennum,moviename,date,time,price,seats,totalprice) values('" + userid + "','" + theatreName + "'," + screenNum + ",'" + movie + "','" + date + "','" + time + "',"+price+",'"+seats+"',"+totalAmount+")");
                    System.out.println("Tickets Booked Successfully");
                }
            }
        }
        catch(SQLException e)
        {
            System.out.println("Entered wrong Details");
        }
    }

    public void cancelMovie(String userid,Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the booking id");
        String bookingid = sc.nextLine();
        cancelSeat(bookingid,con);
        st.executeUpdate("delete from booking where bookingid="+bookingid+"");
    }

    public void printTicket(String userid,Connection con) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select * from booking where userid='"+userid+"' order by bookingid desc limit 1");
        rs.next();
        System.out.println("           "+rs.getString("theatrename"));
        System.out.println("SCREEN NO   :"+rs.getString("screennum"));
        System.out.println("Booking Id  :"+rs.getString("bookingid"));
        System.out.println("Movie       :"+rs.getString("moviename"));
        System.out.println("Date        :"+rs.getString("date"));
        System.out.println("Time        :"+rs.getString("time"));
        System.out.println("Seats       :"+rs.getString("seats"));
        System.out.println("Price       :"+rs.getString("price"));
        if(rs.getString("foodsordered") != null)
        {
            System.out.println("Total       :"+rs.getString("foodsordered"));
        }
        System.out.println("Total       :"+rs.getString("totalprice"));
    }

    public boolean login(String userid, String password, Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet pass = st.executeQuery("select password from user where userid = '" + userid + "'");
            pass.next();
            if (pass.getString("password").equals(password)) {
                st.close();
                return true;
            }
            else
            {
                System.out.println("Wrong login Details");
            }
        }
        catch (SQLException e)
        {
            System.out.println("Id not exists");
        }
        return false;
    }

    public void forgetPassword(Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter userid or mailid");
        String userid = sc.nextLine();
        System.out.println("Enter new Password");
        String password = sc.nextLine();

        try {
            st.executeUpdate("update user set password ='" + password + "' where userid ='" + userid + "'");
            System.out.println("Password Changed Successfully");
        } catch (SQLException e) {
            System.out.println("Wrong userid");
        }
    }
}
