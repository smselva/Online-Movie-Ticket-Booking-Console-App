import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Admin extends Seating{
    String name, theatreName, theatrePlace,password,mailid;
    int row, column, totalScreen ;
    Admin(){}
    Admin(Connection con) throws Exception{
        Scanner sc = new Scanner(System.in);
        Statement st = con.createStatement();
        System.out.println("Enter mailid");
        mailid = sc.nextLine();
        System.out.println("Enter your name");
        name = sc.nextLine();
        System.out.println("Enter Theatre Name");
        theatreName = sc.nextLine();
        System.out.println("Enter your theatrePlace");
        theatrePlace = sc.nextLine();
        System.out.println("Enter password");
        password = sc.nextLine();
        st.executeUpdate("insert into admin values('" + mailid + "','" + name + "','" + theatreName + "','" + theatrePlace + "','" + password + "')");
        System.out.println("Account Created Successfully");
        addScreen(theatreName,con);
        st.close();
    }

    public void addScreen(String theatreName,Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of Screens");
        totalScreen = Integer.parseInt(sc.nextLine());
        int screenNum = 1;
        while(screenNum <= totalScreen) {
            System.out.println("Enter Number of Row And Columns For Screen Num:"+screenNum);
            System.out.println("Enter Number of rows");
            row = Integer.parseInt(sc.nextLine());
            System.out.println("Enter Number of column");
            column = Integer.parseInt(sc.nextLine());
            st.executeUpdate("insert into screendetails values('"+theatreName+"',"+screenNum+","+row+","+column+")");
            screenNum++;
        }
        System.out.println("Screen Added Successfully");
    }

    public void scheduleMovies(String theatreName, Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        Scanner in = new Scanner(System.in);
        ResultSet place = st.executeQuery("select theatreplace from admin where theatrename = '" + theatreName + "'");
        place.next();
        String theatrePlace = place.getString("theatreplace");
        ResultSet totalScreen = st.executeQuery("select count(screennum) from screendetails where theatrename = '" + theatreName + "'");
        totalScreen.next();
        int total = totalScreen.getInt("count(screennum)");
        System.out.println("Enter the No of Days");
        int days = in.nextInt();
        while (days != 0) {
            System.out.println("Enter the Date(Ex. YYYY-MM-DD)");
            String date = sc.nextLine();
            int screenNum = 1;
            while (screenNum <= total) {
                System.out.println("Enter the Number of Shows for the Screen:"+screenNum);
                int shows = in.nextInt();
                ResultSet rc = st.executeQuery("select * from screendetails where theatrename = '" + theatreName + "' and screennum = " + screenNum + "");
                rc.next();
                int r = rc.getInt("row_");
                int c = rc.getInt("column_");
                while(shows != 0) {
                    System.out.println("Movie Details For Screen Num:" + screenNum);
                    System.out.println("Enter Time (Ex.HH:MM)");
                    String time = sc.nextLine();
                    System.out.println("Enter Movie Name");
                    String movieName = sc.nextLine();
                    System.out.println("Enter Price of Ticket");
                    int price = in.nextInt();
                    st.executeUpdate("insert into showdetails values ('" + theatreName + "','" + theatrePlace + "'," + screenNum + ",'" + movieName + "','" + date + "','" + time + "'," + price + ")");
                    new Seating(r, c, date, time, theatreName, screenNum, movieName, con);
                    shows--;
                }screenNum++;
            }
            days--;
        }
    }

    public void removeMovies(String theatreName, Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Movie Name to Remove");
        String movieName = sc.nextLine();
        st.executeUpdate("delete from showdetails where moviename='"+movieName+"'");
        st.executeUpdate("delete from seat where moviename='"+movieName+"'");
        st.close();
    }

    public void addFood(String theatreName,Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        int option = 1;
        while(option == 1) {
            System.out.println("Enter the food name");
            String foodName = sc.nextLine();
            System.out.println("Enter the food price");
            int price = Integer.parseInt(sc.nextLine());
            st.executeUpdate("insert into foodandbeverages values('"+theatreName+"','" + foodName + "'," + price + ")");
            System.out.println("Enter option 1.Add Food  2.Exit");
            option = Integer.parseInt(sc.nextLine());
        }
        st.close();
    }

    public void removeFood(String theatreName,Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        int option = 1;
        while(option == 1) {
            System.out.println("Enter option 1.Remove Food  2.Exit");
            option = Integer.parseInt(sc.nextLine());
            System.out.println("Enter the food name to remove");
            String foodName = sc.nextLine();
            st.executeUpdate("delete from foodandbeverages where foodname = '" + foodName + "' where theatrename = '"+theatreName+"'");
            st.close();
        }
    }

    public HashMap<String, Object> food(String theatreName, Connection con) throws SQLException {
        Statement st = con.createStatement();
        Scanner in = new Scanner(System.in);

        ResultSet rs = st.executeQuery("select * from foodandbeverages where theatrename = '" + theatreName + "'");
        while (rs.next())
        {
            System.out.println(rs.getString("foodname") +"   "+rs.getString("price"));
        }
        int key = 1;
        int foodPrice = 0;
        String foodOrdered = "";
        String foodname;
        while (key != 0) {
            System.out.println("Enter the name of the food");
            foodname = in.nextLine();
            ResultSet food = st.executeQuery("select * from foodandbeverages where theatrename = '" + theatreName + "' and foodname='"+foodname+"'");
            food.next();
            foodPrice += food.getInt("price");
            foodOrdered += food.getString("foodname") + " ";
            System.out.println("Enter 0 to proceed 1 to continue");
            key = Integer.parseInt(in.nextLine());
        }
        HashMap<String,Object> foodlist = new HashMap<>();
        foodlist.clear();
        foodlist.put("foodprice",foodPrice);
        foodlist.put("foodordered",foodOrdered);
        return foodlist;
    }

    public boolean login(String userid, String password, Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet pass = st.executeQuery("select password from admin where userid = '" + userid + "'");
            pass.next();
            if (pass.getString("password").equals(password)) {
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
        System.out.println("Enter your userid or mailid");
        String userid = sc.nextLine();
        System.out.println("Enter new Password");
        String password = sc.nextLine();

        try {
            st.executeUpdate("update admin set password ='" + password + "' where userid ='" + userid + "'");
            System.out.println("Password Changed Successfully");
        } catch (SQLException e) {
            System.out.println("Wrong userid");
        }
    }

}
