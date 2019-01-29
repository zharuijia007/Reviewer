package com.ruijiazha.reviewer.database;

import com.ruijiazha.reviewer.data.Review;
import com.ruijiazha.reviewer.data.ReviewShow;
import com.ruijiazha.reviewer.data.User;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper {

    public static String driver = "com.mysql.jdbc.Driver";
    public static String url = "";
    public static String user = "";
    public static String password = "";

    //set connection
    public static Connection setConnection() {
        Connection con = null;
        try {
            Class.forName(driver);

            con = DriverManager.getConnection(url, user, password);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    //register
    public static int register(Connection con, User u) {
        int res = 0;
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO user_info(username,password,gender,age,years,time) VALUES (?,?,?,?,?,?)";
            //String sql = "INSERT INTO test(col) VALUES ('asd')";
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getGender());
            ps.setString(4, u.getAge());
            ps.setString(5, u.getYears());
            ps.setString(6, getCurrentTime());
            res = ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return res;
    }

    //save a review
    public static int reviewSave(Connection con, Review r, FileInputStream fis, File image) {
        int res = -1;
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO collected_review(appname,reviewer,rating,screenshot,marker,overall,type,Q1,Q2,Q3,Q4,Q5,Q6,Q7,time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //String sql = "INSERT INTO test(col) VALUES ('asd')";
            ps = con.prepareStatement(sql);
            ps.setString(1, r.getAppName());
            ps.setString(2, r.getUsername());
            ps.setInt(3, r.getRating());
            ps.setBytes(4, r.getScreenshot());
//            if (image == null) {
//                ps.setBinaryStream(4, null);
//            } else {
//                ps.setBinaryStream(4, (InputStream) fis, (int) (image.length()));
//            }
            ps.setString(5, r.getMarker());
            ps.setString(6, r.getOverall());
            ps.setString(7, r.getType());
            ps.setString(8, r.getQ1());
            ps.setString(9, r.getQ2());
            ps.setString(10, r.getQ3());
            ps.setString(11, r.getQ4());
            ps.setString(12, r.getQ5());
            ps.setString(13, r.getQ6());
            ps.setString(14, r.getQ7());
            ps.setString(15, getCurrentTime());
            res = ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return res;
    }

    //fetch review list for profile
    public static List<ReviewShow> getReviewList(Connection con, String username) {
        List<ReviewShow> list = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM collected_review WHERE reviewer = ? ORDER BY time DESC";
        try {
            if (con != null && (!con.isClosed())) {
                ps = con.prepareStatement(sql);
                ps.setString(1, username);
                rs = ps.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        ReviewShow reviewShow = new ReviewShow();
                        reviewShow.setAppName(rs.getString("appname"));
                        reviewShow.setRates(rs.getInt("rating"));
                        reviewShow.setTime(rs.getString("time"));
                        reviewShow.setReviewid(rs.getInt("reviewid"));
                        list.add(reviewShow);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        return list;
    }

    //fetch a review for detail
    public static Review getAReview(Connection con, int reviewid) {
        List<ReviewShow> list = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        Review review = new Review();
        String sql = "SELECT * FROM collected_review WHERE reviewid = ?";
        try {
            if (con != null && (!con.isClosed())) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, reviewid);
                rs = ps.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        review.setAppName(rs.getString("appname"));
                        review.setRating(rs.getInt("rating"));
                        review.setTime(rs.getString("time"));
                        review.setOverall(rs.getString("overall"));
                        review.setScreenshot(rs.getBytes("screenshot"));
                        review.setMarker(rs.getString("marker"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        return review;
    }

    //delete a review on profile
    public static int deleteReview(Connection con, int reviewid) {
        int res = -1;
        PreparedStatement ps = null;
        String sql = "DELETE FROM collected_review WHERE reviewid = ?";
        try {
            if (con != null && (!con.isClosed())) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, reviewid);
                res = ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        return res;
    }


    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
