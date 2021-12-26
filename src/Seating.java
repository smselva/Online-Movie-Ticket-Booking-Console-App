import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

class Seating {
    Seating(){}

    Seating(int row,int col,String date,String time,String theatreName,int screenNum,String movieName,Connection con) throws SQLException {
        Statement st = con.createStatement();
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= col; j++) {
                st.executeUpdate("insert into seat values("+i+","+j+",'"+date+"','"+time+"','"+theatreName+"',"+screenNum+",'"+movieName+"',false)");
            }
        }
    }

    public String seatBook(Connection con, String theatreName,String movieName,String date,String time,int screenNum, int tickets) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rc = st.executeQuery("select * from screendetails where theatrename='"+theatreName+"' and screennum = "+screenNum+"");
        rc.next();
        int row = rc.getInt("row_");
        int col = rc.getInt("column_");
        System.out.print("\t");
        for (int j = 1; j <= col; j++) {
            System.out.print(j + "\t");
        }
        System.out.println();
        ResultSet book = st.executeQuery("select booked from seat where theatrename = '"+theatreName+"' and moviename = '"+movieName+"' and date='"+date+"' and time = '"+time+"' and screennum = "+screenNum+"");
        book.next();
        for (int i = 1; i <= row; i++) {
            System.out.print((char) (i + 64) +"\t");
            for (int j = 1; j <= col; j++) {
                if (!book.getBoolean("booked"))
                    System.out.print("-" + "\t");
                else
                    System.out.print("Na" + "\t");
                book.next();
            }
            System.out.println();
        }
        Scanner sc = new Scanner(System.in);
        Scanner in = new Scanner(System.in);
        String s = "";
        int count = 0;
        while(tickets != 0) {
            System.out.println("Enter Row");
            char r = sc.next().charAt(0);
            System.out.println("Enter column");
            int c = in.nextInt();
            s += r + "" + c + " ";
            int r1 = (int) r - 64;
            count = st.executeUpdate("update seat set booked = 1 where row_ ="+r1+" and column_ ="+c+" and  theatrename='"+theatreName+"' and  moviename ='"+movieName+"' and date ='"+date+"' and time ='"+time+"' and screennum = "+screenNum+"");
            tickets--;
        }
        if(count > 0) {
            return s;
        }
        return null;
    }

    public void cancelSeat(String bookingid,Connection con) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select * from booking where bookingid =" + bookingid);
        rs.next();
        String theatreName = rs.getString("theatrename");
        String movieName = rs.getString("moviename");
        String date = rs.getString("date");
        String time = rs.getString("time");
        int screenNum = rs.getInt("screennum");
        String[] seats = rs.getString("seats").split(" ");
        int r,c;
        for (String s : seats) {
            r = s.charAt(0) - 64;
            String ch = s.substring(1);
            c = Integer.parseInt(ch);
            st.executeUpdate("update seat set booked = 0 where row_ =" + r + " and column_ =" + c + " and  theatrename='" + theatreName + "' and  moviename ='" + movieName + "' and date ='" + date + "' and time ='" + time + "' and screennum = "+screenNum+"");
        }
        System.out.println("Amount will be Refunded!!!");
    }
}
